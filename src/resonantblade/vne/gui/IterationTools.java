package resonantblade.vne.gui;

import java.util.List;
import java.util.function.Consumer;

public class IterationTools
{
	/**
	 * This method iterates through a list and ignores concurrent modification.
	 * NOTE: This method works slower on lists that you need to iterate through in order to get to any given index.
	 * @param list
	 * @param consumer
	 */
	public static <T> void forEach(List<T> list, Consumer<T> consumer)
	{
		for(int i = 0; i < list.size(); i++)
			consumer.accept(list.get(i));
	}
}