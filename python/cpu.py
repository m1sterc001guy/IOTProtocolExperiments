import threading
import time
import threading
import sys

i = 0
prevStats = {}
running = True

utilizationArray = []
timeArray = []

def getCpuStats():
  stats = {}
  with open("/proc/stat") as f:
    content = f.readlines()
    for i in xrange(0, 5):
      coreStats = {}
      tokens = content[i].split()
      key = tokens[0]
      idle = int(tokens[4]) + int(tokens[5])
      nonidle = int(tokens[1]) + int(tokens[2]) + int(tokens[3]) + int(tokens[6]) + int(tokens[7]) + int(tokens[8])
      coreStats["idle"] = idle
      coreStats["nonidle"] = nonidle
      stats[key] = coreStats

  return stats

def computeCpuUtilization(prevStats, currStats):
  prevtotal = prevStats["cpu"]["idle"] + prevStats["cpu"]["nonidle"]
  currtotal = currStats["cpu"]["idle"] + currStats["cpu"]["nonidle"]

  totalDiff = currtotal - prevtotal

  idleDiff = currStats["cpu"]["idle"] - prevStats["cpu"]["idle"]
  nonidleDiff = currStats["cpu"]["nonidle"] - prevStats["cpu"]["nonidle"]

  cpuUtilization = float(nonidleDiff) / float(totalDiff)
  return cpuUtilization

def outputCpuUtilization():
  global prevStats, i, running, f
  if i == 0:
    prevStats = getCpuStats()
  else:
    currStats = getCpuStats()
    util = computeCpuUtilization(prevStats, currStats)
    prevStats = currStats
    if running:
      timeArray.append(i)
      utilizationArray.append(util)
      outputString = str(i) + " " + str(util) + "\n"
      f.write(outputString)

  i += 1
  if running:
    threading.Timer(1, outputCpuUtilization).start()

def stopRunning():
  print 'Stop recording CPU'
  global running, f
  f.close()
  running = False

if __name__ == "__main__":
  global f
  pubOrSub = ''
  if sys.argv[2] == 'false':
    pubOrSub = 'pub'
  else:
    pubOrSub = 'sub'
  f = open('data/' + sys.argv[1] + "_cpu_" + pubOrSub, 'w')
  outputCpuUtilization() 
  totalTime = int(sys.argv[3])
  threading.Timer(totalTime, stopRunning).start()
