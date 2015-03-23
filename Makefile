all: compile

compile:
	javac MultithreadedChatServer.java
	javac ChatClient.java
	javac Message.java
	javac ReliableBroadcast.java
	javac Process.java
	javac BroadcastReceiver.java
	javac RB.java
	javac FIFORB.java
	javac STRB.java
	javac STFF.java

server:
	java MultithreadedChatServer 1234

RB:
	java RB 1234
	
FIFO:
	java FIFORB 1234
	
STRB:
	java STRB 1234

STFF:
	java STFF 1234
	
