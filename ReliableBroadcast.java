public interface ReliableBroadcast {
	public void init ( Process currentProcess , BroadcastReceiver br ) ;
	public void addMember ( P r o c e s s member ) ;
	public void removeMember ( P r o c e s s member ) ;
	public void rbroadcast ( Message m) ;
}

package edu.purdue.cs490;
public class Process {
	String IP ;
	int port ;
	String ID ;
	
	public Node ( String IP , int port , String ID ) {
		this.IP = IP ;
		this.port = port ;
		this.ID = ID ;
	}
	public String getIP () {
		return IP ;
	}
	public int getPort () {
		return port ;
	}
	publicString getID () {
		return ID ;
	}
}
package edu.purdue.cs490 ;
public interface BroadcastReceiver {
	void receive ( Message m) ;
}

package edu.purdue.cs490 ;
public interface Message {
	int getMessageNumber ( ) ;
	void setMessageNumber ( int messageNumber ) ;
	String getMessageContents ( ) ;
	void setMessageContents ( String contents ) ;
}
