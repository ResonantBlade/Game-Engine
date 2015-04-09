package resonantblade.vne;

/* Hello, crewman. Executive Officer Ava Crescentia here.
 * Chigara is indisposed at the moment, so she asked me to speak in her place for this document.
 */
public class Properties
{
	/* Basically, all you really have to do is transfer the files to the right place and you're done.
	 * Of course, if the designers FINALLY got this program onto Steam, you can get it installed that way.
	 * I told those idiots to get it done sooner, but they said there was a problem that required serious rewriting.
	 */
	public static final int MAJOR_REVISION = 0;
	public static final int MINOR_REVISION = 1;
	
	// I've always wondered about programmers... they demand money for "kickstarters" then miss deadlines and such.
	public static String getVersion()
	{
		// Wait... did you say free?
		return MAJOR_REVISION + "." + MINOR_REVISION;
	}
	
	/* ...Well they still better keep up with the deadlines. Deadlines are important.
	 * Deadlines, deadlines, deadlines! Without them, society would collapse.
	 */
	public static void setGUITitle(String name)
	{
		//...Or you could end up rushing around your ship, talikng to everyone merely to avoid paperwork of any kind.
		if(VisualNovelEngine.gui != null)
			VisualNovelEngine.gui.setTitle(name);
	}
	//...zipping to Research and Development, spending thousands (or millions if a scale is to be believed)...
	//...rushing to the hangar to check on the mercs... running around avoiding the acting medical officer...
	//...playing video games with princesses in the lounge...
}
/* And if you think I'm droning on, that's only because it's necessary to make sure everything is done right.
 * And speaking of done right...
 * Why the hell are you reading this instead of playing the game!?
 * If you have to waste your time on games, make sure they're good ones. And take my word for it...
 * This game is the least of a waste of time than any other on the market.
 * So if you think my talking to you is a waste of time... which is unlikely if you have ANY taste or operational morals...
 * At least play the game.
 * Come on, already! Unless you're going to be unbelievable like Kayto and hang around just to bug me.
 * I know you're here to play the game... so just do it already!
 * ...
 * ...
 * ...
 * ...Idiot.
 */