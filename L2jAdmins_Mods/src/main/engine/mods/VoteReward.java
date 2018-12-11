package main.engine.mods;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import main.data.ConfigData;
import main.data.ObjectData;
import main.engine.AbstractMod;
import main.holders.RewardHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.util.Broadcast;

/**
 * @author fissban
 */
public class VoteReward extends AbstractMod
{
	private enum TopType
	{
		HOPZONE,
		TOPZONE,
		NETWORK,
	}
	
	private static int lastVoteCountHopzone = 0;
	private static int lastVoteCountTopzone = 0;
	private static int lastVoteCountNetwork = 0;
	
	public VoteReward()
	{
		registerMod(ConfigData.ENABLE_VoteReward && (ConfigData.ENABLE_HOPZONE || ConfigData.ENABLE_TOPZONE || ConfigData.ENABLE_NETWORK));
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				startTimer("getLastVote", 0, null, null, false);
				startTimer("getVotes", ConfigData.TIME_CHECK_VOTES * 60 * 1000, null, null, true);
				break;
			case END:
				cancelTimers("getLastVote");
				cancelTimers("getVotes");
				break;
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		
		switch (timerName)
		{
			case "getLastVote":
				if (ConfigData.ENABLE_HOPZONE)
				{
					lastVoteCountHopzone = getVotesHopzone();
				}
				if (ConfigData.ENABLE_TOPZONE)
				{
					lastVoteCountTopzone = getVotesTopzone();
				}
				if (ConfigData.ENABLE_NETWORK)
				{
					lastVoteCountNetwork = getVotesNetwork();
				}
				break;
			
			case "getVotes":
				var voteHopzone = 0;
				var voteTopzone = 0;
				var voteNetwork = 0;
				// the number of votes obtained.
				if (ConfigData.ENABLE_HOPZONE)
				{
					voteHopzone = getVotesHopzone();
				}
				if (ConfigData.ENABLE_TOPZONE)
				{
					voteTopzone = getVotesTopzone();
				}
				if (ConfigData.ENABLE_NETWORK)
				{
					voteNetwork = getVotesNetwork();
				}
				// the number of votes of each page is displayed
				Broadcast.toAllOnlinePlayers("------------^^------------");
				if (ConfigData.ENABLE_HOPZONE)
				{
					checkVoteRewards(voteHopzone, TopType.HOPZONE);
				}
				if (ConfigData.ENABLE_TOPZONE)
				{
					checkVoteRewards(voteTopzone, TopType.TOPZONE);
				}
				if (ConfigData.ENABLE_NETWORK)
				{
					checkVoteRewards(voteNetwork, TopType.NETWORK);
				}
				Broadcast.toAllOnlinePlayers("------------^^------------");
				break;
		}
	}
	
	// MISC -----------------------------------------------------------------------------------------
	
	/**
	 * prizes are awarded.<br>
	 * <li>Check that the character is online.</li>
	 * <li>Check that the reward is there.</li>
	 * <li>Send message.</li>
	 * @param player
	 * @param reward
	 */
	private static void giveRewardAllPlayers(RewardHolder reward, TopType topName)
	{
		for (var player : L2World.getInstance().getAllPlayers())
		{
			if (reward == null)
			{
				return;
			}
			
			var ph = ObjectData.get(PlayerHolder.class, player);
			if (ph.isOffline())
			{
				return;
			}
			
			if (player.isInJail())
			{
				return;
			}
			
			// XXX If you want to add an ip control here is the place
			
			player.getInventory().addItem("voteReward", reward.getRewardId(), reward.getRewardCount(), null, true);
		}
	}
	
	/**
	 * @param rewardsList
	 * @param voteTop
	 * @return
	 */
	private static void checkVoteRewards(int voteTop, TopType topName)
	{
		// if votes are not obtained from the "top" no actions are performed
		if (voteTop == 0)
		{
			return;
		}
		
		RewardHolder reward = null;
		
		var nextVote = 0;
		var lastVote = 0;
		
		// the last vote of the page is obtained according to the top
		switch (topName)
		{
			case HOPZONE:
				lastVote = lastVoteCountHopzone;
				break;
			case TOPZONE:
				lastVote = lastVoteCountTopzone;
				break;
			case NETWORK:
				lastVote = lastVoteCountNetwork;
				break;
		}
		
		for (var top : ConfigData.VOTE_REWARDS.entrySet())
		{
			if (top.getKey() < lastVote)
			{
				continue;
			}
			else
			{
				nextVote = top.getKey();
				reward = top.getValue();
				break;
			}
		}
		
		Broadcast.toAllOnlinePlayers(" @ " + topName.name().toLowerCase() + ":  " + voteTop + " votes.");
		
		if (nextVote == 0)
		{
			// no has more rewards!!!
			return;
		}
		
		if (nextVote < voteTop)
		{
			Broadcast.toAllOnlinePlayers(" @ " + topName.name().toLowerCase() + ": You won the Reward Vote!");
			
			giveRewardAllPlayers(reward, topName);
		}
		else
		{
			Broadcast.toAllOnlinePlayers(" @ " + topName.name().toLowerCase() + ": next reward in " + nextVote + " votes.");
		}
		
		// update last votes
		switch (topName)
		{
			case HOPZONE:
				lastVoteCountHopzone = voteTop;
				break;
			case TOPZONE:
				lastVoteCountTopzone = voteTop;
				break;
			case NETWORK:
				lastVoteCountNetwork = voteTop;
				break;
		}
	}
	
	/**
	 * Get the votes of TOPZONE
	 * @return
	 */
	public static int getVotesTopzone()
	{
		var votes = 0;
		try
		{
			var con = new URL(ConfigData.TOPZONE_URL).openConnection();
			
			con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
			con.setConnectTimeout(5000);
			
			try (var in = new BufferedReader(new InputStreamReader(con.getInputStream())))
			{
				
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					if (inputLine.contains("fa fa-fw fa-lg fa-thumbs-up"))
					{
						return Integer.valueOf(inputLine.split(">")[3].replace("</span", ""));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error while getting Topzone server vote count.");
			// e.printStackTrace();
		}
		
		return votes;
	}
	
	/**
	 * Get the votes of HOPZONE
	 * @return
	 */
	public static int getVotesHopzone()
	{
		var votes = 0;
		try
		{
			var con = new URL(ConfigData.HOPZONE_URL).openConnection();
			
			con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
			con.setConnectTimeout(5000);
			
			try (var in = new BufferedReader(new InputStreamReader(con.getInputStream())))
			{
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					if (inputLine.contains("rank tooltip"))
					{
						return Integer.valueOf(inputLine.split(">")[2].replace("</span", ""));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error while getting Hopzone server vote count.");
			// e.printStackTrace();
		}
		
		return votes;
	}
	
	/**
	 * Get the votes of NETWORK
	 * @return
	 */
	public static int getVotesNetwork()
	{
		var votes = 0;
		try
		{
			var con = new URL(ConfigData.NETWORK_URL).openConnection();
			
			con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
			con.setConnectTimeout(5000);
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
			{
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				{
					if (inputLine.contains("tls-in-sts"))
					{
						return Integer.valueOf(inputLine.split(">")[2].replace("</b", ""));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error while getting Network server vote count.");
			// e.printStackTrace();
		}
		
		return votes;
	}
}
