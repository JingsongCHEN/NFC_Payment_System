# -*-coding:utf-8 -*-

import argparse
import numpy as np

import torchcraft_py.proto as proto
import torchcraft_py.torchcraft as tc
import torchcraft_py.utils as utils
import State

DEBUG = 0

total_battles = 0
nrestarts = -1

parser = argparse.ArgumentParser()
parser.add_argument('--ip', help='server ip', default='10.212.47.158')
parser.add_argument('--port', help='server port', default='11111')
args = parser.parse_args()
print args

while total_battles < 40:
    nloop = 1
    battles_won = 0
    battles_game = 0

    print ""
    print "GAME STARTED"

    # Create a client and connect to the TorchCraft server
    client = tc.Client(args.ip, args.port)
    init = client.connect()
    if DEBUG > 0:
        print "Received init: " + init

    # Setup the game
    setup = [proto.concat_cmd(proto.commands['set_speed'], 60),
             proto.concat_cmd(proto.commands['set_gui'], 1),
             proto.concat_cmd(proto.commands['set_frameskip'], 9),
             proto.concat_cmd(proto.commands['set_cmd_optim'], 1)]
    if DEBUG > 0:
        print "Setting up the game: " + ':'.join(setup)
    client.send(setup)
    utils.progress(nloop, battles_won, battles_game, total_battles)

    # 接收服务器端的游戏数据
    update = client.receive()

    # 使用当前的游戏数据创建State对象
    MyState = State.State()

    while True:
        # Print the progress
        if np.mod(nloop, 50) == 0:
            utils.progress(nloop, battles_won, battles_game, total_battles)

        # 接收服务器端的游戏数据
        update = client.receive()
        MyState.update(client.state)



        if DEBUG > 0:
            print "Received state: " + update

        nloop += 1
        actions = []

        if bool(client.state.d['game_ended']):
            if DEBUG > 0:
                print "GAME ENDED"
            break

        elif client.state.d['battle_just_ended']:
            if DEBUG > 0:
                print "BATTLE ENDED"
            if bool(client.state.d['battle_won']):
                battles_won += 1
            battles_game += 1
            total_battles += 1

            # 当一场对战结束后，要重新创建一个State对象
            MyState = State.State()
            MyState.Print()
            if battles_game >= 10:
                actions = [proto.concat_cmd(proto.commands['restart'])]

        elif client.state.d['waiting_for_restart']:
            if DEBUG > 0:
                print("WAITING FOR RESTART")

        else:
            MyState.Print()
            # 此处为测试，得到一个Unit的所有可能命令对应的Feature矩阵
            if len(MyState.units_id):
                _, _, act_list = MyState.getU_allFeature(client.state, 0)
                print act_list
            MyState.Print()

            ''' 可以在此处进行训练和决策'''

        if DEBUG > 0:
            print "Sending actions: " + str(actions)

        print "*" * 100

        # 将决策出的命令发送到服务器端
        client.send(actions)

    client.close()