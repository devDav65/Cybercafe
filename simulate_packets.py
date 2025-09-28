# simulate_packets.py
import json
import random
import time
from datetime import datetime
# from scapy.all import IP, TCP  # si tu veux générer vrais paquets pcap (optionnel)

IPS = ["192.168.1.10", "10.0.0.5", "172.16.0.2", "8.8.8.8"]
DESTS = ["192.168.1.1", "10.0.0.1", "172.16.0.1"]
PROTOS = ["TCP", "UDP", "ICMP"]

FOUT = "simulated_packets.jsonl"  # fichier que Java lit

def gen_packet():
    pkt = {
        "ipsource": random.choice(IPS),
        "ipdestination": random.choice(DESTS),
        "protocole": random.choice(PROTOS),
        "length": random.randint(40, 1500),
        "payload": "hello-" + str(random.randint(0,9999)),
        "timestamp": datetime.now().isoformat()
    }
    return pkt

def main():
    with open(FOUT, "a") as f:
        try:
            while True:
                p = gen_packet()
                f.write(json.dumps(p) + "\n")
                f.flush()
                print("WROTE", p)
                time.sleep(1.0)  # 1 seconde entre paquets
        except KeyboardInterrupt:
            print("Stopped")

if __name__ == "__main__":
    main()
