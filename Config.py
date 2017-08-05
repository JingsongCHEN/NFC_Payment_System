import ConfigParser

cf = ConfigParser.ConfigParser()
cf.read("config.dat")
class Config(object):

    unit_size = cf.getint('model', 'unit_size')
    feature_size = cf.getint('model', 'feature_size')
    hidden1_units = cf.getint('model', 'hidden1_units')
    hidden2_units = cf.getint('model', 'hidden2_units')
    hidden3_units = cf.getint('model', 'hidden3_units')
    hidden4_units = cf.getint('model', 'hidden4_units')
    unit_myself_size = cf.getint('model', 'unit_myself_size')

if __name__ == '__main__':
    config = Config()
    print config.hidden1_units
