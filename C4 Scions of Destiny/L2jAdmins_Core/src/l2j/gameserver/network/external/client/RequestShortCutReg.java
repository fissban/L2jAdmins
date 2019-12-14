package l2j.gameserver.network.external.client;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsHolder;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsType;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestShortCutReg extends AClientPacket
{
	private ShortCutsType type;
	private int id;
	private int slot;
	private int page;
	private int characterType;
	
	@Override
	protected void readImpl()
	{
		switch (readD())
		{
			case 0x01: // item
				type = ShortCutsType.ITEM;
				break;
			case 0x02: // skill
				type = ShortCutsType.SKILL;
				break;
			case 0x03: // action
				type = ShortCutsType.ACTION;
				break;
			case 0x04: // macro
				type = ShortCutsType.MACRO;
				break;
			case 0x05: // recipe
				type = ShortCutsType.RECIPE;
				break;
			default:
				type = ShortCutsType.ITEM;// never happend
		}
		
		int slot = readD();
		id = readD();
		characterType = readD();
		this.slot = slot % 12;
		page = slot / 12;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		switch (type)
		{
			case ITEM: // item
			case ACTION: // action
			case MACRO: // macro
			case RECIPE: // recipe
			{
				activeChar.getShortCuts().registerShortCut(new ShortCutsHolder(slot, page, type, id, -1, characterType), true);
				break;
			}
			case SKILL: // skill
			{
				Skill shortCutSkill = SkillData.getInstance().getSkill(id, activeChar.getSkillLevel(id));
				
				if (shortCutSkill != null)
				{
					if (shortCutSkill.isPassive())
					{
						return;
					}
					
					activeChar.getShortCuts().registerShortCut(new ShortCutsHolder(slot, page, type, id, shortCutSkill.getLevel(), characterType), true);
				}
				break;
			}
		}
	}
}
