
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;

public class POC {

	    public static void main(String args[]) {
	    	POC test = new POC();
	        test.run();
	    }

	    private static final DisplayMode POSSIBLE_MODES[] = {
	        new DisplayMode(800, 600, 32, 0),
	        new DisplayMode(800, 600, 24, 0),
	        new DisplayMode(800, 600, 16, 0),
	        new DisplayMode(640, 480, 32, 0),
	        new DisplayMode(640, 480, 24, 0),
	        new DisplayMode(640, 480, 16, 0)
	    };

	    private static final long DEMO_TIME = 10000;
	    private static final long FADE_TIME = 100;
	    
	    private Map map;
	    private RectPlayer rp;

	    private ScreenManager screen;

	    public void run() {
	        screen = new ScreenManager();
	        map = new Map(new long[1][33]);
	        rp = new RectPlayer(21,41,16,16);
	        try {
	            DisplayMode displayMode =
	                screen.findFirstCompatibleMode(POSSIBLE_MODES);
	            screen.setFullScreen(displayMode);
	            animationLoop();
	        }
	        finally {
	            screen.restoreScreen();
	        }
	    }


	    public void animationLoop() {
	        long startTime = System.currentTimeMillis();
	        long currTime = startTime;

	        while (currTime - startTime < DEMO_TIME) {
	            long elapsedTime =
	                System.currentTimeMillis() - currTime;
	            currTime += elapsedTime;

	            // update the sprites
	            update(elapsedTime);

	            // draw and update screen
	            Graphics2D g = screen.getGraphics();
	            draw(g);
	            drawFade(g, currTime - startTime);
	            g.dispose();
	            screen.update();

	            // take a nap
	            try {
	                Thread.sleep(200);
	            }
	            catch (InterruptedException ex) { }
	        }

	    }


	    public void drawFade(Graphics2D g, long currTime) {
	        long time = 0;
	        if (currTime <= FADE_TIME) {
	            time = FADE_TIME - currTime;
	        }
	        else if (currTime > DEMO_TIME - FADE_TIME) {
	            time = FADE_TIME - DEMO_TIME + currTime;
	        }
	        else {
	            return;
	        }

	        byte numBars = 8;
	        int barHeight = screen.getHeight() / numBars;
	        int blackHeight = (int)(time * barHeight / FADE_TIME);

	        g.setColor(Color.black);
	        for (int i = 0; i < numBars; i++) {
	            int y = i * barHeight + (barHeight - blackHeight) / 2;
	            g.fillRect(0, y, screen.getWidth(), blackHeight);
	        }

	    }


	    public void update(long elapsedTime) {
	    	//map.handleCollision(rp);
	    	rp.update(map);
	    }

	    public void draw(Graphics2D g) {
	    	g.setColor(Color.BLACK);
	    	g.fillRect(0,0,1000,1000);
	    	
	        map.drawOn(g);
	        g.draw(rp);
	    }

	}

