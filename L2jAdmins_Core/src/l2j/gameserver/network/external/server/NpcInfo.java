package l2j.gameserver.network.external.server;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2NpcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AServerPacket;
import l2j.util.Rnd;
import main.data.memory.ObjectData;
import main.holders.objects.NpcHolder;

/**
 * This class ...
 * @version $Revision: 1.7.2.4.2.9 $ $Date: 2005/04/11 10:05:54 $
 */
public class NpcInfo extends AServerPacket
{
	// ddddddddddddddddddffffdddcccccSSddd dddddc
	// ddddddddddddddddddffffdddcccccSSddd dddddccffd
	private final L2Character cha;
	private final int x, y, z, heading;
	private final int idTemplate;
	private final boolean isAttackable, isSummoned;
	private final int mAtkSpd, pAtkSpd;
	private final int runSpd, walkSpd, swimRunSpd, swimWalkSpd;
	private int flRunSpd;
	private int flWalkSpd;
	private int flyRunSpd;
	private int flyWalkSpd;
	private final float movementMultiplier;
	private final float attackSpeedMultiplier;
	private final int rhand, lhand, chest, val;
	private final double collisionHeight, collisionRadius;
	private String title = "";
	private String name = "";
	private int clanCrest, allyCrest, allyId, clanId;
	
	/**
	 * @param cha
	 * @param attacker
	 */
	public NpcInfo(L2Npc cha, L2Character attacker)
	{
		this.cha = cha;
		idTemplate = cha.getId();
		isAttackable = cha.isAutoAttackable(attacker);
		isSummoned = false;
		rhand = cha.getRhand();
		lhand = cha.getLhand();
		chest = 0;
		val = 0;
		collisionHeight = cha.getCollisionHeight();
		collisionRadius = cha.getCollisionRadius();
		
		title = cha.getTitle();
		if (Config.SHOW_NPC_LVL && (cha instanceof L2MonsterInstance))
		{
			String t = "Lv " + cha.getLevel() + (cha.getAggroRange() > 0 ? "*" : "");
			if (title != null)
			{
				t += " " + title;
			}
			title = t;
		}
		
		// If a value is defined and the npc has a color assigned to its title, it will not be displayed.
		if (!cha.isRaid() && !cha.isQuestMonster())
		{
			name = cha.getName();
		}
		
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
		heading = cha.getHeading();
		mAtkSpd = cha.getStat().getMAtkSpd();
		pAtkSpd = cha.getStat().getPAtkSpd();
		runSpd = cha.getTemplate().getBaseRunSpd();
		walkSpd = cha.getTemplate().getBaseWalkSpd();
		swimRunSpd = flRunSpd = flyRunSpd = runSpd;
		swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
		movementMultiplier = cha.getStat().getMovementSpeedMultiplier();
		attackSpeedMultiplier = cha.getStat().getAttackSpeedMultiplier();
		
		if ((cha.getCastle() != null) && (cha.getCastle().getOwnerId() != 0) && (cha instanceof L2NpcInstance))
		{
			Clan clan = ClanData.getInstance().getClanById(cha.getCastle().getOwnerId());
			clanCrest = clan.getCrestId();
			clanId = clan.getId();
			allyCrest = clan.getAllyCrestId();
			allyId = clan.getAllyId();
		}
	}
	
	public NpcInfo(L2Summon cha, L2Character attacker, int val)
	{
		this.cha = cha;
		idTemplate = cha.getTemplate().getIdTemplate();
		isAttackable = cha.isAutoAttackable(attacker);
		isSummoned = cha.isShowSummonAnimation();
		rhand = cha.getWeapon();
		lhand = 0;
		chest = cha.getArmor();
		this.val = val;
		
		collisionHeight = cha.getTemplate().getCollisionHeight();
		collisionRadius = cha.getTemplate().getCollisionRadius();
		
		title = cha.getOwner() != null ? (!cha.getOwner().isOnline() ? "" : cha.getOwner().getName()) : "";
		
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
		heading = cha.getHeading();
		mAtkSpd = cha.getStat().getMAtkSpd();
		pAtkSpd = cha.getStat().getPAtkSpd();
		runSpd = cha.getPetSpeed();
		walkSpd = cha.isMountable() ? 45 : 30;
		swimRunSpd = flRunSpd = flyRunSpd = runSpd;
		swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
		movementMultiplier = cha.getStat().getMovementSpeedMultiplier();
		attackSpeedMultiplier = cha.getStat().getAttackSpeedMultiplier();
	}
	
	@Override
	public void writeImpl()
	{
		if (cha instanceof L2Summon)
		{
			if (((L2Summon) cha).getOwner() != null)
			{
				if (((L2Summon) cha).getOwner().getInvisible())
				{
					return;
				}
				
				if (((L2Summon) cha).getOwner().isInOlympiadMode())
				{
					if (getClient().getActiveChar() != null)
					{
						if (!getClient().getActiveChar().isGM() && !getClient().getActiveChar().isInOlympiadMode() && !getClient().getActiveChar().inObserverMode())
						{
							return;
						}
					}
				}
			}
		}
		
		writeC(0x16);
		writeD(cha.getObjectId());
		writeD(idTemplate + 1000000); // npctype id
		writeD(isAttackable ? 0x01 : 0x00);
		
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
		
		writeD(0x00);
		
		writeD(mAtkSpd);
		writeD(pAtkSpd);
		writeD(runSpd);
		writeD(walkSpd);
		writeD(swimRunSpd);
		writeD(swimWalkSpd);
		writeD(flRunSpd);
		writeD(flWalkSpd);
		writeD(flyRunSpd);
		writeD(flyWalkSpd);
		writeF(movementMultiplier);
		writeF(attackSpeedMultiplier);
		
		writeF(collisionRadius);
		writeF(collisionHeight);
		
		writeD(rhand);
		writeD(chest);
		writeD(lhand);
		
		writeC(0x01); // name above char 0x01=true
		
		writeC(cha.isRunning() ? 0x01 : 0x00);
		writeC(cha.isInCombat() ? 0x01 : 0x00);
		writeC(cha.isAlikeDead() ? 0x01 : 0x00);
		writeC(isSummoned ? 0x02 : val); // 0=teleported 1=default 2=summoned
		
		writeS(name);// name
		writeS(title); // title
		
		if (cha instanceof L2Summon)
		{
			writeD(0x01);// 0x00 (color client) 0x01 (no color)
			writeD(((L2Summon) cha).getOwner().getPvpFlag().ordinal());
			writeD(((L2Summon) cha).getOwner().getKarma()); // hmm karma ??
		}
		else
		{
			writeD(0x00); // 0x00 (color client) 0x01 (no color)
			writeD(0x00);
			writeD(0x00);
		}
		
		writeD(cha.getAbnormalEffect());
		
		writeD(clanId);
		writeD(clanCrest);
		writeD(allyId);
		writeD(allyCrest);
		
		writeC(0x00); // FIXME npc fly?
		
		writeC((cha instanceof L2Summon) ? ((L2Summon) cha).getOwner().getTeam().ordinal() : cha.getTeam().ordinal());
		
		writeF(collisionRadius);
		writeF(collisionHeight);
		
		NpcHolder nh = ObjectData.get(NpcHolder.class, cha);
		if (nh != null)
		{
			writeD(nh.isChampion() ? Rnd.get(3, 20) : 0); // weapon enchant effect
		}
		else
		{
			writeD(0x00);
		}
	}
}
