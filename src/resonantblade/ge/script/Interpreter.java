package resonantblade.ge.script;

import java.util.List;

public interface Interpreter
{
	List<String> getLabelHeaders();
	boolean hasLabel(String label);
	void addLabel(Label label);
	void jump(String label);
	void call(String label);
	void interpret(String label);
}