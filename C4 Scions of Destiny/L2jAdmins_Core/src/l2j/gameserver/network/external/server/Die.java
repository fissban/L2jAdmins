package l2j.gameserver.network.external.server;

import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0b 952a1048 objectId 00000000 00000000 00000000 00000000 00000000 00000000 format dddddd rev 377 format ddddddd rev 417
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 18:46:18 $
 */
public class Die extends AServerPacket
{
	private int chaId;
	private boolean fake;
	private boolean spoiled;
	
	private int access;
	private Clan clan;
	
	L2Character cha;
	
	public Die(L2Character cha)
	{
		this.cha = cha;
		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;
			access = player.getAccessLevel();
			clan = player.getClan();
		}
		chaId = cha.getObjectId();
		fake = !cha.isDead();
		if (cha instanceof L2Attackable)
		{
			spoiled = ((L2Attackable) cha).isSpoil();
		}
	}
	
	@Override
	public void writeImpl()
	{
		if (fake)
		{
			return;
		}
		
		writeC(0x06);
		writeD(chaId);
		
		writeD(0x01); // to nearest village
		if (clan != null)
		{
			boolean isAttackerWithFlag = false;
			boolean isDefender = false;
			
			Siege siege = SiegeManager.getInstance().getSiege(cha);
			if (siege != null)
			{
				isAttackerWithFlag = (siege.isAttacker(clan)) && (siege.getClansListMngr().getClan(SiegeClanType.ATTACKER, clan.getId()).getFlags().size() > 0) && !siege.isDefender(clan);
				isDefender = (!siege.isAttacker(clan)) && siege.isDefender(clan);
			}
			
			writeD(clan.hasClanHall() ? 0x01 : 0x00); // to hide away
			writeD((clan.hasCastle()) || isDefender ? 0x01 : 0x00); // to castle
			writeD(isAttackerWithFlag ? 0x01 : 0x00); // to siege HQ
		}
		else
		{
			writeD(0x00); // to hide away
			writeD(0x00); // to castle
			writeD(0x00); // to siege HQ
		}
		
		writeD(spoiled ? 0x01 : 0x00); // sweepable (blue glow)
		writeD(access >= 1 ? 0x01 : 0x00); // to FIXED
	}
}
