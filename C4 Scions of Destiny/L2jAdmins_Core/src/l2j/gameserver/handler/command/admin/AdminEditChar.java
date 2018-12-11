package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.base.Sex;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos para la edicion/administracion de los players.<br>
 *         Contiene los comandos: <br>
 *         <li>char_manage
 *         <li>character_list
 *         <li>current_player
 *         <li>edit_character
 *         <li>find_character
 *         <li>add_sp
 *         <li>changelvl
 *         <li>eval
 *         <li>goto_cha
 *         <li>heal
 *         <li>kick
 *         <li>recall_cha
 *         <li>set_level
 *         <li>setclass
 *         <li>sethero
 *         <li>setkarma
 *         <li>setname
 *         <li>setpk
 *         <li>setpvp
 *         <li>settitle
 *         <li>sex
 */
public class AdminEditChar implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		// HTML
		"admin_char_manage",
		"admin_character_list",
		"admin_current_player",
		"admin_edit_character",
		"admin_find_character",
		"admin_show_characters",
		// MISC
		"admin_add_sp",
		"admin_changelvl",
		"admin_eval",
		"admin_goto_cha",
		"admin_heal",
		"admin_kick",
		"admin_rec",
		"admin_recall_cha",
		"admin_set_level",
		"admin_setclass",
		"admin_sethero",
		"admin_setkarma",
		"admin_setname",
		"admin_setpk",
		"admin_setpvp",
		"admin_settitle",
		"admin_sex"
	};
	
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		/** ====================== [ HTML ] ====================== */
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_char_manage"))
		{
			AdminHelpPage.showHelpPage(activeChar, "charmanage.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_character_list"))
		{
			try
			{
				target = L2World.getInstance().getPlayer(st.nextToken());
				if (target != null)
				{
					showCharacterList(activeChar, target);
				}
				else
				{
					activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
					AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("No characters found");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_current_player"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				return false;
			}
			
			showCharacterList(activeChar, target);
		}
		// ----------~ COMMAND ~---------- //
		if (event.equalsIgnoreCase("admin_edit_character"))
		{
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equalsIgnoreCase("admin_find_character"))
		{
			try
			{
				findCharacter(activeChar, st.nextToken());
			}
			catch (Exception e)
			{
				// Case of empty character name
				activeChar.sendMessage("You didn\'t enter a character name to find.");
				listCharacters(activeChar, 0);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_show_characters"))
		{
			try
			{
				int page = Integer.parseInt(st.nextToken());
				listCharacters(activeChar, page);
			}
			catch (Exception e)
			{
				return false;
			}
		}
		/** ====================== [ MISC ] ====================== */
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_add_sp"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				int sp = Integer.parseInt(st.nextToken());
				target.addExpAndSp(0, sp);
				target.sendMessage("Increase your SP in " + sp + " by admin!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please enter valid value.");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_changelvl"))
		{
			int accesLvl = 0;
			String targetName = "";
			
			if (st.countTokens() > 2)
			{
				activeChar.sendMessage("Correct command //changelvl <playerName|target> <accessLvl>");
			}
			else if (st.countTokens() == 2)
			{
				targetName = st.nextToken();
				target = L2World.getInstance().getPlayer(targetName);
				
				try
				{
					accesLvl = Integer.parseInt(st.nextToken());
					target.setAccessLevel(accesLvl);
					target.sendMessage("You new access level is " + accesLvl + "!");
					activeChar.sendMessage("Character " + targetName + " new access level " + accesLvl + "!");
					AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Invalid number");
					return false;
				}
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target != null)
				{
					try
					{
						accesLvl = Integer.parseInt(st.nextToken());
						target.setAccessLevel(accesLvl);
						target.sendMessage("You new access level is " + accesLvl + "!");
						activeChar.sendMessage("Character " + target.getName() + " new access level " + accesLvl + "!");
						AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
					}
					catch (Exception e)
					{
						activeChar.sendMessage("Invalid number");
						return false;
					}
				}
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_eval"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				if (target == null)
				{
					return false;
				}
				
				target.setRecomLeft(Integer.parseInt(st.nextToken()));
				target.sendMessage("You have " + target.getRecomLeft() + " recommendation");
				target.broadcastUserInfo();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please enter valid value");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_goto_cha"))
		{
			try
			{
				target = L2World.getInstance().getPlayer(st.nextToken());
				if (target != null)
				{
					activeChar.teleToLocation(target.getX(), target.getY(), target.getZ(), true);
				}
				else
				{
					activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Correct command //goto_cha <playerName>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_heal"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			target.setCurrentHpMp(target.getStat().getMaxHp(), target.getStat().getMaxMp());
			target.setCurrentCp(target.getStat().getMaxCp());
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_kick"))
		{
			if (st.countTokens() > 1)
			{
				activeChar.sendMessage("Correct command //kick <playerName>.");
			}
			else if (st.hasMoreTokens())
			{
				String targetName = st.nextToken();
				target = L2World.getInstance().getPlayer(targetName);
				
				try
				{
					if (target.equals(activeChar))
					{
						activeChar.sendMessage("You cannot disconnect your own character.");
						return false;
					}
					target.sendPacket(SystemMessage.DISCONNECTED_FROM_SERVER);
					activeChar.sendMessage(targetName + " have been kicked from server.");
					target.closeConnection();
				}
				catch (Exception e)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ONLINE).addString(targetName));
				}
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				if (target != null)
				{
					if (target.equals(activeChar))
					{
						activeChar.sendMessage("You cannot disconnect your own character.");
						return false;
					}
					target.sendPacket(SystemMessage.DISCONNECTED_FROM_SERVER);
					activeChar.sendMessage(target.getName() + " have been kicked from server.");
					target.closeConnection();
				}
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_rec"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				target.setRecomHave(Integer.parseInt(st.nextToken()));
				target.sendMessage("You have been recommended by a GM");
				target.sendMessage("You have " + target.getRecomHave() + " recommendation");
				target.broadcastUserInfo();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please enter valid value (0-255)");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
			return true;
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_recall_cha"))
		{
			try
			{
				target = L2World.getInstance().getPlayer(st.nextToken());
				if (target != null)
				{
					target.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), true);
				}
				else
				{
					activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Correct command //recall_cha <playerName>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_set_level"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				return false;
			}
			
			try
			{
				byte lvl = Byte.parseByte(st.nextToken());
				
				if ((lvl <= 0) || (lvl > (ExperienceData.getInstance().getMaxLevel())))
				{
					activeChar.sendMessage("Please enter correct value (1-" + (ExperienceData.getInstance().getMaxLevel()) + ")");
				}
				else
				{
					long pXp = target.getExp();
					long tXp = ExperienceData.getInstance().getExpForLevel(lvl);
					
					if (pXp > tXp)
					{
						target.removeExpAndSp(pXp - tXp, 0);
					}
					else if (pXp < tXp)
					{
						target.addExpAndSp(tXp - pXp, 0);
					}
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Invalid number");
			}
			
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
			target.broadcastStatusUpdate();
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setclass"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
				return false;
			}
			
			if (st.hasMoreTokens())
			{
				try
				{
					int classidval = Integer.parseInt(st.nextToken());
					target = AdminHelpTarget.getPlayer(activeChar);
					
					boolean valid = false;
					for (ClassId classid : ClassId.values())
					{
						if (classidval == classid.getId())
						{
							valid = true;
						}
					}
					if (valid && (target.getClassId().getId() != classidval))
					{
						target.setClassId(classidval);
						if (!target.isSubClassActive())
						{
							target.setBaseClass(classidval);
						}
						
						target.store();
						target.sendMessage("A GM changed your class to " + target.getTemplate().getClassId().getName() + ".");
						target.broadcastUserInfo();
						activeChar.sendMessage(target.getName() + " is a " + target.getTemplate().getClassId().getName() + ".");
					}
					else
					{
						activeChar.sendMessage("Correct command //setclass <valid_new_classid>");
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Invalid number");
				}
			}
			else
			{
				AdminHelpPage.showHelpPage(activeChar, "menuClasses.htm");
				activeChar.sendMessage("Correct command //setclass <valid_new_classid>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_sethero"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				return false;
			}
			
			target.setHero(target.isHero() ? false : true);
			target.broadcastUserInfo();
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setkarma"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				return false;
			}
			
			try
			{
				int karma = Integer.parseInt(st.nextToken());
				
				if (karma >= 0)
				{
					// for display
					int oldKarma = target.getKarma();
					// update karma
					target.setKarma(karma);
					// Common character information
					target.sendMessage("Admin has changed your karma from " + oldKarma + " to " + karma + ".");
					// Admin information
					activeChar.sendMessage("Successfully changed karma for " + target.getName() + " from (" + oldKarma + ") to (" + karma + ").");
				}
				else
				{
					// tell admin of mistake
					activeChar.sendMessage("You must enter a value for karma greater than or equal to 0.");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please specify new karma value (0-65535).");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setname"))
		{
			if (st.hasMoreTokens())
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				if (target == null)
				{
					return false;
				}
				
				String name = st.nextToken();
				
				if (CharNameData.getInstance().doesCharNameExist(name))
				{
					activeChar.sendMessage("Name: " + name + " already exists!");
					return false;
				}
				
				if ((name.length() > 3) && (name.length() < 16) && Util.isAlphaNumeric(name))
				{
					target.setName(st.nextToken());
					
					L2World.getInstance().removePlayer(target);
					L2World.getInstance().addVisibleObject(target, null);
					target.broadcastUserInfo();
					target.sendMessage("Your name has changed by Admin");
					// TODO falta agregar actualizacion al bbs y si el player esta en party
				}
				else
				{
					activeChar.sendMessage("Please enter valid new name!");
				}
			}
			else
			{
				activeChar.sendMessage("Please enter new name!");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setpk"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				if (target == null)
				{
					return false;
				}
				
				target.setPkKills(Integer.parseInt(st.nextToken()));
				target.broadcastUserInfo();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please specify new pk value");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_setpvp"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				if (target == null)
				{
					return false;
				}
				
				target.setPvpKills(Integer.parseInt(st.nextToken()));
				target.broadcastUserInfo();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please specify new pvp value");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_settitle"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			if (target == null)
			{
				return false;
			}
			
			if (st.hasMoreTokens())
			{
				String title = st.nextToken();
				if (title.length() > 16)
				{
					activeChar.sendMessage("Please enter valid new title!");
				}
				else
				{
					target.setTitle(title);
				}
			}
			else
			{
				target.setTitle("");
			}
			target.broadcastTitleInfo();
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_sex"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target != null)
			{
				target.setSex(target.getSex() == Sex.FEMALE ? Sex.MALE : Sex.FEMALE);
				target.sendMessage("Your gender has been changed by a GM");
				target.broadcastUserInfo();
				AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
			}
		}
		return true;
	}
	
	private static void listCharacters(L2PcInstance activeChar, int page)
	{
		L2PcInstance[] players = L2World.getInstance().getAllPlayersArray();
		
		int MaxCharactersPerPage = 20;
		int MaxPages = players.length / MaxCharactersPerPage;
		
		if (players.length > (MaxCharactersPerPage * MaxPages))
		{
			MaxPages++;
		}
		
		// Check if number of users changed
		if (page > MaxPages)
		{
			page = MaxPages;
		}
		
		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = players.length;
		if ((CharactersEnd - CharactersStart) > MaxCharactersPerPage)
		{
			CharactersEnd = CharactersStart + MaxCharactersPerPage;
		}
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body><center>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=80><button value=Main action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.btn1_normal_over fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=100><center><font color=\"LEVEL\">Character Menu</font></center></td>");
		sb.append("<td width=80><button value=Back action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.btn1_normal_over fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		sb.append("<table width=270>");
		sb.append("<tr><td width=270>You can find a character by writing his name and</td></tr>");
		sb.append("<tr><td width=270>clicking Find bellow.<br></td></tr>");
		sb.append("<tr><td width=270><font color=\"LEVEL\">Note:</font> Names should be written case sensitive.</td></tr>");
		sb.append("</table><br>");
		sb.append("<table><tr><td>");
		sb.append("<edit var=character_name width=80></td><td><button value=Find action=\"bypass -h admin_find_character $character_name\" width=80 height=25 back=L2UI_CH3.btn1_normal_over fore=L2UI_CH3.Btn1_normal>");
		sb.append("</td></tr></table></center><br><br>");
		
		for (int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			sb.append("<center><a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a></center>");
		}
		sb.append("<br>");
		
		// List Players in a Table
		sb.append("<table width=270");
		sb.append("<tr><td width=80><font color=\"LEVEL\">Name:</font></td><td width=110><font color=\"LEVEL\">Class:</font></td><td width=40><font color=\"LEVEL\">Level:</font></td></tr>");
		sb.append("</table>");
		for (int i = CharactersStart; i < CharactersEnd; i++)
		{ // Add player info into new Table row
			sb.append("<table width=270 bgcolor=\"000000\"");
			sb.append("<tr><td width=80>" + "<a action=\"bypass -h admin_character_list " + players[i].getName() + "\">" + players[i].getName() + "</a></td><td width=110>" + players[i].getTemplate().getClassId().getName() + "</td><td width=40>" + players[i].getLevel() + "</td></tr>");
			sb.append("</table>");
			sb.append("<br1>");
		}
		sb.append("</body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * TODO se puede pasar a un html<br>
	 * Mostramos informacion basica de un player
	 * @param activeChar
	 * @param player
	 */
	private static void showCharacterList(L2PcInstance activeChar, L2PcInstance player)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body><center>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=80><button value=Main action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.btn1_normal_over fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=100><center><font color=\"LEVEL\">Player Information</font></center></td>");
		sb.append("<td width=80><button value=Back action=\"bypass -h admin_show_characters 0\" width=80 height=22 back=L2UI_CH3.btn1_normal_over fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		// Character Player Info
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Name: </font>" + player.getName() + "</td><td width=135><font color=\"LEVEL\">Level: </font>" + player.getLevel() + "</td></tr>");
		sb.append("</table>");
		// sb.append("<br>");
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Clan: </font>" + ClanData.getInstance().getClanById(player.getClanId()) + "</td><td width=135><font color=\"LEVEL\">Exp: </font>" + player.getExp() + "</td></tr>");
		sb.append("</table>");
		// sb.append("<br>");
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Class: </font>" + player.getTemplate().getClassId().getName() + "</td><td width=135><font color=\"LEVEL\">Sp: </font>" + player.getSp() + "</td></tr>");
		sb.append("</table>");
		// sb.append("<br>");
		// Character ClassID & Coordinates
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=270><font color=\"LEVEL\">Class Template ID: </font>" + player.getClassId().ordinal() + "</td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=270><font color=\"LEVEL\">Loc </font>X: " + player.getX() + " Y: " + player.getY() + " Z: " + player.getZ() + "</td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		// Character Stats
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=90><font color=\"LEVEL\">HP: </font>" + player.getStat().getMaxHp() + "</td><td width=90><font color=\"LEVEL\">MP: </font>" + player.getStat().getMaxMp() + "</td><td width=90><font color=\"LEVEL\">CP: </font>" + player.getStat().getMaxCp() + "</td></tr>");
		sb.append("<tr><td width=90><font color=\"LEVEL\">Karma: </font>" + player.getKarma() + "</td><td width=90><font color=\"LEVEL\">PvP Flag: </font>" + player.getPvpFlag() + "</td><td width=90><font color=\"LEVEL\">PvP Kills: </font>" + player.getPvpKills() + "</td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table width=270 bgcolor=\"000000\">");
		sb.append("<tr><td width=135><font color=\"LEVEL\">P.Atk: </font>" + player.getStat().getPAtk(null) + "</td><td width=135><font color=\"LEVEL\">M.Atk: </font>" + player.getStat().getMAtk(null, null) + "</td></tr>");
		sb.append("<tr><td width=135><font color=\"LEVEL\">P.Def: </font>" + player.getStat().getPDef(null) + "</td><td width=135><font color=\"LEVEL\">M.Def: </font>" + player.getStat().getMDef(null, null) + "</td></tr>");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Accuracy: </font>" + player.getStat().getAccuracy() + "</td><td width=135><font color=\"LEVEL\">Evasion: </font>" + player.getStat().getEvasionRate(null) + "</td></tr>");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Critical: </font>" + player.getStat().getCriticalHit(null, null) + "</td><td width=135><font color=\"LEVEL\">Speed: </font>" + (int) (player.getStat().getRunSpeed() * player.getStat().getMovementSpeedMultiplier()) + "</td></tr>");
		sb.append("<tr><td width=135><font color=\"LEVEL\">Atk. Spd: </font>" + player.getStat().getPAtkSpd() + "</td><td width=135><font color=\"LEVEL\">Casting Spd: </font>" + player.getStat().getMAtkSpd() + "</td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table width=240>");
		sb.append("<tr>");
		sb.append("<td width=80><button value=Go action=\"bypass -h admin_goto_cha " + player.getName() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=80><button value=Summon action=\"bypass -h admin_recall " + player.getName() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=80><button value=Skill action=\"bypass -h admin_show_skills " + player.getName() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width=80><button value=Ban action=\"bypass -h admin_ban_char\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=80><button value=Jail action=\"bypass -h admin_jail " + player.getName() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("<td width=80></td>");
		sb.append("</tr></table>");
		sb.append("</center></body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	// FIXME: needs removal, whole thing needs to use getTarget()
	private static void findCharacter(L2PcInstance activeChar, String CharacterToFind)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		int CharactersFound = 0;
		
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=80><button value=\"Main\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("<td width=100><center>Character Selection</center></td>");
		sb.append("<td width=80><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{ // Add player info into new Table row
			
			if (player.getName().startsWith((CharacterToFind)))
			{
				CharactersFound = CharactersFound + 1;
				sb.append("<table width=270 bgcolor=\"000000\">");
				sb.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
				sb.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + player.getName() + "\">" + player.getName() + "</a></td><td width=110>" + player.getTemplate().getClassId().getName() + "</td><td width=40>" + player.getLevel() + "</td></tr>");
				sb.append("</table><br>");
			}
		}
		
		if (CharactersFound == 0)
		{
			sb.append("<table width=270>");
			sb.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
			sb.append("<tr><td width=270>Please try again.<br></td></tr>");
			sb.append("</table><br>");
			sb.append("<center><table><tr><td>");
			sb.append("<edit var=character_name width=80></td><td><button value=Find action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=sek.cbui94 fore=sek.cbui92>");
			sb.append("</td></tr></table></center>");
		}
		else
		{
			sb.append("<center><br>Found " + CharactersFound + " character");
			
			if (CharactersFound == 1)
			{
				sb.append(".");
			}
			else
			{
				if (CharactersFound > 1)
				{
					sb.append("s.");
				}
			}
		}
		
		sb.append("</center></body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
