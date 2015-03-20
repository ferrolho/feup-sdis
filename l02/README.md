# L02

## Server

Open a Terminal by pressing <kbd>CTRL</kbd> + <kbd>ALT</kbd> + <kbd>T</kbd>, and start the server.

**Usage:** java Server \<servicePort\> \<multicastIP\> \<multicastPort\>

**Example:** ```java l02.Server 8080 225.0.0 8000```

Press <kbd>CTRL</kbd> + <kbd>C</kbd> to stop the server.


## Client

Open a Terminal and send a request to the server.

**Usage:** java Client \<mcast_addr\> \<mcast_port\> \<oper\> \<opnd\>*

### Register

**Usage:** java Client \<mcast_addr\> \<mcast_port\> register \<plate\> \<owner\>

**Example:** ```java l02.Client 225.0.0.0 8000 register 11-11-AA ferrolho```


### Lookup

**Usage:** java Client \<mcast_addr\> \<mcast_port\> lookup \<plate\>

**Example:** ```java l02.Client 225.0.0.0 8000 lookup 11-11-AA```
