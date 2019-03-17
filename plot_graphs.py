"""
Lab 4 Graph Plotter - ECE3700 Winter 2019

Plots a graph of number of packets received by receiver B over the number of
packets sent by A.

This is intended to be a tool to visualize the total throughput for different
amounts of network loss and corruption. It also is intended to visualize how
the throughput is affected by the window size for the Go Back N (GBN) protocol.

This script only accepts a very strict input file format. It only takes as
input the System.out.println() output from running the Java project for Lab 4.
"""

import matplotlib
matplotlib.use('QT4Agg')
import matplotlib.pyplot as plt
from pathlib import Path

# Set these based on the file to process
filename = 'Run_Output/80Pkts_L0.1_C0_W8.txt'
graph_title = '80 Packets, 0.1 Loss, 0 Corruption, Window Size 8'


filelines = []
with open(Path(filename)) as f:
    filelines = f.readlines()

packets_sent_A = []
packets_received_B = []
curr_num_packets_sent_A = 0
curr_num_packets_received_B = 0
last_event_was_A = False

for line in filelines:
    if "(A): Sending a message with" in line:
        if last_event_was_A:
            packets_sent_A.append(curr_num_packets_sent_A)
            packets_received_B.append(curr_num_packets_received_B)
        curr_num_packets_sent_A += 1
        last_event_was_A = True

    if "(B): Received the correct packet" in line:
        curr_num_packets_received_B += 1
        last_event_was_A = False
        packets_sent_A.append(curr_num_packets_sent_A)
        packets_received_B.append(curr_num_packets_received_B)

x_ticks = list(range(0, curr_num_packets_sent_A, 10))
x_ticks.append(curr_num_packets_sent_A)

plt.plot(packets_sent_A, packets_received_B)
plt.title(graph_title)
plt.axvline(curr_num_packets_sent_A, color="black")
plt.xticks(x_ticks)
plt.xlabel("Number of packets sent by A")
plt.ylabel("Number of correct packets received by B")
plt.show()
