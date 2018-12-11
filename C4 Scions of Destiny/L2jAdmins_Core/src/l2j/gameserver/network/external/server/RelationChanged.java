package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Luca Baldi
 */
public class RelationChanged extends AServerPacket
{
	public static final int RELATION_PVP_FLAG = 0x00002; // pvp ???
	public static final int RELATION_HAS_KARMA = 0x00004; // karma ???
	public static final int RELATION_PARTY1 = 0x00001; // party member
	public static final int RELATION_PARTY2 = 0x00002; // party member
	public static final int RELATION_PARTY3 = 0x00004; // party member
	public static final int RELATION_PARTY4 = 0x00008; // party member (for information, see L2PcInstance.getRelation())
	public static final int RELATION_PARTYLEADER = 0x00010; // true if is party leader
	public static final int RELATION_HAS_PARTY = 0x00020; // true if is in party
	public static final int RELATION_CLAN_MEMBER = 0x00040; // true if is in clan
	public static final int RELATION_LEADER = 0x00080; // true if is clan leader
	public static final int RELATION_INSIEGE = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER = 0x00400; // true when attacker
	public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR = 0x08000; // double fist
	public static final int RELATION_1SIDED_WAR = 0x10000; // single fist
	
	private final int objId, relation, autoAttackable;
	
	private int karma;
	
	private int pvpFlag;
	
	public RelationChanged(L2Playable activeChar, int relation, boolean autoAttackable)
	{
		objId = activeChar.getObjectId();
		this.relation = relation;
		this.autoAttackable = autoAttackable ? 1 : 0;
		
		if (activeChar instanceof L2PcInstance)
		{
			karma = ((L2PcInstance) activeChar).getKarma();
			pvpFlag = ((L2PcInstance) activeChar).getPvpFlag().ordinal();
		}
		else if (activeChar instanceof L2Summon)
		{
			karma = ((L2Summon) activeChar).getOwner().getKarma();
			pvpFlag = ((L2Summon) activeChar).getOwner().getPvpFlag().ordinal();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xce);
		writeD(objId);
		writeD(relation);
		writeD(autoAttackable);
		writeD(karma);
		writeD(pvpFlag);
	}
}
