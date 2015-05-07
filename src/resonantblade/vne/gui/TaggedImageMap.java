package resonantblade.vne.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaggedImageMap
{
	private HashMap<String, List<Image>> map = new HashMap<String, List<Image>>();
	
	public int size()
	{
		return map.values().stream().mapToInt(List::size).sum();
	}
	
	public boolean isEmpty()
	{
		return map.isEmpty();
	}
	
	public Image get(String key, List<String> tags)
	{
		List<Image> images = map.get(key);
		return images.stream().filter(img -> img.tagsAreSame(tags)).findFirst().orElseThrow(() -> new IllegalStateException("No image found for key: " + key + ", tags: " + tags));
	}
	
	public Image put(String key, Image value)
	{
		List<Image> images = map.get(key);
		if(images == null)
		{
			images = new ArrayList<Image>();
			map.put(key, images);
		}
		
		Image ret = images.stream().filter(img -> img.tagsAreSame(value.getTags())).findFirst().orElse(null);
		images.remove(ret);
		images.add(value);
		
		return ret;
	}
	
	public void clear()
	{
		map.clear();
	}
}