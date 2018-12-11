package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author zabbix Lets drink to code!
 */
public class RequestLinkHtml extends AClientPacket
{
	private String link;
	
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance actor = getClient().getActiveChar();
		if (actor == null)
		{
			return;
		}
		
		link = readS();
		
		if (link.contains("..") || !link.contains(".htm"))
		{
			LOG.warning("[RequestLinkHtml] hack? link contains prohibited characters: '" + link + "', skipped");
			return;
		}
		
		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setFile(link);
		sendPacket(msg);
	}
}
