package l2j.gameserver.network.external.server;

import l2j.Config;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * the HTML parser in the client knowns these standard and non-standard tags and attributes VOLUMN UNKNOWN UL U TT TR TITLE TEXTCODE TEXTAREA TD TABLE SUP SUB STRIKE SPIN SELECT RIGHT PRE P OPTION OL MULTIEDIT LI LEFT INPUT IMG I HTML H7 H6 H5 H4 H3 H2 H1 FONT EXTEND EDIT COMMENT COMBOBOX CENTER
 * BUTTON BR BODY BAR ADDRESS A SEL LIST VAR FORE READONL ROWS VALIGN FIXWIDTH BORDERCOLORLI BORDERCOLORDA BORDERCOLOR BORDER BGCOLOR BACKGROUND ALIGN VALU READONLY MULTIPLE SELECTED TYP TYPE MAXLENGTH CHECKED SRC Y X QUERYDELAY NOSCROLLBAR IMGSRC B FG SIZE FACE COLOR DEFFON DEFFIXEDFONT WIDTH VALUE
 * TOOLTIP NAME MIN MAX HEIGHT DISABLED ALIGN MSG LINK HREF ACTION
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class NpcHtmlMessage extends AServerPacket
{
	// d S
	// d is usually 0, S is the html text starting with <html> and ending with </html>
	private final int npcObjId;
	private String html;
	
	/**
	 * @param npcObjId
	 * @param text
	 */
	public NpcHtmlMessage(int npcObjId, String text)
	{
		this.npcObjId = npcObjId;
		setHtml(text);
	}
	
	/**
	 * @param npcObjId
	 */
	public NpcHtmlMessage(int npcObjId)
	{
		this.npcObjId = npcObjId;
	}
	
	public void setHtml(String text)
	{
		if (text.length() > 8192)
		{
			LOG.warning("Html is too long! this will crash the client!");
			html = "<html><body>Html was too long</body></html>";
			return;
		}
		html = text; // html code must not exceed 8192 bytes
	}
	
	public boolean setFile(String path)
	{
		String content = HtmData.getInstance().getHtm(path);
		
		if (content == null)
		{
			setHtml("<html><body>My Text is missing:<br>" + path + "</body></html>");
			LOG.warning("missing html page " + path);
			return false;
		}
		
		setHtml(content);
		
		return true;
	}
	
	public void replace(String pattern, String value)
	{
		html = html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
	}
	
	public void replace(String pattern, int value)
	{
		html = html.replaceAll(pattern, String.valueOf(value));
	}
	
	private final void buildBypassCache()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.clearBypass();
		
		for (int i = 0; i < html.length(); i++)
		{
			int start = html.indexOf("bypass -h ", i);
			int finish = html.indexOf("\"", start);
			
			if ((start < 0) || (finish < 0))
			{
				break;
			}
			
			start += 10;
			i = finish;
			int finish2 = html.indexOf("$", start);
			if ((finish2 < finish) && (finish2 > 0))
			{
				activeChar.addBypass2(html.substring(start, finish2), npcObjId);
			}
			else
			{
				activeChar.addBypass(html.substring(start, finish), npcObjId);
			}
		}
	}
	
	@Override
	public void runImpl()
	{
		if (Config.BYPASS_VALIDATION)
		{
			buildBypassCache();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x0f);
		writeD(npcObjId);
		writeS(html);
		writeD(0x00); // itemId
	}
}
