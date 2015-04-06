# Distributed Backup Service: Enhancements

## Chunk backup subprotocol

The project specification proposes the following actions to be taken when a **PUTCHUNK** message is received:

1. Store the chunk
2. Wait for a random interval uniformly distributed between 0 and 400 ms
3. Send **STORED** message


Our enhancement does not require any additional special messages, and therefore is interoperable with any other implementations of the protocol:

```
wait for a random interval uniformly distributed between 0 and 400 ms;

meanwhile, save the number of received STORED messages related to the chunk being backed up

if (that number < desired replication degree) {
  store the chunk;
  send STORED message;
}
```


This enhancement:

- Ensures the desired replication degree almost every time
- Does not deplete the backup space rapidly


## Chunk restore protocol

To do.


## File deletion subprotocol

To do.


## Space reclaiming subprotocol

The project specification proposes the following actions to be taken when a **REMOVED** message is received:

1. Update local count of peers backing up the chunk (chunk mirrors)
2. If that count drops below the desired replication degree of that chunk
  - Wait for a random interval uniformly distributed between 0 and 400 ms
  - If a **PUTCHUNK** message has NOT been received meanwhile
    - Start chunk backup subprotocol


This implementation has the following flaw:

If a peer fails during the chunk backup subprotocol step, the replication degree of the file chunk may be lower than desired.


An even worst case scenario would be the following:  

- There are 3 peers running: A, B, and C
- Peer A backs up file X.png with replication degree = 1
- X.png originates 4 chunks upon the splitting process
- Peer B stores the first chunk
- Peer C stores the remaining 3 chunks
- Peer B reclaims total space of his disk, and therefore sends a **REMOVED** message regarding the first chunk of X.png
- Since the replication degree is 1, and taking the actions described in the specification, neither peer A or C would back up the to be deleted chunk, because none of them has another backup if that chunk


Our enhancement does not require any additional special messages, and therefore is interoperable with any other implementations of the protocol:

When a peer receives a **REMOVED** message:

```
if (peer is backink up a copy of that chunk) {
  update available mirrors of chunk;
	
	if (new replication degree < desiredRepDeg) {
		wait for a random interval uniformly distributed between 0 and 400 ms;
		
		meanwhile, save number of received PUTCHUNK messages;

		if (no PUTCHUNK message was registered meanwhile)
			start backup sub-protocol of that chunk;
}
```

Meanwhile, the peer who sent the **REMOVED** message does the following:
```
```
