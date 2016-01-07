# Cascade
Cascade is a simple but powerful event driven network framework.

Cascade is in an stable state and tested.

## Why Cascade?

- easy to use
- fast setup
- lightweight
- simple API design
- powerful NIO implementation
- UDP and TCP support
- packets
- protocol system
- buffer pooling (in progress)

## Important
Packet id -128 is used for internal UDP port handling, so it is better to not use this id. Maybe some other negative ids will be used for internal stuff such as cryptography in the future. 
It is preferably to use the ids between 0 and 127 first.

## Installation

- Install [Maven 3](http://maven.apache.org/download.cgi)
- Clone/Download this repo
- Install it with: ```mvn clean install```

**Maven dependency**
```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-all</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

If you don't want the client and the server in your project you can use **cascade-client** or **cascade-server** as artifact id as well.

## Examples

### Echo-Server
- [EchoServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/echo/EchoServer.java)
- [EchoServerProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/echo/EchoServerProtocol.java)

### Echo-Client
- [EchoClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClient.java)
- [EchoClientProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClientProtocol.java)
- [EchoClientPacketListener](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClientPacketListener.java)

### Byte-Array-Server
- [ByteArrayServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/bytes/ByteArrayServer.java)

### Byte-Array-Client
- [ByteArrayClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/bytes/ByteArrayClient.java)

### License

Licensed under the GNU General Public License, Version 3.0.
