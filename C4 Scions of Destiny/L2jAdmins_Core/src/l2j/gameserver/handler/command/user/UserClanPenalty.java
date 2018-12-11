package l2j.gameserver.handler.command.user;

import java.text.SimpleDateFormat;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author Tempy
 */
public class UserClanPenalty implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			100
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		boolean penalty = false;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<center><table width=270 border=0 bgcolor=111111>");
		sb.append("<tr><td width=170>Penalty</td>");
		sb.append("<td width=100 align=center>Expiration Date</td></tr>");
		sb.append("</table><table width=270 border=0><tr>");
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			sb.append("<tr><td width=170>Unable to join a clan.</td>");
			sb.append("<td width=100 align=center>" + format.format(activeChar.getClanJoinExpiryTime()) + "</td></tr>");
			penalty = true;
		}
		
		if (activeChar.getClanCreateExpiryTime() > System.currentTimeMillis())
		{
			sb.append("<tr><td width=170>Unable to create a clan.</td>");
			sb.append("<td width=100 align=center>" + format.format(activeChar.getClanCreateExpiryTime()) + "</td></tr>");
			penalty = true;
		}
		
		if (activeChar.getClan() != null)
		{
			if (activeChar.getClan().getCharPenaltyExpiryTime() > System.currentTimeMillis())
			{
				sb.append("<tr><td width=170>Unable to invite players to clan.</td>");
				sb.append("<td width=100 align=center>" + format.format(activeChar.getClan().getCharPenaltyExpiryTime()) + "</td></tr>");
				penalty = true;
			}
			
			if (activeChar.getClan().getRecoverPenaltyExpiryTime() > System.currentTimeMillis())
			{
				sb.append("<tr><td width=170>Unable to dissolve clan.</td>");
				sb.append("<td width=100 align=center>" + format.format(activeChar.getClan().getRecoverPenaltyExpiryTime()) + "</td></tr>");
				penalty = true;
			}
		}
		
		if (!penalty)
		{
			sb.append("<td width=170>No penalties currently in effect.</td>");
			sb.append("<td width=100 align=center> </td>");
		}
		
		sb.append("</tr></table><img src=L2UI.SquareWhite width=270 height=1>");
		sb.append("</center></body></html>");
		
		NpcHtmlMessage penaltyHtml = new NpcHtmlMessage(0);
		penaltyHtml.setHtml(sb.toString());
		activeChar.sendPacket(penaltyHtml);
		return true;
	}
}
