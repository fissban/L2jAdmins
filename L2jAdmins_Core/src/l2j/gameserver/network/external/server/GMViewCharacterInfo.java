package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.AServerPacket;

/**
 * TODO Add support for Eval. Score dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSddd rev420 dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSdddcccddhh rev478
 * dddddSdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffddddSdddcccddhhddd rev551
 * @version $Revision: 1.2.2.2.2.8 $ $Date: 2005/03/27 15:29:39 $
 */
public class GMViewCharacterInfo extends AServerPacket
{
	private final L2PcInstance cha;
	private final int runSpd;
	private final int walkSpd;
	private final float moveMultiplier;
	
	/**
	 * @param cha
	 */
	public GMViewCharacterInfo(L2PcInstance cha)
	{
		this.cha = cha;
		moveMultiplier = cha.getStat().getMovementSpeedMultiplier();
		runSpd = (int) (cha.getStat().getRunSpeed() / moveMultiplier);
		walkSpd = (int) (cha.getStat().getWalkSpeed() / moveMultiplier);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x8f);
		
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
		writeD(cha.getHeading());
		writeD(cha.getObjectId());
		writeS(cha.getName());
		writeD(cha.getRace().ordinal());
		writeD(cha.getSex().ordinal());
		writeD(cha.getClassId().getId());
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
		writeD(runSpd); // swimspeed
		writeD(walkSpd); // swimspeed
		writeD(runSpd);
		writeD(walkSpd);
		writeD(runSpd);
		writeD(walkSpd);
		writeF(moveMultiplier);
		writeF(cha.getStat().getAttackSpeedMultiplier()); // 2.9);//
		writeF(cha.getTemplate().getCollisionRadius()); // scale
		writeF(cha.getTemplate().getCollisionHeight()); // y offset ??!? fem dwarf 4033
		writeD(cha.getHairStyle());
		writeD(cha.getHairColor());
		writeD(cha.getFace());
		writeD(cha.isGM() ? 0x01 : 0x00); // builder level
		
		writeS(cha.getTitle());
		writeD(cha.getClanId()); // pledge id
		writeD(cha.getClanCrestId()); // pledge crest id
		writeD(cha.getAllyId()); // ally id
		writeC(cha.getMountType().ordinal()); // ??
		writeC(cha.getPrivateStore().getStoreType().getValue()); // ??
		writeC(cha.hasDwarvenCraft() ? 1 : 0); // ??
		writeD(cha.getPkKills());
		writeD(cha.getPvpKills());
		
		writeH(cha.getRecomLeft());
		writeH(cha.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeD(cha.getClassId().getId());
		
		writeD(cha.getStat().getMaxCp());
		writeD((int) cha.getCurrentCp());
	}
}
