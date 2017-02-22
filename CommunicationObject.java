import java.io.Serializable;


public class CommunicationObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int type, row, col;
	static final int KEEP_PLAYING = 0, WON = 1, LOST = -1, CLOSE_WIN = 2, PLAYER_1 = 3, PLAYER_2 = -3, DRAW = -2;
	CommunicationObject(int type, int row, int col)
	{
		this.type = type;
		this.row = row;
		this.col = col;
	}
	
	int getRow()
	{
		return this.row;
	}
	int getCol()
	{
		return this.col;
	}
	int getType()
	{
		return this.type;
	}
}
