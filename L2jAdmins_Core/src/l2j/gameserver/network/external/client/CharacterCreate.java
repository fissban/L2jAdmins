package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.CharTemplateData;
import l2j.gameserver.data.InitialEquipamentData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.base.Sex;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.templates.PcTemplate;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.CharCreateFail;
import l2j.gameserver.network.external.server.CharCreateFail.CharCreateFailType;
import l2j.gameserver.network.external.server.CharCreateOk;
import l2j.gameserver.network.external.server.CharSelectInfo;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.9.2.3.2.8 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterCreate extends AClientPacket
{
	// cSdddddddddddd
	public String name;
	public int race;
	public Sex sex;
	public int classId;
	public byte hairStyle;
	public byte hairColor;
	public byte face;
	
	@Override
	protected void readImpl()
	{
		name = readS();
		race = readD();
		sex = ((byte) readD() == 1) ? Sex.FEMALE : Sex.MALE;
		classId = readD();
		readD();// int
		readD();// str
		readD();// con
		readD();// men
		readD();// dex
		readD();// wit
		hairStyle = (byte) readD();
		hairColor = (byte) readD();
		face = (byte) readD();
	}
	
	@Override
	public void runImpl()
	{
		if ((name.length() < 1) || (name.length() > 16) || !Util.isAlphaNumeric(name) || !Util.isValidNameTitle(name))
		{
			sendPacket(new CharCreateFail(CharCreateFailType.REASON_16_ENG_CHARS));
			return;
		}
		
		L2PcInstance newChar = null;
		PcTemplate template = null;
		
		/*
		 * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
		 */
		synchronized (CharNameData.getInstance())
		{
			if ((CharNameData.getInstance().accountCharNumber(getClient().getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0))
			{
				sendPacket(new CharCreateFail(CharCreateFailType.REASON_TOO_MANY_CHARACTERS));
				return;
			}
			else if (CharNameData.getInstance().doesCharNameExist(name))
			{
				sendPacket(new CharCreateFail(CharCreateFailType.REASON_NAME_ALREADY_EXISTS));
				return;
			}
			
			template = CharTemplateData.getInstance().getTemplate(classId);
			
			if ((template == null) || (template.getClassBaseLevel() > 1))
			{
				sendPacket(new CharCreateFail(CharCreateFailType.REASON_CREATION_FAILED));
				return;
			}
			
			int objectId = IdFactory.getInstance().getNextId();
			newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(), name, hairStyle, hairColor, face, sex);
		}
		
		newChar.setCurrentHp(newChar.getStat().getMaxHp());
		newChar.setCurrentCp(0);
		newChar.setCurrentMp(newChar.getStat().getMaxMp());
		
		L2World.getInstance().addObject(newChar);
		
		newChar.setXYZInvisible(template.getSpawnLoc().getX(), template.getSpawnLoc().getY(), template.getSpawnLoc().getZ());
		newChar.setTitle("");
		
		// add attack shortcut
		newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(0, 0, PcShortCutsType.ACTION, 2, -1, 1), false);
		// add take shortcut
		newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(3, 0, PcShortCutsType.ACTION, 5, -1, 1), false);
		// add sit shortcut
		newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(10, 0, PcShortCutsType.ACTION, 0, -1, 1), false);
		
		for (int itemId : InitialEquipamentData.getInstance().getItemsById(newChar.getClassId().getId()))
		{
			ItemInstance item = newChar.getInventory().addItem("Init", itemId, 1, newChar, null);
			
			if (item == null)
			{
				LOG.warning("Could not create item during char creation: itemId " + itemId + ".");
				continue;
			}
			
			if (item.getId() == 5588)
			{
				// add tutbook shortcut
				newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(11, 0, PcShortCutsType.ITEM, item.getObjectId(), -1, 1), false);
			}
			
			if (item.isEquipable())
			{
				if ((newChar.getActiveWeaponItem() == null) || !(item.getItem().getType2() != ItemType2.WEAPON))
				{
					newChar.getInventory().equipItemAndRecord(item);
				}
			}
		}
		
		for (SkillLearnHolder startSkill : SkillTreeData.getAvailableSkillsTrainer(newChar, newChar.getClassId()))
		{
			newChar.addSkill(SkillData.getInstance().getSkill(startSkill.getId(), startSkill.getLevel()), true);
			if ((startSkill.getId() == 1001) || (startSkill.getId() == 1177))
			{
				newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(1, 0, PcShortCutsType.SKILL, startSkill.getId(), startSkill.getLevel(), 1), false);
			}
			
			if (startSkill.getId() == 1216)
			{
				newChar.getShortCuts().registerShortCut(new PcShortCutsInstance(9, 0, PcShortCutsType.SKILL, startSkill.getId(), startSkill.getLevel(), 1), false);
			}
			
			if (Config.DEBUG)
			{
				LOG.fine("adding starter skill:" + startSkill.getId() + " / " + startSkill.getLevel());
			}
		}
		
		if (Config.ALT_ENABLE_TUTORIAL)
		{
			startTutorialQuest(newChar);
		}
		
		EngineModsManager.onCreateCharacter(newChar);
		
		newChar.deleteMe(); // release the world of this character and it's inventory
		
		// send char list
		CharSelectInfo cs = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		getClient().getConnection().sendPacket(cs);
		getClient().setCharSelectSlot(cs.getCharInfo());
		
		// send acknowledgement
		sendPacket(CharCreateOk.STATIC_PACKET);
	}
	
	public void startTutorialQuest(L2PcInstance player)
	{
		if (player.getScriptState("Q255_Tutorial") == null)
		{
			Script s = ScriptsData.get("Q255_Tutorial");
			
			if (s != null)
			{
				s.newState(player).setState(ScriptStateType.STARTED);
			}
		}
	}
}
