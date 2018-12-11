package l2j.gameserver.model.request;

import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.DlgAnswer.DlgAnswerType;

/**
 * @author fissban
 */
public class PcRequestDoor
{
	private L2DoorInstance door = null;
	private final L2PcInstance player;
	
	public PcRequestDoor(L2PcInstance player)
	{
		this.player = player;
	}
	
	public void setDoor(L2DoorInstance door)
	{
		this.door = door;
	}
	
	public void getDoor(int answer, DlgAnswerType type)
	{
		if (door == null)
		{
			return;
		}
		
		if ((player.getTarget() == door) && (answer == 1))
		{
			switch (type)
			{
				case OPEN:
					door.openMe();
					break;
				case CLOSE:
					door.closeMe();
					break;
			}
		}
		
		door = null;
	}
}
