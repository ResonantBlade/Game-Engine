package resonantblade.vne.script;

public class Label
{
	public final String header;
	public final String name;
	public final String[] data;
	public Label next;
	
	public Label(String header, String name, String data)
	{
		this.header = header;
		this.name = name;
		this.data = data.split("\n");
		next = null;
	}
}