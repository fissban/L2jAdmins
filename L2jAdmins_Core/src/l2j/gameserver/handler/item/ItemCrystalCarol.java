package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;

/**
 * This class ...
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */
public class ItemCrystalCarol implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5562,
			5563,
			5564,
			5565,
			5566,
			5583,
			5584,
			5585,
			5586,
			5587,
			4411,
			4412,
			4413,
			4414,
			4415,
			4416,
			4417,
			5010,
			6903,
			7061,
			7062
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		switch (item.getId())
		{
			case 5562:// crystal_carol_01
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2140, 1, 1, 0));
				break;
			case 5563:// crystal_carol_02
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2141, 1, 1, 0));
				break;
			case 5564:// crystal_carol_03
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2142, 1, 1, 0));
				break;
			case 5565:// crystal_carol_04
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2143, 1, 1, 0));
				break;
			case 5566:// crystal_carol_05
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2144, 1, 1, 0));
				break;
			case 5583: // crystal_carol_06
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2145, 1, 1, 0));
				break;
			case 5584:// crystal_carol_07
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2146, 1, 1, 0));
				break;
			case 5585:// crystal_carol_08
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2147, 1, 1, 0));
				break;
			case 5586:// crystal_carol_09
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2148, 1, 1, 0));
				break;
			case 5587:// crystal_carol_10
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2149, 1, 1, 0));
				break;
			case 4411:// crystal_journey
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2069, 1, 1, 0));
				break;
			case 4412:// crystal_battle
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2068, 1, 1, 0));
				break;
			case 4413:// crystal_love
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2070, 1, 1, 0));
				break;
			case 4414:// crystal_solitude
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2072, 1, 1, 0));
				break;
			case 4415:// crystal_festival
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2071, 1, 1, 0));
				break;
			case 4416:// crystal_celebration
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2073, 1, 1, 0));
				break;
			case 4417:// crystal_comedy
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2067, 1, 1, 0));
				break;
			case 5010:// crystal_victory
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2066, 1, 1, 0));
				break;
			case 6903:// music_box_m
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2187, 1, 1, 0));
				break;
			case 7061:// crystal_birthday
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2073, 1, 1, 0));
				break;
			case 7062:// crystal_wedding
				activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2230, 1, 1, 0));
		}
		
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
	}
}
