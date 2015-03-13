import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class ReliableBroadcast implements ReliableBroadcastInterface{
  String name;
  InetAddress address;
  MulticastSocket socket;
  int channel = 2222;
  Process local;
  public static ArrayList<Process> p_group;
  
  @Override
     public void rbroadcast(Message m){
    System.out.println("Successs with extention");
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
    ;
  }
  
  public void printGroup(){
    System.out.println("Number of processes \t\t" + p_group.size()); 
    for(int i = 0; i < ReliableBroadcast.p_group.size(); i++)
        {
          Process temp = ReliableBroadcast.p_group.get(i);
          String id = temp.getID();
          System.out.println("Process at \t\t\ti = "+ i + "\t\tis = " + id ); 
        }
  }
  
  public ReliableBroadcast()
  {
     
  }
}


