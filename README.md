# Cascade
Cascade is a simple but powerful event driven network framework.

Cascade is in an stable state and tested.

## Why Cascade?

- easy to use
- fast setup
- lightweight
- simple API design
- powerful [netty](https://github.com/netty/netty) implementation
- packets
- protocol system
- object serialization
- encryption
- SSL

## Installation

- Install [Maven 3](http://maven.apache.org/download.cgi)
- Clone/Download this repo
- Install it with: ```mvn clean install```

**Maven dependency**
```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-all</artifactId>
    <version>2.2.1-SNAPSHOT</version>
</dependency>
```

If you don't want the client and the server in your project you can use **cascade-client** or **cascade-server** as artifact id as well.

```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-client</artifactId>
    <version>2.2.1-SNAPSHOT</version>
</dependency>
```

```xml
<dependency>
    <groupId>de.jackwhite20</groupId>
    <artifactId>cascade-server</artifactId>
    <version>2.2.1-SNAPSHOT</version>
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
- [TestObject](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/shared/echo/TestObject.java)

### Crypto-Server
- [CryptoServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/crypto/CryptoServer.java)
- [CryptoServerConfig](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/crypto/CryptoServerConfig.java)

### Crypto-Client
- [CryptoClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/crypto/CryptoClient.java)
- [CryptoClientConfig](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/crypto/CryptoClientConfig.java)

### Ssl-Server
- [SslServer](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/ssl/SslServer.java)
- [SslServerConfig](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/ssl/SslServerConfig.java)

### Ssl-Client
- [SslClient](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/ssl/SslClient.java)
- [SslClientConfig](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/ssl/SslClientConfig.java)

### License

Licensed under the GNU General Public License, Version 3.0.
