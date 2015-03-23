
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultithreadedChatServer extends ReliableBroadcast implements Runnable {
    
    //Constant Variable
    private static int PORT = 1222;
    private static final int THREAD_POOL_CAPACITY = 11;
    public static ReliableBroadcast rb;
    
    private ServerSocket serverSocket;
    private static ArrayList<String> group;
    private static ConcurrentHashMap<String, Long> heart_beat;
    private ThreadPoolExecutor executor;
    
    //child server attributes
    private Socket _client;
    BufferedWriter bw;
    private String name;
    static Message m;
    
    public MultithreadedChatServer() {        
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        
        //create the server socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        group = new ArrayList<String>();
        heart_beat = new ConcurrentHashMap<String, Long>();
        //System.out.println("\t\t\t\tObject of RB created ");
        rb = new ReliableBroadcast();
        rb.p_group = new ArrayList<Process>();
    }
    
    public MultithreadedChatServer(Socket client) {
        this._client = client;
        try {
            this.bw = new BufferedWriter(new OutputStreamWriter(this._client.getOutputStream()));
        } catch (Exception e) {
            System.out.println("init failed");
        }
    }
    
    private synchronized boolean addToGroup(String s) {
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        sa[1] = sa[1].substring(1);
        sa[2] = sa[2].substring(1, sa[2].length()-1);
        int port =  Integer.parseInt(sa[2]);
        //System.out.println(sa[0]);
        //System.out.println(sa[1]);
        //System.out.println(sa[2]);
        
        Process member = new Process();
        member.Node(sa[1], port, sa[0]);
        
        for(int i = 0; i < MultithreadedChatServer.group.size(); i++) {
            if(MultithreadedChatServer.group.get(i).contains(sa[0])) {
                try {             
                    bw.write("Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        MultithreadedChatServer.group.add(s);
        rb.addMember(member);
        rb.printGroup();
        
        
        try {            
            bw.write("Success\n");
            bw.flush();
        } catch (IOException e) {
            System.out.println("error: sending success");
            e.printStackTrace();
        }
        
        return true;
    }
    
    private synchronized static boolean removeFromGroup(String s) {
      if(MultithreadedChatServer.group.remove(s)){
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        for(int i = 0; i < rb.p_group.size(); i++)
        {
          Process temp = rb.p_group.get(i);
          String id = temp.getID();
          if(id.equals(sa[0])){
            if(rb.p_group.remove(temp)){
              System.out.println("\t\t\tDONE REMOVING FROM PROCESS LIST");
            }
            else
            {
              System.out.println("ERROR: REMOVING PROCESS DID NOT WORK");
            }
          }
        }
        return true;
      }
      return false;
        
    }
    
    
    public void checkingHeartbeat() {
     this.executor.execute(new Runnable() {

   @Override
   public void run() {
    while(true) {
     System.out.println("Checking Heartbeat");
     try {
      Thread.sleep(1000);
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
     Long time = System.currentTimeMillis();
     for(int i = 0; i < group.size(); i++) {
      String id = group.get(i);
      Long t = heart_beat.get(id);
      if(time - t > 10000) {
       heart_beat.remove(id);
       removeFromGroup(id);
       System.out.printf("%s removed\n time stap:\t%s\n time now:\t%s\n", id, t.toString(), time.toString());
      }
     }
    }
   }
      
     });
    }
    
    @Override
    public void run() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(_client.getInputStream()));
            while(true) {
                String m;
                m = br.readLine();
                System.out.println(m);
                if(m.contains("register")) {
                    m = m.substring(m.indexOf('<'));
                    //add to Group
                    this.addToGroup(m);
                    this.name = m;
                    MultithreadedChatServer.heart_beat.put(m, System.currentTimeMillis());
                } else if(m.equals("get")) {
                    ObjectOutputStream oos = new ObjectOutputStream(this._client.getOutputStream());
                    oos.writeObject(MultithreadedChatServer.group);
                    oos.flush();
                } else if(m.equals("getp")){
                  ObjectOutputStream oos = new ObjectOutputStream(this._client.getOutputStream());
                  oos.writeObject(MultithreadedChatServer.rb.p_group);
                  //System.out.println(MultithreadedChatServer.rb.p_group.toString());
                  oos.flush();
                }           
                else if(m.contains("heartbeat")) {
                 Long l = System.currentTimeMillis();
                 m = m.substring(m.indexOf('<'));
                    if(MultithreadedChatServer.heart_beat.put(m, l) == null) {
                     System.out.println("updating failed");
                    }
                    System.out.printf("update time to %s\n", l.toString());
                            rb.printGroup();
                }
            }
        } catch (Exception e) {
            System.out.printf("Connection to %s lost\n", this.name);
            Thread.yield();
        }
    }
    
    public void startServer() {
     this.checkingHeartbeat();

        while (true) {
            try {
                Socket _client = this.serverSocket.accept();
                MultithreadedChatServer ms = new MultithreadedChatServer(_client);
                this.executor.execute(ms);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {

        if(args.length != 1){
            System.err.println("need port number");
            System.exit(1);
        }
         PORT = Integer.parseInt(args[0]);
 System.out.println("Start!");
        MultithreadedChatServer ms = new MultithreadedChatServer();
        ms.startServer();
    }
}
