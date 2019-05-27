//package javachatclient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	static JFrame f1 = new JFrame("Welcome to Chat-on"); //main interfaces
	static JTextArea ta1;
	static JScrollPane sc1;
	volatile public static int state = 0; //current state
	
	public synchronized static void RefreshFriends(Socket s) //main friends room interface
	{	
		f1.getContentPane().removeAll();
		f1.repaint();
		int count = 0;
		synchronized(friends)
		{
			for(int i : friends.keySet())
			{
				JButton b1 = new JButton("Message with User " + i);
				JLabel l1 = new JLabel();
				if(friends.get(i)) l1.setText("Online");
				else l1.setText("Offline");
				l1.setFont(new Font("Arial",Font.PLAIN,18));
				l1.setBounds(30,50*count,300,50);
				b1.setFont(new Font("Arial",Font.PLAIN,18));
				b1.setBounds(110,50*count,320,50);
				b1.addActionListener(new ActionListener() { //chatroom enter button
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if(e.getSource() == b1)
						{
							try {
								PrintWriter pw = new PrintWriter(s.getOutputStream());
								pw.println("c" + b1.getText().charAt(b1.getText().length()-1));
								pw.flush();
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						}
					}
				});
				f1.add(l1);
				f1.add(b1);
				count++;
			}
		}
		
		JButton b1 = new JButton("Request");
		b1.setFont(new Font("Arial",Font.PLAIN,18));
		b1.setBounds(280,700,150,50);
		f1.add(b1);
		b1.addActionListener(new ActionListener() { //request button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					RequestSend(s);
				}
			}
		});
		JButton b2 = new JButton("Exit");
		b2.setFont(new Font("Arial",Font.PLAIN,18));
		b2.setBounds(0,700,100,50);
		f1.add(b2);
		b2.addActionListener(new ActionListener() { //quit button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b2)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("q");
						pw.flush();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
			}
		});
		f1.repaint();
	}
	
	public synchronized static void Rejected(Socket s, String str) //rejection pop-up
	{
		JFrame f2 = new JFrame("Rejected");
		f2.setLocationRelativeTo(null);
		f2.setLayout(null);
		f2.setSize(400,400);
		JButton b1 = new JButton("Close");
		JLabel l1 = new JLabel("You are rejected from");
		JLabel l2 = new JLabel("User " + str.charAt(2));
		b1.setBounds(140,250,100,70);
		l1.setBounds(70,100,300,50);
		l2.setBounds(150,150,100,50);
		l1.setFont(new Font("Arial",Font.PLAIN,24));
		l2.setFont(new Font("Arial",Font.PLAIN,24));
		b1.setFont(new Font("Arial",Font.PLAIN,18));
		
		f2.add(b1);
		f2.add(l1);
		f2.add(l2);
		f2.setVisible(true);
		
		b1.addActionListener(new ActionListener() { //close button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					f2.dispose();
				}
			}
		});
	}
	
	public synchronized static void RequestSend(Socket s) //send request
	{
		JFrame f2 = new JFrame("Send Request");
		f2.setLocationRelativeTo(null);
		f2.setLayout(null);
		f2.setSize(400,400);
		JTextField t1 = new JTextField(); //input user id
		JButton b1 = new JButton("Send");
		JLabel l1 = new JLabel("Ask other user for a friend");
		JLabel l2 = new JLabel("User : ");
		b1.setBounds(140,250,100,70);
		l2.setBounds(155,180,100,20);
		t1.setBounds(205,180,20,20);
		l1.setBounds(50,100,300,70);
		l1.setFont(new Font("Arial",Font.PLAIN,24));
		b1.setFont(new Font("Arial",Font.PLAIN,18));
		l2.setFont(new Font("Arial",Font.PLAIN,18));
		
		f2.add(b1);
		f2.add(t1);
		f2.add(l1);
		f2.add(l2);
		f2.setVisible(true);
		
		b1.addActionListener(new ActionListener() { // request send button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("f" + t1.getText());
						pw.flush();
						f2.dispose();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public synchronized static void RequestReceive(Socket s, int i) // request receive pop-up
	{
		JFrame f2 = new JFrame("FriendRequest");
		f2.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f2.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println("fx"+i);
					pw.flush();
					e.getWindow().dispose();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		f2.setLocationRelativeTo(null);
		f2.setLayout(null);
		f2.setSize(400,400);
		JButton b1 = new JButton("Accept");
		JButton b2 = new JButton("Decline");
		JLabel l1 = new JLabel("User " + i + " askes you a friend");
		b1.setBounds(50,250,100,70);
		b2.setBounds(230,250,100,70);
		l1.setBounds(50,120,300,50);
		l1.setFont(new Font("Arial",Font.PLAIN,24));
		b1.setFont(new Font("Arial",Font.PLAIN,18));
		b2.setFont(new Font("Arial",Font.PLAIN,18));
		
		f2.add(b1);
		f2.add(b2);
		f2.add(l1);
		f2.setVisible(true);
		
		b1.addActionListener(new ActionListener() { //accept button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("fo"+i);
						pw.flush();
						friends.put(i,false);
						CheckifOnline(s,i);
						f2.dispose();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		b2.addActionListener(new ActionListener() { //decline button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b2)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("fx"+i);
						pw.flush();
						f2.dispose();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	static void CheckifOnline(Socket s, int i) // check if a friend is online
	{
		try {
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.println("n"+i);
			pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void ChatControl(Socket s, BufferedReader buf) //main func with states that decides what interface to be seen.
	{
		while(true)
		{
			if(state == 0)
			{
				Imagestart(s);
				state = 1;
			}
			else if(state == 2)
			{
				try {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println("r");
					pw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state = 3;
			}
			else if(state == 4)
			{
				synchronized(friends)
				{
					for(int i : friends.keySet())
						CheckifOnline(s,i);
				}
				RefreshFriends(s);
				state = 5;
			}
			else if(state == 7)
			{
				break;
			}
			else
			{
				
			}
			ReadInputChat(s,buf);
		}
	}
	
	static void ReadyChatRoom(Socket s, String str) //chatroom interface
	{
		f1.getContentPane().removeAll();
		f1.repaint();
		JTextField t1 = new JTextField();
		ta1 = new JTextArea("");
		sc1 = new JScrollPane(ta1);
		JLabel l1 = new JLabel("Chating with User "+str.charAt(1));
		JButton b1 = new JButton("Send");
		JButton b2 = new JButton("Back");
		ta1.setFont(new Font("Arial",Font.PLAIN,20));
		t1.setFont(new Font("Arial",Font.PLAIN,20));
		b1.setFont(new Font("Arial",Font.PLAIN,20));
		b2.setFont(new Font("Arial",Font.PLAIN,20));
		l1.setFont(new Font("Arial",Font.PLAIN,24));
		l1.setBounds(100,0,400,50);
		ta1.setBounds(30,100,370,500);
		sc1.setBounds(30,100,370,500);
		t1.setBounds(0,680,330,50);
		b1.setBounds(330,680,100,50);
		b2.setBounds(330,0,100,50);
		f1.add(l1);
		f1.add(b2);
		f1.add(sc1);
		f1.add(b1);
		f1.add(t1);
		f1.repaint();
		
		b1.addActionListener(new ActionListener() { //send button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("m" + t1.getText());
						pw.flush();
						t1.setText("");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		b2.addActionListener(new ActionListener() { //back button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b2)
				{
					try {
						PrintWriter pw = new PrintWriter(s.getOutputStream());
						pw.println("d" + str.charAt(1));
						pw.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	static void ReadInputChat(Socket s, BufferedReader buf)
	{
		try {
			System.out.println(state);
			String str = buf.readLine();
			System.out.println(str); //read string and do jobs with protocol
			if(str.charAt(0) == 'f')
			{
				if(str.charAt(1) == 'o')
				{
					synchronized(Client.friends)
					{
						friends.put((int)str.charAt(2)-48,false);
						CheckifOnline(s,(int)str.charAt(2)-48);
					}
				}
				else if (str.charAt(1) == 'x')
				{
					Rejected(s,str);
				}
				else
				{
					Client.RequestReceive(s, (int)str.charAt(1)-48);
				}
			}
			else if(str.charAt(0) == 'q')
			{
				state = 7;
			}
			else if(str.charAt(0) == 'o')
			{
				synchronized(Client.friends)
				{
					friends.put((int)str.charAt(1)-48,true);
					if(state == 5)
						RefreshFriends(s);
				}
			}
			else if(str.charAt(0) == 'n')
			{
				synchronized(Client.friends)
				{
					friends.put((int)str.charAt(1)-48,false);
					if(state == 5)
						RefreshFriends(s);
				}
			}
			else if(str.charAt(0) == 's')
			{
				state = 2;
			}
			else if(str.charAt(0) == 'l')
			{
				friends.put((int)str.charAt(1)-48, true);
				if(state == 5)
					RefreshFriends(s);
			}
			else if(str.charAt(0) == 'y')
			{
				ta1.setText("");
			}
			else if(str.charAt(0) == 'c')
			{
				ReadyChatRoom(s,str);
				state = 6;
			}
			else if(str.charAt(0) == 'd')
			{
				RefreshFriends(s);
				state = 5;
			}
			else if(str.charAt(0) == 'r')
			{
				friends.put((int)str.charAt(1)-48, false);
			}
			else if(str.charAt(0) == 'e')
			{
				System.out.println("friends number" + friends.size());
				state = 4;
			}
			else if(str.charAt(0) == 'a') 
			{
				if(str.charAt(1) == 'y')
					ta1.append("You" + ":" + str.substring(3,str.length()) + System.lineSeparator());
				else
					ta1.append("You" + ":" + str.substring(3,str.length()) + "(Not Read)" + System.lineSeparator()); //scroll down at message receive/send
				sc1.getVerticalScrollBar().setValue(sc1.getVerticalScrollBar().getMaximum());
				sc1.validate();
			}
			else 
			{
				ta1.append("User" + str.charAt(0) + ":" + str.substring(2,str.length()) + System.lineSeparator());
				sc1.getVerticalScrollBar().setValue(sc1.getVerticalScrollBar().getMaximum());
				sc1.validate();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void Imagestart(Socket s) //start image
	{
		f1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f1.addWindowListener(new java.awt.event.WindowAdapter() { //send quit at close (main window)
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println("q");
					pw.flush();
					e.getWindow().dispose();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		f1.setLocationRelativeTo(null);
		f1.setLayout(null);
		f1.setSize(450,800);
		JTextField t1 = new JTextField(24);

		JButton b1 = new JButton("Start");
		JLabel l1 = new JLabel("Login as User :");

		l1.setBounds(140,450,150,20);
		t1.setBounds(270,450,20,20);
		b1.setBounds(140,550,150,70);
		l1.setFont(new Font("Arial",Font.PLAIN,18));
		b1.setFont(new Font("Arial",Font.PLAIN,24));
	
		f1.add(l1);
		f1.add(t1);
		f1.add(b1);

		f1.setVisible(true);
		

		b1.addActionListener(new ActionListener() { //start button
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == b1)
				{
					PrintWriter p;
					try {
						p = new PrintWriter(s.getOutputStream());
						p.println(t1.getText() + "a");
						p.flush();
						state = 2;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
					
			}
		});
	}
	
	public static Map<Integer, Boolean> friends = new HashMap<Integer, Boolean>(); // friends from list and request
	
	public static void main(String args[])
	{
		try {
			Socket s = new Socket("147.46.241.102",20464);
			BufferedReader buf = new BufferedReader(new InputStreamReader(s.getInputStream()));
			ChatControl(s,buf); // main function of messenger
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(1);
	}
}
