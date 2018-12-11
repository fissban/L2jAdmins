package l2j.gameserver.model.holder;

/**
 * @author Rayan RPG, fissban
 */
public class NpcWalkerHolder
{
	private final int npcId;
	private final String chatText;
	private final int moveX;
	private final int moveY;
	private final int moveZ;
	private final int delay;
	
	private boolean running;
	
	public NpcWalkerHolder(int npcId, String chatText, int moveX, int moveY, int moveZ, boolean running, int delay)
	{
		this.npcId = npcId;
		this.chatText = chatText;
		this.moveX = moveX;
		this.moveY = moveY;
		this.moveZ = moveZ;
		this.delay = delay;
	}
	
	public int getNpcId()
	{
		return npcId;
	}
	
	public String getChatText()
	{
		return chatText;
	}
	
	public int getMoveX()
	{
		return moveX;
	}
	
	public int getMoveY()
	{
		return moveY;
	}
	
	public int getMoveZ()
	{
		return moveZ;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public boolean getRunning()
	{
		return running;
	}
}
