package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.client.Say2.SayType;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class CreatureSay extends AServerPacket
{
	// ddSS
	private final int objectId;
	private final SayType sayType;
	private final String charName;
	private final String text;
	
	/**
	 * @param cha
	 * @param sayType
	 * @param charName
	 * @param text
	 */
	public CreatureSay(L2Character cha, SayType sayType, String charName, String text)
	{
		objectId = cha.getObjectId();
		this.sayType = sayType;
		this.charName = charName;
		this.text = text;
	}
	
	/**
	 * @param sayType
	 * @param charName
	 * @param text
	 */
	public CreatureSay(SayType sayType, String charName, String text)
	{
		objectId = 0;
		this.sayType = sayType;
		this.charName = charName;
		this.text = text;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x4a);
		writeD(objectId);
		writeD(sayType.ordinal());
		writeS(charName);
		writeS(text);
	}
}
