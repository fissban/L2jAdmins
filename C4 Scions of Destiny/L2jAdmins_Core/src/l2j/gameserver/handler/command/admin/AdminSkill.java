package l2j.gameserver.handler.command.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class handles following admin commands: - show_skills - remove_skills - skill_list - skill_index - add_skill - remove_skill - get_skills - reset_skills - give_all_skills - remove_all_skills
 * @version $Revision: 1.2.4.7 $ $Date: 28/06/2014
 */
public class AdminSkill implements IAdminCommandHandler
{
	private static final Logger LOG = Logger.getLogger(AdminSkill.class.getName());
	
	private static final String[] ADMINCOMMANDS =
	{
		// html
		"admin_show_skills",
		"admin_skill_index",
		// misc
		"admin_remove_skills",
		
		"admin_add_skill",
		"admin_remove_skill",
		"admin_get_skills",
		"admin_reset_skills",
		"admin_give_all_skills",
		"admin_remove_all_skills"
	};
	
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		
		// Generamos un log con el COMMAND usado por el GM
		GMAudit.auditGMAction(activeChar.getName(), command, activeChar.getTarget() == null ? "no-_target?" : "_target " + activeChar.getTarget().getName(), "");
		
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_show_skills"))
		{
			if (st.hasMoreTokens())
			{
				target = L2World.getInstance().getPlayer(st.nextToken());
				
				if (target == null)
				{
					return false;
				}
				
				showSkillsPage(activeChar, target);
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				showSkillsPage(activeChar, target);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_skill_index"))
		{
			try
			{
				AdminHelpPage.showHelpPage(activeChar, "skills/" + st.nextToken() + ".htm");
			}
			catch (final Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_remove_skills"))
		{
			try
			{
				removeSkillsPage(activeChar, Integer.parseInt(st.nextToken()));
			}
			catch (final Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_skill_index"))
		{
			try
			{
				AdminHelpPage.showHelpPage(activeChar, "skills/" + st.nextToken() + ".htm");
			}
			catch (final Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_add_skill"))
		{
			try
			{
				adminAddSkill(activeChar, st.nextToken(), st.nextToken());
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Error while adding skill.");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_remove_skill"))
		{
			try
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				final int idval = Integer.getInteger(st.nextToken());
				
				final Skill skill = SkillData.getInstance().getSkill(idval, target.getSkillLevel(idval));
				
				if (skill != null)
				{
					activeChar.sendMessage("Admin removed the skill " + skill.getName() + ".");
					
					target.removeSkill(skill);
					
					// Admin information
					activeChar.sendMessage("You removed the skill " + skill.getName() + " from " + target.getName() + ".");
					
					if (Config.DEBUG)
					{
						LOG.fine("[GM]" + activeChar.getName() + "removed the skill " + skill.getName() + " from " + target.getName() + ".");
					}
				}
				else
				{
					activeChar.sendMessage("Error: there is no such skill.");
				}
				removeSkillsPage(activeChar, 0); // Back to start
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Error while removing skill.");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_get_skills"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			if (target.getName().equals(activeChar.getName()))
			{
				activeChar.sendMessage("There is no point in doing it on your character...");
			}
			else
			{
				final Collection<Skill> skills = target.getAllSkills();
				
				for (final Skill adminSkill : target.getAllSkills())
				{
					activeChar.removeSkill(adminSkill);
				}
				for (final Skill skill : skills)
				{
					activeChar.addSkill(skill, true);
				}
				activeChar.sendMessage("You now have all the skills of  " + target.getName() + ".");
			}
			showSkillsPage(activeChar, target);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_reset_skills"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			final Collection<Skill> skills = target.getAllSkills();
			for (final Skill skill : skills)
			{
				target.removeSkill(skill);
			}
			
			for (final SkillLearnHolder s : SkillTreeData.getMaxAvailableSkills(target))
			{
				if (s == null)
				{
					continue;
				}
				
				final Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
				if ((sk == null) || !sk.getCanLearn(target.getClassId()))
				{
					continue;
				}
				
				target.addSkill(sk, true);
			}
			
			activeChar.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");
			activeChar.sendMessage("You now have all your skills back.");
			
			showSkillsPage(activeChar, target);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_give_all_skills"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			int skillCounter = 0;
			
			for (final SkillLearnHolder s : SkillTreeData.getMaxAvailableSkills(target))
			{
				if (s == null)
				{
					continue;
				}
				
				final Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
				if ((sk == null) || !sk.getCanLearn(target.getClassId()))
				{
					continue;
				}
				
				if (target.getSkillLevel(sk.getId()) == -1)
				{
					skillCounter++;
				}
				
				target.addSkill(sk, true);
			}
			
			// Notify player and admin
			if (skillCounter > 0)
			{
				target.sendMessage("A GM gave you " + skillCounter + " skills.");
				activeChar.sendMessage("You gave " + skillCounter + " skills to " + target.getName());
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_remove_all_skills"))
		{
			target = AdminHelpTarget.getPlayer(activeChar);
			
			if (target == null)
			{
				return false;
			}
			
			List<Skill> skills = new ArrayList<>();
			
			for (Skill skill : target.getAllSkills())
			{
				skills.add(skill);
			}
			
			for (Skill skill : skills)
			{
				target.removeSkill(skill);
			}
			activeChar.sendMessage("You removed all skills from " + target.getName());
			target.sendMessage("Admin removed all skills from you.");
			
		}
		return true;
	}
	
	// ok
	private static void removeSkillsPage(L2PcInstance activeChar, int page)
	{
		target = AdminHelpTarget.getPlayer(activeChar);
		
		if (target == null)
		{
			return;
		}
		
		final Collection<Skill> skills = target.getAllSkills();
		
		final int MaxSkillsPerPage = 10;
		int MaxPages = skills.size() / MaxSkillsPerPage;
		if (skills.size() > (MaxSkillsPerPage * MaxPages))
		{
			MaxPages++;
		}
		
		if (page > MaxPages)
		{
			page = MaxPages;
		}
		
		final int SkillsStart = MaxSkillsPerPage * page;
		int SkillsEnd = skills.size();
		if ((SkillsEnd - SkillsStart) > MaxSkillsPerPage)
		{
			SkillsEnd = SkillsStart + MaxSkillsPerPage;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		final StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td width=180><center><font color=\"LEVEL\">Character Selection Menu</font></center></td>");
		sb.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		sb.append("<center>Editing <font color=\"LEVEL\">" + target.getName() + "</font></center>");
		sb.append("<br><table width=270><tr><td>Lv: " + target.getLevel() + " " + target.getTemplate().getClassId().getName() + "</td></tr></table>");
		sb.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
		sb.append("<tr><td>ruin the game...</td></tr></table>");
		sb.append("<br><center>Click on the skill you wish to remove:</center>");
		sb.append("<br>");
		String pages = "<center><table width=270><tr>";
		for (int x = 0; x < MaxPages; x++)
		{
			final int pagenr = x + 1;
			pages += "<td><a action=\"bypass -h admin_remove_skills " + x + "\">Page " + pagenr + "</a></td>";
		}
		pages += "</tr></table></center>";
		sb.append(pages);
		sb.append("<br><table width=270>");
		sb.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		
		for (int i = SkillsStart; i < SkillsEnd; i++)
		{
			sb.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill " + target.getSkills().get(i).getId() + "\">" + target.getSkills().get(i).getName() + "</a></td><td width=60>" + target.getSkills().get(i).getLevel() + "</td><td width=40>" + target.getSkills().get(i).getId()
				+ "</td></tr>");
		}
		sb.append("</table>");
		sb.append("<br><center><table>");
		sb.append("Remove custom skill:");
		sb.append("<tr><td>Id: </td>");
		sb.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
		sb.append("</table></center>");
		sb.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center>");
		sb.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	/**
	 * @param activeChar
	 * @param player
	 */
	private static void showSkillsPage(L2PcInstance activeChar, L2PcInstance player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=80><button value=\"Main\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("<td width=100><center><font color=\"LEVEL\">Character Selection</font></center></td>");
		sb.append("<td width=80><button value=\"Back\" action=\"bypass -h admin_current_player\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		sb.append("<center>Editing <font color=\"LEVEL\">" + player.getName() + "</font></center>");
		sb.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
		sb.append("<tr><td>ruin the game...</td></tr></table>");
		sb.append("<br><center><table>");
		sb.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_help skills.htm\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td></tr>");
		sb.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills 0\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td></tr>");
		sb.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
		sb.append("<td><button value=\"Remove All skills\" action=\"bypass -h admin_remove_all_skills\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td></tr>");
		sb.append("</table></center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	private static void adminAddSkill(L2PcInstance activeChar, String id, String level)
	{
		target = AdminHelpTarget.getPlayer(activeChar);
		
		if (target == null)
		{
			return;
		}
		
		if ((id == null) && (level == null))
		{
			showSkillsPage(activeChar, target);
		}
		else
		{
			final Skill skill = SkillData.getInstance().getSkill(Integer.parseInt(id), Integer.parseInt(level));
			
			if (skill != null)
			{
				activeChar.sendMessage("Admin gave you the skill " + skill.getName() + ".");
				
				target.addSkill(skill, true);
				
				// Admin information
				activeChar.sendMessage("You gave the skill " + skill.getName() + " to " + target.getName() + ".");
				
				if (Config.DEBUG)
				{
					LOG.fine("[GM]" + activeChar.getName() + "gave the skill " + skill.getName() + " to " + target.getName() + ".");
				}
			}
			else
			{
				activeChar.sendMessage("Error: there is no such skill.");
			}
			showSkillsPage(activeChar, target); // Back to start
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMANDS;
	}
}
