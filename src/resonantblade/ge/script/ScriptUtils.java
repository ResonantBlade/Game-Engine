package resonantblade.ge.script;

public class ScriptUtils
{
	public static String cleanComments(String line)
	{
		int index = 0;
		while(index < line.length())
		{
			while(index < line.length() && line.charAt(index) != '#')
				index++;
			
			if(index == line.length())
				return line;
			
			Quote q = nextQuote(line, 0);
			while(q != null && q.endIndex < index)
				q = nextQuote(line, q.endIndex + 1);
			
			if(q == null || q.startIndex > index)
				break;
		}
		
		return line.substring(0, index);
	}
	
	public static Quote nextQuote(String str, int startIndex, int endIndex)
	{
		if(endIndex - startIndex < 2)
			return null;
		
		int startQuoteIndex = str.indexOf('"', startIndex);
		if(startQuoteIndex == -1 || startQuoteIndex >= endIndex)
			return null;
		
		int endQuoteIndex = str.indexOf('"', startQuoteIndex + 1);
		while(endQuoteIndex != -1)
		{
			int numEscapes = 0;
			while(str.charAt(endQuoteIndex - 1 - numEscapes) == '\\')
				numEscapes++;
			
			if(numEscapes % 2 == 0)
				break;
			
			endQuoteIndex = str.indexOf('"', endQuoteIndex + 1);
		}
		
		if(endQuoteIndex == -1)
			return null;
		return new Quote(str.substring(startQuoteIndex + 1, endQuoteIndex), startQuoteIndex, endQuoteIndex);
	}
	
	public static Quote nextQuote(String str, int startIndex)
	{
		return nextQuote(str, startIndex, str.length());
	}
	
	public static Quote nextQuote(String str)
	{
		return nextQuote(str, 0);
	}
	
	public static class Quote
	{
		public final String quoteText;
		public final int startIndex;
		public final int endIndex;
		
		public Quote(String quoteText, int startIndex, int endIndex)
		{
			this.quoteText = quoteText;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
	}
}