package l2j.gameserver.model.actor.status;

import l2j.gameserver.model.actor.instance.L2PetInstance;

public class PetStatus extends SummonStatus
{
	private int currentFed = 0; // Current Fed of the L2PetInstance
	
	public PetStatus(L2PetInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2PetInstance getActiveChar()
	{
		return (L2PetInstance) super.getActiveChar();
	}
	
	public int getCurrentFed()
	{
		return currentFed;
	}
	
	public void setCurrentFed(int value)
	{
		currentFed = value;
	}
}
