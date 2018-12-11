package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.2.4 $ $Date: 2005/04/06 16:13:51 $
 */
public class ItemRemedy implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			1831,
			1832,
			1833,
			1834,
			3889,
			6654
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar = playable.getActingPlayer();
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		int skillId = 0;
		
		switch (item.getId())
		{
			case 1831:// antidote
				skillId = 2042;
				negateEffects(activeChar, EffectType.POISON, 3);
				break;
			
			case 1832:// advanced antidote
				skillId = 2043;
				negateEffects(activeChar, EffectType.POISON, 7);
				break;
			
			case 1833:// bandage
				skillId = 34;
				negateEffects(activeChar, EffectType.BLEED, 3);
				break;
			
			case 1834: // emergency dressing
				skillId = 2045;
				negateEffects(activeChar, EffectType.BLEED, 7);
				break;
			
			case 3889:// potion of recovery
				skillId = 2042;
				activeChar.stopEffect(4082);
				break;
			
			case 6654:// Amulet: Flames of Valakas
				skillId = 2233;
				activeChar.stopEffect(4683);
				activeChar.stopEffect(4684);
				break;
		}
		
		activeChar.broadcastPacket(new MagicSkillUse(playable, playable, skillId, 1, 0, 0));
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, activeChar, playable);
	}
	
	private static void negateEffects(L2Playable playable, EffectType type, float negatePower)
	{
		// Get all skills effects on the L2Character
		
		for (Effect e : playable.getAllEffects())
		{
			if (e == null)
			{
				continue;
			}
			
			EffectType effectType = e.getEffectType();
			
			if ((effectType != null) && (effectType == type))
			{
				if ((e.getSkill().getMagicLevel() / 10) <= negatePower)
				{
					e.exit();
				}
			}
		}
	}
}
