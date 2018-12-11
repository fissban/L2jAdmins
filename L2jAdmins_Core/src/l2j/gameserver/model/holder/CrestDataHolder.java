package l2j.gameserver.model.holder;

import l2j.gameserver.data.CrestData.CrestType;

/**
 * @author fissban
 */
public class CrestDataHolder
{
	private final CrestType crestType;
	private final int crestId;
	private final byte[] hash;
	
	public CrestDataHolder(CrestType crestType, int crestId, byte[] hash)
	{
		this.crestType = crestType;
		this.crestId = crestId;
		this.hash = hash;
	}
	
	public CrestType getCrestType()
	{
		return crestType;
	}
	
	public int getCrestId()
	{
		return crestId;
	}
	
	public byte[] getHash()
	{
		return hash;
	}
}
