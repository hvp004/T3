public class T3Controller
{
	int[] track;
	int count;
	public T3Controller()
	{
		track = new int[8];
		count = 0;
		System.out.println("Tracking initialized");
	}
	public int determineState(int player, int row, int col)
	{
		++count;
		this.track[row] = this.track[row] + (player / 3);
		this.track[col + 3] = this.track[col + 3] + (player / 3);
		if(row == col)
		{	
			this.track[6] = this.track[6] + (player/3);
		}	
		if(row + col == 2)
			this.track[7] = this.track[7] + (player/3);
		//System.out.println(this.track[0]);
		for(int i = 0; i < this.track.length; i++)
		{
			if(track[i] == 3 || track[i] == -3)
			{
				return CommunicationObject.WON;
			}
		}
		if(count == 5 && player == CommunicationObject.PLAYER_1)
			return CommunicationObject.DRAW;
		//System.out.println(count);
		return CommunicationObject.KEEP_PLAYING;
	}
}
