package l2j.gameserver.network.external.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.DatabaseManager;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.base.Sex;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.holder.CharSelectInfoHolder;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.GameClient;

/**
 * This class ...
 * @version $Revision: 1.8.2.4.2.6 $ $Date: 2005/04/06 16:13:46 $
 */
public class CharSelectInfo extends AServerPacket
{
	// d SdSddddddddddffddddddddddddddddddddddddddddddddddddddddddddddffd
	private final String loginName;
	private final int sessionId;
	private final List<CharSelectInfoHolder> characterPackages;
	
	/**
	 * @param loginName
	 * @param sessionId
	 */
	public CharSelectInfo(String loginName, int sessionId)
	{
		this.sessionId = sessionId;
		this.loginName = loginName;
		characterPackages = loadCharacterSelectInfo();
	}
	
	public List<CharSelectInfoHolder> getCharInfo()
	{
		return characterPackages;
	}
	
	@Override
	public void writeImpl()
	{
		int size = (characterPackages.size());
		
		writeC(0x13);
		writeD(size);
		
		long lastAccess = 0L;
		int lastUsed = -1;
		
		for (int i = 0; i < size; i++)
		{
			if (lastAccess < characterPackages.get(i).getLastAccess())
			{
				lastAccess = characterPackages.get(i).getLastAccess();
				lastUsed = i;
			}
		}
		
		for (int i = 0; i < size; i++)
		{
			CharSelectInfoHolder csi = characterPackages.get(i);
			
			writeS(csi.getName());
			writeD(csi.getCharId()); // ?
			writeS(loginName);
			writeD(sessionId);
			writeD(csi.getClanId());
			writeD(0x00); // ??
			
			writeD(csi.getSex().ordinal());
			writeD(csi.getRace());
			
			if (csi.getClassId() == csi.getBaseClassId())
			{
				writeD(csi.getClassId());
			}
			else
			{
				writeD(csi.getBaseClassId());
			}
			
			writeD(0x01); // active ??
			
			writeD(csi.getLoc().getX());
			writeD(csi.getLoc().getY());
			writeD(csi.getLoc().getZ());
			
			writeF(csi.getCurrentHp());
			writeF(csi.getCurrentMp());
			
			writeD(csi.getSp());
			writeD(csi.getExp());
			writeD(csi.getLevel());
			
			writeD(csi.getKarma());
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeD(csi.getPaperdollObjectId(ParpedollType.UNDER));
			writeD(csi.getPaperdollObjectId(ParpedollType.REAR));
			writeD(csi.getPaperdollObjectId(ParpedollType.LEAR));
			writeD(csi.getPaperdollObjectId(ParpedollType.NECK));
			writeD(csi.getPaperdollObjectId(ParpedollType.RFINGER));
			writeD(csi.getPaperdollObjectId(ParpedollType.LFINGER));
			writeD(csi.getPaperdollObjectId(ParpedollType.HEAD));
			writeD(csi.getPaperdollObjectId(ParpedollType.RHAND));
			writeD(csi.getPaperdollObjectId(ParpedollType.LHAND));
			writeD(csi.getPaperdollObjectId(ParpedollType.GLOVES));
			writeD(csi.getPaperdollObjectId(ParpedollType.CHEST));
			writeD(csi.getPaperdollObjectId(ParpedollType.LEGS));
			writeD(csi.getPaperdollObjectId(ParpedollType.FEET));
			writeD(csi.getPaperdollObjectId(ParpedollType.BACK));
			writeD(csi.getPaperdollObjectId(ParpedollType.LRHAND));
			writeD(csi.getPaperdollObjectId(ParpedollType.HAIR));
			
			writeD(csi.getPaperdollItemId(ParpedollType.UNDER));
			writeD(csi.getPaperdollItemId(ParpedollType.REAR));
			writeD(csi.getPaperdollItemId(ParpedollType.LEAR));
			writeD(csi.getPaperdollItemId(ParpedollType.NECK));
			writeD(csi.getPaperdollItemId(ParpedollType.RFINGER));
			writeD(csi.getPaperdollItemId(ParpedollType.LFINGER));
			writeD(csi.getPaperdollItemId(ParpedollType.HEAD));
			writeD(csi.getPaperdollItemId(ParpedollType.RHAND));
			writeD(csi.getPaperdollItemId(ParpedollType.LHAND));
			writeD(csi.getPaperdollItemId(ParpedollType.GLOVES));
			writeD(csi.getPaperdollItemId(ParpedollType.CHEST));
			writeD(csi.getPaperdollItemId(ParpedollType.LEGS));
			writeD(csi.getPaperdollItemId(ParpedollType.FEET));
			writeD(csi.getPaperdollItemId(ParpedollType.BACK));
			writeD(csi.getPaperdollItemId(ParpedollType.LRHAND));
			writeD(csi.getPaperdollItemId(ParpedollType.HAIR));
			
			writeD(csi.getHairStyle());
			writeD(csi.getHairColor());
			writeD(csi.getFace());
			
			writeF(csi.getMaxHp()); // hp max
			writeF(csi.getMaxMp()); // mp max
			
			writeD((csi.getAccessLevel() >= 0) ? ((csi.getDeleteTimer() > 0) ? (int) ((csi.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0) : -1);
			writeD(csi.getClassId());
			writeD((i == lastUsed) ? 1 : 0);
			
			writeC(csi.getEnchantEffect() > 127 ? 127 : csi.getEnchantEffect());
		}
		
		getClient().setCharSelectSlot(characterPackages);
	}
	
	private List<CharSelectInfoHolder> loadCharacterSelectInfo()
	{
		CharSelectInfoHolder charInfopackage;
		List<CharSelectInfoHolder> characterList = new ArrayList<>();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT obj_id,char_name,deletetime,clanid,level,maxHp,curHp,maxMp,curMp,karma,face,hairstyle,haircolor,sex,exp,sp,race,x,y,z,accesslevel,base_class,classid,lastAccess FROM characters WHERE account_name=?"))
		{
			ps.setString(1, loginName);
			try (ResultSet charList = ps.executeQuery())
			{
				while (charList.next()) // fills the package
				{
					charInfopackage = restoreChar(charList);
					if (charInfopackage != null)
					{
						characterList.add(charInfopackage);
					}
				}
			}
			
			return characterList;
		}
		catch (Exception e)
		{
			LOG.warning("Could not restore char info: " + e);
		}
		
		return Collections.emptyList();
	}
	
	private void loadCharacterSubclassInfo(CharSelectInfoHolder charInfopackage, int objectId, int activeClassId)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT exp, sp, level FROM character_subclasses WHERE char_obj_id=? && class_id=? ORDER BY char_obj_id"))
		{
			ps.setInt(1, objectId);
			ps.setInt(2, activeClassId);
			try (ResultSet charList = ps.executeQuery())
			{
				if (charList.next())
				{
					charInfopackage.setExp(charList.getInt("exp"));
					charInfopackage.setSp(charList.getInt("sp"));
					charInfopackage.setLevel(charList.getInt("level"));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Could not restore char subclass info: " + e);
		}
	}
	
	private CharSelectInfoHolder restoreChar(ResultSet charData) throws Exception
	{
		int objectId = charData.getInt("obj_id");
		
		String name = charData.getString("char_name");
		
		// See if the char must be deleted
		long deleteTime = charData.getLong("deletetime");
		if (deleteTime > 0)
		{
			if (System.currentTimeMillis() > deleteTime)
			{
				Clan clan = ClanData.getInstance().getClanById(charData.getInt("clanid"));
				if (clan != null)
				{
					clan.removeClanMember(objectId, 0);
				}
				
				GameClient.deleteCharByObjId(objectId);
				return null;
			}
		}
		
		CharSelectInfoHolder charInfoPackage = new CharSelectInfoHolder(objectId, name);
		charInfoPackage.setLevel(charData.getInt("level"));
		charInfoPackage.setMaxHp(charData.getInt("maxHp"));
		charInfoPackage.setCurrentHp(charData.getDouble("curHp"));
		charInfoPackage.setMaxMp(charData.getInt("maxMp"));
		charInfoPackage.setCurrentMp(charData.getDouble("curMp"));
		charInfoPackage.setKarma(charData.getInt("karma"));
		
		charInfoPackage.setFace(charData.getInt("face"));
		charInfoPackage.setHairStyle(charData.getInt("hairstyle"));
		charInfoPackage.setHairColor(charData.getInt("haircolor"));
		charInfoPackage.setSex(charData.getInt("sex") == 1 ? Sex.FEMALE : Sex.MALE);
		
		charInfoPackage.setExp(charData.getInt("exp"));
		charInfoPackage.setSp(charData.getInt("sp"));
		charInfoPackage.setClanId(charData.getInt("clanid"));
		
		charInfoPackage.setRace(charData.getInt("race"));
		charInfoPackage.setLoc(charData.getInt("x"), charData.getInt("y"), charData.getInt("z"));
		
		charInfoPackage.setAccessLevel(charData.getInt("accesslevel"));
		
		final int baseClassId = charData.getInt("base_class");
		final int activeClassId = charData.getInt("classid");
		
		// if is in subclass, load subclass exp, sp, lvl info
		if (baseClassId != activeClassId)
		{
			loadCharacterSubclassInfo(charInfoPackage, objectId, activeClassId);
		}
		
		charInfoPackage.setClassId(activeClassId);
		
		/*
		 * Check if the base class is set to zero and alse doesn't match with the current active class, otherwise send the base class ID. This prevents chars created before base class was introduced from being displayed incorrectly.
		 */
		if ((baseClassId == 0) && (activeClassId > 0))
		{
			charInfoPackage.setBaseClassId(activeClassId);
		}
		else
		{
			charInfoPackage.setBaseClassId(baseClassId);
		}
		
		charInfoPackage.setDeleteTimer(deleteTime);
		charInfoPackage.setLastAccess(charData.getLong("lastAccess"));
		
		return charInfoPackage;
	}
}
