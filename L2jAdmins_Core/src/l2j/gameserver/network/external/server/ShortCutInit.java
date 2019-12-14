package l2j.gameserver.network.external.server;

import java.util.Collection;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * ShortCutInit format d *(1dddd)/(2ddddd)/(3dddd)
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShortCutInit extends AServerPacket
{
	private L2PcInstance activeChar;
	
	public ShortCutInit(L2PcInstance activeChar)
	{
		this.activeChar = activeChar;
		
		if (activeChar == null)
		{
			return;
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x45);
		Collection<ShortCutsHolder> allShortCuts = activeChar.getShortCuts().getAllShortCuts();
		writeD(allShortCuts.size());
		for (ShortCutsHolder sc : allShortCuts)
		{
			if (sc == null)
			{
				continue;
			}
			
			writeD(sc.getType().getMask());
			writeD(sc.getSlot() + (sc.getPage() * 12));
			writeD(sc.getId());
			if (sc.getLevel() > -1)
			{
				writeD(activeChar.getSkillLevel(sc.getId()));
			}
			writeD(0x01);
		}
	}
}
