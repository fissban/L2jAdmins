package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * This class handles following admin commands: - gm = turns gm mode on/off
 * @version $Revision: 1.1.2.1 $ $Date: 2005/03/15 21:32:48 $
 */
public class AdminFightCalculator implements IAdminCommandHandler
{
	private static final String[] ADMINCOMMAND =
	{
		"admin_fight_calculator",
		"admin_fight_calculator_show",
		"admin_fcs",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		try
		{
			// ----------~ COMMAND ~---------- //
			if (command.startsWith("admin_fight_calculator_show"))
			{
				handleShow(command.substring("admin_fight_calculator_show".length()), activeChar);
			}
			// ----------~ COMMAND ~---------- //
			else if (command.startsWith("admin_fcs"))
			{
				handleShow(command.substring("admin_fcs".length()), activeChar);
			}
			// ----------~ COMMAND ~---------- //
			else if (command.startsWith("admin_fight_calculator"))
			{
				handleStart(command.substring("admin_fight_calculator".length()), activeChar);
			}
		}
		catch (final Exception e)
		{
			//
		}
		return true;
	}
	
	private void handleStart(String params, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(params);
		int lvl1 = 0;
		int lvl2 = 0;
		int mid1 = 0;
		int mid2 = 0;
		while (st.hasMoreTokens())
		{
			final String s = st.nextToken();
			if (s.equals("lvl1"))
			{
				lvl1 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("lvl2"))
			{
				lvl2 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("mid1"))
			{
				mid1 = Integer.parseInt(st.nextToken());
				continue;
			}
			if (s.equals("mid2"))
			{
				mid2 = Integer.parseInt(st.nextToken());
				continue;
			}
		}
		
		NpcTemplate npc1 = null;
		if (mid1 != 0)
		{
			npc1 = NpcData.getInstance().getTemplate(mid1);
		}
		NpcTemplate npc2 = null;
		if (mid2 != 0)
		{
			npc2 = NpcData.getInstance().getTemplate(mid2);
		}
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		StringBuilder replyMSG = new StringBuilder();
		if ((npc1 != null) && (npc2 != null))
		{
			replyMSG.append("<html><title>Selected mobs to fight</title>");
			replyMSG.append("<body>");
			replyMSG.append("<table>");
			replyMSG.append("<tr><td>First</td><td>Second</td></tr>");
			replyMSG.append("<tr><td>level " + lvl1 + "</td><td>level " + lvl2 + "</td></tr>");
			replyMSG.append("<tr><td>id " + npc1.getId() + "</td><td>id " + npc2.getId() + "</td></tr>");
			replyMSG.append("<tr><td>" + npc1.getName() + "</td><td>" + npc2.getName() + "</td></tr>");
			replyMSG.append("</table>");
			replyMSG.append("<center><br><br><br>");
			replyMSG.append("<button value=\"OK\" action=\"bypass -h admin_fight_calculator_show " + npc1.getId() + " " + npc2.getId() + "\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			replyMSG.append("</center>");
			replyMSG.append("</body></html>");
		}
		else if ((lvl1 != 0) && (npc1 == null))
		{
			replyMSG.append("<html><title>Select first mob to fight</title>");
			replyMSG.append("<body><table>");
			for (final NpcTemplate n : NpcData.getInstance().getAllOfLevel(lvl1))
			{
				replyMSG.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 " + lvl1 + " lvl2 " + lvl2 + " mid1 " + n.getId() + " mid2 " + mid2 + "\">" + n.getName() + "</a></td></tr>");
			}
			replyMSG.append("</table></body></html>");
		}
		else if ((lvl2 != 0) && (npc2 == null))
		{
			replyMSG.append("<html><title>Select second mob to fight</title>");
			replyMSG.append("<body><table>");
			for (final NpcTemplate n : NpcData.getInstance().getAllOfLevel(lvl2))
			{
				replyMSG.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 " + lvl1 + " lvl2 " + lvl2 + " mid1 " + mid1 + " mid2 " + n.getId() + "\">" + n.getName() + "</a></td></tr>");
			}
			replyMSG.append("</table></body></html>");
		}
		else
		{
			replyMSG.append("<html><title>Select mobs to fight</title>");
			replyMSG.append("<body>");
			replyMSG.append("<table>");
			replyMSG.append("<tr><td>First</td><td>Second</td></tr>");
			replyMSG.append("<tr><td><edit var=\"lvl1\" width=80></td><td><edit var=\"lvl2\" width=80></td></tr>");
			replyMSG.append("</table>");
			replyMSG.append("<center><br><br><br>");
			replyMSG.append("<button value=\"OK\" action=\"bypass -h admin_fight_calculator lvl1 $lvl1 lvl2 $lvl2\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			replyMSG.append("</center>");
			replyMSG.append("</body></html>");
		}
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private void handleShow(String params, L2PcInstance activeChar)
	{
		params = params.trim();
		
		L2Character npc1 = null;
		L2Character npc2 = null;
		if (params.length() == 0)
		{
			npc1 = activeChar;
			npc2 = (L2Character) activeChar.getTarget();
			if (npc2 == null)
			{
				activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
				return;
			}
		}
		else
		{
			int mid1 = 0;
			int mid2 = 0;
			final StringTokenizer st = new StringTokenizer(params);
			mid1 = Integer.parseInt(st.nextToken());
			mid2 = Integer.parseInt(st.nextToken());
			
			npc1 = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcData.getInstance().getTemplate(mid1));
			npc2 = new L2MonsterInstance(IdFactory.getInstance().getNextId(), NpcData.getInstance().getTemplate(mid2));
		}
		
		int miss1 = 0;
		int miss2 = 0;
		int shld1 = 0;
		int shld2 = 0;
		int crit1 = 0;
		int crit2 = 0;
		double patk1 = 0;
		double patk2 = 0;
		double pdef1 = 0;
		double pdef2 = 0;
		double dmg1 = 0;
		double dmg2 = 0;
		
		// ATTACK speed in milliseconds
		int sAtk1 = npc1.calculateTimeBetweenAttacks(null);
		int sAtk2 = npc2.calculateTimeBetweenAttacks(null);
		// number of ATTACK per 100 seconds
		sAtk1 = 100000 / sAtk1;
		sAtk2 = 100000 / sAtk2;
		
		for (int i = 0; i < 10000; i++)
		{
			final boolean miss1Formula = Formulas.calcHitMiss(npc1, npc2);
			if (miss1Formula)
			{
				miss1++;
			}
			final boolean shld1Formula = Formulas.calcShldUse(npc1, npc2);
			if (shld1Formula)
			{
				shld1++;
			}
			final boolean crit1Formula = Formulas.calcCrit(npc1.getStat().getCriticalHit(npc2, null));
			if (crit1Formula)
			{
				crit1++;
			}
			
			double patk1Formula = npc1.getStat().getPAtk(npc2);
			patk1Formula += Rnd.nextDouble() * npc1.getRandomDamageMultiplier();
			patk1 += patk1Formula;
			
			final double pdef1Formula = npc1.getStat().getPDef(npc2);
			pdef1 += pdef1Formula;
			
			if (!miss1Formula)
			{
				final double dmg1Formula = Formulas.calcPhysDam(npc1, npc2, null, shld1Formula, crit1Formula, false);
				dmg1 += dmg1Formula;
				npc1.abortAttack();
			}
		}
		
		for (int i = 0; i < 10000; i++)
		{
			final boolean _miss2 = Formulas.calcHitMiss(npc2, npc1);
			if (_miss2)
			{
				miss2++;
			}
			final boolean _shld2 = Formulas.calcShldUse(npc2, npc1);
			if (_shld2)
			{
				shld2++;
			}
			final boolean _crit2 = Formulas.calcCrit(npc2.getStat().getCriticalHit(npc1, null));
			if (_crit2)
			{
				crit2++;
			}
			
			double _patk2 = npc2.getStat().getPAtk(npc1);
			_patk2 += Rnd.nextDouble() * npc2.getRandomDamageMultiplier();
			patk2 += _patk2;
			
			final double _pdef2 = npc2.getStat().getPDef(npc1);
			pdef2 += _pdef2;
			
			if (!_miss2)
			{
				final double _dmg2 = Formulas.calcPhysDam(npc2, npc1, null, _shld2, _crit2, false);
				dmg2 += _dmg2;
				npc2.abortAttack();
			}
		}
		
		miss1 /= 100;
		miss2 /= 100;
		shld1 /= 100;
		shld2 /= 100;
		crit1 /= 100;
		crit2 /= 100;
		patk1 /= 10000;
		patk2 /= 10000;
		pdef1 /= 10000;
		pdef2 /= 10000;
		dmg1 /= 10000;
		dmg2 /= 10000;
		
		// total damage per 100 seconds
		final int tdmg1 = (int) (sAtk1 * dmg1);
		final int tdmg2 = (int) (sAtk2 * dmg2);
		// HP restored per 100 seconds
		final double maxHp1 = npc1.getStat().getMaxHp();
		final int hp1 = (int) ((Formulas.calcHpRegen(npc1) * 100000) / Formulas.getRegeneratePeriod(npc1));
		
		final double maxHp2 = npc2.getStat().getMaxHp();
		final int hp2 = (int) ((Formulas.calcHpRegen(npc2) * 100000) / Formulas.getRegeneratePeriod(npc2));
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		final StringBuilder replyMSG = new StringBuilder();
		replyMSG.append("<html><title>Selected mobs to fight</title>");
		replyMSG.append("<body>");
		replyMSG.append("<table>");
		if (params.length() == 0)
		{
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>me</td><td width=70>target</td></tr>");
		}
		else
		{
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>" + ((NpcTemplate) npc1.getTemplate()).getName() + "</td><td width=70>" + ((NpcTemplate) npc2.getTemplate()).getName() + "</td></tr>");
		}
		replyMSG.append("<tr><td>miss</td><td>" + miss1 + "%</td><td>" + miss2 + "%</td></tr>");
		replyMSG.append("<tr><td>shld</td><td>" + shld2 + "%</td><td>" + shld1 + "%</td></tr>");
		replyMSG.append("<tr><td>crit</td><td>" + crit1 + "%</td><td>" + crit2 + "%</td></tr>");
		replyMSG.append("<tr><td>pAtk / pDef</td><td>" + ((int) patk1) + " / " + ((int) pdef1) + "</td><td>" + ((int) patk2) + " / " + ((int) pdef2) + "</td></tr>");
		replyMSG.append("<tr><td>made hits</td><td>" + sAtk1 + "</td><td>" + sAtk2 + "</td></tr>");
		replyMSG.append("<tr><td>dmg per hit</td><td>" + ((int) dmg1) + "</td><td>" + ((int) dmg2) + "</td></tr>");
		replyMSG.append("<tr><td>got dmg</td><td>" + tdmg2 + "</td><td>" + tdmg1 + "</td></tr>");
		replyMSG.append("<tr><td>got regen</td><td>" + hp1 + "</td><td>" + hp2 + "</td></tr>");
		replyMSG.append("<tr><td>had HP</td><td>" + (int) maxHp1 + "</td><td>" + (int) maxHp2 + "</td></tr>");
		replyMSG.append("<tr><td>die</td>");
		if ((tdmg2 - hp1) > 1)
		{
			replyMSG.append("<td>" + (int) ((100 * maxHp1) / (tdmg2 - hp1)) + " sec</td>");
		}
		else
		{
			replyMSG.append("<td>never</td>");
		}
		if ((tdmg1 - hp2) > 1)
		{
			replyMSG.append("<td>" + (int) ((100 * maxHp2) / (tdmg1 - hp2)) + " sec</td>");
		}
		else
		{
			replyMSG.append("<td>never</td>");
		}
		replyMSG.append("</tr>");
		replyMSG.append("</table>");
		replyMSG.append("<center><br>");
		if (params.length() == 0)
		{
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		}
		else
		{
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show " + ((NpcTemplate) npc1.getTemplate()).getId() + " " + ((NpcTemplate) npc2.getTemplate()).getId() + "\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		}
		replyMSG.append("</center>");
		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
		
		if (params.length() != 0)
		{
			((L2MonsterInstance) npc1).deleteMe();
			((L2MonsterInstance) npc2).deleteMe();
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
