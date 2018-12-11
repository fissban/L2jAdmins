package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.network.AServerPacket;

/**
 * @author devScarlet & mrTJO
 */
public final class ServerObjectInfo extends AServerPacket
{
	private final L2Npc activeChar;
	private final boolean isAttackable;
	
	public ServerObjectInfo(L2Npc activeChar, L2Character actor)
	{
		this.activeChar = activeChar;
		isAttackable = activeChar.isAutoAttackable(actor);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x8c);
		writeD(activeChar.getObjectId());
		writeD(activeChar.getTemplate().getIdTemplate() + 1000000);
		writeS(""); // name
		writeD(isAttackable ? 1 : 0);
		writeD(activeChar.getX());
		writeD(activeChar.getY());
		writeD(activeChar.getZ());
		writeD(activeChar.getHeading());
		writeF(1.0); // movement multiplier
		writeF(1.0); // attack speed multiplier
		writeF(activeChar.getCollisionRadius());
		writeF(activeChar.getCollisionHeight());
		writeD((int) (isAttackable ? activeChar.getCurrentHp() : 0));
		writeD(isAttackable ? activeChar.getStat().getMaxHp() : 0);
		writeD(0x01); // object type
		writeD(0x00); // special effects
	}
}
