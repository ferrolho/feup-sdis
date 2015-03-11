# L03

## Server

Open a Terminal by pressing **CTRL+ALT+T**, and run the server at a chosen port:

**Usage:** java l03.Server \<port\>

**Example:** ```java l03.Server 8080```

Press **CTRL+C** to stop the server.


## Client

Open a Terminal and send a request to the server:

### Register a plate

**Usage:** java l03.Client \<serverIP\> \<port\> register \<plate\> \<owner\>

**Example:** ```java l03.Client 192.168.1.66 8080 register 11-22-33 Rafiki```

To which the server will respond:
- 1 (size of the database), if the plate was added to the database;
- -1, if the plate already exists in the database.

### Lookup a plate

**Usage:** java l03.Client \<serverIP\> \<port\> lookup \<plate\>

**Example:** ```java l03.Client 192.168.1.66 8080 lookup 11-22-33```

To which the server will respond:
- Rafiki (plate owner), if the plate was found;
- NOT_FOUND, if the plate was not found.
