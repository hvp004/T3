import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Server extends Frame
{
	ServerSocket serversocket;
	Socket socket;
	ObjectOutputStream oout;
	ObjectInputStream oin;
	boolean keepchatting;
	boolean turn;
	T3Controller controller;
	Server()
	{
		super("Player 1", true);
		turn = true;
		keepchatting = false;
		controller = new T3Controller();
	}
	public void actionPerformed(ActionEvent ae)
	{
		if(this.turn)
		{	
			this.setLabel("Player 2's Turn!");
			JButton tile_pressed = (JButton) ae.getSource();
			int row = (int) tile_pressed.getClientProperty("row");
			int col = (int) tile_pressed.getClientProperty("column");
			int state = this.controller.determineState(CommunicationObject.PLAYER_1,row, col);
			CommunicationObject co = new CommunicationObject(state,row, col);
			this.disableButton(co, "O");
			try 
			{
				this.sendMessage(co);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			this.turn = false;
			if(state == CommunicationObject.WON)
			{
				this.setLabel("You Won!");
				this.keepchatting = false;
			}
			if(state == CommunicationObject.DRAW)
			{
				this.setLabel("Match Drawn!");
				this.keepchatting = false;
			}
		}
	}
	public void sendMessage(CommunicationObject co) throws IOException
	{
		//System.out.println("from sendmessage"+this.oout);
		this.oout.writeObject(co);
		this.oout.flush();
		//System.out.println("MSG SEND");
	}
	public void disableButton(CommunicationObject co, String sign)
	{
		int row = co.getRow();
		int col = co.getCol();
		String resource = "resources/X.png";
		if(sign == "O")
		{
			resource = "resources/O.png";
		}
		ImageIcon img = new ImageIcon(getClass().getResource(resource));
		tile[row][col].setIcon(img);
		tile[row][col].setEnabled(false);
	}
	private void startStream() throws IOException
	{
		this.oout=new ObjectOutputStream(this.socket.getOutputStream()); 
		this.oout.flush();
		this.oin = new ObjectInputStream(this.socket.getInputStream());
		this.keepchatting = true;
	}
	private void startConnection() throws IOException
	{
		this.serversocket = new ServerSocket(6066);
		this.socket = this.serversocket.accept();
		System.out.println("Connection Estrablished!");
		//this.keepchatting = true;
		this.setUpTile();
		this.setLabel("Your Turn");
	}
	protected void setUpTile()
	{
		super.setUpTile();
	}
	public void windowClosing(WindowEvent arg0)
	{
		System.out.println("Closing..");
		if(this.keepchatting)
		{
			this.keepchatting = false;
			CommunicationObject co = new CommunicationObject(CommunicationObject.CLOSE_WIN, -1, -1);
			try
			{
				this.sendMessage(co);
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			try
			{
				this.closeStream();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try 
			{
				this.closeServer();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		this.frame.dispose();
		System.exit(0);
	}
	private void closeServer() throws IOException
	{	
		this.socket.close();
		this.serversocket.close();
	}
	private void closeStream() throws IOException
	{
		this.oout.flush();
		this.oout.close();
		this.oin.close();
	}
	public static void main(String[] args) throws ClassNotFoundException
	{
		Server server = new Server();
		try
		{
		server.startConnection();
		server.startStream();
		CommunicationObject co = null;
		while(server.keepchatting)
		{
			co = (CommunicationObject) server.oin.readObject();
			if(co.getType() != CommunicationObject.KEEP_PLAYING)
			{	
				server.turn = false;
				//System.out.println(co.getType());
				server.keepchatting = false;
				break;
			}	
			else
			{	
				server.setLabel("Your turn!");
				server.disableButton(co, "X");
				server.turn = true;
			}	
			//server.turn = true;
			//System.out.println(server.turn);
		}
		//System.out.println("Out of loop: "+ co.getType());
		if(co.getType() == CommunicationObject.WON)
		{
			server.disableButton(co, "X");
			server.setLabel("You Lost!");
		}
		else if(co.getType() == CommunicationObject.LOST)
		{
			server.setLabel("You Won!");
		}
		else
		{
			server.setLabel("Player 2 Left! You Won!");
		}
		server.closeStream();
		server.closeServer();
		}
		catch(EOFException e){System.out.println("bye");}
		catch(SocketException se)
		{
			server.setLabel("Player 2 Left! You Won!");
			server.keepchatting = false;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
