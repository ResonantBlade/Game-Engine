package resonantblade.ge.gui;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class FPSController
{
    public static final long REFRESH_INTERVAL_MS;
    private final Object redrawLock = new Object();
    private GUI gui;
    private boolean keepGoing = true;
    
    static
    {
    	GraphicsDevice[] gda = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    	int refreshRate = 0;;
    	for(GraphicsDevice gd : gda)
    	{
    		DisplayMode dm = gd.getDisplayMode();
    		refreshRate = Math.max(refreshRate, dm.getRefreshRate());
    	}
    	
		REFRESH_INTERVAL_MS = (long) Math.ceil(1000.0D / (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN ? 60 : refreshRate));
    }
    
    public void start(GUI gui)
    {
        this.gui = gui;
        new Thread(() -> runGameLoop()).start();
    }
    
    public void stop()
    {
        keepGoing = false;
    }
    
    private void runGameLoop()
    {
        // update the game repeatedly
        while(keepGoing)
        {
            long durationMs = redraw();
            if(REFRESH_INTERVAL_MS - durationMs > 0)
            	try{Thread.sleep(REFRESH_INTERVAL_MS - durationMs);}catch(InterruptedException e){}
        }
    }
    
    private long redraw()
    {
        long t = System.currentTimeMillis();
        
        // At this point perform changes to the model that the component will
        // redraw
        
        gui.updateVisible();
        
        // draw the model state to a buffered image which will get
        // painted by component.paint().
        gui.drawToImageBuffer();
        
        // asynchronously signals the paint to happen in the awt event
        // dispatcher thread
        //gui.repaint(); MOVED INTO waitForPaint METHOD
        
        // use a lock here that is only released once the paintComponent
        // has happened so that we know exactly when the paint was completed and
        // thus know how long to pause till the next redraw.
        waitForPaint();
        
        // return time taken to do redraw in ms
        return System.currentTimeMillis() - t;
    }
    
    private void waitForPaint()
    {
        try
        {
            synchronized(redrawLock)
            {
            	gui.repaint();
                redrawLock.wait();
            }
        }
        catch(InterruptedException e) {}
    }
    
    public void resume()
    {
        synchronized(redrawLock)
        {
            redrawLock.notifyAll();
        }
    }
}