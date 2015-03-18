# L04

## Server

Open a Terminal by pressing <kbd>CTRL</kbd> + <kbd>ALT</kbd> + <kbd>T</kbd>

Start the RMI registry: ```rmiregistry &```

**Usage:** java Server <remote_object_name>

**Example:** ```java l04/Server test```

Press <kbd>CTRL</kbd> + <kbd>C</kbd> to stop the server.


## Client

**Usage:** java Client \<host_name\> \<remote_object_name\> \<oper\> \<opnd\>*

Open a Terminal and send a request to the server:

### Register a plate

**Usage:** java Client \<host_name\> \<remote_object_name\> register \<plate\> \<owner\>

**Example:** ```java l04/Client localhost test register 11-22-AA ferrolho```


### Lookup a plate

**Usage:** java Client \<host_name\> \<remote_object_name\> lookup \<plate\>

**Example:** ```java l04/Client localhost test lookup 11-22-AA```
