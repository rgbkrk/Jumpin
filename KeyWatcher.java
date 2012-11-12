import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyWatcher implements KeyListener
{
	private RectPlayer rp;

	public KeyWatcher(RectPlayer rp)
	{
		this.rp = rp;
	}

	// Invoked when a key has been pressed.
	public void keyPressed(KeyEvent e)
	{
		// System.out.println("In " + "keyPressed " + e.getKeyCode());
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:
		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_SPACE:
			// System.out.println("Pressed up");
			rp.jump();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_KP_RIGHT:
			// System.out.println("Pressed right");
			rp.hMoveDown(false);
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_KP_LEFT:
			// System.out.println("Pressed left");
			rp.hMoveDown(true);
			break;
		default:
			// System.out.println("What character is that?");
		}
	}

	// Invoked when a key has been released.
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_KP_RIGHT:
			// System.out.println("Pressed right");
			rp.hMoveUp();
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_KP_LEFT:
			// System.out.println("Pressed left");
			rp.hMoveUp();
			break;
		default:
			// System.out.println("What character is that?");
		}
	}

	// Invoked when a key has been typed.
	public void keyTyped(KeyEvent e)
	{
		switch(e.getKeyChar())
		{
		case 's':
			rp.skipLevel();
			break;
		}
	}
}
