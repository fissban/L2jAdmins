package l2j.gameserver.handler.item;

import java.util.List;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.handler.SkillHandler;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */

public class ItemPotions implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			65,
			725,
			726,
			727,
			728,
			731, // Endeavor Potion
			734,
			735,
			1060,
			1061,
			1062,
			1073,
			1374,
			1375,
			1539,
			1540,
			4679, // Bless of Eva'
			5234,
			5283,
			5591,
			5592,
			6035,
			6036
		};
	}
	
	@Override
	public synchronized void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar = playable.getActingPlayer();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		int itemId = item.getId();
		
		if (!activeChar.tryToUseItem(item))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addItemName(itemId));
			return;
		}
		
		switch (itemId)
		{
			case 65:
				usePotion(activeChar, item, 2001, 1);
				break;
			
			case 726:// Mana potions
				usePotion(activeChar, item, 2003, 1);
				break;
			
			case 728:// Healing and speed potions
				usePotion(activeChar, item, 2005, 1);
				break;
			
			case 725: // healing_drug, xml: 2002
				if (!isEffectReplaceable(activeChar, EffectType.HEAL_OVER_TIME, itemId))
				{
					return;
				}
				usePotion(activeChar, item, 2002, 1);
				break;
			
			case 727: // healing_potion, xml: 2032
				if (!isEffectReplaceable(activeChar, EffectType.HEAL_OVER_TIME, itemId))
				{
					return;
				}
				usePotion(activeChar, item, 2032, 1);
				break;
			
			case 731:// Endeavor potion, xml: 2010
				usePotion(activeChar, item, 2010, 1);
				break;
			
			case 734:
				usePotion(activeChar, item, 2011, 1);
				break;
			
			case 735:
				usePotion(activeChar, item, 2012, 1);
				break;
			
			case 1073:
			case 1060: // lesser_healing_potion, beginner's potion, xml: 2031
				if (!isEffectReplaceable(activeChar, EffectType.HEAL_OVER_TIME, itemId))
				{
					return;
				}
				
				usePotion(activeChar, item, 2031, 1);
				break;
			
			case 1061: // healing_potion, xml: 2032
				if (!isEffectReplaceable(activeChar, EffectType.HEAL_OVER_TIME, itemId))
				{
					return;
				}
				usePotion(activeChar, item, 2032, 1);
				break;
			
			case 1062:
				usePotion(activeChar, item, 2033, 1);
				break;
			
			case 1374:
				usePotion(activeChar, item, 2034, 1);
				break;
			
			case 1375:
				usePotion(activeChar, item, 2035, 1);
				break;
			
			case 1539: // greater_healing_potion, xml: 2037
				if (!isEffectReplaceable(activeChar, EffectType.HEAL_OVER_TIME, itemId))
				{
					return;
				}
				usePotion(activeChar, item, 2037, 1);
				break;
			
			case 4679:
				usePotion(activeChar, item, 2076, 1);
				break;
			
			case 1540:
				usePotion(activeChar, item, 2038, 1);
				break;
			
			case 5234:
				usePotion(activeChar, item, 2103, 1);
				break;
			
			case 5283:
				usePotion(activeChar, item, 2136, 1);
				break;
			
			case 5591:
			case 5592: // CP and Greater CP Potion
				// Leave it here just in case of admins changing skill usage
				if (!isEffectReplaceable(activeChar, EffectType.COMBAT_POINT_HEAL_OVER_TIME, itemId))
				{
					return;
				}
				usePotion(activeChar, item, 2166, (itemId == 5591) ? 1 : 2);
				break;
			
			case 6035:
				usePotion(activeChar, item, 2169, 1);
				break;
			
			case 6036:
				usePotion(activeChar, item, 2169, 2);
				break;
		}
	}
	
	private void usePotion(L2PcInstance player, ItemInstance item, int magicId, int level)
	{
		Skill skill = SkillData.getInstance().getSkill(magicId, level);
		if (skill != null)
		{
			if (player.isSkillDisabled(skill))
			{
				player.sendPacket(new SystemMessage(SystemMessage.S1_PREPARED_FOR_REUSE).addSkillName(skill.getId(), skill.getLevel()));
				return;
			}
			
			if (!player.getInventory().destroyItem("Consume", item, 1, null, false))
			{
				return;
			}
			
			// player.sendPacket(new SystemMessage(SystemMessage.USE_S1).addItemName(item.getId()));
			
			if (skill.getReuseDelay() > 10)
			{
				player.disableSkill(skill, skill.getReuseDelay());
			}
			
			player.broadcastPacket(new MagicSkillUse(player, player, skill.getId(), skill.getLevel(), skill.getHitTime(), 0));
			
			// Apply effects
			ISkillHandler handler = SkillHandler.getHandler(skill.getSkillType());
			if (handler != null)
			{
				handler.useSkill(player, skill, List.of(player));
			}
		}
		
	}
	
	private boolean isEffectReplaceable(L2Playable playable, Enum<?> effectType, int itemId)
	{
		L2PcInstance activeChar = (L2PcInstance) ((playable instanceof L2PcInstance) ? playable : ((L2Summon) playable).getOwner());
		
		for (Effect e : playable.getAllEffects())
		{
			if (e.getEffectType() == effectType)
			{
				int time = e.getCount() * e.getTime();
				
				// One can reuse pots after 2/3 of their duration is over.
				// It would be faster to check if its > 10 but that would screw custom pot durations...
				if (e.getTaskTime() > (time * 0.67))
				{
					return true;
				}
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_PREPARED_FOR_REUSE).addItemName(itemId));
				return false;
			}
		}
		return true;
	}
}
