package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.6.2.5.2.12 $ $Date: 2005/03/31 09:19:16 $
 */
public class PetInfo extends AServerPacket
{
	private final L2Summon summon;
	private final int x, y, z, heading;
	private final boolean isSummoned;
	private final int mAtkSpd, pAtkSpd;
	private final int runSpd, walkSpd, swimRunSpd, swimWalkSpd;
	private int flRunSpd;
	private int flWalkSpd;
	private int flyRunSpd;
	private int flyWalkSpd;
	private final int maxHp, maxMp;
	private int maxFeed, curFeed;
	private final int val;
	private final float multiplier;
	
	/**
	 * rev 478 dddddddddddddddddddffffdddcccccSSdddddddddddddddddddddddddddhc
	 * @param summon
	 * @param val
	 */
	public PetInfo(L2Summon summon, int val)
	{
		this.summon = summon;
		isSummoned = summon.isShowSummonAnimation();
		x = summon.getX();
		y = summon.getY();
		z = summon.getZ();
		heading = summon.getHeading();
		mAtkSpd = summon.getStat().getMAtkSpd();
		pAtkSpd = summon.getStat().getPAtkSpd();
		multiplier = summon.getStat().getMovementSpeedMultiplier();
		runSpd = summon.getPetSpeed();
		walkSpd = summon.isMountable() ? 45 : 30;
		swimRunSpd = flRunSpd = flyRunSpd = runSpd;
		swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
		maxHp = summon.getStat().getMaxHp();
		maxMp = summon.getStat().getMaxMp();
		this.val = val;
		if (summon instanceof L2PetInstance)
		{
			
			curFeed = summon.getCurrentFed(); // how fed it is
			maxFeed = summon.getMaxFeed(); // max fed it can be
		}
		else if (summon instanceof L2SummonInstance)
		{
			L2SummonInstance servitor = (L2SummonInstance) summon;
			curFeed = servitor.getTimeRemaining();
			maxFeed = servitor.getTotalLifeTime();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb1);
		writeD(summon.getSummonType());
		writeD(summon.getObjectId());
		writeD(summon.getTemplate().getIdTemplate() + 1000000);
		writeD(0); // 1=attackable
		
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
		writeD(0);
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
		
		writeF(multiplier); // speed multiplier
		writeF(1); // atk speed multiplier
		writeF(summon.getTemplate().getCollisionRadius());
		writeF(summon.getTemplate().getCollisionHeight());
		writeD(summon.getWeapon()); // right hand weapon
		writeD(summon.getArmor());
		writeD(0); // left hand weapon
		writeC(summon.getOwner() != null ? 1 : 0); // master name above pet 1=true ...
		writeC(1); // running=1
		writeC(summon.isInCombat() ? 1 : 0); // attacking 1=true
		writeC(summon.isAlikeDead() ? 1 : 0); // dead 1=true
		writeC(isSummoned ? 2 : val); // 0=teleported 1=default 2=summoned
		writeS(summon.getName());
		writeS(summon.getTitle());
		writeD(1);
		writeD(summon.getOwner().getPvpFlag().ordinal());
		writeD(summon.getOwner().getKarma()); // hmm karma ??
		writeD(curFeed); // how fed it is
		writeD(maxFeed); // max fed it can be
		writeD((int) summon.getCurrentHp());// current hp
		writeD(maxHp);// max hp
		writeD((int) summon.getCurrentMp());// current mp
		writeD(maxMp);// max mp
		writeD(summon.getStat().getSp()); // sp
		writeD(summon.getLevel());// lvl
		writeD((int) summon.getStat().getExp());
		writeD((int) summon.getExpForThisLevel());// 0% absolute value
		writeD((int) summon.getExpForNextLevel());// 100% Absolute value
		
		if (summon instanceof L2PetInstance)
		{
			writeD(((L2PetInstance) summon).getInventory().getTotalWeight());// weight
			writeD(((L2PetInstance) summon).getMaxLoad());// max weight it can carry
		}
		else
		{
			writeD(0);
			writeD(0);
		}
		
		writeD(summon.getStat().getPAtk(null));// patk
		writeD(summon.getStat().getPDef(null));// pdef
		writeD(summon.getStat().getMAtk(null, null));// matk
		writeD(summon.getStat().getMDef(null, null));// mdef
		writeD(summon.getStat().getAccuracy());// accuracy
		writeD(summon.getStat().getEvasionRate(null));// evasion
		writeD(summon.getStat().getCriticalHit(null, null));// critical
		writeD((int) summon.getStat().getMoveSpeed());// speed
		writeD(summon.getStat().getPAtkSpd());// atkspeed
		writeD(summon.getStat().getMAtkSpd());// casting speed
		
		writeD(summon.getAbnormalEffect());// c2 abnormal visual effect... bleed=1; poison=2; poison & bleed=3; flame=4;
		writeH(summon.isMountable() ? 1 : 0);// c2 ride button
		
		writeC(0); // c2
		
		// Following all added in C4.
		writeH(0); // ??
		writeC(summon.getOwner() != null ? summon.getOwner().getTeam().ordinal() : 0); // team aura (1 = blue, 2 = red)
		
		writeD(summon.getSoulShotsPerHit());
		writeD(summon.getSpiritShotsPerHit());
	}
}
