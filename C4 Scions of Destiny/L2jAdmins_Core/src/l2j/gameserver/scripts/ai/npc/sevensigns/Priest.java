package l2j.gameserver.scripts.ai.npc.sevensigns;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.util.Util;

/**
 * Original code in Source by Tempy (L2SignsPriestInstance)
 * @author fissban
 */
public class Priest extends Script
{
	//@formatter:off
	// Npc
	private static final int[] TOWN_DAWN =
	{
		8078,8079,8080,8081,8082,8083,8084,8692,8694,8168
	};
	private static final int[] TOWN_DUSK =
	{
		8085,8086,8087,8088,8089,8090,8091,8693,8695,8169
	};
	//@formatter:on
	// Html
	private static final String HTML_PATH = "data/html/sevenSigns/priest/";
	
	public Priest()
	{
		super(-1, "ai/npc/sevensigns");
		
		addStartNpc(TOWN_DAWN);
		addStartNpc(TOWN_DUSK);
		
		addFirstTalkId(TOWN_DAWN);
		addFirstTalkId(TOWN_DUSK);
		
		addTalkId(TOWN_DAWN);
		addTalkId(TOWN_DUSK);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// int sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SevenSignsManager.SEAL_AVARICE);
		CabalType sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		boolean isSealValidationPeriod = SevenSignsManager.getInstance().isSealValidationPeriod();
		CabalType compWinner = SevenSignsManager.getInstance().getCabalHighestScore();
		
		if (Util.contains(TOWN_DAWN, npc.getId()))
		{
			switch (playerCabal)
			{
				case DAWN:
					if (isSealValidationPeriod)
					{
						if (compWinner == CabalType.DAWN)
						{
							if (compWinner != sealGnosisOwner)
							{
								return HTML_PATH + "dawn_priest_2c.htm";
							}
							return HTML_PATH + "dawn_priest_2a.htm";
						}
						return HTML_PATH + "dawn_priest_2b.htm";
					}
					return HTML_PATH + "dawn_priest_1b.htm";
				case DUSK:
					if (isSealValidationPeriod)
					{
						return HTML_PATH + "dawn_priest_3b.htm";
					}
					return HTML_PATH + "dawn_priest_3a.htm";
				default:
					if (isSealValidationPeriod)
					{
						if (compWinner == CabalType.DAWN)
						{
							return HTML_PATH + "dawn_priest_4.htm";
						}
						return HTML_PATH + "dawn_priest_2b.htm";
					}
					return HTML_PATH + "dawn_priest_1a.htm";
			}
		}
		else if (Util.contains(TOWN_DUSK, npc.getId()))
		{
			switch (playerCabal)
			{
				case DUSK:
					if (isSealValidationPeriod)
					{
						if (compWinner == CabalType.DUSK)
						{
							if (compWinner != sealGnosisOwner)
							{
								return HTML_PATH + "dusk_priest_2c.htm";
							}
							return HTML_PATH + "dusk_priest_2a.htm";
						}
						return HTML_PATH + "dusk_priest_2b.htm";
					}
					return HTML_PATH + "dusk_priest_1b.htm";
				case DAWN:
					if (isSealValidationPeriod)
					{
						return HTML_PATH + "dusk_priest_3b.htm";
					}
					return HTML_PATH + "dusk_priest_3a.htm";
				default:
					if (isSealValidationPeriod)
					{
						if (compWinner == CabalType.DUSK)
						{
							return HTML_PATH + "dusk_priest_4.htm";
						}
						return HTML_PATH + "dusk_priest_2b.htm";
					}
					return HTML_PATH + "dusk_priest_1a.htm";
			}
		}
		
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		st.nextToken(); // actual command
		
		if (event.startsWith("SevenSignsDesc"))
		{
			int val = Integer.parseInt(st.nextToken());
			
			showChatWindow(player, npc, val, null, true);
		}
		else if (event.startsWith("SevenSigns"))
		{
			int val = 0;
			CabalType cabal = CabalType.NULL;
			SealType seal = SealType.NULL;
			
			try
			{
				val = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
				LOG.warning("Failed to retrieve cabal from bypass command. NpcId: " + npc.getId() + "; Command: " + event);
			}
			
			switch (val)
			{
				case 2: // Purchase Record of the Seven Signs
					if (!player.getInventory().validateCapacity(1))
					{
						player.sendPacket(SystemMessage.SLOTS_FULL);
						break;
					}
					
					if (!player.getInventory().reduceAdena("SevenSigns", SevenSignsManager.RECORD_SEVEN_SIGNS_COST, npc, true))
					{
						player.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
						break;
					}
					player.getInventory().addItem("SevenSigns", SevenSignsManager.RECORD_SEVEN_SIGNS_ID, 1, npc, false);
					
					break;
				case 3: // Join Cabal Intro 1
				case 8: // Festival of Darkness Intro - SevenSigns x [0]1
				case 10: // Teleport Locations List
					cabal = getCabal(st.nextToken());
					showChatWindow(player, npc, val, cabal.getShortName(), false);
					break;
				case 4: // Join a Cabal - SevenSigns 4 [0]1 x
					cabal = getCabal(st.nextToken());
					seal = getSeal(st.nextToken());
					
					CabalType oldCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
					
					if (oldCabal != CabalType.NULL)
					{
						player.sendMessage("You are already a member of the " + cabal.getName() + ".");
						return null;
					}
					
					if (player.getClassId().level() == 0)
					{
						player.sendMessage("You must have already completed your first class transfer.");
						break;
					}
					else if (player.getClassId().level() >= 2)
					{
						if (Config.ALT_GAME_REQUIRE_CASTLE_DAWN)
						{
							if (getPlayerAllyHasCastle(player))
							{
								if (cabal == CabalType.DUSK)
								{
									player.sendMessage("You must not be a member of a castle-owning clan to join the Revolutionaries of Dusk.");
									return null;
								}
							}
							else
							{
								// If the player is trying to join the Lords of Dawn, check if they are carrying a Lord's certificate. If not then try to take the required amount of adena instead.
								if (cabal == CabalType.DAWN)
								{
									boolean allowJoinDawn = false;
									
									if (player.getInventory().destroyItemByItemId("SevenSigns", SevenSignsManager.CERTIFICATE_OF_APPROVAL_ID, 1, npc, false))
									{
										player.sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addNumber(1).addItemName(SevenSignsManager.CERTIFICATE_OF_APPROVAL_ID));
										allowJoinDawn = true;
									}
									else if (player.getInventory().reduceAdena("SevenSigns", SevenSignsManager.ADENA_JOIN_DAWN_COST, npc, false))
									{
										player.sendPacket(new SystemMessage(SystemMessage.S1_DISAPPEARED_ADENA).addNumber(SevenSignsManager.ADENA_JOIN_DAWN_COST));
										allowJoinDawn = true;
									}
									
									if (!allowJoinDawn)
									{
										player.sendMessage("You must be a member of a castle-owning clan, have a Certificate of Lord's Approval, or pay 50000 adena to join the Lords of Dawn.");
										return null;
									}
								}
							}
						}
					}
					
					SevenSignsManager.getInstance().setPlayerInfo(player, cabal, seal);
					
					if (cabal == CabalType.DAWN)
					{
						player.sendPacket(SystemMessage.SEVENSIGNS_PARTECIPATION_DAWN); // Joined Dawn
					}
					else
					{
						player.sendPacket(SystemMessage.SEVENSIGNS_PARTECIPATION_DUSK); // Joined Dusk
					}
					
					// Show a confirmation message to the user, indicating which seal they chose.
					switch (seal)
					{
						case AVARICE:
							player.sendPacket(SystemMessage.FIGHT_FOR_AVARICE);
							break;
						case GNOSIS:
							player.sendPacket(SystemMessage.FIGHT_FOR_GNOSIS);
							break;
						case STRIFE:
							player.sendPacket(SystemMessage.FIGHT_FOR_STRIFE);
							break;
					}
					
					showChatWindow(player, npc, 4, cabal.getShortName(), false);
					break;
				case 6: // Contribute Seal Stones - SevenSigns 6 x
					ItemInstance redStones = player.getInventory().getItemById(SevenSignsManager.SEAL_STONE_RED_ID);
					int redStoneCount = redStones == null ? 0 : redStones.getCount();
					ItemInstance greenStones = player.getInventory().getItemById(SevenSignsManager.SEAL_STONE_GREEN_ID);
					int greenStoneCount = greenStones == null ? 0 : greenStones.getCount();
					ItemInstance blueStones = player.getInventory().getItemById(SevenSignsManager.SEAL_STONE_BLUE_ID);
					int blueStoneCount = blueStones == null ? 0 : blueStones.getCount();
					int contribScore = SevenSignsManager.getInstance().getPlayerContribScore(player);
					boolean stonesFound = false;
					
					if (contribScore == Config.ALT_MAXIMUM_PLAYER_CONTRIB)
					{
						player.sendPacket(SystemMessage.CONTRIB_SCORE_EXCEEDED);
						break;
					}
					int redContribCount = 0;
					int greenContribCount = 0;
					int blueContribCount = 0;
					
					int stoneType = Integer.parseInt(st.nextToken());
					switch (stoneType)
					{
						case 1:
							blueContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSignsManager.BLUE_CONTRIB_POINTS;
							if (blueContribCount > blueStoneCount)
							{
								blueContribCount = blueStoneCount;
							}
							break;
						case 2:
							greenContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSignsManager.GREEN_CONTRIB_POINTS;
							if (greenContribCount > greenStoneCount)
							{
								greenContribCount = greenStoneCount;
							}
							break;
						case 3:
							redContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSignsManager.RED_CONTRIB_POINTS;
							if (redContribCount > redStoneCount)
							{
								redContribCount = redStoneCount;
							}
							break;
						case 4:
							int tempContribScore = contribScore;
							redContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.RED_CONTRIB_POINTS;
							if (redContribCount > redStoneCount)
							{
								redContribCount = redStoneCount;
							}
							tempContribScore += redContribCount * SevenSignsManager.RED_CONTRIB_POINTS;
							greenContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.GREEN_CONTRIB_POINTS;
							if (greenContribCount > greenStoneCount)
							{
								greenContribCount = greenStoneCount;
							}
							tempContribScore += greenContribCount * SevenSignsManager.GREEN_CONTRIB_POINTS;
							blueContribCount = (Config.ALT_MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.BLUE_CONTRIB_POINTS;
							if (blueContribCount > blueStoneCount)
							{
								blueContribCount = blueStoneCount;
							}
							break;
					}
					if (redContribCount > 0)
					{
						if (player.getInventory().destroyItemByItemId("SevenSigns", SevenSignsManager.SEAL_STONE_RED_ID, redContribCount, npc, false))
						{
							stonesFound = true;
						}
					}
					if (greenContribCount > 0)
					{
						if (player.getInventory().destroyItemByItemId("SevenSigns", SevenSignsManager.SEAL_STONE_GREEN_ID, greenContribCount, npc, false))
						{
							stonesFound = true;
						}
					}
					if (blueContribCount > 0)
					{
						if (player.getInventory().destroyItemByItemId("SevenSigns", SevenSignsManager.SEAL_STONE_BLUE_ID, blueContribCount, npc, false))
						{
							stonesFound = true;
						}
					}
					
					if (!stonesFound)
					{
						player.sendMessage("You do not have any seal stones of that type.");
						break;
					}
					
					contribScore = SevenSignsManager.getInstance().addPlayerStoneContrib(player, blueContribCount, greenContribCount, redContribCount);
					
					player.sendPacket(new SystemMessage(SystemMessage.CONTRIB_SCORE_INCREASED_S1).addNumber(contribScore));
					
					showChatWindow(player, npc, 6, null, false);
					break;
				case 9: // Receive Contribution Rewards
					CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
					CabalType winningCabal = SevenSignsManager.getInstance().getCabalHighestScore();
					
					if (SevenSignsManager.getInstance().isSealValidationPeriod() && (playerCabal == winningCabal))
					{
						int ancientAdenaReward = SevenSignsManager.getInstance().getAncientAdenaReward(player, true);
						
						if (ancientAdenaReward < 3)
						{
							showChatWindow(player, npc, 9, "b", false);
							break;
						}
						// add ancient adena
						player.getInventory().addAncientAdena("SevenSigns", ancientAdenaReward, npc, true);
						
						showChatWindow(player, npc, 9, "a", false);
					}
					break;
				case 11: // Teleport to Hunting Grounds
					try
					{
						int x = Integer.parseInt(st.nextToken());
						int y = Integer.parseInt(st.nextToken());
						int z = Integer.parseInt(st.nextToken());
						int ancientAdenaCost = Integer.parseInt(st.nextToken());
						
						if (ancientAdenaCost > 0)
						{
							if (!player.getInventory().reduceAncientAdena("SevenSigns", ancientAdenaCost, npc, true))
							{
								break;
							}
						}
						
						player.teleToLocation(x, y, z, true);
					}
					catch (Exception e)
					{
						LOG.warning("SevenSigns: Error occurred while teleporting player: " + e);
					}
					break;
				case 17: // Exchange Seal Stones for Ancient Adena (Type Choice) - SevenSigns 17 x
					stoneType = Integer.parseInt(st.nextToken());
					int stoneId = 0;
					int stoneCount = 0;
					int stoneValue = 0;
					String stoneColor = null;
					
					switch (stoneType)
					{
						case 1:
							stoneColor = "blue";
							stoneId = SevenSignsManager.SEAL_STONE_BLUE_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_BLUE_VALUE;
							break;
						case 2:
							stoneColor = "green";
							stoneId = SevenSignsManager.SEAL_STONE_GREEN_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_GREEN_VALUE;
							break;
						case 3:
							stoneColor = "red";
							stoneId = SevenSignsManager.SEAL_STONE_RED_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_RED_VALUE;
							break;
					}
					
					ItemInstance stoneInstance = player.getInventory().getItemById(stoneId);
					
					if (stoneInstance != null)
					{
						stoneCount = stoneInstance.getCount();
					}
					
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "signs_17.htm");
					html.replace("%stoneColor%", stoneColor);
					html.replace("%stoneValue%", String.valueOf(stoneValue));
					html.replace("%stoneCount%", String.valueOf(stoneCount));
					html.replace("%stoneItemId%", String.valueOf(stoneId));
					player.sendPacket(html);
					
					break;
				case 18: // Exchange Seal Stones for Ancient Adena - SevenSigns 18 xxxx xxxxxx
					int convertStoneId = Integer.parseInt(st.nextToken());
					int convertCount = 0;
					
					try
					{
						convertCount = Integer.parseInt(st.nextToken());
					}
					catch (Exception NumberFormatException)
					{
						player.sendMessage("You must enter an integer amount.");
						break;
					}
					
					ItemInstance convertItem = player.getInventory().getItemById(convertStoneId);
					
					if (convertItem == null)
					{
						player.sendMessage("You do not have any seal stones of that type.");
						break;
					}
					
					int totalCount = convertItem.getCount();
					int ancientAdenaReward = 0;
					
					if ((convertCount <= totalCount) && (convertCount > 0))
					{
						switch (convertStoneId)
						{
							case SevenSignsManager.SEAL_STONE_BLUE_ID:
								ancientAdenaReward = SevenSignsManager.calcAncientAdenaReward(convertCount, 0, 0);
								break;
							case SevenSignsManager.SEAL_STONE_GREEN_ID:
								ancientAdenaReward = SevenSignsManager.calcAncientAdenaReward(0, convertCount, 0);
								break;
							case SevenSignsManager.SEAL_STONE_RED_ID:
								ancientAdenaReward = SevenSignsManager.calcAncientAdenaReward(0, 0, convertCount);
								break;
						}
						
						if (player.getInventory().destroyItemByItemId("SevenSigns", convertStoneId, convertCount, npc, true))
						{
							player.getInventory().addAncientAdena("SevenSigns", ancientAdenaReward, npc, true);
						}
					}
					else
					{
						player.sendMessage("You do not have that many seal stones.");
					}
					break;
				case 19: // Seal Information (for when joining a cabal)
					cabal = getCabal(st.nextToken());
					seal = getSeal(st.nextToken());
					
					String fileSuffix = seal.getShortName() + "_" + cabal.getShortName();
					
					showChatWindow(player, npc, val, fileSuffix, false);
					break;
				case 20: // Seal Status (for when joining a cabal)
					cabal = getCabal(st.nextToken());
					
					StringBuilder contentBuffer = new StringBuilder("<html><body><font color=\"LEVEL\">[ Seal Status ]</font><br>");
					
					for (int i = 1; i < 4; i++)
					{
						SealType sealType = SealType.values()[i];
						CabalType sealOwner = SevenSignsManager.getInstance().getSealOwner(sealType);
						
						if (sealOwner != CabalType.NULL)
						{
							contentBuffer.append("[" + sealType.getName() + ": " + sealOwner.getName() + "]<br>");
						}
						else
						{
							contentBuffer.append("[" + sealType.getName() + ": Nothingness]<br>");
						}
					}
					
					contentBuffer.append("<a action=\"bypass -h Quest Priest SevenSigns 3 " + cabal + "\">Go back.</a></body></html>");
					
					NpcHtmlMessage html20 = new NpcHtmlMessage(npc.getObjectId());
					html20.setHtml(contentBuffer.toString());
					player.sendPacket(html20);
					break;
				default:
					// 1 = Purchase Record Intro
					// 5 = Contrib Seal Stones Intro
					// 16 = Choose Type of Seal Stones to Convert
					
					showChatWindow(player, npc, val, null, false);
					break;
			}
		}
		return null;
	}
	
	private static void showChatWindow(L2PcInstance player, L2Npc npc, int val, String suffix, boolean isDescription)
	{
		String filename = HTML_PATH;
		
		filename += (isDescription) ? "desc_" + val : "signs_" + val;
		filename += (suffix != null) ? "_" + suffix + ".htm" : ".htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(filename);
		player.sendPacket(html);
	}
	
	private static final boolean getPlayerAllyHasCastle(L2PcInstance player)
	{
		Clan playerClan = player.getClan();
		
		// The player is not in a clan, so return false.
		if (playerClan == null)
		{
			return false;
		}
		
		// If castle ownage check is clan-based rather than ally-based,
		// check if the player's clan has a castle and return the result.
		if (!Config.ALT_GAME_REQUIRE_CLAN_CASTLE)
		{
			int allyId = playerClan.getAllyId();
			
			// The player's clan is not in an alliance, so return false.
			if (allyId != 0)
			{
				// Check if another clan in the same alliance owns a castle,
				// by traversing the list of clans and act accordingly.
				for (Clan clan : ClanData.getInstance().getClans())
				{
					if (clan.getAllyId() == allyId)
					{
						if (clan.hasCastle())
						{
							return true;
						}
					}
				}
			}
		}
		
		return (playerClan.hasCastle());
	}
	
	private static CabalType getCabal(String val)
	{
		return CabalType.values()[Integer.parseInt(val)];
	}
	
	private static SealType getSeal(String val)
	{
		return SealType.values()[Integer.parseInt(val)];
	}
}
