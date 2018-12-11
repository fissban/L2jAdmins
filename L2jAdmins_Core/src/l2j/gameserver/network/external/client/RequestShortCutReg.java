package l2j.gameserver.network.external.client;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestShortCutReg extends AClientPacket
{
	private PcShortCutsType type;
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
				type = PcShortCutsType.ITEM;
				break;
			case 0x02: // skill
				type = PcShortCutsType.SKILL;
				break;
			case 0x03: // action
				type = PcShortCutsType.ACTION;
				break;
			case 0x04: // macro
				type = PcShortCutsType.MACRO;
				break;
			case 0x05: // recipe
				type = PcShortCutsType.RECIPE;
				break;
			default:
				type = PcShortCutsType.ITEM;// never happend
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
				activeChar.getShortCuts().registerShortCut(new PcShortCutsInstance(slot, page, type, id, -1, characterType), true);
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
					
					activeChar.getShortCuts().registerShortCut(new PcShortCutsInstance(slot, page, type, id, shortCutSkill.getLevel(), characterType), true);
				}
				break;
			}
		}
	}
}
