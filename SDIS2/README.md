# FEUP-SDIS2

javac -d out -sourcepath src src/peer/Peer.java src/testapp/TestApp.java

java -classpath out peer.Peer 1.0 1 45000

java -classpath out testapp.TestApp 1 BACKUP test.txt 1