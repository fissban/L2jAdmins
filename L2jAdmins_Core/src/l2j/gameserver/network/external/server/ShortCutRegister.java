package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 56 01000000 04000000 dd9fb640 01000000 56 02000000 07000000 38000000 03000000 01000000 56 03000000 00000000 02000000 01000000 format dd d/dd/d d
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShortCutRegister extends AServerPacket
{
	private final ShortCutsHolder shortcut;
	
	/**
	 * Register new skill shortcut
	 * @param shortcut
	 */
	public ShortCutRegister(ShortCutsHolder shortcut)
	{
		this.shortcut = shortcut;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x44);
		
		writeD(shortcut.getType().getMask());
		writeD(shortcut.getSlot() + (shortcut.getPage() * 12)); // C4 Client
		writeD(shortcut.getId());
		
		if (shortcut.getLevel() > -1)
		{
			writeD(shortcut.getLevel());
		}
		writeD(shortcut.getCharacterType());
	}
}
