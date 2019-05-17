
## Como invocar as funções

### Server

java -Djavax.net.ssl.keyStore=server_keys -Djavax.net.ssl.keyStorePassword=123456 -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=123456 Server

### Client

java -Djavax.net.ssl.keyStore=client_keys -Djavax.net.ssl.keyStorePassword=123456 -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=123456 Client