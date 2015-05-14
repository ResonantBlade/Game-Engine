package resonantblade.ge.script;

public class LabelIterator
{
	private Label label;
	private int index;
	
	public LabelIterator(Label label)
	{
		this.label = label;
		index = -1;
	}
	
	public boolean hasNextLine()
	{
		return index < label.data.length - 1;
	}
	
	public String nextLine()
	{
		return label.data[++index];
	}
	
	public String currentLine()
	{
		return index < 0 ? null : label.data[index];
	}
	
	public boolean rollback()
	{
		if(index >= 0)
		{
			index--;
			return true;
		}
		return false;
	}
}