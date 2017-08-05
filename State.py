# -*-coding:utf-8 -*-

import torchcraft_py.proto as proto
import random
import command
import torchcraft_py.frame as frame
import torchcraft_py.utils as utils
import numpy as np

class unit_n:
    def __init__(self, uc, flag):  # flag myself or enemy
        self.uid = uc.id
        self.x = uc.x
        self.y = uc.y
        #self.pos = self.set_pos(self.x, self.y)
        self.enemy = flag
        self.type = uc.type
        self.hp = uc.health
        self.shield = uc.shield
        self.cd = uc.groundCD
        self.cur_cmd = command.Command()
        self.next_cmd = command.Command()
        self.order = uc.orders[0]
        self.set_cur_cmd()
        self.feature = []
        self.death = 0


    def __str__(self):
        s = utils.to_str(self.uid, " ", self.x, " ", self.y, " ",
                         self.enemy, " ",
                         self.type, " ", self.hp, " ",
                         self.shield, " ", self.cd, " ",
                         self.cur_cmd.target_pos_x, " ", self.cur_cmd.target_pos_y, " ",
                         self.cur_cmd.command_type, " ",self.cur_cmd.act_type," ",
                         self.order.targetX, " ", self.order.targetY, " ", self.order.type, " ",
                         self.next_cmd.target_pos_x, " ", self.next_cmd.target_pos_y, " ",
                         self.next_cmd.command_type, " ", self.next_cmd.act_type, " ",self.death
                         )
        return s


    def dead(self):
        self.death = 1
        #self.pos = -1
        self.hp = 0
        self.shield = 0
        self.cd = -1
        self.order = frame.Order()
        self.cur_cmd = command.Command()
        self.next_cmd = command.Command()

    def set_cur_cmd(self):
        if self.order.type == proto.orders['AttackUnit'] and self.order.targetId != -1:
            self.cur_cmd.act_type = 1
            self.cur_cmd.targetID = self.order.targetId
        else:
            self.cur_cmd.act_type = 2
            self.cur_cmd.targetID = -1

        # self.cur_cmd.act_type = self.order.type
        self.cur_cmd.target_pos_x = self.order.targetX
        self.cur_cmd.target_pos_y = self.order.targetY

        # self.cur_cmd.target_pos = self.set_pos(self.order.targetX, self.order.targetY)

    ''''@staticmethod
    def set_cd(x, y, z):
        #CD_temp = (x + y + 1) * (x + y) + x + 1
        #cd = (z + CD_temp + 1) * (z + CD_temp) / 2 + z + 1
        return y'''

    @staticmethod
    def distance(x1, y1, x2, y2):
        distance = np.sqrt(((x1-x2)*(x1-x2)/400.0) + ((y1-y2)*(y1-y2)/400.0))
        return distance

    def setFeature(self, un, c):
        self.feature = [self.enemy, self.type, self.hp/20.0, self.shield/20.0, self.cd/10.0, self.cur_cmd.act_type,
                         self.next_cmd.act_type, un.type]
        #self.feature = [self.uid, self.enemy, self.cur_cmd.target_pos_x, self.cur_cmd.target_pos_y, self.x, self.y, self.cur_cmd.act_type,
        #                self.cur_cmd.targetID]

        # upos_un_pos = abs(self.pos - un.pos)
        upos_un_pos = self.distance(self.x, self.y, un.x, un.y)

        # upos_un_cur_cpos = abs(self.pos - un.cur_cmd.target_pos)
        upos_un_cur_cpos = self.distance(self.x, self.y, un.cur_cmd.target_pos_x, un.cur_cmd.target_pos_y)

        # upos_un_c_pos = abs(self.pos - c.target_pos)
        upos_un_c_pos = self.distance(self.x, self.y, c.target_pos_x, c.target_pos_y)

        # u_cur_cpos_un_pos = abs(self.cur_cmd.target_pos - un.pos)
        u_cur_cpos_un_pos = self.distance(self.cur_cmd.target_pos_x, self.cur_cmd.target_pos_y, un.x, un.y)

        # u_cur_cpos_un_cur_cpos = abs(self.cur_cmd.target_pos - un.cur_cmd.target_pos)
        u_cur_cpos_un_cur_cpos = self.distance(self.cur_cmd.target_pos_x, self.cur_cmd.target_pos_y, un.cur_cmd.target_pos_x, un.cur_cmd.target_pos_y)

        # u_cur_cpos_un_c_pos = abs(self.cur_cmd.target_pos - c.target_pos)
        u_cur_cpos_un_c_pos = self.distance(self.cur_cmd.target_pos_x, self.cur_cmd.target_pos_y, c.target_pos_x, c.target_pos_y)

        # u_next_cpos_un_pos = abs(self.next_cmd.target_pos - un.pos)
        u_next_cpos_un_pos = self.distance(self.next_cmd.target_pos_x, self.next_cmd.target_pos_y, un.x, un.y)

        # u_next_cpos_un_cur_pos = abs(self.next_cmd.target_pos - un.cur_cmd.target_pos)
        u_next_cpos_un_cur_pos = self.distance(self.next_cmd.target_pos_x, self.next_cmd.target_pos_y, un.cur_cmd.target_pos_x, un.cur_cmd.target_pos_y)

        # u_next_cpos_un_c_pos = abs(self.next_cmd.target_pos - c.target_pos)
        u_next_cpos_un_c_pos = self.distance(self.next_cmd.target_pos_x, self.next_cmd.target_pos_y, c.target_pos_x, c.target_pos_y)

        self.feature.append(upos_un_pos)
        self.feature.append(upos_un_cur_cpos)
        self.feature.append(upos_un_c_pos)
        self.feature.append(u_cur_cpos_un_pos)
        self.feature.append(u_cur_cpos_un_cur_cpos)
        self.feature.append(u_cur_cpos_un_c_pos)
        self.feature.append(u_next_cpos_un_pos)
        self.feature.append(u_next_cpos_un_cur_pos)
        self.feature.append(u_next_cpos_un_c_pos)


class State:
    def __init__(self):
        self.size_m = 0
        self.size_e = 0
        self.units = {}  # dict
        self.features = {}
        self.units_id = []
        self.units_e_id = []
        self.units_dead_id = []

    @staticmethod
    def upset_unit(ServerState):
        #battle_size = len(ServerState.d['units_myself']) + len(ServerState.d['units_enemy'])
        units_id = []
        for uid, ut in ServerState.d['units_myself'].iteritems():
            units_id.append(uid)
        random.shuffle(units_id)  # upset
        return units_id

    def update(self, ServerState):  # update all before new round

        self.size_m = len(ServerState.d['units_myself'])
        self.size_e = len(ServerState.d['units_enemy'])

        if len(self.units) == 0:
            for uid, ut in ServerState.d['units_myself'].iteritems():
                u = unit_n(ut, 0)
                self.units[u.uid] = u
                self.units_id.append(u.uid)
            for uid, ut in ServerState.d['units_enemy'].iteritems():
                u = unit_n(ut, 1)
                self.units[u.uid] = u
                self.units_e_id.append(u.uid)
        else:
            self.units_id = self.upset_unit(ServerState)
            self.units_e_id = []
            for uid, ut in ServerState.d['units_enemy'].iteritems():
                self.units_e_id.append(uid)
            self.units_dead_id = []

        for uid, ut in self.units.iteritems():
            if ut.enemy == 0:
                if uid in ServerState.d['units_myself']:  # alive
                    self.units[uid].order = ServerState.d['units_myself'][uid].orders[0]
                    self.units[uid].x = ServerState.d['units_myself'][uid].x
                    self.units[uid].y = ServerState.d['units_myself'][uid].y
                    self.units[uid].hp = ServerState.d['units_myself'][uid].health
                    self.units[uid].shield = ServerState.d['units_myself'][uid].shield
                    self.units[uid].cd = ServerState.d['units_myself'][uid].groundCD
                    self.units[uid].next_cmd = command.Command()
                    self.units[uid].next_cmd.target_pos_x = self.units[uid].x
                    self.units[uid].next_cmd.target_pos_y = self.units[uid].y
                else:
                    self.units[uid].dead()
                    self.units_dead_id.append(uid)

            else:
                if uid in ServerState.d['units_enemy']:  # alive
                    self.units[uid].order = ServerState.d['units_enemy'][uid].orders[0]
                    self.units[uid].x = ServerState.d['units_enemy'][uid].x
                    self.units[uid].y = ServerState.d['units_enemy'][uid].y
                    self.units[uid].hp = ServerState.d['units_enemy'][uid].health
                    self.units[uid].shield = ServerState.d['units_enemy'][uid].shield
                    self.units[uid].cd = ServerState.d['units_enemy'][uid].groundCD
                    self.units[uid].next_cmd = command.Command()
                    self.units[uid].next_cmd.target_pos_x = self.units[uid].x
                    self.units[uid].next_cmd.target_pos_y = self.units[uid].y
                else:
                    self.units[uid].dead()
                    self.units_dead_id.append(uid)
            if self.units[uid].death == 0:
                #self.units[uid].pos = unit_n.set_pos(self.units[uid].x, self.units[uid].y)
                self.units[uid].set_cur_cmd()  # order to command
                if (self.units[uid].cur_cmd.act_type == 1):
                    self.units[uid].cur_cmd.target_pos_x = self.units[self.units[uid].cur_cmd.targetID].x
                    self.units[uid].cur_cmd.target_pos_y = self.units[self.units[uid].cur_cmd.targetID].y


    def getU_allFeature(self, ServerState, index):
        commandset = self.itera_command(ServerState, self.units_id[index])
        #!!!!!!!!!!!!!!!!!!!!!!!!u_allstate type {} => []
        u_allstate = []
        acttype_list = []
        act_list  = []
        for c in commandset:
            self.updateFeatures(self.units_id[index],c)
            for uid, feature in self.features.iteritems():
                u_allstate.append(feature)
            act_list.append(c)
            if c.act_type == 1:
                acttype_list.append((0, 1))
            elif c.act_type == 2:
                acttype_list.append((1, 0))
        return u_allstate, acttype_list, act_list

    @staticmethod
    def itera_command(ServerState, Uid):
        # command_size = len(ServerState.d['units_enemy']) + 9
        commandset = []
        for id, act in command.command_types.iteritems():
            #print act
            if act == command.command_types['Attack']:
                for uid, ut in ServerState.d['units_enemy'].iteritems():
                    cmd = command.Command()
                    cmd.command_type = act
                    cmd.act_type = 1
                    cmd.targetID = uid
                    cmd.setTargetPos(ut.x, ut.y)
                    commandset.append(cmd)
            elif act != command.command_types['NULL']:
                cmd = command.Command()
                cmd.command_type = act
                cmd.act_type = 2
                #print str(cmd.command_type)
                cmd.setTargetPos(ServerState.d['units_myself'][Uid].x, ServerState.d['units_myself'][Uid].y)
                commandset.append(cmd)
        return commandset

    def setU_command(self, index, cmd):  # index + 1 action is determined
        self.units[self.units_id[index]].next_cmd = cmd

    def updateFeatures(self,un_id,cmd):
        # iterate get all features
        self.features = {}
        #c = command.Command()
        for uid, ut in self.units.iteritems():
            if (self.units[uid].death == 0):
                self.units[uid].setFeature(self.units[un_id], cmd)
                self.features[uid] = self.units[uid].feature[:]

        '''for k in range(0, len(self.units_dead_id)):
            self.units[self.units_dead_id[k]].setDeadFeature()
            self.features[self.units_dead_id[k]] = self.units[self.units_dead_id[k]].feature[:]'''


    def getActions(self):
        actions = []
        for uid in self.units_id:
            actions.append(self.units[uid].next_cmd.getCommand(uid))
        return actions

    def Print(self):
        for uid, ut in self.units.iteritems():
            print ut

        print "size_m size_e"
        s = utils.to_str(self.size_m, " ", self.size_e)
        s += "\n"

        for uid, F in self.features.iteritems():
            s += utils.to_str(uid, " ")
            for f in F:
                s += utils.to_str(f, " ")
            s += "\n"

        for uid in self.units_id:
            s += utils.to_str(uid, " ")
        s += "\n"

        for uid in self.units_e_id:
            s += utils.to_str(uid, " ")
        s += "\n"

        for uid in self.units_dead_id:
            s += utils.to_str(uid, " ")
        s += "\n"

        print s








