import socket
import threading
import sys
import time
import random

class Server:
    connections = []
    peers = []
    
    def __init__(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # tcp, ipv4
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) #não dar problema de reuso da porta
        sock.bind(('0.0.0.0', 10000))   #criar o bind, p/ todos os end. da máquina local
        sock.listen(1)
        print("Servidor...")

        while True:
            c, a = sock.accept()
            cThread = threading.Thread(target=self.handler, args=(c, a))
            cThread.daemon = True # é interrompido, caso a main termine de executar
            cThread.start()
            self.connections.append(c)
            self.peers.append(a[0])
            print(str(a[0]) + ':' + str(a[1]), " conectado.")
            self.send_peers()

    def handler(self, c, a):
        while True:
            data = c.recv(1024)
            for connection in self.connections:
                connection.send(data)
            if not data:
                print(str(a[0]) + ':' + str(a[1]), " desconectado.")
                self.connections.remove(c)
                self.peers.remove(a[0]) #remove os peers
                c.close()
                self.send_peers()
                break

    def send_peers(self): #atualizar os peers
        p = ""
        for peer in self.peers:
            p = p + peer + ","

        for connection in self.connections:
            connection.send(b'\x11' + bytes(p, 'utf-8'))

class Client:
    def send_msg(self, sock):
        while True:
            sock.send(bytes(input(""), 'utf-8'))
    
    def __init__(self, address):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.connect((address, 10000)) ## conectar no servidor

        iThread = threading.Thread(target=self.send_msg, args=(sock,))
        iThread.daemon = True
        iThread.start()
        
        while True:
            data = sock.recv(1024)
            if not data:
                break
            if data[0:1] == b'\x11': #atualiza os peers, depois de remover
                self.update_peers(data[1:])
            else:
                print(str(data, 'utf-8'))

    def update_peers(self, peer_data):
        p2p.peers = str(peer_data, 'utf-8').split(",")[:-1]

class p2p:
    peers = ['127.0.0.1']

while True:
    try:
        print("Tentando conectar...")
        time.sleep(random.randint(1, 5))
        for peer in p2p.peers:
            try:
                client = Client(peer)
            except KeyboardInterrupt:
                sys.exit(0)
            except:
                try:
                    server = Server()
                except KeyboardInterrupt:   
                    sys.exit(0)
                except:
                    print("Erro ao conectar ao servidor...")
            
    except KeyboardInterrupt:
        sys.exit(0)