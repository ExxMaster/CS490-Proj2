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
  
  public ReliableBroadcast(String procName, String groupAddress)
  {
    name = procName;
      try
      {
         socket = new MulticastSocket(channel);
         address = InetAddress.getByName(groupAddress);
         socket.joinGroup(address);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
  }
  public ReliableBroadcast(String procName)
   {
      this(procName, "127.0.0.1");
   }
 
  public static void main(String [] args)
  {
    if (args.length < 1)
    {
      System.out.println("Need to be in this format: java ReliableBroadcast \"your name\"");
      System.exit(0);
    }
    ReliableBroadcast rb = new ReliableBroadcast(args[0]);
    //rb.start();
  } 
}


