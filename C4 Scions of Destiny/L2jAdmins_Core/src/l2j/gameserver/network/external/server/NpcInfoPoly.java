package l2j.gameserver.network.external.server;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.7.2.4.2.9 $ $Date: 2005/04/11 10:05:54 $
 */
public class NpcInfoPoly extends AServerPacket
{
	// ddddddddddddddddddffffdddcccccSSddd dddddc
	private L2Character cha;
	private final L2Object obj;
	private int x, y, z, heading;
	private final int npcId;
	private boolean isAttackable;
	private final boolean isSummoned;
	private boolean isRunning;
	private boolean isInCombat;
	private boolean isAlikeDead;
	private int mAtkSpd, pAtkSpd;
	private int runSpd, walkSpd, swimRunSpd, swimWalkSpd, flRunSpd, flWalkSpd, flyRunSpd, flyWalkSpd;
	private int rHand;
	private int lHand;
	private String name, title;
	private int abnormalEffect;
	NpcTemplate template;
	
	private final double collisionRadius;
	
	private final double collisionHeight;
	
	/**
	 * @param cha
	 * @param attacker
	 */
	public NpcInfoPoly(L2Object cha, L2Character attacker)
	{
		obj = cha;
		npcId = cha.getPolyId();
		template = NpcData.getInstance().getTemplate(npcId);
		isAttackable = true;
		rHand = 0;
		lHand = 0;
		isSummoned = false;
		
		collisionRadius = template.getCollisionRadius();
		
		collisionHeight = template.getCollisionHeight();
		if (obj instanceof L2Character)
		{
			this.cha = (L2Character) cha;
			isAttackable = cha.isAutoAttackable(attacker);
			rHand = template.getRhand();
			lHand = template.getLhand();
		}
		
		if (obj instanceof ItemInstance)
		{
			x = obj.getX();
			y = obj.getY();
			z = obj.getZ();
			heading = 0;
			mAtkSpd = 100; // yes, an item can be dread as death
			pAtkSpd = 100;
			runSpd = 120;
			walkSpd = 80;
			swimRunSpd = flRunSpd = flyRunSpd = runSpd;
			swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
			isRunning = isInCombat = isAlikeDead = false;
			name = "item";
			title = "polymorphed";
			abnormalEffect = 0;
		}
		else
		{
			x = this.cha.getX();
			y = this.cha.getY();
			z = this.cha.getZ();
			heading = this.cha.getHeading();
			mAtkSpd = this.cha.getStat().getMAtkSpd();
			pAtkSpd = this.cha.getStat().getPAtkSpd();
			runSpd = this.cha.getStat().getRunSpeed();
			walkSpd = this.cha.getStat().getWalkSpeed();
			swimRunSpd = flRunSpd = flyRunSpd = runSpd;
			swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
			isRunning = this.cha.isRunning();
			isInCombat = this.cha.isInCombat();
			isAlikeDead = this.cha.isAlikeDead();
			name = this.cha.getName();
			title = this.cha.getTitle();
			abnormalEffect = this.cha.getAbnormalEffect();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x16);
		writeD(obj.getObjectId());
		writeD(npcId + 1000000); // npctype id
		writeD(isAttackable ? 1 : 0);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
		writeD(0x00);
		writeD(mAtkSpd);
		writeD(pAtkSpd);
		writeD(runSpd);
		writeD(walkSpd);
		writeD(swimRunSpd); // swimspeed
		writeD(swimWalkSpd); // swimspeed
		writeD(flRunSpd);
		writeD(flWalkSpd);
		writeD(flyRunSpd);
		writeD(flyWalkSpd);
		writeF(1);
		writeF(1);
		writeF(collisionRadius);
		writeF(collisionHeight);
		writeD(rHand); // right hand weapon
		writeD(0);
		writeD(lHand); // left hand weapon
		writeC(1); // name above char 1=true ... ??
		writeC(isRunning ? 1 : 0);
		writeC(isInCombat ? 1 : 0);
		writeC(isAlikeDead ? 1 : 0);
		writeC(isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		writeS(name);
		writeS(title);
		writeD(0);
		writeD(0);
		writeD(0000); // hmm karma ??
		
		writeH(abnormalEffect); // C2
		writeH(0x00); // C2
		writeD(0000); // C2
		writeD(0000); // C2
		writeD(0000); // C2
		writeD(0000); // C2
		writeC(0000); // C2
	}
}
