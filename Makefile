all: compile

compile:
	javac MultithreadedChatServer.java
	javac ChatClient.java
	javac Message.java
	javac ReliableBroadcast.java
	javac Process.java
	javac BroadcastReceiver.java

server:
	java MultithreadedChatServer 1234

client:
	java ChatClient 1234
