package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.network.AServerPacket;

public class ShowBoard extends AServerPacket
{
	private final String content;
	
	public ShowBoard(String htmlCode, String id)
	{
		content = id + "\u0008" + htmlCode;
	}
	
	public ShowBoard(List<String> arg)
	{
		StringBuilder sb = new StringBuilder(5 + getLength(arg) + arg.size()).append("1002\u0008");
		for (String str : arg)
		{
			sb.append(str).append("\u0008");
		}
		content = sb.toString();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x6e);
		writeC(0x01); // c4 1 to show community 00 to hide
		writeS("bypass _bbshome"); // top
		writeS("bypass _bbsgetfav"); // favorite
		writeS("bypass _bbsloc"); // region
		writeS("bypass _bbsclan"); // clan
		writeS("bypass _bbsmemo"); // memo
		writeS("bypass _maillist_0_1_0_"); // mail
		writeS("bypass _friendlist_0_"); // friends
		writeS("bypass _bbs_add_fav"); // add fav.
		writeS(content);
	}
	
	private static int getLength(final Iterable<String> strings)
	{
		int length = 0;
		for (final String string : strings)
		{
			if (string == null)
			{
				length += 4;
			}
			else
			{
				length += string.length();
			}
		}
		return length;
	}
}
