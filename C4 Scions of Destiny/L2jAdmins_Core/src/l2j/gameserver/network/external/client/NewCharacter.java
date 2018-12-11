package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CharTemplateData;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.CharTemplates;

/**
 * This class ...
 * @version $Revision: 1.3.4.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class NewCharacter extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		CharTemplates ct = new CharTemplates();
		
		ct.addChar(CharTemplateData.getInstance().getTemplate(0));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.HUMAN_FIGHTER));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.HUMAN_MAGE));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.ELF_FIGHTER));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.ELF_MAGE));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.DARK_ELF_FIGHTER));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.DARK_ELF_MAGE));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.ORC_FIGHTER));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.ORC_MAGE));
		ct.addChar(CharTemplateData.getInstance().getTemplate(ClassId.DWARF_FIGHTER));
		
		sendPacket(ct);
	}
}
