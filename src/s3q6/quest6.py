#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  
#  

#  
from stellar_sdk import Keypair,Server,Network,TransactionBuilder
import base64
from math import ceil

data=open("GBBUY7VWPUR4GRPMXFWNUYMQK3AD7NHJPHPF4JOXCB3AZNKJDIM3IKNX?network=public&v=1.png","rb").read() 
b64=base64.b64encode(data) #encode it into base 64 string binary

keypair=Keypair.from_secret("")
print(keypair.public_key)
print()
server = Server(horizon_url="https://horizon-testnet.stellar.org")

	
tx= (
	TransactionBuilder(
		source_account = server.load_account(account_id=keypair.public_key), 
		network_passphrase=Network.TESTNET_NETWORK_PASSPHRASE, 
		base_fee=10000) 	
)

#/w00kie split
def chunk(text: str, size: int) -> list:
	return [text[i : i + size] for i in range(0, len(text), size)]

i = 0
for entry in chunk(b64, 62 + 64):
	key = f"{i:02d}{entry[:62].decode('utf8')}"
	value = entry[62:]
	tx.append_manage_data_op(
		data_name=key,
		data_value=None,
	)
	i += 1
	
txtosign=tx.build()
txtosign.sign(keypair)
response = server.submit_transaction(txtosign)
print("\nTransaction hash: {}".format(response["hash"]))
print("Push any key to continue...")
input()
