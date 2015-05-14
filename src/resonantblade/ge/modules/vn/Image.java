package resonantblade.ge.modules.vn;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import resonantblade.ge.Resources;

public class Image
{
	protected List<String> tags;
	protected String url;
	protected BufferedImage image;
	
	public Image(String url, String... tags)
	{
		this.url = url;
		this.tags = Arrays.asList(tags);
		this.tags = this.tags.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
	}
	
	public Image(BufferedImage image, String... tags)
	{
		this.image = image;
		this.tags = Arrays.asList(tags);
		this.tags = this.tags.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
	}
	
	public boolean hasTags(List<String> otherTags)
	{
		for(String tag : otherTags)
			if(!tags.contains(tag))
				return false;
		return true;
	}
	
	public boolean tagsAreSame(List<String> otherTags)
	{
		otherTags = otherTags.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
		return tags.size() == otherTags.size() && hasTags(otherTags);
	}
	
	public List<String> getTags()
	{
		return tags;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public BufferedImage getImage()
	{
		if(image == null)
			image = Resources.loadImage(new File(url));
		return image;
	}
}