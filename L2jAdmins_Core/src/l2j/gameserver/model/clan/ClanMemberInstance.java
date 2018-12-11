package l2j.gameserver.model.clan;

import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public class ClanMemberInstance
{
	private int objectId;
	private String name;
	private int level;
	private int classId;
	private L2PcInstance player;
	
	public ClanMemberInstance(String name, int level, int classId, int objectId)
	{
		this.name = name;
		this.level = level;
		this.classId = classId;
		this.objectId = objectId;
	}
	
	public ClanMemberInstance(L2PcInstance player)
	{
		setPlayerInstance(player);
	}
	
	public void setPlayerInstance(L2PcInstance player)
	{
		if ((this.player == null) && (player != null))
		{
			// this is here to keep the data when the player logs off
			name = player.getName();
			level = player.getLevel();
			classId = player.getClassId().getId();
			objectId = player.getObjectId();
		}
		
		this.player = player;
	}
	
	public L2PcInstance getPlayerInstance()
	{
		return player;
	}
	
	public boolean isOnline()
	{
		if (player == null)
		{
			return false;
		}
		
		if (player.getPrivateStore().inOfflineMode())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return Returns the classId.
	 */
	public int getClassId()
	{
		if (player != null)
		{
			return player.getClassId().getId();
		}
		return classId;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		if (player != null)
		{
			return player.getLevel();
		}
		return level;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		if (player != null)
		{
			return player.getName();
		}
		return name;
	}
	
	/**
	 * @return Returns the objectId.
	 */
	public int getObjectId()
	{
		if (player != null)
		{
			return player.getObjectId();
		}
		return objectId;
	}
	
	public String getTitle()
	{
		if (player != null)
		{
			return player.getTitle();
		}
		return " ";
	}
}
