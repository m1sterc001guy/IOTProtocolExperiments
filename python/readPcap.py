import dpkt

counter = 0
amqpcounter = 0

filename = 'exampleAMQP.pcap'

for ts, pkt, in dpkt.pcap.Reader(open(filename, 'r')):

  counter += 1
  eth = dpkt.ethernet.Ethernet(pkt)
  ip = eth.data
  tcp = ip.data

  print "Timestamp: ", ts

  if tcp.dport == 5672 or tcp.sport == 5672:
    if len(tcp.data) > 0:
      amqpcounter += 1

print "Total number of packets in the pcap file: ", counter
print "Total number of amqp packets: ", amqpcounter
