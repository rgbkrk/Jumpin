import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Map
{
	private static final int width = 32;
	private static final int height = 32;

	public static final int BOX_SIZE = 24;
	public static RectPlayer bluebie;

	// Will want to set a zoom factor as well

	private long[][] maps;
	private boolean[][] locations;
	private int goal_x;
	private int goal_y;
	private int currentMap = 0;

	public boolean isOn(int x, int y)
	{
		// j is x
		// i is y
		// Within the game board
		if ((0 <= x && x < width) && (0 <= y && y < height))
			return locations[y][x];
		// Outside game board
		return true;
	}

	public boolean isGoal(int x, int y)
	{
		return (goal_x == x && goal_y == y);
	}

	/* Dummy Map with tiles on or off */
	public Map(long[][] maps)
	{
		this.maps = maps;
		loadMap(maps[0]);
	}

	private void loadMap(long[] map)
	{
		locations = new boolean[width][height];

		for (int i = 0; i < width; i++)
		{
			long rowBits = map[i];

			for (int j = 0; j < height; j++)
			{
				locations[i][j] = (((rowBits >> j) & 0x1) == 0x1);
			}
		}

		int start = ((int) (map[width] & 0xFFFF));
		int goal = ((int) (map[width] >> 16) & 0xFFFF);

		goal_x = (goal % width);
		goal_y = (goal / width);

		bluebie.x = (start % width) * Map.BOX_SIZE;
		bluebie.y = (start / width) * Map.BOX_SIZE;

		System.out.println(start);
	}

	public void nextMap()
	{
		loadMap(maps[++currentMap]);
	}

	public void drawOn(Graphics2D g)
	{

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
			{
				if (locations[i][j])
					g.setColor(Color.RED);
				else if (isGoal(j, i))
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.WHITE);

				g.fillRect(j * BOX_SIZE, i * BOX_SIZE, BOX_SIZE, BOX_SIZE);
			}

	}

	public static void main(String[] args) throws Exception
	{
		JFrame jframe = new JFrame("Test!");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);

		// final RectPlayer rp = new
		// RectPlayer(BOX_SIZE*6,BOX_SIZE*3,BOX_SIZE,BOX_SIZE);
		bluebie = new RectPlayer(BOX_SIZE * 4 + 15, BOX_SIZE * 2, BOX_SIZE,
				BOX_SIZE);
		final Map m = new Map(MapBits.maps);

		long[] map = MapBits.maps[0];

		int start = (int) (map[width] & 0xFFFF);

		JPanel panel = new JPanel()
		{
			private static final long serialVersionUID = -403250971215465050L;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				m.drawOn((Graphics2D) g);
				g.setColor(Color.BLUE);
				((Graphics2D) g).fill(bluebie);
			}

		};

		panel.setFocusable(true); // Needed to know we are handling the key
		// events
		panel.addKeyListener(new KeyWatcher(bluebie));

		jframe.add(panel);
		jframe.setSize((width + 2) * BOX_SIZE, (height + 2) * BOX_SIZE);
		panel.repaint();

		while (true)
		{
			bluebie.update(m);
			panel.repaint();
			Thread.sleep(50);
		}
	}
}
