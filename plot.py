import numpy as np
import package.command as command
import matplotlib.pyplot as plt

x = []
y = []
i = 0
flag = 0
old = 0
fopen = open("../record/tmp/win", 'r')
for eachLine in fopen:
    eachLine = float(eachLine)
    if i == 0:
        if(eachLine % 50 == 0):
            x.append(eachLine)
            flag = 1
        i = 1
    else:
        if(flag == 1):
            y.append((eachLine - old)/50.0)
            old = eachLine
            flag = 0
        i = 0
fopen.close()

x = np.array(x)
y = np.array(y)

plt.plot(x, y, linestyle='-',color='green')

plt.ylim(0, 1)

plt.show()

x = []
y = []
i = 0
flag = 0
sum = 0
fopen = open("../record/tmp/loss", 'r')
for eachLine in fopen:
    eachLine = float(eachLine)
    if i == 0:
        if (eachLine % 50 == 0):
            x.append(eachLine)
            flag = 1
        i = 1
    else:
        sum += eachLine
        if (flag == 1):
            y.append(sum / 50.0)
            sum = 0
            flag = 0
        i = 0
fopen.close()

x = np.array(x)
y = np.array(y)

plt.plot(x, y, linestyle='-',color='green')

plt.ylim(-35000, 1000)

plt.show()

x = []
y = []
i = 0
flag = 0
sum = 0
fopen = open("../record/tmp/rewards", 'r')
for eachLine in fopen:
    eachLine = float(eachLine)
    if i == 0:
        if (eachLine % 50 == 0):
            x.append(eachLine)
            flag = 1
        i = 1
    else:
        sum += eachLine
        if (flag == 1):
            y.append(sum / 50.0)
            sum = 0
            flag = 0
        i = 0
fopen.close()

x = np.array(x)
y = np.array(y)

plt.plot(x, y, linestyle='-',color='green')

plt.ylim(-1, 1)

plt.show()

