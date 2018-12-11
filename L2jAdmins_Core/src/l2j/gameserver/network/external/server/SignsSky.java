package l2j.gameserver.network.external.server;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.network.AServerPacket;

/**
 * Changes the sky color depending on the outcome of the Seven Signs competition. packet type id 0xf8 format: c h
 * @author Tempy
 */
public class SignsSky extends AServerPacket
{
	private int state;
	
	public SignsSky()
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			state = SevenSignsManager.getInstance().getCabalHighestScore().ordinal();
		}
	}
	
	public SignsSky(int state)
	{
		this.state = state;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf8);
		
		if (state == 2)
		{
			writeH(258);
		}
		else if (state == 1)
		{
			writeH(257);
		}
	}
}
