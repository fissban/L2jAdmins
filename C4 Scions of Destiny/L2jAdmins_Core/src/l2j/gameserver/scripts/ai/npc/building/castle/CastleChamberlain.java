package l2j.gameserver.scripts.ai.npc.building.castle;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.external.server.BuyListSeed;
import l2j.gameserver.network.external.server.ExShowCropInfo;
import l2j.gameserver.network.external.server.ExShowCropSetting;
import l2j.gameserver.network.external.server.ExShowManorDefaultInfo;
import l2j.gameserver.network.external.server.ExShowSeedInfo;
import l2j.gameserver.network.external.server.ExShowSeedSetting;
import l2j.gameserver.network.external.server.ExShowSellCropList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CastleChamberlain extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		12121, // Crosby
		12152, // Saul
		12240, // Brasseur
		12252, // Sayres
		12255, // Logan
		12600, // Neurath
		12791,// Alfred
	};
	// Misc
	private static final int CROWN_OF_LORDS = 6841;
	private static final String HTML_PATH = "data/html/castle/chamberlain/";
	
	public CastleChamberlain()
	{
		super(-1, "ai/npc/building/castle");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				return HTML_PATH + "chamberlain-no.htm";
			
			case BUSY_BECAUSE_OF_SIEGE:
				return HTML_PATH + "chamberlain-busy.htm";
			
			case CASTLE_OWNER:
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "chamberlain.htm");
				html.replace("%npcId%", npc.getId());
				player.sendPacket(html);
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				return null;
			
			case BUSY_BECAUSE_OF_SIEGE:
				return null;
			
			case CASTLE_OWNER:
				int castleId;
				
				StringTokenizer st = new StringTokenizer(event, " ");
				String actualCommand = st.nextToken(); // Get actual command
				
				switch (actualCommand)
				{
					case "banish_foreigner":// castle/chamberlain/chamberlain-manageFunctions.htm
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_DISMISS))
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						
						// Move non-clan members off castle area
						npc.getCastle().banishForeigners();
						break;
					
					case "list_siege_clans": // castle/chamberlain/chamberlain.htm
						if (player.isClanLeader())
						{
							// List current register clan
							npc.getCastle().getSiege().listRegisterClan(player);
						}
						else
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						break;
					
					case "crown": // castle/chamberlain/chamberlain.htm
						if (!player.isClanLeader())
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						
						if (player.getInventory().getItemById(CROWN_OF_LORDS) != null)
						{
							player.sendMessage("You have already received a Lord's Crown.");
							break;
						}
						
						player.getInventory().addItem("Circlet", CROWN_OF_LORDS, 1, null, true);
						break;
					
					case "items": // castle/chamberlain/chamberlain.htm
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_OTHER_RIGHTS))
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						
						if (!st.hasMoreTokens())
						{
							break;
						}
						
						castleId = npc.getCastle().getId();
						int circlet = CastleData.getInstance().getCircletByCastleId(castleId);
						
						if (player.getInventory().getItemById(circlet) == null)
						{
							npc.showBuyWindow(player, Integer.parseInt(st.nextToken() + "1"));
						}
						else
						{
							npc.showBuyWindow(player, Integer.parseInt(st.nextToken() + "2"));
						}
						break;
					
					case "receive_report": // castle/chamberlain/chamberlain.htm
						Clan clan = ClanData.getInstance().getClanById(npc.getCastle().getOwnerId());
						if (clan == null)
						{
							break;
						}
						
						NpcHtmlMessage htmlReceiveReport = new NpcHtmlMessage(npc.getObjectId());
						htmlReceiveReport.setFile(HTML_PATH + "chamberlain-report.htm");
						htmlReceiveReport.replace("%clanname%", clan.getName());
						htmlReceiveReport.replace("%clanleadername%", clan.getLeaderName());
						htmlReceiveReport.replace("%castlename%", npc.getCastle().getName());
						
						switch (SevenSignsManager.getInstance().getCurrentPeriod())
						{
							case RECRUITING:
								htmlReceiveReport.replace("%ss_event%", "Quest Event Initialization");
								break;
							case COMPETITION:
								htmlReceiveReport.replace("%ss_event%", "Competition (Quest Event)");
								break;
							case RESULTS:
								htmlReceiveReport.replace("%ss_event%", "Quest Event Results");
								break;
							case SEAL_VALIDATION:
								htmlReceiveReport.replace("%ss_event%", "Seal Validation");
								break;
						}
						
						switch (SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE))
						{
							case NULL:
								htmlReceiveReport.replace("%ss_avarice%", "Not in Possession");
								break;
							case DAWN:
								htmlReceiveReport.replace("%ss_avarice%", "Lords of Dawn");
								break;
							case DUSK:
								htmlReceiveReport.replace("%ss_avarice%", "Revolutionaries of Dusk");
								break;
						}
						
						switch (SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
						{
							case NULL:
								htmlReceiveReport.replace("%ss_gnosis%", "Not in Possession");
								break;
							case DAWN:
								htmlReceiveReport.replace("%ss_gnosis%", "Lords of Dawn");
								break;
							case DUSK:
								htmlReceiveReport.replace("%ss_gnosis%", "Revolutionaries of Dusk");
								break;
						}
						
						switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
						{
							case NULL:
								htmlReceiveReport.replace("%ss_strife%", "Not in Possession");
								break;
							case DAWN:
								htmlReceiveReport.replace("%ss_strife%", "Lords of Dawn");
								break;
							case DUSK:
								htmlReceiveReport.replace("%ss_strife%", "Revolutionaries of Dusk");
								break;
						}
						
						player.sendPacket(htmlReceiveReport);
						break;
					
					case "manage_vault": // chamberlain/chamberlain.htm
						if (!player.isClanLeader())
						{
							break;
						}
						
						String filenameManageVault = HTML_PATH + "chamberlain-vault.htm";
						int amount = 0;
						
						try
						{
							amount = Integer.parseInt(st.nextToken());
						}
						catch (NoSuchElementException e)
						{
							//
						}
						
						if (amount > 0)
						{
							if (npc.getCastle().getTreasury() < amount)
							{
								filenameManageVault = HTML_PATH + "chamberlain-vault-no.htm";
							}
							else
							{
								if (npc.getCastle().addToTreasuryNoTax((-1) * amount))
								{
									player.getInventory().addAdena("Castle", amount, npc, true);
								}
							}
						}
						
						NpcHtmlMessage htmlWithdraw = new NpcHtmlMessage(npc.getObjectId());
						htmlWithdraw.setFile(filenameManageVault);
						htmlWithdraw.replace("%tax_income%", formatAdena(npc.getCastle().getTreasury()));
						htmlWithdraw.replace("%withdraw_amount%", formatAdena(amount));
						player.sendPacket(htmlWithdraw);
						break;
					
					case "manor": // castle/chamberlain/chamberlain.htm
						String filenameManor = "";
						if (CastleManorManager.getInstance().isDisabled())
						{
							filenameManor = "data/html/npcdefault.htm";
						}
						else
						{
							switch (Integer.parseInt(st.nextToken()))
							{
								case 0:
									filenameManor = HTML_PATH + "manor/manor.htm";
									break;
								case 1:
									filenameManor = HTML_PATH + "manor/manor_help00" + st.nextToken() + ".htm";
									break;
								default:
									filenameManor = HTML_PATH + "chamberlain-no.htm";
									break;
							}
						}
						
						NpcHtmlMessage htmlManor = new NpcHtmlMessage(npc.getObjectId());
						htmlManor.setFile(filenameManor);
						htmlManor.replace("%castle_tax_rate%", npc.getCastle().getTaxPercent());
						player.sendPacket(htmlManor);
						break;
					
					case "manor_menu_select": // castle/chamberlain/manor/manor.htm
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_OTHER_RIGHTS))
						{
							player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
							break;
						}
						
						if (CastleManorManager.getInstance().isUnderMaintenance())
						{
							player.sendPacket(SystemMessage.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
							break;
						}
						
						String params = event.substring(event.indexOf("?") + 1);
						StringTokenizer str = new StringTokenizer(params, "&");
						int ask = Integer.parseInt(str.nextToken().split("=")[1]);
						int state = Integer.parseInt(str.nextToken().split("=")[1]);
						int time = Integer.parseInt(str.nextToken().split("=")[1]);
						
						if (state == -1)
						{
							castleId = npc.getCastle().getId();
						}
						else
						{
							castleId = state;
						}
						
						switch (ask)
						{
							// Main action
							case 1: // Seed purchase
								if (castleId != npc.getCastle().getId())
								{
									player.sendPacket(SystemMessage.HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR);
								}
								else
								{
									MerchantTradeList tradeList = new MerchantTradeList(0);
									List<SeedProductionHolder> seeds = npc.getCastle().getSeedProduction(CastleManorManager.PERIOD_CURRENT);
									
									for (SeedProductionHolder s : seeds)
									{
										ItemInstance item = ItemData.getInstance().createDummyItem(s.getId());
										item.setPriceToSell(s.getPrice());
										item.setCount(s.getCanProduce());
										if ((item.getCount() > 0) && (item.getPriceToSell() > 0))
										{
											tradeList.addItem(item);
										}
									}
									player.sendPacket(new BuyListSeed(tradeList, castleId, player.getInventory().getAdena()));
								}
								break;
							
							case 2: // Crop sales
								player.sendPacket(new ExShowSellCropList(player, castleId, npc.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT)));
								break;
							
							case 3: // Current seeds (Manor info)
								if ((time == 1) && !CastleData.getInstance().getCastleById(castleId).isNextPeriodApproved())
								{
									player.sendPacket(new ExShowSeedInfo(castleId, null));
								}
								else
								{
									player.sendPacket(new ExShowSeedInfo(castleId, CastleData.getInstance().getCastleById(castleId).getSeedProduction(time)));
								}
								break;
							
							case 4:// Current crops (Manor info)
								if ((time == 1) && !CastleData.getInstance().getCastleById(castleId).isNextPeriodApproved())
								{
									player.sendPacket(new ExShowCropInfo(castleId, null));
								}
								else
								{
									player.sendPacket(new ExShowCropInfo(castleId, CastleData.getInstance().getCastleById(castleId).getCropProcure(time)));
								}
								break;
							
							case 5:// Basic info (Manor info)
								player.sendPacket(new ExShowManorDefaultInfo());
								break;
							
							case 7: // Edit seed setup
								if (npc.getCastle().isNextPeriodApproved())
								{
									player.sendPacket(SystemMessage.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
								}
								else
								{
									player.sendPacket(new ExShowSeedSetting(npc.getCastle().getId()));
								}
								break;
							
							case 8: // Edit crop setup
								if (npc.getCastle().isNextPeriodApproved())
								{
									player.sendPacket(SystemMessage.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
								}
								else
								{
									player.sendPacket(new ExShowCropSetting(npc.getCastle().getId()));
								}
								break;
						}
						break;
					
					case "operate_door": // castle/chamberlain/chamberlain-manageFunctions.htm && chamberlain/npcId-doorControl.htm
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_OPEN_DOOR))
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						
						if (st.hasMoreTokens())
						{
							boolean open = (Integer.parseInt(st.nextToken()) == 1);
							while (st.hasMoreTokens())
							{
								npc.getCastle().openCloseDoor(Integer.parseInt(st.nextToken()), open);
							}
						}
						
						NpcHtmlMessage htmlOperateDoor = new NpcHtmlMessage(npc.getObjectId());
						htmlOperateDoor.setFile(HTML_PATH + "" + npc.getId() + "-doorControl.htm");
						player.sendPacket(htmlOperateDoor);
						break;
					
					case "tax_set": // castle/chamberlain/chamberlein-adjustTaxRate.htm
						if (!player.isClanLeader())
						{
							return HTML_PATH + "chamberlain-noAuthorized.htm";
						}
						
						if (st.hasMoreTokens())
						{
							npc.getCastle().setNextTaxRateInDB(Integer.parseInt(st.nextToken()));
							
							NpcHtmlMessage htmlTaxSet = new NpcHtmlMessage(npc.getObjectId());
							htmlTaxSet.setFile(HTML_PATH + "chamberlain-adjustTaxRate-ok.htm");
							htmlTaxSet.replace("%tax_rate%", npc.getCastle().getTaxPercent());
							htmlTaxSet.replace("%new_tax_rate%", npc.getCastle().getNextTaxRatePorcent());
							player.sendPacket(htmlTaxSet);
						}
						else
						{
							NpcHtmlMessage htmlTaxSet = new NpcHtmlMessage(npc.getObjectId());
							htmlTaxSet.setFile(HTML_PATH + "chamberlain-adjustTaxRate.htm");
							htmlTaxSet.replace("%castle_tax_rate%", npc.getCastle().getTaxPercent());
							player.sendPacket(htmlTaxSet);
						}
						break;
					
					case "chamberlain":
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "chamberlain.htm");
						html.replace("%npcId%", npc.getId());
						player.sendPacket(html);
						break;
				}
				
				break;
		}
		
		return null;
	}
	
	// TODO mover a util
	private static String formatAdena(int amount)
	{
		String s = "";
		int rem = amount % 1000;
		s = Integer.toString(rem);
		amount = (amount - rem) / 1000;
		while (amount > 0)
		{
			if (rem < 99)
			{
				s = '0' + s;
			}
			if (rem < 9)
			{
				s = '0' + s;
			}
			rem = amount % 1000;
			s = Integer.toString(rem) + "," + s;
			amount = (amount - rem) / 1000;
		}
		return s;
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getCastle() != null) && (player.getClan() != null))
		{
			if (npc.getCastle().getSiege().isInProgress())
			{
				return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
			}
			else if (npc.getCastle().getOwnerId() == player.getClanId())
			{
				return ConditionInteractNpcType.CASTLE_OWNER; // Owner
			}
		}
		
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
