package l2j.gameserver.handler.item;

import l2j.Config;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class ItemScrollOfEscape implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			736,
			1830,
			1829,
			1538,
			3958,
			5858,
			5859,
			
			7117,
			7118,
			7119,
			7120,
			7121,
			7122,
			7123,
			7124,
			
			7125,
			7126,
			7127,
			7128,
			7129,
			7130,
			7131,
			7132,
			7133,
			7134,
			7554,
			7555,
			7556,
			7557,
			7558,
			7559,
			7618,
			7619
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
		
		if (activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.isAlikeDead() || activeChar.isAllSkillsDisabled())
		{
			return;
		}
		
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessage.CANT_MOVE_SITTING);
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		// Check to see if the player is in a festival.
		if (activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("You may not use an escape skill in a festival.");
			return;
		}
		
		// Check to see if player is in jail
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You cannot escape from jail.");
			return;
		}
		
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (activeChar.getKarma() > 0))
		{
			activeChar.sendMessage("You cannot use an escape skill.");
			return;
		}
		
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
		{
			return;
		}
		
		activeChar.sendPacket(new SystemMessage(SystemMessage.S1_DISAPPEARED).addItemName(item.getId()));
		
		SkillHolder sh = null;
		
		switch (item.getId())
		{
			case 7117:
				sh = new SkillHolder(2213, 1);// Talking Island
				break;
			case 7118:
				sh = new SkillHolder(2213, 2);// Elven Village
				break;
			case 7119:
				sh = new SkillHolder(2213, 3);// Dark Elven Village
				break;
			case 7120:
				sh = new SkillHolder(2213, 4);// Orc Village
				break;
			case 7121:
				sh = new SkillHolder(2213, 5);// Dwarven Village
				break;
			case 7122:
				sh = new SkillHolder(2213, 6);// Gludin Village
				break;
			case 7123:
				sh = new SkillHolder(2213, 7);// Gludio Castle Town
				break;
			case 7124:
				sh = new SkillHolder(2213, 8);// Dion Castle Town
				break;
			case 7125:
				sh = new SkillHolder(2213, 9);// Floran
				break;
			case 7126:
				sh = new SkillHolder(2213, 10);// Giran Castle Town
				break;
			case 7127:
				sh = new SkillHolder(2213, 11);// Hardin's Private Academy
				break;
			case 7128:
				sh = new SkillHolder(2213, 12); // Heine
				break;
			case 7129:
				sh = new SkillHolder(2213, 13);// Oren Castle Town
				break;
			case 7130:
				sh = new SkillHolder(2213, 14);// Ivory Tower
				break;
			case 7131:
				sh = new SkillHolder(2213, 15);// Hunters Village
				break;
			case 7132:
				sh = new SkillHolder(2213, 16);// Aden Castle Town
				break;
			case 7133:
				sh = new SkillHolder(2213, 17); // Goddard Castle Town
				break;
			case 7134:
				sh = new SkillHolder(2213, 18);// Rune Castle Town
				break;
			case 7554:
				sh = new SkillHolder(2214, 1);// Talking Island quest scroll
				break;
			case 7555:
				sh = new SkillHolder(2214, 2);// Elven Village quest scroll
				break;
			case 7556:
				sh = new SkillHolder(2214, 3);// Dark Elven Village quest scroll
				break;
			case 7557:
				sh = new SkillHolder(2214, 4);// Orc Village quest scroll
				break;
			case 7558:
				sh = new SkillHolder(2214, 5);// Dwarven Village quest scroll
				break;
			case 7559:
				sh = new SkillHolder(2214, 10);// Giran Castle Town quest scroll
				break;
			case 7618:
				sh = new SkillHolder(2213, 20);// Ketra Orc Village
				break;
			case 7619:
				sh = new SkillHolder(2213, 21);// Varka Silenos Village
				break;
			case 5858:
				sh = new SkillHolder(2177, 1);// Blessed Scroll of Escape: Clan Hall
				break;
			case 1829:
				sh = new SkillHolder(2040, 1);// Scroll of Escape: Clan Hall
				break;
			case 1830:
				sh = new SkillHolder(2041, 1);// Scroll of Escape: Castle
				break;
			case 5859:
				sh = new SkillHolder(2178, 1);// Blessed Scroll of Escape: Castle
				break;
			case 736:
				sh = new SkillHolder(2013, 1);// Scroll of Escape
				break;
			case 1538:
				sh = new SkillHolder(2036, 1);// Blessed Scroll of Escape
				break;
			case 3958:
				sh = new SkillHolder(2036, 2);// Blessed Scroll of Escape
				break;
		}
		
		if (sh != null)
		{
			// activeChar.getAI().setIntention(CtrlIntentionType.IDLE);
			activeChar.setTarget(activeChar);
			activeChar.useMagic(sh.getSkill(), true, true);
		}
	}
}
