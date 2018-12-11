package l2j.gameserver.network.external.server;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.AServerPacket;
import main.data.ObjectData;
import main.holders.objects.PlayerHolder;

/**
 * 0000: 03 32 15 00 00 44 fe 00 00 80 f1 ff ff 00 00 00 .2...D..........
 * <p>
 * 0010: 00 6b b4 c0 4a 45 00 6c 00 6c 00 61 00 6d 00 69 .k..JE.l.l.a.m.i
 * <p>
 * 0020: 00 00 00 01 00 00 00 01 00 00 00 12 00 00 00 00 ................
 * <p>
 * 0030: 00 00 00 2a 00 00 00 42 00 00 00 71 02 00 00 31 ...*...B...q...1
 * <p>
 * 0040: 00 00 00 18 00 00 00 1f 00 00 00 25 00 00 00 00 ...........%....
 * <p>
 * 0050: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f9 ................
 * <p>
 * 0060: 00 00 00 b3 01 00 00 00 00 00 00 00 00 00 00 7d ...............}
 * <p>
 * 0070: 00 00 00 5a 00 00 00 32 00 00 00 32 00 00 00 00 ...Z...2...2....
 * <p>
 * 0080: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 67 ...............g
 * <p>
 * 0090: 66 66 66 66 66 f2 3f 5f 63 97 a8 de 1a f9 3f 00 fffff.?_c.....?.
 * <p>
 * 00a0: 00 00 00 00 00 1e 40 00 00 00 00 00 00 37 40 01 .............7..
 * <p>
 * 00b0: 00 00 00 01 00 00 00 01 00 00 00 00 00 c1 0c 00 ................
 * <p>
 * 00c0: 00 00 00 00 00 00 00 00 00 01 01 00 00 00 00 00 ................
 * <p>
 * 00d0: 00 00
 * <p>
 * <p>
 * dddddSdddddddddddddddddddddddddddffffdddSdddccccccc (h)
 * <p>
 * dddddSdddddddddddddddddddddddddddffffdddSdddddccccccch dddddSddddddddddddddddddddddddddddffffdddSdddddccccccch (h) c (dchd) ddc dcc c cddd d
 * @version $Revision: 1.7.2.6.2.11 $ $Date: 2005/04/11 10:05:54 $
 */
public class CharInfo extends AServerPacket
{
	private final L2PcInstance cha;
	private final Inventory inv;
	private final int x, y, z, heading;
	private final int mAtkSpd, pAtkSpd;
	private final int runSpd, walkSpd, swimRunSpd, swimWalkSpd;
	private int flRunSpd;
	private int flWalkSpd;
	private int flyRunSpd;
	private int flyWalkSpd;
	private final float moveSpeedMultiplier;
	
	/**
	 * @param cha
	 */
	public CharInfo(L2PcInstance cha)
	{
		this.cha = cha;
		inv = cha.getInventory();
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
		heading = cha.getHeading();
		mAtkSpd = cha.getStat().getMAtkSpd();
		pAtkSpd = cha.getStat().getPAtkSpd();
		moveSpeedMultiplier = cha.getStat().getMovementSpeedMultiplier();
		runSpd = (int) (cha.getStat().getRunSpeed() / moveSpeedMultiplier);
		walkSpd = (int) (cha.getStat().getWalkSpeed() / moveSpeedMultiplier);
		swimRunSpd = flRunSpd = flyRunSpd = runSpd;
		swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
	}
	
	@Override
	public void writeImpl()
	{
		boolean gmSeeInvis = false;
		L2PcInstance tmp = getClient().getActiveChar();
		if (tmp != null)
		{
			if (cha.getInvisible())
			{
				if (tmp.isGM())
				{
					gmSeeInvis = true;
				}
				else
				{
					return;
				}
			}
			else if (cha.isInOlympiadMode())
			{
				if (!tmp.isGM() && !tmp.isInOlympiadMode() && !tmp.inObserverMode())
				{
					return;
				}
			}
		}
		
		if (cha.isPoly())
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(cha.getPolyId());
			
			if (template == null)
			{
				LOG.warning("Character " + cha.getName() + " (" + cha.getObjectId() + ") morphed in a Npc (" + cha.getPolyId() + ") w/o template.");
				return;
			}
			
			writeC(0x16);
			writeD(cha.getObjectId());
			writeD(template.getId() + 1000000); // npctype id
			writeD(cha.getKarma() > 0 ? 1 : 0);
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
			writeF(moveSpeedMultiplier);
			writeF(cha.getStat().getAttackSpeedMultiplier());
			writeF(template.getCollisionRadius());
			writeF(template.getCollisionHeight());
			writeD(inv.getPaperdollItemId(ParpedollType.RHAND)); // right hand weapon
			writeD(0);
			writeD(inv.getPaperdollItemId(ParpedollType.LHAND)); // left hand weapon
			writeC(1); // name above char 1=true ... ??
			writeC(cha.isRunning() ? 1 : 0);
			writeC(cha.isInCombat() ? 1 : 0);
			writeC(cha.isAlikeDead() ? 1 : 0);
			
			if (gmSeeInvis)
			{
				writeC(0);
			}
			else
			{
				writeC(cha.getInvisible() ? 1 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
			}
			
			writeS(cha.getName());
			
			if (gmSeeInvis)
			{
				writeS("Invisible");
			}
			else
			{
				writeS(cha.getTitle());
			}
			
			writeD(0);
			writeD(0);
			writeD(0000); // hmm karma ??
			
			writeD(cha.getAbnormalEffect()); // C2
			writeD(0000); // C2
			writeD(0000); // C2
			writeD(0000); // C2
			writeD(0000); // C2
			
			writeC(0000); // C2
			writeC(0x00); // event team?
			
			writeF(template.getCollisionRadius());
			writeF(template.getCollisionHeight());
			writeD(0x00); // C4
		}
		else
		{
			writeC(0x03);
			writeD(x);
			writeD(y);
			writeD(z);
			writeD(heading);
			writeD(cha.getObjectId());
			
			writeS(cha.getName());
			writeD(cha.getRace().ordinal());
			writeD(cha.getSex().ordinal());
			
			if (cha.getClassIndex() == 0)
			{
				writeD(cha.getClassId().getId());
			}
			else
			{
				writeD(cha.getBaseClass());
			}
			
			writeD(0); // unknown, maybe underwear?
			writeD(inv.getPaperdollItemId(ParpedollType.HEAD));
			writeD(inv.getPaperdollItemId(ParpedollType.RHAND));
			writeD(inv.getPaperdollItemId(ParpedollType.LHAND));
			writeD(inv.getPaperdollItemId(ParpedollType.GLOVES));
			writeD(inv.getPaperdollItemId(ParpedollType.CHEST));
			writeD(inv.getPaperdollItemId(ParpedollType.LEGS));
			writeD(inv.getPaperdollItemId(ParpedollType.FEET));
			writeD(inv.getPaperdollItemId(ParpedollType.BACK));
			writeD(inv.getPaperdollItemId(ParpedollType.LRHAND));
			writeD(inv.getPaperdollItemId(ParpedollType.HAIR));
			
			writeD(cha.getPvpFlag().ordinal());
			writeD(cha.getKarma());
			
			writeD(mAtkSpd);
			writeD(pAtkSpd);
			
			writeD(cha.getPvpFlag().ordinal());
			writeD(cha.getKarma());
			
			writeD(runSpd);
			writeD(walkSpd);
			writeD(swimRunSpd); // swimspeed
			writeD(swimWalkSpd); // swimspeed
			writeD(flRunSpd);
			writeD(flWalkSpd);
			writeD(flyRunSpd);
			writeD(flyWalkSpd);
			writeF(cha.getStat().getMovementSpeedMultiplier());
			writeF(cha.getStat().getAttackSpeedMultiplier());
			
			L2Summon pet = cha.getPet();
			if ((cha.isMounted()) && (pet != null))
			{
				writeF(pet.getTemplate().getCollisionRadius());
				writeF(pet.getTemplate().getCollisionHeight());
			}
			else
			{
				writeF(cha.getCollisionRadius());
				writeF(cha.getCollisionHeight());
			}
			
			writeD(cha.getHairStyle());
			writeD(cha.getHairColor());
			writeD(cha.getFace());
			
			if (gmSeeInvis)
			{
				writeS("Invisible");
			}
			else
			{
				writeS(cha.getTitle());
			}
			
			writeD(cha.getClanId());
			writeD(cha.getClanCrestId());
			writeD(cha.getAllyId());
			writeD(cha.getAllyCrestId());
			// In UserInfo leader rights and siege flags, but here found nothing??
			// Therefore RelationChanged packet with that info is required
			writeD(0);
			
			writeC(cha.isSitting() ? 0 : 1); // standing = 1 sitting = 0
			writeC(cha.isRunning() ? 1 : 0); // running = 1 walking = 0
			writeC(cha.isInCombat() ? 1 : 0);
			writeC(!cha.isInOlympiadMode() && cha.isAlikeDead() ? 1 : 0);// En olympiadas los players no mueren
			
			if (gmSeeInvis)
			{
				writeC(0);
			}
			else
			{
				writeC(cha.getInvisible() ? 1 : 0); // invisible = 1 visible =0
			}
			
			writeC(cha.getMountType().ordinal()); // 1 on strider 2 on wyvern 0 no mount
			writeC(ObjectData.get(PlayerHolder.class, cha).isSellBuff() ? 1 : cha.getPrivateStore().getStoreType().getValue());
			
			writeH(cha.getCubics().size());
			for (CubicType type : cha.getCubics().keySet())
			{
				writeH(type.ordinal());
			}
			
			writeC(cha.isLookingForParty() ? 1 : 0);
			
			writeD(cha.getAbnormalEffect());
			
			writeC(cha.getRecomLeft());
			writeH(cha.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
			writeD(cha.getMountNpcId() + 1000000);
			
			writeD(cha.getClassId().getId());
			
			writeD(0x00); // ??
			
			writeC(cha.isMounted() ? 0 : cha.getEnchantEffect());
			
			writeC(cha.getTeam().ordinal()); // team circle around feet 1= Blue, 2 = red
			
			writeD(cha.getClanCrestLargeId());
			writeC(cha.isNoble() ? 1 : 0); // Symbol on char menu ctrl+I
			writeC((cha.isHero() || (cha.isGM() && Config.GM_HERO_AURA)) ? 1 : 0); // Hero Aura
			
			writeC(cha.getFishing().isFishing() ? 1 : 0); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
			writeD(cha.getFishing().getLoc().getX());
			writeD(cha.getFishing().getLoc().getY());
			writeD(cha.getFishing().getLoc().getZ());
			
			writeD(cha.getNameColor());
		}
	}
}
