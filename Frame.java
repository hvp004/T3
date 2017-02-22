import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Frame implements WindowListener, ActionListener
{
	Container pane;
	JPanel panel;
	JFrame frame;
	JButton[][] tile;
	JLabel label;
	Frame(String playerN, boolean wait)
	{
		frame = new JFrame(playerN);
		pane = frame.getContentPane();
		label = new JLabel("", SwingConstants.CENTER);
		label.setSize(10, 100);
		panel = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(label, BorderLayout.NORTH);
		pane.add(panel, BorderLayout.SOUTH);
		frame.addWindowListener(this);
		panel.setLayout(new GridLayout(3,3));
		frame.setSize(400,400);
		if(!wait)
		{
			this.label.setText("Connecting To The Server");
			this.frame.add(label);
			//this.setUpTile();
		}
		else
		{
			this.label.setText("Waiting For Other Player");
			this.frame.add(label);
		}
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
	}
	protected void setUpTile()
	{
		//System.out.println("Super Called!");
		this.label.setText("Connected");
		this.tile = new JButton[3][3];
		for(int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				this.tile[i][j] = new JButton(new ImageIcon("O.png"));
				this.tile[i][j].setPreferredSize(new Dimension(100, 100));
				this.tile[i][j].addActionListener(this);
				this.tile[i][j].putClientProperty("row", i);
				this.tile[i][j].putClientProperty("column", j);
				this.panel.add(tile[i][j]);
			}
		}
		this.frame.setVisible(true);
	}
	protected void setLabel(String text)
	{	
		this.label.setText(text);
	}
	public void actionPerformed(ActionEvent ae){}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowOpened(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0)
	{
		System.out.println("Closing..");
		this.frame.dispose();
		System.exit(0);
	}
}
