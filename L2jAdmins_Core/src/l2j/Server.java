package l2j;

/**
 * This class used to be the starter class, since LS/GS split, it only retains server mode
 */
public class Server
{
	// constants for the server mode
	public static final int MODE_NONE = 0;
	public static final int MODE_GAMESERVER = 1;
	public static final int MODE_LOGINSERVER = 2;
	
	public static int SERVER_MODE = MODE_NONE;
}
