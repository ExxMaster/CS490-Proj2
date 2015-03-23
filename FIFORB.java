import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
 
class FIFORB implements Runnable, BroadcastReceiver{
	 
	private String _name, _ip;
    private int _port;
    ArrayList<String> group = null;
    public static ArrayList<Process> p_client_group = null;
    ArrayList<String> messages = null;
    //Connection to server
    private Socket s;
    int msgnum = 0;
    //Socket for incoming chat
    private ServerSocket serverSocket;
    private Socket _client;
     
    //ThreadPool
    private ThreadPoolExecutor executor;
    
    //IO buffer
    BufferedWriter bw;
    BufferedReader br;
    static Process p;
    public static ReliableBroadcast rb;
    
    //constant variable
    private static final int heartbeat_rate = 5;
    private static final String serverAddress = "data.cs.purdue.edu";
    private static int portNumber = 1222;      //this gets reset to what the user inputs
    private static final int THREAD_POOL_CAPACITY = 11;
	
	@Override
  public void receive(Message m){
    ;
  }
	
	public FIFORB() {
	
	
        try{
            s = new Socket(serverAddress, portNumber);
            p = new Process();
            rb = new ReliableBroadcast();
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            serverSocket = new ServerSocket(0);
            messages = new ArrayList<String>();
        }catch (IOException e) {
            System.out.println("Connextion to server failed");
            System.exit(1);
        }
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        this._ip = s.getLocalAddress().toString().substring(1);
        this._port = serverSocket.getLocalPort();
        
        //listen for chat
        this.listen();
    }
    
	private void listen() {
		this.executor.execute(this);
	}
	
	 /*
    private void prompt() {
        Scanner sc = new Scanner(System.in);
        while(true) {
            try {
                //this.listen();
                System.out.print("> ");
                String command = sc.nextLine();
                
                if(command.equals("exit")) {
                    sc.close();
                    System.exit(1);
                } 
                else if(command.equals("get")) {
                    this.group = this.get();
                } 
                else if(command.equals("getp")) {
                    this.p_client_group = this.getProcess();
                } else if(command.contains("chat")) {
                    this.Chat(command);
                } else if(command.equals("y")) {
                    this.AcceptChat();
                    this.listen();
                } else if(command.equals("n")) {
                    bw = new BufferedWriter(new OutputStreamWriter(this._client.getOutputStream()));
                    bw.write("Declined\n");
                    bw.flush();
                } 
                
                else {
                  System.out.println("Calling BroadCast");
                  this.p_client_group = this.getProcess();
                  this.broadCast(this.p_client_group);
                  /*Message m = new Message();
                  m.Message(command, 1);
                  //m.setMessageContents(command);
                  rb.rbroadcast(m);*/
                /*}
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
                 
            }
        }
    }*/
	@SuppressWarnings("unchecked")
    private ArrayList<Process> getProcess() {
        ArrayList<Process> ret = null;
        try {
            String m = "getp\n";
            this.sendMessage(m);
            ObjectInputStream ois = new ObjectInputStream(this.s.getInputStream());
            ret = (ArrayList<Process>) ois.readObject();
            //System.out.println(ret.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
	private void prompt() {
		this.listen();
		Scanner sc = new Scanner(System.in);
		while(true) {
		    try {
		        //System.out.print("> ");
		        String command = sc.nextLine();
		        //System.out.println("Command: "+command);
		        this.p_client_group = this.getProcess();
		         for(int i = 0; i<this.p_client_group.size(); i++){
					  Process temp = this.p_client_group.get(i);
					  String ip = temp.getIP();
					  int port = temp.getPort();
					  String id = temp.getID();
					  String send = id.concat(" ").concat(ip).concat(" ").concat(Integer.toString(port));
					  //System.out.println("send: "+send);
					  if(id.equals(this._name)){
					  		continue;
					  }
					  
					  Socket socket = new Socket(ip, port);
					  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					  String msg = this._name+": "+command+"#"+msgnum+"\n";
					  bw.write(msg);
					  bw.flush();
					  socket.close();
				}
		        if(command.equals("exit")) {
		            sc.close();
		            System.exit(1);
		        } 
		        
		        while(true){
		        	int h = 0;
		        	for(int i=0; i<this.messages.size();i++){
			        	String check = this.messages.get(i);
			        	String f = check.substring(0,check.indexOf(' ')-1);
			        	String b = check.substring(check.indexOf('#')+1, check.length());
			        	//System.out.println("f: ("+f+"), b: ("+b+")");
			        	int num = Integer.parseInt(b);
			        	if(f.equals(this._name) && num == msgnum){
			        		h=1;
			        		break;
			        	}
			        }
			        if(h==1){
			        	break;
			        }
		        }
		        
		        msgnum++;
		        
			} catch(Exception e) {
                //e.printStackTrace();
                System.exit(1);
                 
            }
		}
	}
	
	
	//write String s to the Socket
    public boolean sendMessage(String s) {
        try {
            this.bw.write(s);
            this.bw.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
     
    //read message from the socket
    public String readMessage() {
        String s;
        try {
            s = this.br.readLine();
        } catch (IOException e) {
            return null;
        }
        return s;
    }
	
	//register client
	@SuppressWarnings("resource")
	public boolean register() {
		//get the name
		Scanner sc = new Scanner(System.in);
		System.out.print("Please Enter Your Name: ");
		this._name = sc.nextLine();
		String m = "register<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
		//System.out.println(m);
		//System.out.println(this._ip + this._port + this._name);
		//p.Node(this._ip, this._port, this._name); //make the client a process node
		this.sendMessage(m);
		//System.out.println("Message sent to the server : " + m);
		m = this.readMessage();
		//System.out.println(m);
		//System.out.print(m);
		return(m.equals("Success"));
	}
	
	//send heart beat every heartbeat_rate seconds
	public void sendHeartbeat() {
		this.executor.execute(new Runnable() {
			FIFORB c;
			@Override
			public void run() {
				try {
					while(true) {
						String m = "heartbeat<" + c._name + ", " + c._ip + ", " +  c._port + ">" + "\n";
						c.sendMessage(m); 
						//System.out.println("Message sent to the server : " + m);
						Thread.sleep(heartbeat_rate * 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public Runnable init(FIFORB cc) {
				this.c = cc;
				return this;
			}
		}.init(this));
	}
	
	@Override
    public void run() {
        while(true){
	        try {
	        	int has = 0;
        	    this._client = this.serverSocket.accept();
        	    BufferedReader br = new BufferedReader(new InputStreamReader(this._client.getInputStream()));
	        	String msg = br.readLine();
	        	for(int i = 0;i<this.messages.size();i++){
	        		//System.out.println("msg: "+this.messages.get(i));
		        	if (msg.equals(this.messages.get(i))){
		        		has=1;
		        		break;
		        	}
		        }
		        if(has==1){
		        	continue;
		        }
		        this.messages.add(msg);
	        	this.p_client_group = this.getProcess();
		        for(int i = 0; i<this.p_client_group.size(); i++){
					  Process temp = this.p_client_group.get(i);
					  String ip = temp.getIP();
					  int port = temp.getPort();
					  String id = temp.getID();
					  String send = id.concat(" ").concat(ip).concat(" ").concat(Integer.toString(port));
					  //System.out.println("send: "+send);
					  if(id.equals(this._name)){
					  		continue;
					  }
					  
					  Socket socket = new Socket(ip, port);
					  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					 // String newmsg = "new: "+this._name+": "+msg+"\n";
					  bw.write(msg);
					  bw.flush();
					  socket.close();
				}
	        	
	        	String fin = new String();
	        	fin = msg.substring(0,msg.indexOf('#'));
	        	System.out.println(fin);
	        	this._client.close();

    	    } catch(Exception e) {
    	        //e.printStackTrace();
    	    } /*finally {
    	    	this._client.close();
    	    }*/
    	}
    }
	
	public static void main(String[] args) throws Exception {
		if(args.length!=1){
			System.out.println("Need port number");
			System.exit(1);
		}
		portNumber=Integer.parseInt(args[0]);

		FIFORB cc = new FIFORB();
		while(true) {
			if(cc.register()) break;
		} 
		cc.sendHeartbeat();
		cc.prompt();
	}
}
