package l2j.gameserver.model.actor.instance;

import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.base.ClassType;
import l2j.gameserver.model.actor.base.PlayerClass;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.base.SubClass;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.olympiad.OlympiadManager;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.ScriptState;

/**
 * This class ...
 * @version $Revision: 1.4.2.3.2.8 $ $Date: 2005/03/29 23:15:15 $
 */
public final class L2VillageMasterInstance extends L2Npc
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2VillageMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2VillageMasterInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken(); // actual command
		
		if (event.equals("clan"))
		{
			if (!st.hasMoreTokens())
			{
				sendHtml(player, "CreateClan-01.htm");
			}
			else
			{
				switch (st.nextToken())
				{
					case "CreateClan-01.htm":
						sendHtml(player, "CreateClan-01.htm");
						break;
					
					case "CreateClan-03.htm":
						sendHtml(player, "CreateClan-03.htm");
						break;
					
					case "CreateClan-04.htm":
						if (player.isClanLeader())
						{
							sendHtml(player, "CreateClan-04.htm");
						}
						else if (player.getClanId() != 0)
						{
							sendHtml(player, "CreateClan-06.htm");
						}
						else
						{
							sendHtml(player, "CreateClan-07.htm");
						}
						break;
					
					case "CreateClan-05.htm":
						if (player.isClanLeader())
						{
							sendHtml(player, "CreateClan-05.htm");
						}
						else if (player.getClanId() != 0)
						{
							sendHtml(player, "CreateClan-08.htm");
						}
						else
						{
							sendHtml(player, "CreateClan-07.htm");
						}
						break;
					
					default:
						sendHtml(player, "CreateClan-02.htm");
						break;
				}
			}
		}
		else if (event.equals("ally"))
		{
			String file = "";
			if (!st.hasMoreTokens())
			{
				file = "CreateAlliance-01.htm";
			}
			else
			{
				file = st.nextToken();
				
				if (player.getClan() == null)
				{
					file = "CreateAlliance-04.htm";
				}
			}
			sendHtml(player, file);
		}
		else if (event.equalsIgnoreCase("create_clan"))
		{
			if (!st.hasMoreTokens())
			{
				return;
			}
			
			ClanData.getInstance().createClan(player, st.nextToken());
		}
		else if (event.equalsIgnoreCase("create_ally"))
		{
			if (!st.hasMoreTokens())
			{
				return;
			}
			
			if (player.getClan() != null)
			{
				player.getClan().createAlly(player, st.nextToken());
			}
		}
		else if (event.equalsIgnoreCase("dissolve_ally"))
		{
			if (player.getClan() != null)
			{
				player.getClan().dissolveAlly(player);
			}
		}
		else if (event.equalsIgnoreCase("dissolve_clan"))
		{
			dissolveClan(player, player.getClanId());
		}
		else if (event.equalsIgnoreCase("recover_clan"))
		{
			recoverClan(player, player.getClanId());
		}
		else if (event.equalsIgnoreCase("increase_clan_level"))
		{
			levelUpClan(player, player.getClanId());
		}
		else if (event.equals("Subclass"))
		{
			int cmdChoice = Integer.parseInt(st.nextToken());
			
			// Subclasses may not be changed while a skill is in use.
			if (player.isCastingNow() || player.isAllSkillsDisabled())
			{
				player.sendPacket(SystemMessage.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
				return;
			}
			
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST);
				return;
			}
			
			StringBuilder sb = new StringBuilder("<html><body>");
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			Set<PlayerClass> subsAvailable;
			
			int paramOne = 0;
			int paramTwo = 0;
			
			try
			{
				int endIndex = command.length();
				
				if (command.length() > 13)
				{
					endIndex = 13;
					paramTwo = Integer.parseInt(command.substring(13).trim());
				}
				
				paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
			}
			catch (Exception NumberFormatException)
			{
			}
			
			switch (cmdChoice)
			{
				case 1: // Add Subclass - Initial
					// Avoid giving player an option to add a new sub class, if they have three already.
					if (player.getTotalSubClasses() == Config.ALT_MAX_SUBCLASS)
					{
						player.sendMessage("You may only add up to " + Config.ALT_MAX_SUBCLASS + " subclasses at a time.");
						return;
					}
					
					subsAvailable = getAvailableSubClasses(player);
					
					if ((subsAvailable != null) && !subsAvailable.isEmpty())
					{
						sb.append("Add Subclass:<br>Which sub class do you wish to add?<br>");
						
						for (PlayerClass subClass : subsAvailable)
						{
							sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 4 " + subClass.ordinal() + "\" msg=\"1268;" + formatClassForDisplay(subClass) + "\">" + formatClassForDisplay(subClass) + "</a><br>");
						}
					}
					else
					{
						player.sendMessage("There are no available subclasses at this time.");
						return;
					}
					break;
				case 2: // Change Class - Initial
					sb.append("Change Subclass:<br>");
					
					final int baseClassId = player.getBaseClass();
					
					if (player.getSubClasses().isEmpty())
					{
						sb.append("You can't change subclasses when you don't have a subclass to begin with.<br>" + "<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 1\">Add subclass.</a>");
					}
					else
					{
						sb.append("Which class would you like to switch to?<br>");
						
						if (baseClassId == player.getActiveClass())
						{
							sb.append(ClassId.getById(baseClassId).getName() + "&nbsp;<font color=\"LEVEL\">(Base Class)</font><br><br>");
						}
						else
						{
							sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 5 0\">" + ClassId.getById(baseClassId).getName() + "</a>&nbsp;" + "<font color=\"LEVEL\">(Base Class)</font><br><br>");
						}
						
						for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							int subClassId = subClass.getClassId();
							
							if (subClassId == player.getActiveClass())
							{
								sb.append(ClassId.getById(subClassId).getName() + "<br>");
							}
							else
							{
								sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 5 " + subClass.getClassIndex() + "\">" + ClassId.getById(subClassId).getName() + "</a><br>");
							}
						}
					}
					break;
				case 3: // Change/Cancel Subclass - Initial
					sb.append("Change Subclass:<br>Which of the following sub classes would you like to change?<br>");
					int classIndex = 1;
					
					for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
					{
						SubClass subClass = subList.next();
						
						sb.append("Sub-class " + classIndex + "<br1>");
						sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 6 " + subClass.getClassIndex() + "\">" + ClassId.getById(subClass.getClassId()).getName() + "</a><br>");
						
						classIndex++;
					}
					
					sb.append("<br>If you change a sub class, you'll start at level 40 after the 2nd class transfer.");
					break;
				case 4: // Add Subclass - Action (Subclass 4 x[x])
					boolean allowAddition = true;
					
					/*
					 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
					 */
					
					if (!player.tryToUseAction(FloodProtectorType.SUBCLASS))
					{
						LOG.warning("Player " + player.getName() + " has performed a subclass change too fast.");
						return;
					}
					
					if (player.getLevel() < 75)
					{
						player.sendMessage("You may not add a new subclass until all your subclasses reach level 75.");
						allowAddition = false;
					}
					
					if (allowAddition)
					{
						if (!player.getSubClasses().isEmpty())
						{
							for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
							{
								SubClass subClass = subList.next();
								
								if (subClass.getLevel() < 75)
								{
									player.sendMessage("You may not add a new subclass until all your subclasses reach level 75.");
									allowAddition = false;
									break;
								}
							}
						}
					}
					
					if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
					{
						ScriptState qs = player.getScriptState("Q235_MimirsElixir");
						if ((qs == null) || !qs.isCompleted())
						{
							allowAddition = false;
						}
					}
					
					if (allowAddition)
					{
						String className = ClassId.getById(paramOne).getName();
						
						if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1))
						{
							player.sendMessage("The subclass could not be added.");
							return;
						}
						
						player.setActiveClass(player.getTotalSubClasses());
						
						sb.append("Add Subclass:<br>The sub class of <font color=\"LEVEL\">" + className + "</font> has been added.");
						player.sendPacket(new SystemMessage(SystemMessage.CLASS_TRANSFER)); // Transfer to new class.
					}
					else
					{
						html.setFile("data/html/villageMaster/SubClass_Fail.htm");
					}
					
					break;
				case 5: // Change Class - Action
					/*
					 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice. Note: paramOne = classIndex
					 */
					
					if (!player.tryToUseAction(FloodProtectorType.SUBCLASS))
					{
						LOG.warning("Player " + player.getName() + " has performed a subclass change too fast");
						return;
					}
					
					player.setActiveClass(paramOne);
					
					sb.append("Change Subclass:<br>Your active sub class is now a <font color=\"LEVEL\">" + ClassId.getById(player.getActiveClass()).getName() + "</font>.");
					
					player.sendPacket(SystemMessage.SUBCLASS_TRANSFER_COMPLETED); // Transfer completed.
					break;
				case 6: // Change/Cancel Subclass - Choice
					sb.append("Please choose a sub class to change to. If the one you are looking for is not here, " + "please seek out the appropriate master for that class.<br>" + "<font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>");
					
					subsAvailable = getAvailableSubClasses(player);
					
					if ((subsAvailable != null) && !subsAvailable.isEmpty())
					{
						for (PlayerClass subClass : subsAvailable)
						{
							sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 7 " + paramOne + " " + subClass.ordinal() + "\">" + formatClassForDisplay(subClass) + "</a><br>");
						}
					}
					else
					{
						player.sendMessage("There are no available subclasses at this time.");
						return;
					}
					break;
				case 7: // Change Subclass - Action
					/*
					 * Warning: the information about this subclass will be removed from the subclass list even if false!
					 */
					if (!player.tryToUseAction(FloodProtectorType.SUBCLASS))
					{
						LOG.warning("Player " + player.getName() + " has performed a subclass change too fast");
						return;
					}
					
					if (player.modifySubClass(paramOne, paramTwo))
					{
						player.setActiveClass(paramOne);
						sb.append("Change Subclass:<br>Your sub class has been changed to <font color=\"LEVEL\">" + ClassId.getById(paramTwo).getName() + "</font>.");
						player.sendPacket(SystemMessage.ADD_NEW_SUBCLASS); // Subclass added.
					}
					else
					{
						/*
						 * This isn't good! modifySubClass() removed subclass from memory we must update classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with other seemingly unrelated problems.
						 */
						player.setActiveClass(0); // Also updates classIndex plus switching classid to baseclass.
						player.sendMessage("The subclass could not be added, you have been reverted to your base class.");
						return;
					}
					break;
			}
			
			sb.append("</body></html>");
			
			// If the content is greater than for a basic blank page,
			// then assume no external HTML file was assigned.
			if (sb.length() > 26)
			{
				html.setHtml(sb.toString());
			}
			
			player.sendPacket(html);
		}
		else
		{
			// this class dont know any other commands, let forward
			// the command to the parent class
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/villageMaster/" + pom + ".htm";
	}
	
	private void dissolveClan(L2PcInstance player, int clanId)
	{
		if (Config.DEBUG)
		{
			LOG.fine(player.getObjectId() + "(" + player.getName() + ") requested dissolve a clan from " + getObjectId() + "(" + getName() + ")");
		}
		
		if (!player.isClanLeader())
		{
			return;
		}
		
		Clan clan = player.getClan();
		if (clan.getAllyId() != 0)
		{
			player.sendPacket(SystemMessage.CANNOT_DISPERSE_THE_CLANS_IN_ALLY);
			return;
		}
		
		if (clan.isAtWar())
		{
			player.sendPacket(SystemMessage.CANNOT_DISSOLVE_WHILE_IN_WAR);
			return;
		}
		
		if ((clan.hasCastle()) || (clan.hasClanHall()))
		{
			player.sendPacket(SystemMessage.CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE);
			return;
		}
		
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getId()))
			{
				player.sendPacket(SystemMessage.CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE);
				return;
			}
		}
		
		if (player.isInsideZone(ZoneType.SIEGE))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CANNOT_DISSOLVE_WHILE_IN_SIEGE));
			return;
		}
		
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(new SystemMessage(SystemMessage.DISSOLUTION_IN_PROGRESS));
			return;
		}
		
		if (clan.getRecoverPenaltyExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(new SystemMessage(SystemMessage.CANNOT_APPLY_DISSOLUTION_AGAIN));
			return;
		}
		
		clan.setRecoverPenaltyExpiryTime(0);
		clan.setDissolvingExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_DISSOLVE_DAYS * 86400000L)); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		
		ClanData.getInstance().scheduleRemoveClan(clan.getId());
		
		// The clan leader should take the XP penalty of a full death.
		player.deathPenalty(false);
		
		clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
		
		player.sendMessage("The clan has been dissolved.");
	}
	
	private void recoverClan(L2PcInstance player, int clanId)
	{
		if (Config.DEBUG)
		{
			LOG.fine(player.getObjectId() + "(" + player.getName() + ") requested recover a clan from " + getObjectId() + "(" + getName() + ")");
		}
		
		if (!player.isClanLeader())
		{
			return;
		}
		
		Clan clan = player.getClan();
		
		if (clan.getDissolvingExpiryTime() == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.NO_REQUESTS_TO_DISPERSE));
			return;
		}
		
		clan.setDissolvingExpiryTime(0);
		clan.setRecoverPenaltyExpiryTime(System.currentTimeMillis() + (Config.ALT_RECOVERY_PENALTY * 86400000L)); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		
		clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
		player.sendMessage("Clan dissolution has been cancelled as requested.");
		
	}
	
	private void levelUpClan(L2PcInstance player, int clanId)
	{
		Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessage.CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS);
			return;
		}
		
		boolean increaseClanLevel = false;
		
		switch (clan.getLevel())
		{
			case 0:
				// upgrade to 1
				if ((player.getSp() >= 30000) && (player.getInventory().getAdena() >= 650000))
				{
					if (player.getInventory().reduceAdena("ClanLvl", 650000, this, true))
					{
						player.setSp(player.getSp() - 30000);
						increaseClanLevel = true;
					}
				}
				break;
			case 1:
				// upgrade to 2
				if ((player.getSp() >= 150000) && (player.getInventory().getAdena() >= 2500000))
				{
					if (player.getInventory().reduceAdena("ClanLvl", 2500000, this, true))
					{
						player.setSp(player.getSp() - 150000);
						increaseClanLevel = true;
					}
				}
				break;
			case 2:
				// upgrade to 3
				if ((player.getSp() >= 500000) && (player.getInventory().getItemById(1419) != null))
				{
					// itemid 1419 == proof of blood
					if (player.getInventory().destroyItemByItemId("ClanLvl", 1419, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 500000);
						increaseClanLevel = true;
					}
				}
				break;
			case 3:
				// upgrade to 4
				if ((player.getSp() >= 1400000) && (player.getInventory().getItemById(3874) != null))
				{
					// itemid 3874 == proof of alliance
					if (player.getInventory().destroyItemByItemId("ClanLvl", 3874, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 1400000);
						increaseClanLevel = true;
					}
				}
				break;
			case 4:
				// upgrade to 5
				if ((player.getSp() >= 3500000) && (player.getInventory().getItemById(3870) != null))
				{
					// itemid 3870 == proof of aspiration
					if (player.getInventory().destroyItemByItemId("ClanLvl", 3870, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 3500000);
						increaseClanLevel = true;
					}
					
				}
				break;
		}
		
		if (increaseClanLevel)
		{
			// the player should know that he has less sp now :p
			StatusUpdate su = new StatusUpdate(player.getObjectId());
			su.addAttribute(StatusUpdateType.SP, player.getSp());
			player.sendPacket(su);
			
			player.sendPacket(new ItemList(player, false));
			
			clan.changeLevel(clan.getLevel() + 1);
		}
		else
		{
			player.sendPacket(new SystemMessage(SystemMessage.CLAN_LEVEL_INCREASE_FAILED));
		}
	}
	
	private final Set<PlayerClass> getAvailableSubClasses(L2PcInstance player)
	{
		int charClassId = player.getBaseClass();
		
		if (charClassId >= 88)
		{
			charClassId = ClassId.getById(charClassId).getParent().ordinal();
		}
		
		final Race npcRace = getVillageMasterRace();
		final ClassType npcTeachType = getVillageMasterTeachType();
		
		PlayerClass currClass = PlayerClass.values()[charClassId];
		
		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class, and you may not select Overlord and Warsmith class as a subclass. You may not select a similar class as the subclass. The occupations classified as similar classes are as
		 * follows: Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
		 * Spellhowler
		 */
		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses(player);
		
		if (availSubs != null)
		{
			for (PlayerClass availSub : availSubs)
			{
				for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
				{
					SubClass prevSubClass = subList.next();
					
					int subClassId = prevSubClass.getClassId();
					if (subClassId >= 88)
					{
						subClassId = ClassId.getById(subClassId).getParent().getId();
					}
					
					if ((availSub.ordinal() == subClassId) || (availSub.ordinal() == player.getBaseClass()))
					{
						availSubs.remove(PlayerClass.values()[availSub.ordinal()]);
					}
				}
				
				if (((npcRace == Race.HUMAN) || (npcRace == Race.ELF)))
				{
					// If the master is human or light elf, ensure that fighter-type
					// masters only teach fighter classes, and priest-type masters
					// only teach priest classes etc.
					if (!availSub.isOfType(npcTeachType))
					{
						availSubs.remove(availSub);
					}
					else if (!availSub.isOfRace(Race.HUMAN) && !availSub.isOfRace(Race.ELF))
					{
						// Remove any non-human or light elf classes.
						availSubs.remove(availSub);
					}
				}
				else
				{
					// If the master is not human and not light elf,
					// then remove any classes not of the same race as the master.
					if (((npcRace != Race.HUMAN) && (npcRace != Race.ELF)) && !availSub.isOfRace(npcRace))
					{
						availSubs.remove(availSub);
					}
				}
			}
		}
		
		return availSubs;
	}
	
	private final String formatClassForDisplay(PlayerClass className)
	{
		String classNameStr = "";
		
		char[] charArray = className.toString().toCharArray();
		
		boolean isCapital = true;
		for (char element : charArray)
		{
			if (element == '_')
			{
				isCapital = true;
				classNameStr += " ";
			}
			else if (isCapital)
			{
				isCapital = !isCapital;
				classNameStr += element;
			}
			else
			{
				classNameStr += String.valueOf(element).toLowerCase();
			}
		}
		
		return classNameStr;
	}
	
	private final Race getVillageMasterRace()
	{
		String npcClass = getTemplate().getjClass().toLowerCase();
		
		if (npcClass.indexOf("human") > -1)
		{
			return Race.HUMAN;
		}
		if (npcClass.indexOf("darkelf") > -1)
		{
			return Race.DARK_ELF;
		}
		if (npcClass.indexOf("elf") > -1)
		{
			return Race.ELF;
		}
		if (npcClass.indexOf("orc") > -1)
		{
			return Race.ORC;
		}
		
		return Race.DWARF;
	}
	
	private final ClassType getVillageMasterTeachType()
	{
		String npcClass = getTemplate().getjClass().toLowerCase();
		
		if ((npcClass.indexOf("sanctuary") > -1) || (npcClass.indexOf("clergyman") > -1) || (npcClass.indexOf("temple_master") > -1))
		{
			return ClassType.PRIEST;
		}
		
		if ((npcClass.indexOf("mageguild") > -1) || (npcClass.indexOf("patriarch") > -1))
		{
			return ClassType.MYSTIC;
		}
		
		return ClassType.FIGHTER;
	}
	
	private void sendHtml(L2PcInstance player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/villageMaster/" + file);
		html.replace("%objectId%", getObjectId() + "");
		player.sendPacket(html);
	}
	
	private Iterator<SubClass> iterSubClasses(L2PcInstance player)
	{
		return player.getSubClasses().values().iterator();
	}
}
