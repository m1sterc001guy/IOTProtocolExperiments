import subprocess
import threading
import sys

running = True
second = 1

def outputMemory():
  global running, f, second
  memoryused = 0
  freeout = subprocess.check_output(["free"])
  lines = freeout.split('\n')
  tokens = lines[1].split()
  used = float(tokens[2])
  total = float(tokens[1])
  totalutilization = used / total

  if running:
    outputString = str(second) + " " + str(totalutilization) + "\n"
    f.write(outputString)
    second += 1
    threading.Timer(1, outputMemory).start()
  

def closeFile():
  print 'Stop computing memory utilization'
  global running, f
  running = False
  f.close()


if __name__ == "__main__":
  global f 
  pubOrSub = ''
  if sys.argv[2] == 'true':
    pubOrSub = 'sub'
  else:
    pubOrSub = 'pub'
  f = open('data/' + sys.argv[1] + '_memory_' + pubOrSub + "_" + sys.argv[4] + "_" + sys.argv[5] + "_normal", 'w')
  outputMemory()
  totalTime = int(sys.argv[3])
  threading.Timer(totalTime, closeFile).start() 
