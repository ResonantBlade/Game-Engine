package resonantblade.vne.script;

import java.util.List;

public interface Interpreter
{
	List<String> getLabelHeaders();
	boolean hasLabel(String label);
	void jump(String label);
}