package l2j.gameserver.scripts.ai.npc.sevensigns;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.util.Util;

/**
 * @Original code in source by Tempy (L2FestivalGuideInstance)
 * @author   fissban
 */
public class Festival extends Script
{
	//@formatter:off
	private static final int[] NPCS_DAWN =
	{
		8127,8128,8129,8130,8131
	};
	
	private static final int[] NPCS_DUSK =
	{
		8137,8138,8139,8140,8141
	};
	private static final int[] NPCS_WITCH =
	{
		8132,8133,8134,8135,8136,8142,8143,8144,8145,8146
	};
	//@formatter:on
	// Html
	private static final String HTML_PATH = "data/html/sevenSigns/festival/";
	// Misc
	private static int festivalType;
	private static CabalType festivalOracle;
	private static int blueStonesNeeded;
	private static int greenStonesNeeded;
	private static int redStonesNeeded;
	
	public Festival()
	{
		super(-1, "ai/npc/sevensigns");
		
		addStartNpc(NPCS_DAWN);
		addStartNpc(NPCS_DUSK);
		addStartNpc(NPCS_WITCH);
		
		addFirstTalkId(NPCS_DAWN);
		addFirstTalkId(NPCS_DUSK);
		addFirstTalkId(NPCS_WITCH);
		
		addTalkId(NPCS_DAWN);
		addTalkId(NPCS_DUSK);
		addTalkId(NPCS_WITCH);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (Util.contains(NPCS_DAWN, npc.getId()))
		{
			return HTML_PATH + "dawn_guide.htm";
		}
		if (Util.contains(NPCS_DUSK, npc.getId()))
		{
			return HTML_PATH + "dusk_guide.htm";
		}
		if (Util.contains(NPCS_WITCH, npc.getId()))
		{
			return HTML_PATH + "festival_witch.htm";
		}
		
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (npc.getId())
		{
			case 8127:
			case 8132:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_31;
				festivalOracle = CabalType.DAWN;
				blueStonesNeeded = 900;
				greenStonesNeeded = 540;
				redStonesNeeded = 270;
				break;
			case 8128:
			case 8133:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_42;
				festivalOracle = CabalType.DAWN;
				blueStonesNeeded = 1500;
				greenStonesNeeded = 900;
				redStonesNeeded = 450;
				break;
			case 8129:
			case 8134:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_53;
				festivalOracle = CabalType.DAWN;
				blueStonesNeeded = 3000;
				greenStonesNeeded = 1800;
				redStonesNeeded = 900;
				break;
			case 8130:
			case 8135:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_64;
				festivalOracle = CabalType.DAWN;
				blueStonesNeeded = 4500;
				greenStonesNeeded = 2700;
				redStonesNeeded = 1350;
				break;
			case 8131:
			case 8136:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_NONE;
				festivalOracle = CabalType.DAWN;
				blueStonesNeeded = 6000;
				greenStonesNeeded = 3600;
				redStonesNeeded = 1800;
				break;
			case 8137:
			case 8142:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_31;
				festivalOracle = CabalType.DUSK;
				blueStonesNeeded = 900;
				greenStonesNeeded = 540;
				redStonesNeeded = 270;
				break;
			case 8138:
			case 8143:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_42;
				festivalOracle = CabalType.DUSK;
				blueStonesNeeded = 1500;
				greenStonesNeeded = 900;
				redStonesNeeded = 450;
				break;
			case 8139:
			case 8144:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_53;
				festivalOracle = CabalType.DUSK;
				blueStonesNeeded = 3000;
				greenStonesNeeded = 1800;
				redStonesNeeded = 900;
				break;
			case 8140:
			case 8145:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_64;
				festivalOracle = CabalType.DUSK;
				blueStonesNeeded = 4500;
				greenStonesNeeded = 2700;
				redStonesNeeded = 1350;
				break;
			case 8141:
			case 8146:
				festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_NONE;
				festivalOracle = CabalType.DUSK;
				blueStonesNeeded = 6000;
				greenStonesNeeded = 3600;
				redStonesNeeded = 1800;
				break;
		}
		
		StringTokenizer st = new StringTokenizer(event, " ");
		
		if (event.startsWith("FestivalDesc"))
		{
			int val = Integer.parseInt(st.nextToken());
			showChatWindow(player, npc, val, null, true);
		}
		else if (event.startsWith("Festival"))
		{
			Party playerParty = player.getParty();
			int val = Integer.parseInt(st.nextToken());
			
			switch (val)
			{
				case 1: // Become a Participant
					// Check if the festival period is active, if not then don't allow registration.
					if (SevenSignsManager.getInstance().isSealValidationPeriod())
					{
						showChatWindow(player, npc, 2, "a", false);
						return null;
					}
					
					// Check if a festival is in progress, then don't allow registration yet.
					if (SevenSignsFestival.getInstance().isFestivalInitialized())
					{
						player.sendMessage("You cannot sign up while a festival is in progress.");
						return null;
					}
					
					// Check if the player is in a formed party already.
					if (playerParty == null)
					{
						showChatWindow(player, npc, 2, "b", false);
						return null;
					}
					
					// Check if the player is the party leader.
					if (!playerParty.isLeader(player))
					{
						showChatWindow(player, npc, 2, "c", false);
						return null;
					}
					
					// Check to see if the party has at least 5 members.
					if (playerParty.getMemberCount() < Config.ALT_FESTIVAL_MIN_PLAYER)
					{
						showChatWindow(player, npc, 2, "b", false);
						return null;
					}
					
					// Check if all the party members are in the required level range.
					if (playerParty.getLevel() > SevenSignsFestival.getMaxLevelForFestival(festivalType))
					{
						showChatWindow(player, npc, 2, "d", false);
						return null;
					}
					
					// TODO: Check if the player has delevelled by comparing their skill levels.
					
					/*
					 * Check to see if the player has already signed up, if they are then update the participant list providing all the required criteria has been met.
					 */
					if (player.isFestivalParticipant())
					{
						SevenSignsFestival.getInstance().setParticipants(festivalOracle, festivalType, playerParty);
						showChatWindow(player, npc, 2, "f", false);
						return null;
					}
					
					showChatWindow(player, npc, 1, null, false);
					break;
				case 2: // Festival 2 xxxx
					int stoneType = Integer.parseInt(st.nextToken());
					int stonesNeeded = 0;
					
					switch (stoneType)
					{
						case SevenSignsManager.SEAL_STONE_BLUE_ID:
							stonesNeeded = blueStonesNeeded;
							break;
						case SevenSignsManager.SEAL_STONE_GREEN_ID:
							stonesNeeded = greenStonesNeeded;
							break;
						case SevenSignsManager.SEAL_STONE_RED_ID:
							stonesNeeded = redStonesNeeded;
							break;
					}
					
					if (!player.getInventory().destroyItemByItemId("SevenSigns", stoneType, stonesNeeded, npc, true))
					{
						return null;
					}
					
					SevenSignsFestival.getInstance().setParticipants(festivalOracle, festivalType, playerParty);
					SevenSignsFestival.getInstance().addAccumulatedBonus(festivalType, stoneType, stonesNeeded);
					
					showChatWindow(player, npc, 2, "e", false);
					break;
				case 3: // Score Registration
					// Check if the festival period is active, if not then don't register the score.
					if (SevenSignsManager.getInstance().isSealValidationPeriod())
					{
						showChatWindow(player, npc, 3, "a", false);
						return null;
					}
					
					// Check if a festival is in progress, if it is don't register the score.
					if (SevenSignsFestival.getInstance().isFestivalInProgress())
					{
						player.sendMessage("You cannot register a score while a festival is in progress.");
						return null;
					}
					
					// Check if the player is in a party.
					if (playerParty == null)
					{
						showChatWindow(player, npc, 3, "b", false);
						return null;
					}
					
					List<L2PcInstance> prevParticipants = SevenSignsFestival.getInstance().getPreviousParticipants(festivalOracle, festivalType);
					
					// Check if there are any past participants.
					if (prevParticipants == null)
					{
						return null;
					}
					
					// Check if this player was among the past set of participants for this festival.
					if (!prevParticipants.contains(player))
					{
						showChatWindow(player, npc, 3, "b", false);
						return null;
					}
					
					// Check if this player was the party leader in the festival.
					if (player.getObjectId() != prevParticipants.get(0).getObjectId())
					{
						showChatWindow(player, npc, 3, "b", false);
						return null;
					}
					
					ItemInstance bloodOfferings = player.getInventory().getItemById(SevenSignsFestival.FESTIVAL_OFFERING_ID);
					int offeringCount = 0;
					
					// Check if the player collected any blood offerings during the festival.
					if (bloodOfferings == null)
					{
						player.sendMessage("You do not have any blood offerings to contribute.");
						return null;
					}
					
					offeringCount = bloodOfferings.getCount();
					
					int offeringScore = offeringCount * SevenSignsFestival.FESTIVAL_OFFERING_VALUE;
					boolean isHighestScore = SevenSignsFestival.getInstance().setFinalScore(player, festivalOracle, festivalType, offeringScore);
					
					player.getInventory().destroyItem("SevenSigns", bloodOfferings, npc, false);
					
					// Send message that the contribution score has increased.
					player.sendPacket(new SystemMessage(SystemMessage.CONTRIB_SCORE_INCREASED_S1).addNumber(offeringScore));
					
					if (isHighestScore)
					{
						showChatWindow(player, npc, 3, "c", false);
					}
					else
					{
						showChatWindow(player, npc, 3, "d", false);
					}
					break;
				case 4: // Current High Scores
					StringBuilder strBuffer = new StringBuilder("<html><body>Festival Guide:<br>These are the top scores of the week, for the ");
					
					final StatsSet dawnData = SevenSignsFestival.getInstance().getHighestScoreData(CabalType.DAWN, festivalType);
					final StatsSet duskData = SevenSignsFestival.getInstance().getHighestScoreData(CabalType.DUSK, festivalType);
					final StatsSet overallData = SevenSignsFestival.getInstance().getOverallHighestScoreData(festivalType);
					
					final int dawnScore = dawnData.getInteger("score");
					final int duskScore = duskData.getInteger("score");
					int overallScore = 0;
					
					// If no data is returned, assume there is no record, or all scores are 0.
					if (overallData != null)
					{
						overallScore = overallData.getInteger("score");
					}
					
					strBuffer.append(SevenSignsFestival.getFestivalName(festivalType) + " festival.<br>");
					
					if (dawnScore > 0)
					{
						strBuffer.append("Dawn: " + calculateDate(dawnData.getString("date")) + ". Score " + dawnScore + "<br>" + dawnData.getString("members") + "<br>");
					}
					else
					{
						strBuffer.append("Dawn: No record exists. Score 0<br>");
					}
					
					if (duskScore > 0)
					{
						strBuffer.append("Dusk: " + calculateDate(duskData.getString("date")) + ". Score " + duskScore + "<br>" + duskData.getString("members") + "<br>");
					}
					else
					{
						strBuffer.append("Dusk: No record exists. Score 0<br>");
					}
					
					if (overallScore > 0)
					{
						String cabalStr = "Children of Dusk";
						
						if (overallData == null)
						{
							LOG.warning(getClass().getSimpleName() + ": null point, pliz contact L2jAdmins team! (overallData)");
							return null;
						}
						if (overallData.getString("cabal").equals("dawn"))
						{
							cabalStr = "Children of Dawn";
						}
						
						strBuffer.append("Consecutive top scores: " + calculateDate(overallData.getString("date")) + ". Score " + overallScore + "<br>Affilated side: " + cabalStr + "<br>" + overallData.getString("members") + "<br>");
					}
					else
					{
						strBuffer.append("Consecutive top scores: No record exists. Score 0<br>");
					}
					
					strBuffer.append("<a action=\"bypass -h npc_" + npc.getObjectId() + "_Chat 0\">Go back.</a></body></html>");
					
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setHtml(strBuffer.toString());
					player.sendPacket(html);
					break;
				case 8: // Increase the Festival Challenge
					if (playerParty == null)
					{
						return null;
					}
					
					if (!SevenSignsFestival.getInstance().isFestivalInProgress())
					{
						return null;
					}
					
					if (!playerParty.isLeader(player))
					{
						showChatWindow(player, npc, 8, "a", false);
						break;
					}
					
					if (SevenSignsFestival.getInstance().increaseChallenge(festivalOracle, festivalType))
					{
						showChatWindow(player, npc, 8, "b", false);
					}
					else
					{
						showChatWindow(player, npc, 8, "c", false);
					}
					break;
				case 9: // Leave the Festival
					if (playerParty == null)
					{
						return null;
					}
					
					/**
					 * If the player is the party leader, remove all participants from the festival (i.e. set the party to null, when updating the participant list) otherwise just remove this player from the "arena", and also remove them from the party.
					 */
					boolean isLeader = playerParty.isLeader(player);
					
					if (isLeader)
					{
						SevenSignsFestival.getInstance().updateParticipants(player, null);
					}
					else
					{
						SevenSignsFestival.getInstance().updateParticipants(player, playerParty);
						playerParty.removePartyMember(player, true);
					}
					break;
				case 0: // Distribute Accumulated Bonus
					if (!SevenSignsManager.getInstance().isSealValidationPeriod())
					{
						player.sendMessage("Bonuses cannot be paid during the competition period.");
						return null;
					}
					
					if (SevenSignsFestival.getInstance().distribAccumulatedBonus(player) > 0)
					{
						showChatWindow(player, npc, 0, "a", false);
					}
					else
					{
						showChatWindow(player, npc, 0, "b", false);
					}
					break;
				default:
					showChatWindow(player, npc, val, null, false);
			}
		}
		
		return null;
	}
	
	private static void showChatWindow(L2PcInstance player, L2Npc npc, int val, String suffix, boolean isDescription)
	{
		String filename = HTML_PATH;
		filename += (isDescription) ? "desc_" : "festival_";
		filename += (suffix != null) ? val + suffix + ".htm" : val + ".htm";
		
		// Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(filename);
		html.replace("%festivalType%", SevenSignsFestival.getFestivalName(festivalType));
		html.replace("%cycleMins%", String.valueOf(SevenSignsFestival.getInstance().getMinsToNextCycle()));
		if (!isDescription && "2b".equals(val + suffix))
		{
			html.replace("%minFestivalPartyMembers%", String.valueOf(Config.ALT_FESTIVAL_MIN_PLAYER));
		}
		
		switch (val)
		{
			case 5:// If the stats or bonus table is required, construct them.
				html.replace("%statsTable%", getStatsTable());
				break;
			case 6:// If the stats or bonus table is required, construct them.
				html.replace("%bonusTable%", getBonusTable());
				break;
			case 1:// festival's fee
				html.replace("%blueStoneNeeded%", blueStonesNeeded);
				html.replace("%greenStoneNeeded%", greenStonesNeeded);
				html.replace("%redStoneNeeded%", redStonesNeeded);
				break;
		}
		
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private static final String getStatsTable()
	{
		StringBuilder sb = new StringBuilder();
		
		// Get the scores for each of the festival level ranges (types).
		for (int i = 0; i < 5; i++)
		{
			int dawnScore = SevenSignsFestival.getInstance().getHighestScore(CabalType.DAWN, i);
			int duskScore = SevenSignsFestival.getInstance().getHighestScore(CabalType.DUSK, i);
			String festivalName = SevenSignsFestival.getFestivalName(i);
			String winningCabal = "Children of Dusk";
			
			if (dawnScore > duskScore)
			{
				winningCabal = "Children of Dawn";
			}
			else if (dawnScore == duskScore)
			{
				winningCabal = "None";
			}
			
			sb.append("<tr><td width=\"100\" align=\"center\">" + festivalName + "</td><td align=\"center\" width=\"35\">" + duskScore + "</td><td align=\"center\" width=\"35\">" + dawnScore + "</td><td align=\"center\" width=\"130\">" + winningCabal + "</td></tr>");
		}
		
		return sb.toString();
	}
	
	private static final String getBonusTable()
	{
		StringBuilder sb = new StringBuilder();
		
		// Get the accumulated scores for each of the festival level ranges (types).
		for (int i = 0; i < 5; i++)
		{
			int accumScore = SevenSignsFestival.getInstance().getAccumulatedBonus(i);
			String festivalName = SevenSignsFestival.getFestivalName(i);
			
			sb.append("<tr><td align=\"center\" width=\"150\">" + festivalName + "</td><td align=\"center\" width=\"150\">" + accumScore + "</td></tr>");
		}
		
		return sb.toString();
	}
	
	private static final String calculateDate(String milliFromEpoch)
	{
		Calendar calCalc = Calendar.getInstance();
		calCalc.setTimeInMillis(Long.valueOf(milliFromEpoch));
		
		return calCalc.get(Calendar.YEAR) + "/" + calCalc.get(Calendar.MONTH) + "/" + calCalc.get(Calendar.DAY_OF_MONTH);
	}
}
