package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.templates.PcTemplate;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.7 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharTemplates extends AServerPacket
{
	// dddddddddddddddddddd
	private final List<PcTemplate> chars = new ArrayList<>();
	
	public void addChar(PcTemplate template)
	{
		chars.add(template);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x17);
		writeD(chars.size());
		
		for (PcTemplate temp : chars)
		{
			writeD(temp.getRace().ordinal());
			writeD(temp.getClassId().getId());
			writeD(0x46);
			writeD(temp.getBaseSTR());
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.getBaseDEX());
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.getBaseCON());
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.getBaseINT());
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.getBaseWIT());
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.getBaseMEN());
			writeD(0x0a);
		}
	}
}
