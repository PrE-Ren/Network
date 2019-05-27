
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class RThread extends Thread
{
	private Socket curr;
	private int id;
	
	RThread(Socket s, int i)
	{
		curr = s;
		id = i;
	}
	
	public void run()
	{
		try 
		{
			while(true)
			{
				BufferedReader buf = new BufferedReader(new InputStreamReader(curr.getInputStream())); 
				String str = buf.readLine();
				if(str == null)
				{
					
				}
				else 
				{
					synchronized(Server.connected)
					{
						synchronized (Server.frequest)
						{
							//read message according to protocol
							if(str.charAt(0) == 'm')
							{
								int to = Server.connected.get(id);
								int from = id;
								int key = 0;
								if(to > from) key = from*10 + to; else key = to*10 + from;
								synchronized (Server.received)
								{
									//if(!Server.received.containsKey(key))
									//	Server.received.put(key, new Dataperroom());
									if(!Server.received.containsKey(key))
										Server.received.put(key, new Dataperroom());
									Server.received.get(key).data.add(from + "" + to + str.substring(1,str.length())); // 3>4 hello = 34hello
								}
							}
							else if(str.charAt(0) == 'd')
							{
								if(Server.connected.containsKey(id))
								{
									Server.connected.remove(id);
									Server.frequest.get(id).add("d");
									System.out.println("User"+id+" disconnected");
								}
								else
									System.out.println("not connected");
							}
							if (str.charAt(0) == 'f')
							{
								
								if(str.charAt(1) == 'o' || str.charAt(1) == 'x')
								{
									int to = (int)str.charAt(2) - 48;
									if(str.charAt(1) == 'o')
									{
										int from = id;
										int key = 0;
										if(to > from) key = from*10 + to; else key = to*10 + from;
										synchronized (Server.flist)
										{
											if(!Server.flist.containsKey(id))
												Server.flist.put(id,new ArrayList<Integer>());
											Server.flist.get(id).add(to);
											
											if(!Server.flist.containsKey(to))
												Server.flist.put(to,new ArrayList<Integer>());
											Server.flist.get(to).add(id);
										}
									}
									Server.frequest.get(to).add(str.substring(0,2) + id);
								}
								else
								{
									int to = (int)str.charAt(1) - 48;
									if(!Server.frequest.containsKey(to))
										Server.frequest.put(to, new ArrayList<String>());
									Server.frequest.get(to).add("f" + id);//3>4 fre = f4
									// f4 >> f3 
								}
							}
							else if(str.charAt(0) == 'r')
							{
								synchronized(Server.flist)
								{
									if(!Server.frequest.containsKey(id))
										Server.frequest.put(id, new ArrayList<String>());
									if(!Server.flist.containsKey(id))
										Server.flist.put(id, new ArrayList<Integer>());
									for(int i=0;i<Server.flist.get(id).size();i++)
									{
										int from = Server.flist.get(id).get(i);
										if(!Server.frequest.containsKey(from))
											Server.frequest.put(from, new ArrayList<String>());
										Server.frequest.get(id).add("r"+from);
									}
									//System.out.println("hi");
									Server.frequest.get(id).add("e");
									//System.out.println("send e");
								}
							}
							else if(str.charAt(0) == 'q')
							{
								synchronized(Server.flist)
								{
									synchronized(Server.online)
									{
										Server.frequest.get(id).add("q");
										
										if(!Server.flist.containsKey(id))
											Server.flist.put(id, new ArrayList<Integer>());
										for(int i=0;i<Server.flist.get(id).size();i++)
										{
											Server.frequest.get(Server.flist.get(id).get(i)).add("n"+id);
										}
										for(int i = Server.online.size()-1; i>=0; i--)
										{
											if(Server.online.get(i) == id)
												Server.online.remove(i);
										}
										if(Server.connected.containsKey(id))
											Server.connected.remove(id);
										break;
									}		
								}	
							}
							else if(str.charAt(0) == 'n')
							{
								int key = (int)(str.charAt(1)) - 48;
								if(!Server.frequest.containsKey(id))
									Server.frequest.put(id, new ArrayList<String>());
								synchronized(Server.online)
								{
									if(Server.online.contains(key))
									{
										Server.frequest.get(id).add("o" + key);
									}
									else Server.frequest.get(id).add("n" + key);
								}
							}
							else if(str.charAt(0) == 'c')
							{
								synchronized(Server.flist)
								{
									int key = (int)(str.charAt(1)) - 48;
									if(Server.flist.containsKey(id))
									{
										if(Server.flist.get(id).contains(key))
										{	
											Server.frequest.get(id).add(str);	
											Server.connected.put(id, Server.frequest.get(id).get(0).charAt(1)-48);
											synchronized (Server.received)
											{
												int tmp;
												if(id < key) tmp = id*10+key;
												else tmp = key*10 + id;
												if(!Server.received.containsKey(tmp))
													Server.received.put(tmp, new Dataperroom());
												
												if(Server.connected.containsKey(key) && (Server.connected.get(key) == id))
												{
													Server.received.get(tmp).readcur1 = 0;
													Server.received.get(tmp).readcur2 = 0;
												}
												else if(id < key)
													Server.received.get(tmp).readcur1 = 0;
												else
													Server.received.get(tmp).readcur2 = 0;
												
											}
							
											System.out.println(id + " connected to " + key);
										}
										else
											System.out.println("not a friend");
									}
									else
										System.out.println("not a friend");
									
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

