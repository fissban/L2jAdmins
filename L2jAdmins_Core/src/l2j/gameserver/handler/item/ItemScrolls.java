package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.6.4 $ $Date: 2005/04/06 18:25:18 $
 */
public class ItemScrolls implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			3926,
			3927,
			3928,
			3929,
			3930,
			3931,
			3932,
			3933,
			3934,
			3935,
			4218,
			5593,
			5594,
			5595,
			
			6037,
			5703,
			5803,
			5804,
			5805,
			5806,
			5807,
			6652,
			6655
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar = playable.getActingPlayer();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isAllSkillsDisabled())
		{
			
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		int itemId = item.getId();
		if ((itemId == 5703) || ((itemId >= 5803) && (itemId <= 5807)))
		{
			if (((itemId == 5703) && (activeChar.getExpertiseIndex() == 0)) || // Lucky Charm (No Grade)
				((itemId == 5803) && (activeChar.getExpertiseIndex() == 1)) || // Lucky Charm (D Grade)
				((itemId == 5804) && (activeChar.getExpertiseIndex() == 2)) || // Lucky Charm (C Grade)
				((itemId == 5805) && (activeChar.getExpertiseIndex() == 3)) || // Lucky Charm (B Grade)
				((itemId == 5806) && (activeChar.getExpertiseIndex() == 4)) || // Lucky Charm (A Grade)
				((itemId == 5807) && (activeChar.getExpertiseIndex() == 5))) // Lucky Charm (S Grade)
			{
				if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
				{
					return;
				}
				
				activeChar.broadcastPacket(new MagicSkillUse(playable, playable, 2168, activeChar.getExpertiseIndex() + 1, 1, 0));
				useScroll(activeChar, 2168, activeChar.getExpertiseIndex() + 1);
				activeChar.setCharmOfLuck(true);
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addItemName(item.getId()));
			}
			return;
		}
		
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
		{
			return;
		}
		
		int skillId = 0;
		switch (itemId)
		{
			case 3926:// Scroll of Guidance XML:2050
				skillId = 2050;
				break;
			case 3927:// Scroll of Death Whipser XML:2051
				skillId = 2051;
				break;
			case 3928:// Scroll of Focus XML:2052
				skillId = 2052;
				break;
			case 3929:// Scroll of Greater Acumen XML:2053
				skillId = 2053;
				break;
			case 3930:// Scroll of Haste XML:2054
				skillId = 2054;
				break;
			case 3931:// Scroll of Agility XML:2055
				skillId = 2055;
				break;
			case 3932:// Scroll of Mystic Enpower XML:2056
				skillId = 2056;
				break;
			case 3933:// Scroll of Might XML:2057
				skillId = 2057;
				break;
			case 3934: // Scroll of Wind Walk XML:2058
				skillId = 2058;
				break;
			case 3935: // Scroll of Shield XML:2059
				skillId = 2059;
				break;
			case 4218: // Scroll of Mana Regeneration XML:2064
				skillId = 2064;
				break;
			case 6037: // Scroll of Waking XML:2170
				skillId = 2170;
				break;
			case 6652: // Amulet: Protection of Valakas XML:2231
				skillId = 2231;
				break;
			case 6655: // Amulet: Slay Valakas XML:2232
				skillId = 2232;
				break;
			case 5593:
			case 5594:
			case 5595: // SP Scrolls
				int amountSP = 0;
				
				switch (itemId)
				{
					case 5593: // Low Grade
						amountSP = 500;
						break;
					case 5594: // Medium Grade
						amountSP = 5000;
						break;
					case 5595: // High Grade
						amountSP = 100000;
						break;
				}
				skillId = 2167;
				activeChar.addExpAndSp(0, amountSP);
				break;
		}
		
		activeChar.broadcastPacket(new MagicSkillUse(playable, playable, skillId, 1, 1, 0));
		useScroll(activeChar, skillId, 1);
	}
	
	private static void useScroll(L2PcInstance activeChar, int magicId, int level)
	{
		Skill skill = SkillData.getInstance().getSkill(magicId, level);
		if (skill != null)
		{
			activeChar.doCast(skill);
		}
	}
}
