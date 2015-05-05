#!/usr/bin/python3.4
#1. arguments
#3. spline
from optparse import OptionParser
import os
import sys
import time
from mininet.cli import CLI
from mininet.link import TCLink
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import irange,dumpNodeConnections
from mininet.log import setLogLevel
#from mininet.node import CPULimitedHost
from mininet.node import OVSSwitch, Controller, RemoteController
#global variable here
#######################################
coreList = [ ]
aggList = [ ]
torList = [ ]
spineList = [ ]
leafList = [ ]
allList = [ ]
class dcFatTreeTopo( Topo ):
	"Linear topology of k switches, with one host per switch."
	def __init__(self, k=2, **opts): 
		Topo.__init__(self, **opts)
		self.k = k
		#link1 = dict(bw=10, delay='1ms', loss=0, max_queue_size=1000, use_htb=True)
		#link2 = dict(bw=5, delay='10ms', loss=0, max_queue_size=500, use_htb=True)
		#link3 = dict(bw=1, delay='15ms', loss=0, max_queue_size=100, use_htb=True)
		# Creating an array of core switches
		# Adding them in an array so that they can be refered to later
		#for i in irange(0, k-1):
		#	"core switch"
		#	coreSwitch = self.addSwitch('c%s%s' % (i+1, 0))
		#	#coreList.insert(i, coreSwitch)
		#	coreList.append(coreSwitch)
		#	#lastSwitch = None
		#print "entering aggregation switches"
		for i in irange(1, k):
		#	"aggregation switches"
		#	aggSwitch1 = self.addSwitch('a%s%s' % (i, 1))
		#	aggSwitch2 = self.addSwitch('a%s%s' % (i, 3))
		#	aggList.append(aggSwitch1)
		#	aggList.append(aggSwitch2)
			torSwitch1 = self.addSwitch('t%s%s' % (i, 2))
			torSwitch2 = self.addSwitch('t%s%s' % (i, 4))
			torList.append(torSwitch1)
			torList.append(torSwitch2)
		#	"host = self.addHost('h%s%s' % (i, i+1))"
			host11 = self.addHost('h%s%s' % (i, 1))
			host12 = self.addHost('h%s%s' % (i, 2))
			host13 = self.addHost('h%s%s' % (i, 3))
			host14 = self.addHost('h%s%s' % (i, 4))
			#hosts1 = [ net.addHost( 'h%d' % n ) for n in 3, 4 ]
			"connection of the hosts to the left tor switch "
			#self.addLink(host11, torSwitch1, **link3)
			self.addLink(host11, torSwitch1, cls=TCLink, bw=1, delay='15ms', loss=0, max_queue_size=100, use_htb=True)
			#self.addLink(host12, torSwitch1, **link3)
			self.addLink(host12, torSwitch1, cls=TCLink, bw=1, delay='15ms', loss=0, max_queue_size=100, use_htb=True)
			"connection of the hosts to the right tor switch "
			self.addLink(host13, torSwitch2, cls=TCLink, bw=1, delay='15ms', loss=0, max_queue_size=100, use_htb=True)
			#self.addLink(host14, torSwitch2, **link3)
			self.addLink(host14, torSwitch2, cls=TCLink, bw=1, delay='15ms', loss=0, max_queue_size=100, use_htb=True)
			self.addLink(torSwitch1, torSwitch2,cls=TCLink, bw=5, delay='10ms', loss=0, max_queue_size=500,use_htb=True) 
			#"Connection of the the left tor switch to aggregation switches"
			#self.addLink(torSwitch1, aggSwitch1, **link2)
			#self.addLink(torSwitch1, aggSwitch2, **link2)
			#"connection of the the right tor switch to aggregation switches"
			#self.addLink(torSwitch2, aggSwitch1, **link2)
			#self.addLink(torSwitch2, aggSwitch2, **link2)
	#	"connect the aggregation switch to top pod core switch"
	#	if k == 1:
	#		for r in irange(0, k): #this is to go through the agg switches
	#			self.addLink(aggList[r], coreList[0])
	#	else:
	#		for r in irange(0, (k*2)-1): #this is to go through the agg switches
	#			if r % 2 == 0: #if agg switch is even then connect to first half
	#				for j in irange(0, ((k/2)-1)): #this is to go through the core switches
	#					self.addLink(aggList[r], coreList[j], **link1)
	#			else:
	#				for j in irange((k/2), k-1): #this is to go through the core switches
	#					self.addLink(aggList[r], coreList[j], **link1)
	#	allList.extend(coreList)
	#	allList.extend(aggList)
	#	allList.extend(torList)
def evenSimpleTest():
	for sw in allList:
		print allList[sw]
def simpleTest():
	# argument to put in either remote or local controller
	"Create and test a simple network"
	#c0 = RemoteController( 'c0', ip='192.168.90.146' )
	c0 = RemoteController( 'c0', ip='192.168.56.1' )
# the cmap here needs to dynamically take the switch name from the switchLists[] so that it is not static
#cmap = { 'a11': c0, 'a12': c0, 'a21': c0, 'a22': c0, 'a31': c0, 'a32': c0, 'a41': c0, 'a42': c0, 'c11': c0, 'c21': c0, 'c31': c0, 'c41': c0, 't11': c0, 't12': c0, 't21': c0, 't22': c0, 't31': c0, 't32': c0, 't41': c0, 't42': c0}


	class MultiSwitch( OVSSwitch ):
		"Custom Switch() subclass that connects to different controllers"
	def start( self, controllers ):
		return OVSSwitch.start( self, [ cmap[ self.name ] ] )
	#section for handling the differnt argumetns.... simpleTest(arg1, arg2, ...) will take in arguments from user
	topo = dcFatTreeTopo(k=2)
	net = Mininet( topo=topo, switch=MultiSwitch, build=False, link=TCLink )
	cString = "{"
	for i in irange(0, len(allList)-1):
		if i != len(allList)-1:
			tempCString = "'" + allList[i] + "'" + " : c0, "
		else:
			tempCString = "'" + allList[i] + "'" + " : c0 "
		cString += tempCString
	cmapString = cString + "}"
#print "wowzer" + cmapString
	cmap = cmapString
	net.addController(c0)
	net.build()
	net.start()
	print "Dumping host connections"
	dumpNodeConnections(net.hosts)
	print "Testing network connectivity"
#def perfTest():
		# if user test argument is active then pick the correct test
	net.pingAll()
	net.pingAll()
	print "Testing bandwidth between h11 and h12..............."
	#h11, h12 = net.get('h11', 'h12')
	#net.iperf((h11, h12)
	#print "Testing bandwidth between h11 and h14..............."
	h11, h14 = net.get('h11', 'h14')
	net.iperf((h11, h14))
	#print "Testing bandwidth between h11 and h16..............."
	h11, h22 = net.get('h11', 'h22')
	net.iperf((h11, h22))
	#print "Testing bandwidth between h11 and h18..............."
	h11, h24 = net.get('h11', 'h24')
	net.iperf((h11, h24))
	# also argument for generating traffic
	# arugment for stat analysis
	CLI( net )
	net.stop()
if __name__ == '__main__':
# get arguments here to make the code configurable
# pass in the arguments into simpleTest() so that they can be processed in SimpleTest
	import sys
	print "calling main"
#print (sys.argv[1:])
# Tell mininet to print useful information
	setLogLevel('info')
	simpleTest()
	#myname()
	#evenSimpleTest()


topos = {"kali_fattree":(lambda:dcFatTreeTopo() ) }
