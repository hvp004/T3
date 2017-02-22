import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Client extends Frame
{
	ObjectOutputStream oout;
	ObjectInputStream oin;
	Socket socket;
	boolean turn;
	boolean keepchatting;
	T3Controller controller;
	Client()
	{
		super("Player 2", false);
		turn = false;
		keepchatting = false;
		controller = new T3Controller();
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(this.turn)
		{	
			this.setLabel("Player 1's Turn!");
			JButton tile_pressed = (JButton) ae.getSource();
			int row = (int) tile_pressed.getClientProperty("row");
			int col = (int) tile_pressed.getClientProperty("column");
			//tile[row][col].setEnabled(false);
			int state = this.controller.determineState(CommunicationObject.PLAYER_2,row, col);
			
			CommunicationObject co = new CommunicationObject(state, row, col);
			this.disableButton(co, "X");
			try 
			{
				this.sendMessage(co);
			} 
			catch (IOException e)
			{
				//System.out.println("Can't write");
				e.printStackTrace();
			}
			if(state == CommunicationObject.WON)
			{
				this.setLabel("You Won!");
				this.keepchatting = false;
			}
			this.turn = false;
			//System.out.println("Completed!");
		}
	}
	public void sendMessage(CommunicationObject co) throws IOException
	{
		this.oout.writeObject(co);
		this.oout.flush();
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
	public void windowClosing(WindowEvent arg0)
	{
		System.out.println("Closing..");
		this.keepchatting = false;	
		if(this.keepchatting)
		{	
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
	}
	private void closeStream() throws IOException
	{
		this.oout.close();
		this.oin.close();
	}
	protected void setUpTile()
	{
		super.setUpTile();
	}
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException
	{
		Client client = new Client();
		try
		{
		client.socket = new Socket("localhost", 6066);
		client.oout=new ObjectOutputStream(client.socket.getOutputStream()); 
		client.oout.flush();
		client.oin = new ObjectInputStream(client.socket.getInputStream());
		client.setUpTile();
		CommunicationObject co = null; 
		client.keepchatting = true;
		while(client.keepchatting)
		{
			co = (CommunicationObject) client.oin.readObject();
			if(co.getType() != CommunicationObject.KEEP_PLAYING)
			{	
				client.turn = false;
				//System.out.println(co.getType());
				client.keepchatting = false;
				break;
			}	
			else
			{	
				client.setLabel("Your turn!");
				client.disableButton(co, "O");
			}	
			client.turn = true;
		//System.out.println(client.turn);
		}
		
		//System.out.println("Server Disconnected!");
		//System.out.println("Out of loop"+ co.getType());
		if(co.getType() == CommunicationObject.WON)
		{
			client.disableButton(co, "O");
			client.setLabel("You Lost!");
		}
		else if(co.getType() == CommunicationObject.LOST)
		{
			client.setLabel("You Won!");
		}
		else if(co.getType() == CommunicationObject.DRAW)
		{
			client.setLabel("Match Drawn!");
			client.disableButton(co, "O");
		}
		else
		{
			client.setLabel("Player 1 Left! You Won!");
		}
		client.closeStream();
		client.closeServer();
		}
		catch(EOFException e){System.out.println("bye");}
		catch(ConnectException ce)
		{
			client.setLabel("Server is not available. Try again later.");
		}
		catch(SocketException se)
		{
			client.setLabel("Player 1 Left! You Won!");
			client.keepchatting = false;
		}
		catch(IOException ioe)
		{
			//ioe.printStackTrace();
			System.out.println("Something Went Wrong!");
		}
	}
}
