from twisted.internet.protocol import Factory
from twisted.internet.protocol import ClientFactory

from twisted.protocols.basic import LineReceiver
from twisted.internet import reactor
import random
import urllib2
import json
import time
import logging


class ProxyClient(ClientFactory):
    def __init__(self, msg):
        self.msg = msg
    def buildProtocol(self, addr):
        return ProxyChat(self.msg)

class ProxyChat(LineReceiver):

    def __init__(self, msg):
        self.msg = msg
    def connectionMade(self):
        self.sendLine(self.msg)



class Chat(LineReceiver):

    def __init__(self, users, server_name):
        self.users = users
        self.name = None
        self.location = None
        self.ID = None
        self.time = None
        self.delta = None
        self.server = server_name


    def connectionMade(self):
        self.name = str(random.randint(0,1000))
        self.users[self.name] = self

    def connectionLost(self, reason):
        if self.name in self.users:
            del self.users[self.name]

    def lineReceived(self, line):
        logging.debug( self.server +" Recieved => "+ line)
        operands = line.split()
        correct = False
        if len(operands) == 4 :
            if operands[0] == "IAMAT":
                correct = True
                self.ID = operands[1]
                temp  = operands[2][1:]
                ind = temp.find('+')
                if ind ==-1 :
                    ind = temp.find('-')
                lati = operands[2][0:ind]
                longi = operands[2][ind+1:]
                try:
                    self.location = Location(float(longi) , float(lati))
                    self.time = float(operands[3])
                    self.delta = time.time()  - self.time
                    self.respond_IAMAT()
                except ValueError:
                    correct = False
            elif operands[0] == "WHATSAT":
                correct = True
                other_ID = operands[1]
                try:
                    radius  = float(operands[2])
                    results =  float(operands[3])
                    self.respond_WHATSAT(other_ID,radius,results)
                except ValueError:
                    correct = False
        elif (len(operands) == 7 and operands[0] == 'AT'):
            correct = True
                # AT Alford +0.563873386 kiwi.cs.ucla.edu +34.068930 -118.445127 1400794699.108893381
        
           # temp = Chat(self.users,self.server)
            to_send = True
            for name, user in self.users.iteritems():
                if user.ID == operands[3] and user.time == float(operands[6]):
                    del self.users[self.name]
                    to_send = False
                    break

            self.ID = operands[3]
            self.location = Location(float(operands[5]) , float(operands[4]))
            self.time = float(operands[6])
            self.delta = float(operands[2])  
            if to_send:
                for server_fr in server_graph[self.server]:
                    if server_fr != operands[1]:
                        logging.debug(self.server + " boradcasting location to " +server_fr) 
                        reactor.connectTCP("localhost", port_mapping[server_fr], ProxyClient(line))
            self.server = operands[1]

        if not correct:
            logging.debug("Invalid request from client")
            self.sendLine("? " + line)
           
    def respond_WHATSAT(self, ID , radius, results):
        user_found  =  False
        for name, user in self.users.iteritems():
            if user.ID == ID:
                user_found = True
                urlstr = "https://maps.googleapis.com/maps/api/place/search/json?location=" + str(user.location.lat) + "," + str(user.location.lng) +"&radius="+str(radius)+"&key=AIzaSyDx6SVAfpwkG8o6jyvcstJboqUGamAPIbk"
                a = urllib2.urlopen(urlstr).read()
                js = json.loads(a)
                js["results"] = js["results"][:int(results)]
                a = json.dumps(js)
                resp = "AT %s %.8f %s %.6f%.6f %.8f \n%s" % ( user.server, user.delta , user.ID, user.location.lat, user.location.lng, user.time,a) 
                self.sendLine(resp)
                break

    def respond_IAMAT(self):
        resp = "AT %s %.8f %s %.6f%.6f %.8f" % ( self.server, self.delta, self.ID ,self.location.lat, self.location.lng, self.time) 
        self.sendLine(resp)
        resp = "AT %s %.8f %s %.6f %.6f %.8f" % ( self.server, self.delta,self.ID ,self.location.lat, self.location.lng, self.time) 
        for server_fr in server_graph[self.server]:
            if server_fr != self.server:
                reactor.connectTCP("localhost", port_mapping[server_fr], ProxyClient(resp))
            

class Location:
    def __init__(self, longi, lati):
        self.lng = longi  
        self.lat = lati


class ChatFactory(Factory):
    def __init__(self, name):
        self.name = name
        self.users = {} # maps user names to Chat instances
        self.a = 0
        logging.debug(name+ ' Initiated')
    def buildProtocol(self, addr):
        logging.debug( self.name + "'s number of Users: " + str(self.a))
        self.a =self.a+1
        return Chat(self.users , self.name)





#'Alford', 'Bolden', 'Hamilton', 'Parker', 'Powell'
alfred = "Alfred"
powell = "Powell"
bolden = "Bolden"
hamilton = "Hamilton"
parker = "Parker"


port_mapping = {}
port_mapping[alfred] = 8123
port_mapping[powell] = 8124
port_mapping[hamilton] = 8125
port_mapping[bolden] = 8126
port_mapping[parker] = 8127


server_graph = {}
server_graph[alfred] = [powell , parker]
server_graph[bolden] = [parker , powell]
server_graph[hamilton] = [parker]
server_graph[parker] = [bolden, alfred, hamilton]
server_graph[powell]= [bolden,alfred]

logging.basicConfig(filename='servers.log',level=logging.DEBUG)


reactor.listenTCP(port_mapping[alfred], ChatFactory(alfred))
reactor.listenTCP(port_mapping[powell], ChatFactory(powell))
reactor.listenTCP(port_mapping[hamilton], ChatFactory(hamilton))
reactor.listenTCP(port_mapping[bolden], ChatFactory(bolden))
reactor.listenTCP(port_mapping[parker], ChatFactory(parker))

reactor.run()

