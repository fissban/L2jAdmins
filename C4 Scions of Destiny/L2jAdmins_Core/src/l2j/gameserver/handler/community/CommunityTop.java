package l2j.gameserver.handler.community;

import java.util.StringTokenizer;

import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class CommunityTop extends AbstractCommunityHandler
{
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_bbshome"
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// bbshome
		
		if (!st.hasMoreTokens())
		{
			separateAndSend(HtmData.getInstance().getHtm(CB_PATH + "top/index.htm"), activeChar);
		}
		else
		{
			separateAndSend(HtmData.getInstance().getHtm(CB_PATH + "top/" + st.nextToken()), activeChar);
		}
	}
	
	@Override
	public String getWriteList()
	{
		return "";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		//
	}
}
