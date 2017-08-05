import torchcraft_py.proto as proto

command_types = {
    'NULL': -1,
    'Attack': 0,
    'Hold_Position': 1,
    'Move_Up': 2,
    'Move_Down': 3,
    'Move_Left': 4,
    'Move_Right': 5,
    'Move_UR': 6,
    'Move_DR': 7,
    'Move_DL': 8,
    'Move_UL': 9
}

# act_type 0 NULL 1 Attack 2 Move

class Command:
    def __init__(self):
        #self.target_pos = -1
        self.target_pos_x = -1
        self.target_pos_y = -1
        self.command_type = command_types['NULL']
        self.act_type = 0
        self.targetID = -1

    def setTargetPos(self, x, y):
        if self.command_type == command_types['Attack']:
            self.target_pos_x = x
            self.target_pos_y = y
        elif self.command_type == command_types['Hold_Position']:
            self.target_pos_x = x
            self.target_pos_y = y
        elif self.command_type == command_types['Move_Up']:
            self.target_pos_x = x
            self.target_pos_y = y - 3
            if self.target_pos_y < 110:
                self.target_pos_y += 3
        elif self.command_type == command_types['Move_Down']:
            self.target_pos_x = x
            self.target_pos_y = y + 3
            if self.target_pos_y > 160:
                self.target_pos_y -= 3
        elif self.command_type == command_types['Move_Left']:
            self.target_pos_x = x - 3
            self.target_pos_y = y
            if self.target_pos_x < 60:
                self.target_pos_x += 3
        elif self.command_type == command_types['Move_Right']:
            self.target_pos_x = x + 3
            self.target_pos_y = y
            if self.target_pos_x > 100:
                self.target_pos_x -= 3
        elif self.command_type == command_types['Move_UR']:
            self.target_pos_x = x + 3
            self.target_pos_y = y - 3
            if self.target_pos_x > 100 or self.target_pos_y < 110:
                self.target_pos_x -= 3
                self.target_pos_y += 3
        elif self.command_type == command_types['Move_DR']:
            self.target_pos_x = x + 3
            self.target_pos_y = y + 3
            if self.target_pos_x > 100 or self.target_pos_y > 160:
                self.target_pos_x -= 3
                self.target_pos_y -= 3
        elif self.command_type == command_types['Move_DL']:
            self.target_pos_x = x - 3
            self.target_pos_y = y + 3
            if self.target_pos_x < 60 or self.target_pos_y > 160:
                self.target_pos_x += 3
                self.target_pos_y -= 3
        elif self.command_type == command_types['Move_UL']:
            self.target_pos_x = x - 3
            self.target_pos_y = y - 3
            if self.target_pos_x < 60 or self.target_pos_y < 110:
                self.target_pos_x += 3
                self.target_pos_y += 3
        #self.target_pos = (self.target_pos_x + self.target_pos_y + 1) * (self.target_pos_x + self.target_pos_y) / 2 + self.target_pos_x + 1

    def getCommand(self, uid):
        if self.act_type == 1:
            return proto.concat_cmd(
                        proto.commands['command_unit_protected'], uid,
                        proto.unit_command_types['Attack_Unit'], self.targetID)
        elif self.act_type == 2:
            return proto.concat_cmd(
                        proto.commands['command_unit_protected'], uid,
                        proto.unit_command_types['Move'], -1, self.target_pos_x, self.target_pos_y)



