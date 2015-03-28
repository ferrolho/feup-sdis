# Distributed backup service

## Server

Start the RMI registry: ```rmiregistry &```

Usage: ```java Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>```

Example: ```java service.Peer 224.0.0.0 8000 224.0.0.0 8001 224.0.0.0 8002```


## Client

Example:

```java service.TestClient backup <file> <replication degree>```

```java service.TestClient 224.0.0.0 8001```

```java service.TestClient 224.0.0.0 8002```

