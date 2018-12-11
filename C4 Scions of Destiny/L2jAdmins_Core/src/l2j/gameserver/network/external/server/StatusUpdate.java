package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * 01 // Packet Identifier <BR>
 * c6 37 50 40 // ObjectId <BR>
 * <BR>
 * 01 00 // Number of Attribute Trame of the Packet <BR>
 * <BR>
 * c6 37 50 40 // Attribute Identifier : 01-Level, 02-Experience, 03-STR, 04-DEX, 05-CON, 06-INT, 07-WIT, 08-MEN, 09-Current HP, 0a, Max HP...<BR>
 * cd 09 00 00 // Attribute Value <BR>
 * format d d(dd)
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:39 $
 */
public class StatusUpdate extends AServerPacket
{
	public enum StatusUpdateType
	{
		LEVEL(0x01),
		EXP(0x02),
		STR(0x03),
		DEX(0x04),
		CON(0x05),
		INT(0x06),
		WIT(0x07),
		MEN(0x08),
		
		CUR_HP(0x09),
		MAX_HP(0x0a),
		CUR_MP(0x0b),
		MAX_MP(0x0c),
		
		SP(0x0d),
		CUR_LOAD(0x0e),
		MAX_LOAD(0x0f),
		
		P_ATK(0x11),
		P_ATK_SPD(0x12),
		P_DEF(0x13),
		EVASION(0x14),
		ACCURACY(0x15),
		CRITICAL(0x16),
		M_ATK(0x17),
		M_CAST_SPD(0x18),
		M_DEF(0x19),
		PVP_FLAG(0x1a),
		KARMA(0x1b),
		
		CUR_CP(0x21),
		MAX_CP(0x22);
		
		private final int mask;
		
		StatusUpdateType(int mask)
		{
			this.mask = mask;
		}
		
		public int getMask()
		{
			return mask;
		}
	}
	
	private final int objectId;
	private final List<Attribute> attributes = new ArrayList<>();
	
	public class Attribute
	{
		/**
		 * id values 09 - current health 0a - max health 0b - current mana 0c - max mana
		 */
		public StatusUpdateType updateType;
		public int value;
		
		Attribute(StatusUpdateType updateType, int pValue)
		{
			this.updateType = updateType;
			value = pValue;
		}
	}
	
	public StatusUpdate(int objectId)
	{
		this.objectId = objectId;
	}
	
	public void addAttribute(StatusUpdateType id, int level)
	{
		attributes.add(new Attribute(id, level));
	}
	
	public boolean hasAttributes()
	{
		return !attributes.isEmpty();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x0e);
		writeD(objectId);
		writeD(attributes.size());
		
		for (Attribute temp : attributes)
		{
			writeD(temp.updateType.getMask());
			writeD(temp.value);
		}
	}
}
