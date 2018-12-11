package l2j.gameserver.handler.community;

import java.util.StringTokenizer;

import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class CommunityAddFavorite extends AbstractCommunityHandler
{
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"bbs_add_fav"
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		separateAndSend("<html><body><br><br><center>the command: " + st.nextToken() + " is not implemented yet</center><br><br></body></html>", activeChar);
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
