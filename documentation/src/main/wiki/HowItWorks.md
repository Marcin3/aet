# How It Works
AET is distributed system that consists of modules deployed on 
[Apache Karaf](http://karaf.apache.org/) which communicate with each other using JMS Server - [Active MQ](http://activemq.apache.org/).
Data collected and results of tests processing are saved into Datastorage (which current implementation is [MongoDB](https://www.mongodb.com/)).
Additionally AET use [Browsermob](http://bmp.lightbody.net/) as requests proxy server and [Apache Server](https://httpd.apache.org/) as host server
for [[Reports Web Application|SuiteReport]].

Diagram below presents AET system and communication between AET system components:

![aet-components-diagram](assets/diagrams/aet-components-diagram.png)

#### Third-party software used by system
AET uses the following third party software as parts of the system:

| Software | Used version | Function |
| --------- | ----- | ----------- |
| [Apache ActiveMQ](http://activemq.apache.org/) | 	5.13.1 | JMS Sever used for communication between system components |
| [Apache Karaf](http://karaf.apache.org/) | 2.3.9 with 4.2.1 Apache Felix Framework. | OSGi container for AET bundles and REST API |
| [Apache Server](https://httpd.apache.org/) | 2.2.15 | Http server used to host [[Reports Web Application|SuiteReport]] |
| [Browsermob](http://bmp.lightbody.net/) | 	2.0.0 | Proxy server |
| [Firefox](https://ftp.mozilla.org/pub/firefox/releases/38.6.0esr/win32/en-US/) | 38.6.0 ESR (en-US) | 	Browser with Selenium (2.50.1) |
| [MongoDB](https://www.mongodb.com/) | 3.2.3 | 	System database |