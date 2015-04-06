# Distributed backup service

## Sources

The source files are under the project **src** folder.

## How to compile

The project can be compiled using the javac command.

Alternatively, eclipse can be used to open the project and build it automatically as follows:

1. Open eclipse
2. Under **Files**, select **Import...**
3. Choose the project folder and confirm


## How to run

### RMI registry

As suggested, the interface implementation uses RMI. In order to interact with the service, an *rmiregistry* instance must be running.

The following command launches an *rmiregistry* instance in the background.

```rmiregistry &```

This is only required in machines in which the user intends to interact with the service.


### Peer

If the user intends to interact with a peer, a name for the remote object must be specified, hence the alternative usages:

```
Usage: (w/o trigger)
	java peer.Peer
	java peer.Peer <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>

Usage: (w/ trigger)
	java peer.Peer <RMI obj. name>
	java peer.Peer <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort> <RMI obj. name>
```

#### Defaults

Usages where the user does not specify the multicast addresses and ports will have the following defaults:

|MC            |MDB           |MDR           |
|--------------|--------------|--------------|
|224.0.0.0:8000|224.0.0.0:8001|224.0.0.0:8002|


### Trigger

Following are the possible usages of the *Trigger*, which is used to issue commands to the service.

Note that the ```<RMI obj. name>``` must be the same used to launch the *Peer* we intended to connect this *Trigger* with.

```
Usage:
	java trigger.Trigger <RMI obj. name> backup <file path> <replication degree>
	java trigger.Trigger <RMI obj. name> restore <file path>
	java trigger.Trigger <RMI obj. name> delete <file path>
	java trigger.Trigger <RMI obj. name> space <amount of space>
```
