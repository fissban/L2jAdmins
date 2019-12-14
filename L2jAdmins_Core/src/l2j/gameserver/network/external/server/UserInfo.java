package l2j.gameserver.network.external.server;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.AServerPacket;
import main.data.memory.ObjectData;
import main.holders.objects.PlayerHolder;

/**
 * 0000: 04 03 15 00 00 77 ff 00 00 80 f1 ff ff 00 00 00 .....w.......... 0010: 00 2a 89 00 4c 43 00 61 00 6c 00 61 00 64 00 6f .*..LC.a.l.a.d.o 0020: 00 6e 00 00 00 01 00 00 00 00 00 00 00 19 00 00 .n.............. 0030: 00 0d 00 00 00 ee 81 02 00 15 00 00 00 18 00 00 ................ 0040: 00 19
 * 00 00 00 25 00 00 00 17 00 00 00 28 00 00 .....%.......(.. 0050: 00 14 01 00 00 14 01 00 00 02 01 00 00 02 01 00 ................ 0060: 00 fa 09 00 00 81 06 00 00 26 34 00 00 2e 00 00 .........&4..... 0070: 00 00 00 00 00 db 9f a1 41 93 26 64 41 de c8 31 ........A.&dA..1 0080: 41 ca 73 c0 41 d5
 * 22 d0 41 83 bd 41 41 81 56 10 A.s.A.".A..AA.V. 0090: 41 00 00 00 00 27 7d 30 41 69 aa e0 40 b4 fb d3 A....'}0Ai..@... 00a0: 41 91 f9 63 41 00 00 00 00 81 56 10 41 00 00 00 A..cA.....V.A... 00b0: 00 71 00 00 00 71 00 00 00 76 00 00 00 74 00 00 .q...q...v...t.. 00c0: 00 74 00 00 00 2a 00 00 00 e8
 * 02 00 00 00 00 00 .t...*.......... 00d0: 00 5f 04 00 00 ac 01 00 00 cf 01 00 00 62 04 00 ............b.. 00e0: 00 00 00 00 00 e8 02 00 00 0b 00 00 00 52 01 00 .............R.. 00f0: 00 4d 00 00 00 2a 00 00 00 2f 00 00 00 29 00 00 .M...*.../...).. 0100: 00 12 00 00 00 82 01 00 00 52 01 00 00 53 00
 * 00 .........R...S.. 0110: 00 00 00 00 00 00 00 00 00 7a 00 00 00 55 00 00 .........z...U.. 0120: 00 32 00 00 00 32 00 00 00 00 00 00 00 00 00 00 .2...2.......... 0130: 00 00 00 00 00 00 00 00 00 a4 70 3d 0a d7 a3 f0 ..........p=.... 0140: 3f 64 5d dc 46 03 78 f3 3f 00 00 00 00 00 00 1e
 * ?d].F.x.?....... 0150: 40 00 00 00 00 00 00 38 40 02 00 00 00 01 00 00 @......8@....... 0160: 00 00 00 00 00 00 00 00 00 00 00 c1 0c 00 00 01 ................ 0170: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ................ 0180: 00 00 00 00 ....
 * dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSdddcccdd (h) dddddSddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd ffffddddSdddddcccddh (h) c dc hhdh
 * dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSdddddcccddh (h) c dc hhdh ddddc c dcc cddd d (from 654) but it actually reads dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSdddddcccddh (h) c dc *dddddddd* hhdh ddddc dcc
 * cddd d *...*: here i am not sure at least it looks like it reads that much data (32 bytes), not sure about the format inside because it is not read thanks to the ususal parsing function
 * @version $Revision: 1.14.2.4.2.12 $ $Date: 2005/04/11 10:05:55 $
 */
public class UserInfo extends AServerPacket
{
	private final L2PcInstance cha;
	private float moveSpeedMultiplier;
	private final int runSpd, walkSpd, swimRunSpd, swimWalkSpd;
	private int flRunSpd;
	private int flWalkSpd;
	private int flyRunSpd;
	private int flyWalkSpd;
	private int relation;
	
	/**
	 * @param cha
	 */
	public UserInfo(L2PcInstance cha)
	{
		this.cha = cha;
		moveSpeedMultiplier = cha.getStat().getMovementSpeedMultiplier();
		runSpd = Math.round((cha.getStat().getRunSpeed() / moveSpeedMultiplier));
		walkSpd = (int) (cha.getStat().getWalkSpeed() / moveSpeedMultiplier);
		swimRunSpd = flRunSpd = flyRunSpd = runSpd;
		swimWalkSpd = flWalkSpd = flyWalkSpd = walkSpd;
		relation = cha.isClanLeader() ? 0x40 : 0;
		if (cha.getSiegeState() == PlayerSiegeStateType.ATACKER)
		{
			relation |= 0x180;
		}
		if (cha.getSiegeState() == PlayerSiegeStateType.DEFENDER)
		{
			relation |= 0x80;
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x04);
		
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
		writeD(cha.getHeading());
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
		
		writeD(cha.getLevel());
		writeD((int) cha.getExp());
		writeD(cha.getStat().getSTR());
		writeD(cha.getStat().getDEX());
		writeD(cha.getStat().getCON());
		writeD(cha.getStat().getINT());
		writeD(cha.getStat().getWIT());
		writeD(cha.getStat().getMEN());
		writeD(cha.getStat().getMaxHp());
		writeD((int) cha.getCurrentHp());
		writeD(cha.getStat().getMaxMp());
		writeD((int) cha.getCurrentMp());
		writeD(cha.getSp());
		writeD(cha.getInventory().getTotalWeight());
		writeD(cha.getMaxLoad());
		
		writeD(0x28); // unknown
		
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.UNDER));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.REAR));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.LEAR));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.NECK));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.RFINGER));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.LFINGER));
		
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.HEAD));
		
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.RHAND));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.LHAND));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.GLOVES));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.CHEST));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.LEGS));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.FEET));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.BACK));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.LRHAND));
		writeD(cha.getInventory().getPaperdollObjectId(ParpedollType.HAIR));
		
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.UNDER));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.REAR));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.LEAR));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.NECK));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.RFINGER));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.LFINGER));
		
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.HEAD));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.RHAND));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.LHAND));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.GLOVES));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.CHEST));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.LEGS));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.FEET));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.BACK));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.LRHAND));
		writeD(cha.getInventory().getPaperdollItemId(ParpedollType.HAIR));
		
		writeD(cha.getStat().getPAtk(null));
		writeD(cha.getStat().getPAtkSpd());
		writeD(cha.getStat().getPDef(null));
		writeD(cha.getStat().getEvasionRate(null));
		writeD(cha.getStat().getAccuracy());
		writeD(cha.getStat().getCriticalHit(null, null));
		writeD(cha.getStat().getMAtk(null, null));
		
		writeD(cha.getStat().getMAtkSpd());
		writeD(cha.getStat().getPAtkSpd());
		
		writeD(cha.getStat().getMDef(null, null));
		
		writeD(cha.getPvpFlag().ordinal()); // 0-non-pvp 1-pvp = violett name
		writeD(cha.getKarma());
		
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
		writeD((cha.getAccessLevel() > 0) ? 1 : 0); // builder level
		
		String title = cha.getTitle();
		if (cha.getInvisible() && cha.isGM())
		{
			title = "Invisible";
		}
		if (cha.isPoly())
		{
			NpcTemplate polyObj = NpcData.getInstance().getTemplate(cha.getPolyId());
			if (polyObj != null)
			{
				title += " - " + polyObj.getName();
			}
		}
		writeS(title);
		
		writeD(cha.getClanId());
		writeD(cha.getClanCrestId());
		writeD(cha.getAllyId());
		writeD(cha.getAllyCrestId()); // ally crest id
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeD(relation);
		writeC(cha.getMountType().ordinal()); // mount type
		writeC(ObjectData.get(PlayerHolder.class, cha).isSellBuff() ? 1 : cha.getPrivateStore().getStoreType().getValue());
		writeC(cha.hasDwarvenCraft() ? 1 : 0);
		writeD(cha.getPkKills());
		writeD(cha.getPvpKills());
		
		writeH(cha.getCubics().size());
		for (CubicType type : cha.getCubics().keySet())
		{
			writeH(type.ordinal());
		}
		
		writeC(cha.isLookingForParty() ? 1 : 0);
		
		writeD(cha.getAbnormalEffect());
		writeC(0x00);
		
		writeD(cha.getClanPrivileges());
		writeD(0x00); // swim
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		
		writeH(cha.getRecomLeft()); // c2 recommendations remaining
		writeH(cha.getRecomHave()); // c2 recommendations received
		writeD(cha.getMountNpcId() + 1000000);
		writeH(cha.getInventoryLimit());
		
		writeD(cha.getClassId().getId());
		writeD(0x00); // special effects? circles around player...
		writeD(cha.getStat().getMaxCp());
		writeD((int) cha.getCurrentCp());
		writeC(cha.isMounted() ? 0 : cha.getEnchantEffect());
		
		writeC(cha.getTeam().ordinal()); // team circle around feet 1= Blue, 2 = red
		
		writeD(cha.getClanCrestLargeId());
		writeC(cha.isNoble() ? 1 : 0); // 0x01: symbol on char menu ctrl+I
		writeC((cha.isHero() || (cha.isGM() && Config.GM_HERO_AURA)) ? 1 : 0); // 0x01: Hero Aura
		
		writeC(cha.getFishing().isFishing() ? 1 : 0); // Fishing Mode
		writeD(cha.getFishing().getLoc().getX()); // fishing x
		writeD(cha.getFishing().getLoc().getY()); // fishing y
		writeD(cha.getFishing().getLoc().getZ()); // fishing z
		writeD(cha.getNameColor());
	}
}
