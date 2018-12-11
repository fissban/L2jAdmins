package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.StaticObject;

/**
 * @author GODSON ROX!
 */
public class L2StaticObjectInstance extends L2Object
{
	/** The interaction distance of the L2StaticObjectInstance */
	public static final int INTERACTION_DISTANCE = 150;
	
	private int staticObjectId;
	private int type = -1; // 0 - map signs, 1 - throne , 2 - arena signs
	private int x;
	private int y;
	private String texture;
	
	/**
	 * @param objectId
	 */
	public L2StaticObjectInstance(int objectId)
	{
		super(objectId);
		
		setInstanceType(InstanceType.L2StaticObjectInstance);
	}
	
	/**
	 * @return Returns the StaticObjectId.
	 */
	public int getStaticObjectId()
	{
		return staticObjectId;
	}
	
	/**
	 * @param StaticObjectId
	 */
	public void setStaticObjectId(int StaticObjectId)
	{
		staticObjectId = StaticObjectId;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public void setMap(String texture, int x, int y)
	{
		texture = "town_map." + texture;
		this.x = x;
		this.y = y;
	}
	
	public int getMapX()
	{
		return x;
	}
	
	public int getMapY()
	{
		return y;
	}
	
	public String getTexture()
	{
		return texture;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		activeChar.sendPacket(new StaticObject(this));
	}
}
