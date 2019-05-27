
import java.io.*;
import java.net.*;
import java.util.*;

class Dataperroom //data-per-room 
{
	int readcur1, readcur2; //read indexes of two users
	ArrayList<String> data;
	
	Dataperroom()
	{
		readcur1 = 0;
		readcur2 = 0;
		data = new ArrayList<String>();
	}
	
}

public class Server {
	
	volatile public static ArrayList<Integer> online = new ArrayList<Integer>();
	volatile public static Map<Integer, Integer> connected = new HashMap<Integer,Integer>();
	volatile public static Map<Integer, ArrayList<Integer>> flist = new HashMap<Integer, ArrayList<Integer>>();
	volatile public static Map<Integer, ArrayList<String>> frequest = new HashMap<Integer, ArrayList<String>>();
	volatile public static Map<Integer, Dataperroom> received = new HashMap<Integer, Dataperroom>();
	
	public static void serverstart()
	{
		try {
			ServerSocket ss = new ServerSocket(20464);
			
			while(true)
			{
				Socket s = ss.accept(); //accept
				int id = s.getInputStream().read() - 48;
				synchronized(online)
				{
					online.add(id); //user id is online
				}
				System.out.println("User" + id + " connected");
				synchronized(frequest)
				{
					if(!frequest.containsKey(id))
						frequest.put(id, new ArrayList<String>());
					frequest.get(id).add("s"); //send successly connected
					synchronized(flist)
					{
						if(flist.containsKey(id))
						{
							for(int i=0;i<flist.get(id).size();i++)
							{
								frequest.get(flist.get(id).get(i)).add("l"+id); // send to friends that user connected to server
							}
						}
					}
				}
				RThread r = new RThread(s,id);
				WThread w = new WThread(s,id);
				r.start();
				w.start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		serverstart();
	}

}
