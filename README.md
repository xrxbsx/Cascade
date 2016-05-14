# Cascade
Cascade is a simple but powerful event driven network framework.

Cascade is in an stable state and tested.

## Why Cascade?

- easy to use
- fast setup
- lightweight
- simple API design
- powerful NIO implementation
- TCP support (UDP will follow)
- packets
- protocol system
- callback system

## Installation

- Install [Maven 3](http://maven.apache.org/download.cgi)
- Clone/Download this repo
- Install it with: ```mvn clean install```

**Maven dependency**
```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-all</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

If you don't want the client and the server in your project you can use **cascade-client** or **cascade-server** as artifact id as well.

```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-server</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Examples

### Echo-Server
- [EchoServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/echo/EchoServer.java)
- [EchoServerProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/echo/EchoServerProtocol.java)
- [EchoServerConfig](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/echo/EchoServerConfig.java)

### Echo-Client
- [EchoClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClient.java)
- [EchoClientProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClientProtocol.java)
- [EchoClientPacketListener](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/echo/EchoClientPacketListener.java)

### Echo-Packets
- [ChatPacket](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/shared/echo/ChatPacket.java)

### Byte-Array-Server
- [ByteArrayServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/bytes/ByteArrayServer.java)

### Byte-Array-Client
- [ByteArrayClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/bytes/ByteArrayClient.java)

### Callback-Server
- [CallbackServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/callback/CallbackServer.java)
- [CallbackServerProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/callback/CallbackServerProtocol.java)
- [CallbackServerPacketListener](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/callback/CallbackServerPacketListener.java)

### Callback-Client
- [CallbackClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/callback/CallbackClient.java)
- [CallbackClientProtocol](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/callback/CallbackClientProtocol.java)

### Callback-Packets
- [TestRequestPacket](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/shared/callback/TestRequestPacket.java)
- [TestResponsePacket](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/shared/callback/TestResponsePacket.java)

### License

Licensed under the GNU General Public License, Version 3.0.
