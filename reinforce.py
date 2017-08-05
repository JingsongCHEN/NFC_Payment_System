# -*-coding:utf-8 -*-

import argparse
import torchcraft_py.proto as proto
import torchcraft_py.torchcraft as tc
import torchcraft_py.utils as utils
import package.State as State
import tensorflow as tf
import numpy as np
import Config as Config

DEBUG = 0

# 总盘数
total_battles = 0
# 获胜盘数
battles_won = 0
nrestarts = -1

parser = argparse.ArgumentParser()
parser.add_argument('--ip', help='server ip', default='10.212.47.158')
parser.add_argument('--port', help='server port', default='11112')
args = parser.parse_args()
print args

# 根据概率分布选择动作
def choose_action(prob, act_list):
    lot = np.random.uniform()
    chosen = -1
    a = []
    t = 0
    for i in prob:
        t += i
        chosen += 1
        if lot < t:
            break
    return act_list[chosen], chosen

# 计算累积reward
def normalized_rewards(r, n):
    normalized_r = np.zeros_like(r)
    normalized_r[len(r)-1] = r[len(r)-1]
    for t in reversed(range(0, (len(r)-1))):
        normalized_r[t] = (normalized_r[t+1]*n[t+1] + r[t])/n[t]
    return normalized_r

Learning_Rate = 1e-2

if __name__ == '__main__':

    CONFIG = Config.Config()

    # 神经网络
    state_action = tf.placeholder(tf.float32, [None, CONFIG.feature_size], name="state_action")
    command_size = tf.placeholder(tf.int32, name="command_size")
    unit_size = tf.placeholder(tf.int32, name="unit_size")

    with tf.name_scope('hidden1'):
        W1 = tf.Variable(tf.truncated_normal([CONFIG.feature_size, CONFIG.hidden1_units], stddev=0.1), name="W1")
        b1 = tf.Variable(tf.cast(tf.zeros([CONFIG.hidden1_units]) + 0.1, tf.float32), name="b1")
        hidden1 = tf.nn.elu(tf.matmul(state_action, W1) + b1)

    with tf.name_scope('hidden2'):
        W2 = tf.Variable(tf.truncated_normal([CONFIG.hidden1_units, CONFIG.hidden2_units], stddev=0.1), name="W2")
        b2 = tf.Variable(tf.cast(tf.zeros([CONFIG.hidden2_units]) + 0.1, tf.float32), name="b2")
        hidden2 = tf.nn.tanh(tf.matmul(hidden1, W2) + b2)

        x = tf.reshape(hidden2, [command_size, unit_size, CONFIG.hidden2_units])

        pool_max = tf.reduce_max(x,1)

        pool_avg = tf.reduce_mean(x,1)

        pool_max = tf.reshape(pool_max, [command_size, CONFIG.hidden2_units])
        pool_avg = tf.reshape(pool_avg, [command_size, CONFIG.hidden2_units])

        c_acttype = tf.placeholder(tf.float32, [None, 2])
        c_acttype = tf.reshape(c_acttype, [command_size, 2])

        pool2 = tf.concat([pool_avg, pool_max, c_acttype], 1)
        pool2 = tf.reshape(pool2, [command_size, 202])

    with tf.name_scope('hidden3'):
        W3 = tf.Variable(tf.truncated_normal([202, CONFIG.hidden3_units], stddev=0.1), name="W3")
        b3 = tf.Variable(tf.cast(tf.zeros([CONFIG.hidden3_units]) + 0.1, tf.float32), name="b3")
        hidden3 = tf.nn.elu(tf.matmul(pool2, W3) + b3)

    with tf.name_scope('hidden4'):
        W4 = tf.Variable(tf.truncated_normal([CONFIG.hidden3_units, CONFIG.hidden4_units], stddev=0.1), name="W4")
        b4 = tf.Variable(tf.cast(tf.zeros([CONFIG.hidden4_units]) + 0.1, tf.float32), name="b4")
        hidden4 = tf.nn.relu(tf.matmul(hidden3, W4) + b4)


    with tf.name_scope('out'):
        W_o = tf.Variable(tf.truncated_normal([CONFIG.hidden4_units, 1], stddev=0.1), name="W_o")
        out = tf.matmul(hidden4, W_o)
        out = tf.reshape(out, [command_size])

    # 输出的概率分布
    probability = tf.nn.softmax(out)

    tvars = tf.trainable_variables()

    # label
    input_y = tf.placeholder(tf.float32, [None], name="input_y")

    # reward
    advantages = tf.placeholder(tf.float32, name="reward")
    loglik = tf.nn.softmax_cross_entropy_with_logits(labels=input_y, logits=out)
    loss = tf.reduce_mean(loglik * advantages)

    newGrads = tf.gradients(loss,tvars)

    Learning_rate = tf.placeholder(tf.float32, name="Learning_rate")
    adam = tf.train.AdamOptimizer(learning_rate=Learning_rate)

    # 计算出的梯度
    W1Grad = tf.placeholder(tf.float32, name="batch_grad_W1")
    b1Grad = tf.placeholder(tf.float32, name="batch_grad_b1")
    W2Grad = tf.placeholder(tf.float32, name="batch_grad_W2")
    b2Grad = tf.placeholder(tf.float32, name="batch_grad_b2")
    W3Grad = tf.placeholder(tf.float32, name="batch_grad_W3")
    b3Grad = tf.placeholder(tf.float32, name="batch_grad_b3")
    W4Grad = tf.placeholder(tf.float32, name="batch_grad_W4")
    b4Grad = tf.placeholder(tf.float32, name="batch_grad_b4")
    WoGrad = tf.placeholder(tf.float32, name="batch_grad_Wo")
    batchGrad = [W1Grad, b1Grad, W2Grad, b2Grad, W3Grad, b3Grad, W4Grad, b4Grad, WoGrad]
    updateGrads = adam.apply_gradients(zip(batchGrad, tvars))

    init = tf.global_variables_initializer()
    # 保存网络参数
    saver = tf.train.Saver()

    with tf.Session() as sess:

        sess.run(init)

        # 读取本地的网络参数
        ckpt = tf.train.get_checkpoint_state("../tmp/")
        if ckpt and ckpt.model_checkpoint_path:
            print "OLD VARS!"
            saver.restore(sess, ckpt.model_checkpoint_path)
        else:
            print "NEW VARS!"

        gradBuffer = sess.run(tvars)
        for ix, grad in enumerate(gradBuffer):
            gradBuffer[ix] = grad * 0

        while True:
            nloop = 1
            battles_game = 0

            print ""
            print "GAME STARTED"

            # 连接游戏客户端
            client = tc.Client(args.ip, args.port)
            init = client.connect()
            if DEBUG > 0:
                print "Received init:  " + init

            # 设置游戏参数
            setup = [proto.concat_cmd(proto.commands['set_speed'], 0.001),
                     proto.concat_cmd(proto.commands['set_gui'], 1),
                     proto.concat_cmd(proto.commands['set_frameskip'], 9),
                     proto.concat_cmd(proto.commands['set_cmd_optim'], 1)]

            if DEBUG > 0:
                print "Setting up the game: " + ':'.join(setup)

            client.send(setup)
            utils.progress(nloop, battles_won, battles_game, total_battles)

            # 进行初始化游戏
            if (raw_input() == "r"):
                actions = [proto.concat_cmd(proto.commands['restart'])]
                client.send(actions)

            # 接收服务器端的游戏数据
            update = client.receive()
            # 使用当前的游戏数据创建State对象
            MyState = State.State()
            # 初始化参数
            old_m_hp = -1.0
            old_e_hp = -1.0
            state_action_r = []
            c_acttype_r = []
            rewards = []
            input_y_r = []
            num_units = []
            # 为了避免最后一个reward被清零
            notlast = 1
            str_F_loss = ""
            str_F_win = ""
            str_F_rewards = ""

            while True:

                # 接收服务器端的游戏数据
                update = client.receive()
                MyState.update(client.state)

                # 判断units是否已经被重新生成，原因是每场战斗结束后，在变成"battle_just_ended"状态前就已经生成新的units
                if (len(MyState.units_dead_id) == 10):
                    MyState = State.State()

                # 记录reward
                if(len(MyState.units) and notlast):
                    # 计算reward
                    if (old_m_hp != -1.0 and old_e_hp != -1.0):
                        new_m_hp = 0.0
                        new_e_hp = 0.0
                        for uid in MyState.units_id:
                            new_m_hp += (MyState.units[uid].hp + MyState.units[uid].shield)
                        for uid in MyState.units_e_id:
                            new_e_hp += (MyState.units[uid].hp + MyState.units[uid].shield)
                        rewards[len(rewards)-1] = ((old_e_hp - new_e_hp) - (old_m_hp - new_m_hp))
                        old_m_hp = new_m_hp
                        old_e_hp = new_e_hp
                    # 记录第一次的血量
                    else:
                        old_m_hp = 0.0
                        old_e_hp = 0.0
                        for uid in MyState.units_id:
                            old_m_hp += (MyState.units[uid].hp + MyState.units[uid].shield)
                        for uid in MyState.units_e_id:
                            old_e_hp += (MyState.units[uid].hp + MyState.units[uid].shield)
                    # 判断是否是最后一次攻击
                    if(len(MyState.units_id) == 0 or len(MyState.units_e_id) == 0):
                        notlast = 0


                if DEBUG > 0:
                    print "Received state: " + update

                nloop += 1
                actions = []

                if bool(client.state.d['game_ended']):
                    if True:
                        print "GAME ENDED"
                    break

                # 如果这一场战斗结束了
                elif client.state.d['battle_just_ended']:
                    if DEBUG > 0:
                        print "BATTLE ENDED"

                    # 如果这一场战斗赢了
                    if bool(client.state.d['battle_won']):
                        battles_won += 1

                    battles_game += 1
                    total_battles += 1

                    # 计算累积reward
                    normalized_r = normalized_rewards(rewards, num_units)
                    loss_sum = 0

                    # 计算梯度 因维度会改变所以无法批处理，必须要循环计算
                    for i in range(0, len(state_action_r)):

                        # 记录loss
                        loss_sum += loss.eval(feed_dict={state_action: state_action_r[i], command_size: len(c_acttype_r[i]),
                                                         unit_size: len(state_action_r[i]) / len(c_acttype_r[i]),
                                                         c_acttype: c_acttype_r[i], input_y: input_y_r[i],
                                                         advantages: normalized_r[i]})
                        # 计算梯度
                        tGrad = sess.run(newGrads, feed_dict={state_action: state_action_r[i], command_size: len(c_acttype_r[i]),
                                                              unit_size: len(state_action_r[i]) / len(c_acttype_r[i]),
                                                              c_acttype: c_acttype_r[i], input_y: input_y_r[i],
                                                              advantages: normalized_r[i]})
                        # 累积梯度
                        for ix, grad in enumerate(tGrad):
                            gradBuffer[ix] += grad

                    # 如果出现胜利，则减小学习率
                    if(loss_sum > 0.0 and Learning_Rate == 1e-2):
                        Learning_Rate = 1e-3

                    # 更新梯度
                    sess.run(updateGrads, feed_dict={Learning_rate: Learning_Rate, W1Grad: gradBuffer[0], b1Grad: gradBuffer[1],
                                                     W2Grad: gradBuffer[2], b2Grad: gradBuffer[3],
                                                     W3Grad: gradBuffer[4], b3Grad: gradBuffer[5],
                                                     W4Grad: gradBuffer[6], b4Grad: gradBuffer[7],
                                                     WoGrad: gradBuffer[8]})

                    # 初始化
                    for ix, grad in enumerate(gradBuffer):
                        gradBuffer[ix] = grad * 0

                    # 输出每一场战斗的信息
                    utils.progress(nloop, battles_won, battles_game, total_battles)
                    print "LOSS: ", loss_sum

                    str_F_loss += str(total_battles) + "\n" + str(loss_sum) + "\n"
                    str_F_win += str(total_battles) + "\n" + str(battles_won) + "\n"
                    str_F_rewards += str(total_battles) + "\n" + str(np.mean(rewards)) + "\n"

                    # 当一场对战结束后，要重新创建一个State对象
                    MyState = State.State()
                    old_m_hp = -1.0
                    old_e_hp = -1.0
                    state_action_r = []
                    c_acttype_r = []
                    rewards = []
                    input_y_r = []
                    num_units = []
                    notlast = 1

                    # 每十场对战记录一次参数
                    if(battles_game % 10 == 0):
                        F_loss = open("../tmp/loss", 'a')
                        F_win = open("../tmp/win", 'a')
                        F_rewards = open("../tmp/rewards", 'a')
                        F_loss.write(str_F_loss)
                        str_F_loss = ""
                        F_win.write(str_F_win)
                        str_F_win = ""
                        F_rewards.write(str_F_rewards)
                        str_F_rewards = ""
                        F_loss.close()
                        F_win.close()
                        F_rewards.close()
                        saver.save(sess, "../tmp/model.ckpt", global_step=battles_game)

                    # 当大于100000场时跳出循环，一般不会发生
                    if battles_game >= 100000:
                        actions = [proto.concat_cmd(proto.commands['restart'])]
                        F_loss.close()
                        F_win.close()
                        F_rewards.close()

                elif client.state.d['waiting_for_restart']:
                    if DEBUG > 0:
                        print("WAITING FOR RESTART")

                else:
                    # 选取uid进行决策，当战斗还没结束时才会执行，原因是每场战斗结束后不会立即进入下一场
                    if(len(MyState.units_e_id) != 0 and len(MyState.units_id) != 0):
                        for index in range(0, len(MyState.units_id)):
                            u_allstate, acttype_list, act_list = MyState.getU_allFeature(client.state, index)

                            # 输出u_allstate，用于测试
                            '''for i in range(0, len(act_list)):
                            	for j in range(0, len(u_allstate) / len(acttype_list)):
                                    print u_allstate[i*len(u_allstate) / len(acttype_list)+j]
                            	print'''

                            # 使用神经网络生成动作概率分布
                            tfprob = sess.run(probability, feed_dict={state_action: u_allstate, command_size: len(acttype_list),
                                                                  unit_size: len(u_allstate) / len(acttype_list),
                                                                  c_acttype: acttype_list})

                            # 记录state
                            state_action_r.append(u_allstate)
                            # 记录act_type
                            c_acttype_r.append(acttype_list)
                            # 根据概率选择动作
                            chosen_action, chosen_aid = choose_action(tfprob, act_list)
                            # 设置unit的next_cmd
                            MyState.setU_command(index, chosen_action)
                            # 记录中间reward
                            rewards.append(0.0)
                            # 记录实际被选择动作 label
                            input = np.zeros_like(tfprob)
                            input[chosen_aid] = 1.0
                            input_y_r.append(input)
                            # 记录单位数，用于计算累积reward
                            num_units.append(len(MyState.units_id) + len(MyState.units_e_id))

                        # 得到符合TorchCraft格式的命令
                        actions = MyState.getActions()

                if DEBUG > 0:
                    print "Sending actions: " + str(actions)

                # 将决策出的命令发送到服务器端
                client.send(actions)

            client.close()
