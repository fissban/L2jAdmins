package l2j.gameserver.handler.item;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemChristmasTree implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5560, // x-mas tree
			5561,// Special x-mas tree
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		
		NpcTemplate template = null;
		
		switch (item.getId())
		{
			case 5560:// x-mas tree
				template = NpcData.getInstance().getTemplate(12619);
				break;
			case 5561:// Special x-mas tree
				template = NpcData.getInstance().getTemplate(12620);
				break;
		}
		
		if (template == null)
		{
			return;
		}
		
		try
		{
			Spawn spawn = new Spawn(template);
			// spawn.setId(IdFactory.getInstance().getNextId());
			spawn.setX(target.getX());
			spawn.setY(target.getY());
			spawn.setZ(target.getZ());
			spawn.doSpawn();
			
			activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
			
			activeChar.sendMessage("Created " + template.getName() + " at x: " + spawn.getX() + " y: " + spawn.getY() + " z: " + spawn.getZ());
		}
		catch (Exception e)
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
		}
	}
}
