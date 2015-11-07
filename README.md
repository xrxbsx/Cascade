# Cascade
Cascade is a simple but powerful event driven network framework.

## Why Cascade?

- easy to use
- fast setup
- lightweight
- simple API design
- powerful NIO implementation
- UDP and TCP support

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

If you dont't want the client and the server in your project you can use **cascade-client** or **cascade-server** as artifact id as well.

##  Examples

- [Echo-Server](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/server/ExampleServer.java)
- [Echo-Client](https://github.com/JackWhite20/Cascade/blob/master/Example/src/main/java/de/jackwhite20/cascade/example/client/ExampleClient.java)

### License

Licensed under the GNU General Public License, Version 3.0.
