import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReliableBroadcast implements ReliableBroadcastInterface{
  String name;
  InetAddress address;
  MulticastSocket socket;
  int channel = 2222;
  Process local;
  public static ArrayList<Process> p_group;
  
  @Override
  public void rbroadcast(Message m){
    for(int i = 0; i<p_group.size(); i++)
        {
          Process temp = p_group.get(i);
          String ip = temp.getIP();
          int port = temp.getPort();//.toString();
          try{
            Socket socket = new Socket(ip, port);
            BufferedWriter bbw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
          }
          catch(IOException e){
            ;
          }

        }
  }
  @Override 
  public void removeMember(Process member){
    p_group.remove(member);
  }
  @Override 
  public void addMember(Process member){
    p_group.add(member);
  }
  @Override 
   public void init(Process currentProcess , BroadcastReceiver br){
    
  }
  
  public void printGroup(){
    System.out.println("Number of clients \t\t" + p_group.size()); 
    for(int i = 0; i < ReliableBroadcast.p_group.size(); i++)
        {
          Process temp = ReliableBroadcast.p_group.get(i);
          String id = temp.getID();
          System.out.println("Client at \t\t\ti = "+ i + "\t\tid = " + id ); 
        }
  }
}


