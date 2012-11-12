import java.awt.Point;
import java.awt.Rectangle;

public class RectPlayer extends Rectangle
{
	private static final long serialVersionUID = 1;

	public int vel_x;
	public int vel_y;
	public int acc_x;

	private static final int gravity = 2;
	private static final int groundFriction = 4;

	private static final int MAX_VELOCITY = 15;

	private boolean onGround = false;
	private boolean skipLevel = false;

	public void update(Map m)
	{
		vel_x += acc_x;

		if (onGround)
		{
			// Friction brings our x velocity down
			if (vel_x < 0)
			{
				vel_x = Math.min((vel_x + groundFriction), 0);
			}
			else if (vel_x > 0)
			{
				vel_x = Math.max((vel_x - groundFriction), 0);
			}
		}
		else
		{
			vel_y += gravity;
		}

		// if our velocity went too far, bring it to terminal velocity
		if (vel_y > MAX_VELOCITY)
			vel_y = MAX_VELOCITY;
		else if (vel_y < -MAX_VELOCITY)
			vel_y = -MAX_VELOCITY;

		if (vel_x > MAX_VELOCITY)
			vel_x = MAX_VELOCITY;
		if (vel_x < -MAX_VELOCITY)
			vel_x = -MAX_VELOCITY;

		int newx = this.x + vel_x;
		int newy = this.y + vel_y;

		int newXCorner = newx / Map.BOX_SIZE;
		int newYCorner = newy / Map.BOX_SIZE;

		boolean lt = m.isOn(newXCorner, newYCorner);
		boolean lb = m.isOn(newXCorner, newYCorner + 1);
		boolean rt = m.isOn(newXCorner + 1, newYCorner);
		boolean rb = m.isOn(newXCorner + 1, newYCorner + 1);

		if (newx == x && newy == y)
			return;

		CollisionUpdate ret = update(newx, newy, lt, rt, lb, rb, vel_x, vel_y);

		x = ret.x;
		y = ret.y;
		vel_x = ret.vel_x;
		vel_y = ret.vel_y;

		newXCorner = x / Map.BOX_SIZE;
		newYCorner = y / Map.BOX_SIZE;

		lb = m.isOn(newXCorner, newYCorner + 1);
		rb = m.isOn(newXCorner + 1, newYCorner + 1);

		if (x % Map.BOX_SIZE == 0)
			rb = false;

		if (lb || rb)
			hitGround();
		else
			onGround = false;
		
		if (m.isGoal(newXCorner, newYCorner) ||
				m.isGoal(newXCorner+1, newYCorner) ||
				m.isGoal(newXCorner+1, newYCorner+1) ||
				m.isGoal(newXCorner, newYCorner+1) ||
				skipLevel)
		{
			m.nextMap();
			skipLevel = false;
		}
	}
	
	public void skipLevel()
	{
		skipLevel = true;
	}

	public static CollisionUpdate update(int newx, int newy, boolean lt,
			boolean rt, boolean lb, boolean rb, int vel_x, int vel_y)
	{
		int newXCorner = (newx / Map.BOX_SIZE), newYCorner = (newy / Map.BOX_SIZE);

		// Only ground worries...
		if (newy % Map.BOX_SIZE == 0 && newx % Map.BOX_SIZE == 0) // Perfect fit
		{
		}
		// Perfect fit in y, but not x.
		// Here, we have to consider only if the box is hitting a wall. If
		// it is "in" the left wall, it must have been moving leftwards. If it
		// is is "in" the right wall, it must have been moving rightwards. There
		// is no need to worry about hitting the ground or ceiling here, as our
		// terminal velocity makes this impossible.
		else if (newy % Map.BOX_SIZE == 0)
		{
			// Left block is a wall, and we're in it.
			if (lt)
			{
				vel_x = 0;
				newx = (newXCorner + 1) * Map.BOX_SIZE;
			}
			// Right block is in a wall, and we're in it.
			else if (rt)
			{
				vel_x = 0;
				newx = (newXCorner) * Map.BOX_SIZE;
			}
		}
		// Perfect fit in x, but not y.
		// Here, we only have to worry about the block being in a ceiling or
		// in a floor.
		else if (newx % Map.BOX_SIZE == 0)
		{
			// Top block is a ceiling
			if (lt)
			{
				vel_y = 0;
				newy = (newYCorner + 1) * Map.BOX_SIZE;
			}
			// Bottom block is a floor
			else if (lb)
			{
				vel_y = 0;
				newy = (newYCorner) * Map.BOX_SIZE;

				// We hit the ground -- need to turn on friction.
				// hitGround();
			}
		}
		// Otherwise, HELL CASE. In FOUR blocks. SIXTEEN different cases.
		// FML.
		//
		// +------+------+
		// | LT +-|--+RT |
		// | | | | |
		// +----|-|--|---+
		// | LB +----+RB |
		// | | |
		// +--------------
		else
		{

			// System.out.println("--");
			// System.out.println((lt ? "T" : "F") + (rt ? "T" : "F"));
			// System.out.println((lb ? "T" : "F") + (rb ? "T" : "F"));
			// System.out.println("--" + ((!(lt && rt) && (lb && rb))));

			// If ( top && !bottom )
			if ((lt && rt) && (!lb && !rb))
			{
				vel_y = 0;
				newy = (newYCorner + 1) * Map.BOX_SIZE;
			}
			// If ( !top && bottom )
			else if ((!lt && !rt) && (lb && rb))
			{
				vel_y = 0;
				newy = (newYCorner) * Map.BOX_SIZE;

				// We hit the ground. Turn on friction.
				// hitGround();
			}
			// If ( left && !right )
			else if ((lt && lb) && (!rt && !rb))
			{
				vel_x = 0;
				newx = (newXCorner + 1) * Map.BOX_SIZE;
			}
			// If (!left && right)
			else if ((!lt && !lb) && (rt && rb))
			{
				vel_x = 0;
				newx = (newXCorner) * Map.BOX_SIZE;
			}
			// If (All but one box is turned on)
			else if ((!lt && (rt && lb && rb)) || (!rt && (lt && lb && rb))
					|| (!lb && (lt && rt && rb)) || (!rb && (lt && rt && lb)))
			{
				vel_x = 0;
				vel_y = 0;

				int i = ((!rt || !rb) ? 1 : 0);
				int j = ((!rb || !lb) ? 1 : 0);

				newx = (newXCorner + i) * Map.BOX_SIZE;
				newy = (newYCorner + j) * Map.BOX_SIZE;

				// if (!lt || !rt)
				// hitGround();
			}
			// If (L -> R diagonal)
			else if ((lt && rb) && (!rt && !lb))
			{
				// If moving left, then landing in RT.
				if (vel_x < 0)
				{
					newx = (newXCorner + 1) * Map.BOX_SIZE;
					newy = (newYCorner) * Map.BOX_SIZE;

					// Hitting ground.
					// hitGround();
				}
				// Moving right, hit ceiling.
				else
				{
					newx = (newXCorner) * Map.BOX_SIZE;
					newy = (newYCorner + 1) * Map.BOX_SIZE;

					vel_y = 0;
				}
			}
			// If (R -> L diagonal)
			else if ((!lt && !rb) && (rt && lb))
			{
				// If moving right, then landing in LT
				if (vel_x > 0)
				{
					newx = (newXCorner) * Map.BOX_SIZE;
					newy = (newYCorner) * Map.BOX_SIZE;

					// Hitting ground.
					// hitGround();
				}
				// Moving left, hit ceiling.
				else
				{
					newx = (newXCorner + 1) * Map.BOX_SIZE;
					newy = (newYCorner + 1) * Map.BOX_SIZE;

					vel_y = 0;
				}
			}
			// LB is the only 'on' block
			else if (lb)
			{
				// Moving left-up diagonal. Hit a wall.
				if (vel_x < 0 || vel_y <= 0)
				{
					vel_x = 0;
					newx = (newXCorner + 1) * Map.BOX_SIZE;
				}
				// Otherwise, for now, just assume we make it onto the
				// ground.
				else
				{
					vel_y = 0;
					newy = (newYCorner) * Map.BOX_SIZE;

					// hitGround();
				}
			}
			// RB is the only on block
			else if (rb)
			{
				// Right-up diagonal move. Hit the wall.
				if (vel_x > 0 || vel_y <= 0)
				{
					vel_x = 0;
					newx = (newXCorner) * Map.BOX_SIZE;
				}
				else
				{
					vel_y = 0;
					newy = (newYCorner) * Map.BOX_SIZE;

					// hitGround();
				}
			}
			// LT is the only on block
			else if (lt)
			{
				// left-downward diagonal move
				if (vel_x < 0 || vel_y >= 0)
				{
					vel_x = 0;
					newx = (newXCorner + 1) * Map.BOX_SIZE;
				}
				else
				{
					vel_y = 0;
					newy = (newYCorner + 1) * Map.BOX_SIZE;
				}
			}
			// RT is the only on block
			else if (rt)
			{
				// right-downward diagonal move
				if (vel_x > 0 || vel_y >= 0)
				{
					vel_x = 0;
					newx = (newXCorner) * Map.BOX_SIZE;
				}
				else
				{
					vel_y = 0;
					newy = (newYCorner + 1) * Map.BOX_SIZE;
				}
			}
		}

		return new CollisionUpdate(newx, newy, vel_x, vel_y);
	}

	public void hitGround()
	{
		onGround = true;
		this.vel_y = 0;
	}

	public void hitWall()
	{
		// this.vel_x = - this.vel_x;
		this.vel_x = 0;
	}

	public void jump()
	{
		if (onGround)
		{
			vel_y = -((Map.BOX_SIZE * 5) / 6);
			onGround = false;
		}
		// otherwise, NO! You can't jump, silly guy!
	}

	public void terminalX()
	{
		if (vel_x > MAX_VELOCITY)
			vel_x = MAX_VELOCITY;
		else if (vel_x < -MAX_VELOCITY)
			vel_x = -MAX_VELOCITY;
	}

	public void hMoveDown(boolean isLeft)
	{
		acc_x = (isLeft ? -1 : 1) * (Map.BOX_SIZE / 4);
	}

	public void hMoveUp()
	{
		acc_x = 0;
	}

	/**
	 * Can only construct rectPlayers if they have a position and dimension
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */

	public RectPlayer(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.onGround = false;
		this.vel_x = 0;
		this.vel_y = 0;
	}

}
