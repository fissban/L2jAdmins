package l2j.gameserver.handler.item;

import l2j.gameserver.data.HtmData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.RadarControl;
import l2j.gameserver.network.external.server.ShowMiniMap;

public class ItemBook implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5588,
			6317,
			7561,
			7063, // Map - Forest of the Dead
			7064, // Lidias Diary
			7065,
			7066,
			7082,
			7083,
			7084,
			7085,
			7086,
			7087,
			7088,
			7089,
			7090,
			7091,
			7092,
			7093,
			7094,
			7095,
			7096,
			7097,
			7098,
			7099,
			7100,
			7101,
			7102,
			7103,
			7104,
			7105,
			7106,
			7107,
			7108,
			7109,
			7110,
			7111,
			7112
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getId();
		
		String filename = "data/html/help/" + itemId + ".htm";
		String content = HtmData.getInstance().getHtm(filename);
		
		// Quest item: Lidia's diary
		if (itemId == 7064)
		{
			activeChar.sendPacket(new ShowMiniMap(1665));
			activeChar.sendPacket(new RadarControl(0, 1, 51995, -51265, -3104));
		}
		
		if (content == null)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setHtml("<html><body>My Text is missing:<br>" + filename + "</body></html>");
			activeChar.sendPacket(html);
		}
		else
		{
			NpcHtmlMessage itemReply = new NpcHtmlMessage(0);
			itemReply.setHtml(content);
			activeChar.sendPacket(itemReply);
		}
		
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
