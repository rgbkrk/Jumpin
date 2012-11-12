package Utilities;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

import javax.swing.*;

public class LevelEditor extends JFrame implements MouseListener
{
	JButton[] levelButtons = new JButton[Level.gridSize * Level.gridSize];
	Level level = new Level();

	JPanel main = new JPanel();
	JPanel levelGrid = new JPanel();
	JPanel mapView = new JPanel();
	JTextArea mapOutput = new JTextArea();
	JButton save = new JButton();

	boolean viewingMap = false;

	private enum EditMode
	{
		START, GOAL, LEVEL
	}

	private EditMode mode = EditMode.LEVEL;

	public LevelEditor()
	{
		super("Level Editor");
		try
		{
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}

		setLayout(new BorderLayout());

		main.setLayout(new GridLayout(1, 1));
		levelGrid.setLayout(new GridLayout(Level.gridSize, Level.gridSize));

		this.level.setStart(1, 1);
		this.level.setGoal(this.level.gridSize - 2, this.level.gridSize - 2);

		for (int i = 0; i < Level.gridSize; i++)
		{
			for (int j = 0; j < Level.gridSize; j++)
			{
				JButton tmp = new JButton();
				tmp.addMouseListener(this);
				tmp.setName("" + ((i * Level.gridSize) + j));
				tmp.setBackground(Color.WHITE);

				levelButtons[(i * Level.gridSize) + j] = tmp;
				levelGrid.add(tmp);
				
				if (i == 0 || i == (Level.gridSize - 1) || j == 0 || j == (Level.gridSize - 1))
					level.setCell(i, j, true);
				
				renderButton(tmp, ((j * Level.gridSize) + i));
			}
		}

		main.add(levelGrid);
		add(new JScrollPane(main), BorderLayout.WEST);

		JPanel controls = new JPanel();
		controls.setLayout(new GridBagLayout());

		JRadioButton start = new JRadioButton("Select Start", false);
		JRadioButton end = new JRadioButton("Select Goal", false);
		JRadioButton level = new JRadioButton("Edit Level", true);

		start.setName("start");
		end.setName("end");
		level.setName("level");

		start.addMouseListener(this);
		end.addMouseListener(this);
		level.addMouseListener(this);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(start);
		btnGroup.add(end);
		btnGroup.add(level);

		JPanel rad = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		rad.setLayout(new GridBagLayout());
		c.gridwidth = GridBagConstraints.REMAINDER;
		rad.add(start, c);
		rad.add(end, c);
		rad.add(level, c);

		controls.add(rad, c);

		save = new JButton();
		save.setText("View Map Code");
		save.setName("save");

		save.addMouseListener(this);

		controls.add(save, c);

		add(controls);

		mapView.setLayout(new GridLayout(1, 1));
		mapView.add(mapOutput);
	}

	private void renderButton(JButton b, int index)
	{
		if (level.getGoal() == index)
			b.setBackground(Color.GREEN);
		else if (level.getStart() == index)
			b.setBackground(Color.BLUE);
		else if (level.getCell(index%level.gridSize, index/level.gridSize))
			b.setBackground(Color.RED);
		else
			b.setBackground(Color.WHITE);
	}
	
	private long[] parseMap(String map)
	{
		Scanner scan = new Scanner(map);
		
		// Strip off {
		scan.next(); //{
		
		long[] mapBits = new long[Level.gridSize+1];
		
		for (int i = 0; i < mapBits.length-1; i++)
		{
			String token = scan.next();
			mapBits[i] = Long.parseLong(token.substring(2, token.length()-2), 16);
		}
		
		String token = scan.next();
		mapBits[Level.gridSize] = Long.parseLong(token.substring(2, token.length()-1),16);
		
		return mapBits;
	}
	
	private void loadMap(long[] map)
	{
		int start = (int)(map[Level.gridSize] & 0xFFFF);
		int goal  = (int)((map[Level.gridSize] >> 16) & 0xFFFF);

		int start_y = (start / Level.gridSize);
		int start_x = (start % Level.gridSize);
		int goal_x  = (goal  % Level.gridSize);
		int goal_y  = (goal  / Level.gridSize);
		
		level.setGoal(goal_x, goal_y);
		level.setStart(start_x, start_y);
		
		for (int j = 0; j < Level.gridSize; j++)
		{
			long rowBits = map[j];
			
			for (int i = 0; i < Level.gridSize; i++)
			{
				int index = ((Level.gridSize * j) + i);
				
				level.setCell(i, j, (((rowBits >> i) & 0x1) == 0x1));
				renderButton(levelButtons[index], index);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		String name = e.getComponent().getName();

		if (name.equals("start"))
		{
			mode = EditMode.START;
		}
		else if (name.equals("end"))
		{
			mode = EditMode.GOAL;
		}
		else if (name.equals("level"))
		{
			mode = EditMode.LEVEL;
		}
		else if (name.equals("save"))
		{
			if (! viewingMap)
			{
				main.removeAll();
				main.add(mapView);
				main.invalidate();
				mapOutput.setText(level.toString());
				save.setText("Edit Level");
			}
			else
			{
				main.removeAll();
				main.add(levelGrid);
				main.invalidate();
				save.setText("View Map");
				
				loadMap(parseMap(mapOutput.getText()));
			}
			
			viewingMap = !viewingMap;
		}
		else
		{
			int index = Integer.parseInt(name);
			int x = (index % Level.gridSize), y = (index / Level.gridSize);

			JButton button = levelButtons[index];

			if (mode == EditMode.LEVEL)
			{
				if (level.getCell(x, y))
				{
					button.setBackground(Color.WHITE);
					button.setForeground(Color.WHITE);
				}
				else
				{
					button.setBackground(Color.RED);
					button.setForeground(Color.RED);
				}

				level.setCell(x, y, !level.getCell(x, y));
			}
			else if (mode == EditMode.GOAL)
			{
				int ogi = level.getGoal();
				JButton oldGoal = levelButtons[ogi];
				JButton newGoal = levelButtons[index];

				level.setGoal(x, y);

				renderButton(oldGoal, ogi);
				renderButton(newGoal, index);
			}
			else if (mode == EditMode.START)
			{
				int osi = level.getStart();
				JButton old = levelButtons[osi];
				JButton n = levelButtons[index];

				level.setStart(x, y);

				renderButton(old, osi);
				renderButton(n, index);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		LevelEditor run = new LevelEditor();

		run.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		run.setSize(1440, 900);
		run.setVisible(true);
	}
}
