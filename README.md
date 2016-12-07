# RestFB proxy extension

feature list:

 * Implements WebRequestor interface
 * Uses Apache Http client
 * maven build and test
 * works with jdk 1.7+
 
Allows RestFB to be used through an http proxy such as squid

### Running test suite
```
maven test

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.restfb.example.ApacheWebRequestorTest
Can't set level for java.util.logging.ConsoleHandler
Dec 06, 2016 8:17:48 PM com.restfb.example.ApacheWebRequestorTest setupConnection
INFO: configuration completed
Dec 06, 2016 8:17:49 PM org.apache.http.impl.conn.LoggingManagedHttpClientConnection onRequestSubmitted
FINE: http-outgoing-0 >> GET / HTTP/1.1
Dec 06, 2016 8:17:49 PM org.apache.http.impl.conn.LoggingManagedHttpClientConnection onRequestSubmitted
FINE: http-outgoing-0 >> Host: www.google.com
Dec 06, 2016 8:17:49 PM org.apache.http.impl.conn.LoggingManagedHttpClientConnection onRequestSubmitted
FINE: http-outgoing-0 >> Connection: Keep-Alive
Dec 06, 2016 8:17:49 PM org.apache.http.impl.conn.LoggingManagedHttpClientConnection onRequestSubmitted
FINE: http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.2 (Java/1.8.0_111)

```
### Generating Jar without tests
```
mvn package -DskipTests

[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ restfb-proxy ---
[INFO] Tests are skipped.
[INFO] 
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ restfb-proxy ---
[INFO] Building jar: /run/media/user1/centosDev/enw1/restfb-proxy/target/restfb-proxy-0.0.1-SNAPSHOT.jar
[INFO] BUILD SUCCESS
```

### Stuff used to make this:

 * [restFB](https://github.com/restfb/restfb) Facebook client library
 * [restFB examples](https://github.com/restfb/restfb-examples) for usage examples and test code
 * [apache http client](https://github.com/apache/httpclient) for proxy capable httpclient
 * [jsoup](https://github.com/jhy/jsoup) for testing http client
