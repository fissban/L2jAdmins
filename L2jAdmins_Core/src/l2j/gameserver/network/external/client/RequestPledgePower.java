package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ManagePledgePower;

public class RequestPledgePower extends AClientPacket
{
	private int clanMemberId;
	private int action;
	private int privs;
	
	@Override
	protected void readImpl()
	{
		clanMemberId = readD();
		action = readD();
		
		if (action == 3)
		{
			privs = readD();
		}
		else
		{
			privs = 0;
		}
		
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getClan() == null)
		{
			return;
		}
		
		L2PcInstance member = null;
		if (player.getClan().getClanMember(clanMemberId) != null)
		{
			member = player.getClan().getClanMember(clanMemberId).getPlayerInstance();
		}
		
		switch (action)
		{
			case 1:
				player.sendPacket(new ManagePledgePower(player.getClanPrivileges()));
				break;
			
			case 2:
				if (member != null)
				{
					player.sendPacket(new ManagePledgePower(member.getClanPrivileges()));
				}
				break;
			
			case 3:
				if (player.isClanLeader())
				{
					if (member != null)
					{
						// prevent edit privileges of clan leader
						if (!member.isClanLeader())
						{
							member.setClanPrivileges(ClanPrivilegesType.getAllPrivilegiesById(privs));
						}
					}
					break;
				}
		}
	}
}
