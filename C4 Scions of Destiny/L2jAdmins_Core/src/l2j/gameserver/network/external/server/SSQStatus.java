package l2j.gameserver.network.external.server;

import l2j.Config;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.PeriodType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * Seven Signs Record Update packet type id 0xf5 format: c cc (Page Num = 1 -> 4, period) 1: [ddd cc dd ddd c ddd c] 2: [hc [cd (dc (S))] 3: [ccc (cccc)] 4: [(cchh)]
 * @author Tempy
 */
public class SSQStatus extends AServerPacket
{
	private final L2PcInstance player;
	private final int page;
	
	public SSQStatus(L2PcInstance player, int recordPage)
	{
		this.player = player;
		page = recordPage;
	}
	
	@Override
	public void writeImpl()
	{
		CabalType winningCabal = SevenSignsManager.getInstance().getCabalHighestScore();
		int totalDawnMembers = SevenSignsManager.getInstance().getTotalMembers(CabalType.DAWN);
		int totalDuskMembers = SevenSignsManager.getInstance().getTotalMembers(CabalType.DUSK);
		
		writeC(0xf5);
		
		writeC(page);
		writeC(SevenSignsManager.getInstance().getCurrentPeriod().ordinal()); // current period?
		
		int dawnPercent = 0;
		int duskPercent = 0;
		
		switch (page)
		{
			case 1:
				// [ddd cc dd ddd c ddd c]
				writeD(SevenSignsManager.getInstance().getCurrentCycle());
				
				PeriodType currentPeriod = SevenSignsManager.getInstance().getCurrentPeriod();
				
				switch (currentPeriod)
				{
					case RECRUITING:
						writeD(SystemMessage.INITIAL_PERIOD);
						break;
					case COMPETITION:
						writeD(SystemMessage.SSQ_COMPETITION_UNDERWAY);
						break;
					case RESULTS:
						writeD(SystemMessage.RESULTS_PERIOD);
						break;
					case SEAL_VALIDATION:
						writeD(SystemMessage.VALIDATION_PERIOD);
						break;
				}
				
				switch (currentPeriod)
				{
					case RECRUITING:
					case RESULTS:
						writeD(SystemMessage.UNTIL_TODAY_6PM);
						break;
					case COMPETITION:
					case SEAL_VALIDATION:
						writeD(SystemMessage.UNTIL_MONDAY_6PM);
						break;
				}
				
				writeC(SevenSignsManager.getInstance().getPlayerCabal(player).ordinal());
				writeC(SevenSignsManager.getInstance().getPlayerSeal(player).ordinal());
				
				writeD(SevenSignsManager.getInstance().getPlayerStoneContrib(player)); // Seal Stones Turned-In
				writeD(SevenSignsManager.getInstance().getPlayerAdenaCollect(player)); // Ancient Adena to Collect
				
				double dawnStoneScore = SevenSignsManager.getInstance().getCurrentStoneScore(CabalType.DAWN);
				int dawnFestivalScore = SevenSignsManager.getInstance().getCurrentFestivalScore(CabalType.DAWN);
				
				double duskStoneScore = SevenSignsManager.getInstance().getCurrentStoneScore(CabalType.DUSK);
				int duskFestivalScore = SevenSignsManager.getInstance().getCurrentFestivalScore(CabalType.DUSK);
				
				double totalStoneScore = duskStoneScore + dawnStoneScore;
				
				/*
				 * Scoring seems to be proportionate to a set base value, so base this on the maximum obtainable score from festivals, which is 500.
				 */
				int duskStoneScoreProp = 0;
				int dawnStoneScoreProp = 0;
				
				if (totalStoneScore != 0)
				{
					duskStoneScoreProp = Math.round(((float) duskStoneScore / (float) totalStoneScore) * 500);
					dawnStoneScoreProp = Math.round(((float) dawnStoneScore / (float) totalStoneScore) * 500);
				}
				
				int duskTotalScore = SevenSignsManager.getInstance().getCurrentScore(CabalType.DUSK);
				int dawnTotalScore = SevenSignsManager.getInstance().getCurrentScore(CabalType.DAWN);
				
				int totalOverallScore = duskTotalScore + dawnTotalScore;
				
				if (totalOverallScore != 0)
				{
					dawnPercent = Math.round(((float) dawnTotalScore / (float) totalOverallScore) * 100);
					duskPercent = Math.round(((float) duskTotalScore / (float) totalOverallScore) * 100);
				}
				
				if (Config.DEBUG)
				{
					LOG.info("Dusk Stone Score: " + duskStoneScore + " - Dawn Stone Score: " + dawnStoneScore);
					LOG.info("Dusk Festival Score: " + duskFestivalScore + " - Dawn Festival Score: " + dawnFestivalScore);
					LOG.info("Dusk Score: " + duskTotalScore + " - Dawn Score: " + dawnTotalScore);
					LOG.info("Overall Score: " + totalOverallScore);
					LOG.info("");
					if (totalStoneScore == 0)
					{
						LOG.info("Dusk Prop: 0 - Dawn Prop: 0");
					}
					else
					{
						LOG.info("Dusk Prop: " + ((duskStoneScore / totalStoneScore) * 500) + " - Dawn Prop: " + ((dawnStoneScore / totalStoneScore) * 500));
					}
					LOG.info("Dusk %: " + duskPercent + " - Dawn %: " + dawnPercent);
				}
				
				/* DUSK */
				writeD(duskStoneScoreProp); // Seal Stone Score
				writeD(duskFestivalScore); // Festival Score
				writeD(duskTotalScore); // Total Score
				
				writeC(duskPercent); // Dusk %
				
				/* DAWN */
				writeD(dawnStoneScoreProp); // Seal Stone Score
				writeD(dawnFestivalScore); // Festival Score
				writeD(dawnTotalScore); // Total Score
				
				writeC(dawnPercent); // Dawn %
				break;
			case 2:
				// c cc hc [cd (dc (S))]
				writeH(1);
				
				writeC(5); // Total number of festivals
				
				for (int i = 0; i < 5; i++)
				{
					writeC(i + 1); // Current client-side festival ID
					writeD(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i]);
					
					int duskScore = SevenSignsFestival.getInstance().getHighestScore(CabalType.DUSK, i);
					int dawnScore = SevenSignsFestival.getInstance().getHighestScore(CabalType.DAWN, i);
					
					// Dusk Score
					writeD(duskScore);
					
					StatsSet highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(CabalType.DUSK, i);
					String[] partyMembers = highScoreData.getString("members").split(",");
					
					if (partyMembers != null)
					{
						writeC(partyMembers.length);
						
						for (String partyMember : partyMembers)
						{
							writeS(partyMember);
						}
					}
					else
					{
						writeC(0);
					}
					
					// Dawn Score
					writeD(dawnScore);
					
					highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(CabalType.DAWN, i);
					partyMembers = highScoreData.getString("members").split(",");
					
					if (partyMembers != null)
					{
						writeC(partyMembers.length);
						
						for (String partyMember : partyMembers)
						{
							writeS(partyMember);
						}
					}
					else
					{
						writeC(0);
					}
				}
				break;
			case 3:
				// c cc [ccc (cccc)]
				writeC(10); // Minimum limit for winning cabal to retain their seal
				writeC(35); // Minimum limit for winning cabal to claim a seal
				writeC(3); // Total number of seals
				
				for (int i = 1; i < 4; i++)
				{
					SealType seal = SealType.values()[i];
					int dawnProportion = SevenSignsManager.getInstance().getSealProportion(seal, CabalType.DAWN);
					int duskProportion = SevenSignsManager.getInstance().getSealProportion(seal, CabalType.DUSK);
					
					if (Config.DEBUG)
					{
						LOG.info(seal.getShortName() + " = Dawn Prop: " + dawnProportion + "(" + ((dawnProportion / totalDawnMembers) * 100) + "%)" + ", Dusk Prop: " + duskProportion + "(" + ((duskProportion / totalDuskMembers) * 100) + "%)");
					}
					
					writeC(i);
					writeC(SevenSignsManager.getInstance().getSealOwner(seal).ordinal());
					
					if (totalDuskMembers == 0)
					{
						if (totalDawnMembers == 0)
						{
							writeC(0);
							writeC(0);
						}
						else
						{
							writeC(0);
							writeC(Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100));
						}
					}
					else
					{
						if (totalDawnMembers == 0)
						{
							writeC(Math.round(((float) duskProportion / (float) totalDuskMembers) * 100));
							writeC(0);
						}
						else
						{
							writeC(Math.round(((float) duskProportion / (float) totalDuskMembers) * 100));
							writeC(Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100));
						}
					}
				}
				break;
			case 4:
				// c cc [cc (cchh)]
				writeC(winningCabal.ordinal()); // Overall predicted winner
				writeC(3); // Total number of seals
				
				for (int i = 1; i < 4; i++)
				{
					SealType seal = SealType.values()[i];
					int dawnProportion = SevenSignsManager.getInstance().getSealProportion(seal, CabalType.DAWN);
					int duskProportion = SevenSignsManager.getInstance().getSealProportion(seal, CabalType.DUSK);
					dawnPercent = Math.round((dawnProportion / (totalDawnMembers == 0 ? 1 : (float) totalDawnMembers)) * 100);
					duskPercent = Math.round((duskProportion / (totalDuskMembers == 0 ? 1 : (float) totalDuskMembers)) * 100);
					CabalType sealOwner = SevenSignsManager.getInstance().getSealOwner(seal);
					
					writeC(i);
					
					switch (sealOwner)
					{
						case NULL:
							switch (winningCabal)
							{
								case NULL:
									writeC(CabalType.NULL.ordinal());
									writeH(SystemMessage.COMPETITION_TIE_SEAL_NOT_AWARDED);
									break;
								case DAWN:
									if (dawnPercent >= 35)
									{
										writeC(CabalType.DAWN.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_LESS_VOTED);
									}
									break;
								case DUSK:
									if (duskPercent >= 35)
									{
										writeC(CabalType.DUSK.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_LESS_VOTED);
									}
									break;
							}
							break;
						case DAWN:
							switch (winningCabal)
							{
								case NULL:
									if (dawnPercent >= 10)
									{
										writeC(CabalType.DAWN.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.COMPETITION_TIE_SEAL_NOT_AWARDED);
									}
									break;
								case DAWN:
									if (dawnPercent >= 10)
									{
										writeC(sealOwner.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_LESS_VOTED);
									}
									break;
								case DUSK:
									if (duskPercent >= 35)
									{
										writeC(CabalType.DUSK.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_MORE_VOTED);
									}
									else if (dawnPercent >= 10)
									{
										writeC(CabalType.DAWN.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_LESS_VOTED);
									}
									break;
							}
							break;
						case DUSK:
							switch (winningCabal)
							{
								case NULL:
									if (duskPercent >= 10)
									{
										writeC(CabalType.DUSK.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.COMPETITION_TIE_SEAL_NOT_AWARDED);
									}
									break;
								case DAWN:
									if (dawnPercent >= 35)
									{
										writeC(CabalType.DAWN.ordinal());
										writeH(SystemMessage.SEAL_NOT_OWNED_35_MORE_VOTED);
									}
									else if (duskPercent >= 10)
									{
										writeC(sealOwner.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_LESS_VOTED);
									}
									break;
								case DUSK:
									if (duskPercent >= 10)
									{
										writeC(sealOwner.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_MORE_VOTED);
									}
									else
									{
										writeC(CabalType.NULL.ordinal());
										writeH(SystemMessage.SEAL_OWNED_10_LESS_VOTED);
									}
									break;
							}
							break;
					}
					
					writeH(0);
				}
				break;
		}
	}
}
