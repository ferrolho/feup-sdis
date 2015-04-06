# Distributed Backup Service: Enhancements

## Chunk backup subprotocol

The project specification proposes the following actions to be taken when a **PUTCHUNK** message is received:

1. Store the chunk
2. Wait for a random interval uniformly distributed between 0 and 400 ms
3. Send **STORED** message

Our enhancement does not require any additional special messages, and therefore is interoperable with any other implementations of the protocol:

1. Wait for a random interval uniformly distributed between 0 and 400 ms; meanwhile, save the number of received **STORED** messages related to the chunk being backed up
2. If that number is less than the replication degree of the chunk
  - Store the chunk
  - Send **STORED** message

This enhancement:
- Ensures the desired replication degree almost every time
- Does not deplete the backup space rapidly

## Chunk restore protocol

## File deletion subprotocol

## Space reclaiming subprotocol
