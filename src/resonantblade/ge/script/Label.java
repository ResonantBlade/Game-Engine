package resonantblade.ge.script;

public class Label
{
	public final String header;
	public final String name;
	public final String extraHeaderData;
	public final String[] data;
	
	public Label(String header, String name, String extraHeaderData, String data)
	{
		this.header = header;
		this.name = name;
		this.extraHeaderData = extraHeaderData;
		this.data = data.split("\n");
	}
	
	protected Label(String header, String name, String extraHeaderData, String[] data)
	{
		this.header = header;
		this.name = name;
		this.extraHeaderData = extraHeaderData;
		this.data = data;
	}
}