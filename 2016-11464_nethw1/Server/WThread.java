
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class WThread extends Thread
{
	private Socket curr;
	private int id;
	
	WThread(Socket s, int i)
	{
		curr = s;
		id = i;
	}
	
	public void run()
	{
		try {
a:			while(true)
			{
				synchronized(Server.connected)
				{
					synchronized (Server.frequest)
					{
						if(Server.frequest.containsKey(id)) //send requests(not message)
						{
							while(Server.frequest.get(id).size() > 0)
							{
								PrintWriter pw = new PrintWriter(curr.getOutputStream());
								pw.println(Server.frequest.get(id).get(0));
								pw.flush();
								if(Server.frequest.get(id).get(0).charAt(0) == 'q')
								{
									Server.frequest.get(id).remove(0);
									System.out.println("User"+id+" quit the application");
									break a;
								}	
								Server.frequest.get(id).remove(0);
							}
						}
				
						if(Server.connected.containsKey(id)) //send messages
						{
							int key = 0;
							int con = Server.connected.get(id);
							if(con > id) 
								key = id*10 + con;
							else 
								key = con*10 + id;
							synchronized (Server.received)
							{
								if(Server.received.containsKey(key))
								{
									if(con > id) 
										while(Server.received.get(key).data.size() > Server.received.get(key).readcur1)
										{
											String temp = Server.received.get(key).data.get(Server.received.get(key).readcur1);	
											System.out.println(temp);
											PrintWriter pw = new PrintWriter(curr.getOutputStream());
											if(Server.received.get(key).readcur1 == 0)
											{
												pw.println("y");
												pw.flush();
											}
											if((int)temp.charAt(1) - 48 == id)
											{
												pw.println(temp);
												pw.flush();
											}
											else
											{
												if((Server.connected.containsKey(con) && (Server.connected.get(con) == id)) || (Server.received.get(key).readcur1 < Server.received.get(key).readcur2))
													pw.println("ay" + temp.substring(1,temp.length()));
												else
													pw.println("ax" + temp.substring(1,temp.length()));
												pw.flush();
											}
											Server.received.get(key).readcur1++;	
										}
									else 
										while(Server.received.get(key).data.size() > Server.received.get(key).readcur2)
										{
											String temp = Server.received.get(key).data.get(Server.received.get(key).readcur2);	
											PrintWriter pw = new PrintWriter(curr.getOutputStream());
											if(Server.received.get(key).readcur2 == 0)
											{
												pw.println("y");
												pw.flush();
											}
											if((int)temp.charAt(1) - 48 == id)
											{
												pw.println(temp);
												pw.flush();
											}
											else
											{
												if((Server.connected.containsKey(con) && (Server.connected.get(con) == id)) || (Server.received.get(key).readcur2 < Server.received.get(key).readcur1))
													pw.println("ay" + temp.substring(1,temp.length()));
												else
													pw.println("ax" + temp.substring(1,temp.length()));
												pw.flush();
											}
											Server.received.get(key).readcur2++;
										}
								}
							}
						}		
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
