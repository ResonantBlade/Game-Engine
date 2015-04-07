package resonantblade.vne;

public class Properties
{
	public static final int MAJOR_REVISION = 0;
	public static final int MINOR_REVISION = 1;
	
	public static String getVersion()
	{
		return MAJOR_REVISION + "." + MINOR_REVISION;
	}
	
	public static void setGUITitle(String name)
	{
		if(VisualNovelEngine.gui != null)
			VisualNovelEngine.gui.setTitle(name);
	}
}