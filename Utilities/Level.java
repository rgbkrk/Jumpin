package Utilities;

/**
 * Produces a string representation of the map (in hex).
 *
 * - [0,2] Start position. x = s%16, y = s/16
 * - [3,4] Goal position.  x = s%16, y = s/16
 * - [5,6) (x,y) is on if s = 1, off if s = 0
 * - ...
 * - (261,260] (x,y) is on if s = 1, off if s = 0
 * 
 * @author mullins
 */
public class Level
{
	public static final int gridSize = 32;
	private boolean[][] grid = new boolean[gridSize][gridSize];
	
	private int start = 0;
	private int goal = 0;
	
	public Level()
	{
		
	}
	
	public void setCell(int x, int y, boolean on)
	{
		grid[y][x] = on;
	}
	
	public boolean getCell(int x, int y)
	{
		return grid[y][x];
	}

	public int getStart()
	{
		return start;
	}

	public void setStart(int x, int y)
	{
		this.start = (y * gridSize) + x;
	}

	public int getGoal()
	{
		return goal;
	}

	public void setGoal(int x, int y)
	{
		this.goal = (y * gridSize) + x;
	}
	
	public String toString()
	{
		int w[] = new int[gridSize+1];
		
		String s = "{";
		
		for (int x = 0; x < gridSize; x++)
		{
			for (int y = 0; y < gridSize; y++)
			{
				int bit = (grid[x][y] ? 1 : 0);
				
				w[x] |= ((bit & 0x1) << y);
			}
		}
		
		w[gridSize] = (((goal & 0xFFFF) << 16) | (start & 0xFFFF));
		
		for (int x = 0; x < gridSize+1; x++)
		{
			s += " 0x";
			
			for (int y = 0; y < gridSize; y += 4)
			{
				s += Integer.toHexString((w[x] >> (gridSize - (y + 4))) & 0xF);
			}
			
			s += "L";
			
			if (x < gridSize)
				s += ",";
			
			if (x % 8 == 0)
				s += "\n";
		}
		
		s += " },";
		
		return s;
	}
	
	public static void main(String[] args)
	{
		Level l = new Level();
		
		l.setCell(0, 0, true);
		l.setCell(1, 0, true);
		l.setCell(5, 0, true);
		
		System.out.println(l);
	}
}
