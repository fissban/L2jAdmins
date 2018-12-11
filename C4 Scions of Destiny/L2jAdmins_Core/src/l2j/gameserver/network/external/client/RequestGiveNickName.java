package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGiveNickName extends AClientPacket
{
	private String target;
	private String title;
	
	@Override
	protected void readImpl()
	{
		target = readS();
		title = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!Util.isValidNameTitle(title))
		{
			activeChar.sendMessage("Wrong title!");
		}
		else if (activeChar.isNoble() && target.matches(activeChar.getName()))// Noblesse can bestow a title to themselves
		{
			activeChar.setTitle(title);
			activeChar.sendPacket(SystemMessage.TITLE_CHANGED);
			activeChar.broadcastTitleInfo();
		}
		else if (activeChar.hasClanPrivilege(ClanPrivilegesType.CL_GIVE_TITLE))// Can the player change/give a title?
		{
			if (activeChar.getClan().getLevel() < 3)
			{
				activeChar.sendPacket(SystemMessage.CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE);
				return;
			}
			
			ClanMemberInstance member1 = activeChar.getClan().getClanMember(target);
			if (member1 != null)
			{
				L2PcInstance member = member1.getPlayerInstance();
				if ((member != null) && !member.getPrivateStore().inOfflineMode())
				{
					// is target from the same clan?
					member.setTitle(title);
					member.sendPacket(SystemMessage.TITLE_CHANGED);
					
					member.broadcastTitleInfo();
				}
				else
				{
					activeChar.sendMessage("Target is not online.");
				}
			}
			else
			{
				activeChar.sendMessage("Target does not belong to your clan.");
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
		}
	}
}
