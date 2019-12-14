package l2j.gameserver.scripts.ai.npc.building.clanhall;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;
import l2j.gameserver.model.holder.LocationTeleportHolder;
import l2j.gameserver.network.external.server.ClanHallDecoration;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.WareHouseDepositList;
import l2j.gameserver.network.external.server.WareHouseWithdrawalList;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban, zarie
 */
public class ClanHallManager extends Script
{
	private static final int[] NPCS =
	{
		12833, // Adrienne
		12834, // Bianca
		12835, // Emma
		12836, // Gladys
		12837, // Regina
		12846, // Ruben
		12847, // Horner
		12848, // Bremmer
		12849, // Kalis
		12850, // Winker
		12851, // Black
		12852, // Dillon
		12853, // Boyer
		12854, // Tim
		12855, // Lowell
		12856, // Paranos
		12857, // Klingel
		12858, // Keffer
		12859, // Sand
		12860, // Teters
		12861, // Seth
		12862, // Ron
		12863, // Flynn
		12864, // Watkins
		12865, // Cohen
		12866, // Bint
		12867, // Bourden
		12868, // Pery
		12869, // Gampert
		12870, // Gonti
		12871, // Baraha
		12872, // Vanhal
		12873, // Dan
		12874, // Briggs
		12875, // Stegmann
		12876, // Randolph
		12877, // Trotter
		12878, // Veder
		12879, // Danas
		12880, // Corey
		12881, // Barney
		12882, // Klett
		12883, // Tairee
		12884, // Tanner
		12885, // Cresson
		12886, // Crothers
		12889, // Carey
		12890, // Dianne
		12891, // Crissy
		12892, // Albert
		12893, // Korgan
		12894, // DiMaggio
		12895, // Branhillde
		12896, // Millicent
		12897, // Helga
		12898,// Aida
	};
	// html
	private static final String HTML_PATH = "data/html/clanHall/manager/";
	
	public ClanHallManager()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setMerchant(true);
			NpcData.getInstance().getTemplate(npcId).setWarehouse(true);
		}
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if (player.getClan() != null)
		{
			if (npc.getClanHall().getOwnerId() == player.getClanId())
			{
				return ConditionInteractNpcType.HALL_OWNER;
			}
		}
		
		return ConditionInteractNpcType.ALL_FALSE;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case HALL_OWNER:
				return HTML_PATH + "chamberlain.htm";
			
			case ALL_FALSE:
				return HTML_PATH + "chamberlain-no.htm";
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
			
			case HALL_OWNER:
				StringTokenizer st = new StringTokenizer(event, " ");
				String actualCommand = st.nextToken(); // Get actual command
				
				if (actualCommand.equalsIgnoreCase("banish_foreigner"))
				{
					if (!player.hasClanPrivilege(ClanPrivilegesType.CH_DISMISS))
					{
						player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
						return null;
					}
					
					npc.getClanHall().banishForeigners();
					return null;
				}
				// --------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("manage_vault"))
				{
					if (!player.hasClanPrivilege(ClanPrivilegesType.CL_VIEW_WAREHOUSE))
					{
						player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
						return null;
					}
					
					if (!st.hasMoreTokens())
					{
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "vault.htm");
						player.sendPacket(html);
					}
					else
					{
						switch (st.nextToken())
						{
							case "deposit":
								player.sendPacket(new WareHouseDepositList(player, WareHouseType.CLAN));
								break;
							
							case "withdraw":
								player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.CLAN));
								break;
						}
					}
					return null;
				}
				// --------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("door"))
				{
					if (!player.hasClanPrivilege(ClanPrivilegesType.CH_OPEN_DOOR))
					{
						player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
						return null;
					}
					
					if (!st.hasMoreTokens())
					{
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "door.htm");
						player.sendPacket(html);
					}
					else
					{
						switch (st.nextToken())
						{
							case "open":
								npc.getClanHall().openCloseDoors(true);
								break;
							
							case "close":
								npc.getClanHall().openCloseDoors(false);
								break;
						}
					}
					return null;
				}
				// --------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("functions"))
				{
					if (!player.hasClanPrivilege(ClanPrivilegesType.CH_OTHER_RIGHTS))
					{
						player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
						return null;
					}
					
					if (st.hasMoreTokens())
					{
						switch (st.nextToken())
						{
							case "tele":
								functionTele(player, npc);
								break;
							
							case "item_creation":
								functionItemCreation(player, npc, Integer.parseInt(st.nextToken()));
								break;
							
							case "support":
								functionSupport(player, npc);
								break;
						}
					}
					else
					{
						// html show
						function(player, npc);
					}
					
					return null;
				}
				// MANAGE --------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("manage"))
				{
					if (!player.isClanLeader())
					{
						player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
						return null;
					}
					
					if (!st.hasMoreTokens())
					{
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "manage.htm");
						html.replace("%npcname%", npc.getName());
						player.sendPacket(html);
					}
					else
					{
						String manageOpc = st.nextToken();
						
						// MANAGE RECOVERY --------------------------------------------------------------
						if (manageOpc.equalsIgnoreCase("recovery"))
						{
							if (st.hasMoreTokens())
							{
								switch (st.nextToken())
								{
									case "hp":
										manageHpRecovery(player, npc, Integer.valueOf(st.nextToken()));
										break;
									
									case "mp":
										manageMpRecovery(player, npc, Integer.valueOf(st.nextToken()));
										break;
									
									case "exp":
										manageExpRecovery(player, npc, Integer.valueOf(st.nextToken()));
										break;
								}
							}
							
							// html show
							manageRecovery(player, npc);
							return null;
						}
						// MANAGE OTHER --------------------------------------------------------------
						if (manageOpc.equalsIgnoreCase("other"))
						{
							if (st.hasMoreTokens())
							{
								switch (st.nextToken())
								{
									case "item":
										manageOtherItem(player, npc, Integer.valueOf(st.nextToken()));
										break;
									
									case "tele":
										manageOtherTele(player, npc, Integer.valueOf(st.nextToken()));
										break;
									
									case "support":
										manageOtherSupport(player, npc, Integer.valueOf(st.nextToken()));
										break;
								}
							}
							
							// html show
							manageOther(player, npc);
							return null;
						}
						// MANAGE DECO --------------------------------------------------------------
						if (manageOpc.equalsIgnoreCase("deco"))
						{
							if (st.hasMoreTokens())
							{
								switch (st.nextToken())
								{
									case "curtains":
										manageCurtainsDeco(player, npc, Integer.valueOf(st.nextToken()));
										break;
									
									case "porch":
										managePorchDeco(player, npc, Integer.valueOf(st.nextToken()));
										break;
								}
							}
							
							// html show
							manageDeco(player, npc);
						}
					}
					
					return null;
				}
				// SUPPORT --------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("support"))
				{
					if ((npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT) == null) || (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl() == 0))
					{
						return null;
					}
					
					npc.setTarget(player);
					
					try
					{
						int skillId = Integer.parseInt(st.nextToken());
						int skillLvl = Integer.parseInt(st.nextToken());
						Skill skill = SkillData.getInstance().getSkill(skillId, skillLvl);
						
						if (skill.getSkillType() == SkillType.SUMMON)
						{
							player.doCast(skill);
						}
						else
						{
							npc.doCast(skill);
						}
						
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "support" + npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl() + ".htm");
						html.replace("%mp%", String.valueOf((int) npc.getCurrentMp()));
						player.sendPacket(html);
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid skill!");
					}
					return null;
				}
				
				// GOTO ----------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("goto"))
				{
					doTeleport(player, npc, Integer.parseInt(st.nextToken()));
					return null;
				}
				
				// BACK -----------------------------------------------------------------
				if (actualCommand.equalsIgnoreCase("back"))
				{
					switch (validateCondition(npc, player))
					{
						case HALL_OWNER:
							return HTML_PATH + "chamberlain.htm";
						
						case ALL_FALSE:
							return HTML_PATH + "chamberlain-no.htm";
					}
				}
		}
		return null;
	}
	
	// MISC ----------------------------------------------------------------------------------
	
	private static void doTeleport(L2PcInstance player, L2Npc npc, int val)
	{
		LocationTeleportHolder list = TeleportLocationData.getInstance().getTemplate(val);
		if (list != null)
		{
			if (SiegeManager.getInstance().getSiege(list.getX(), list.getY(), list.getZ()) != null)
			{
				// you cannot teleport to village that is in siege Not sure about this one though
				player.sendPacket(SystemMessage.NO_PORT_THAT_IS_IN_SIGE);
			}
			else if (player.getInventory().reduceAdena("Teleport", list.getPrice(), npc, true))
			{
				player.teleToLocation(list.getX(), list.getY(), list.getZ(), true);
			}
		}
		else
		{
			LOG.warning("No teleport destination with id:" + val);
		}
	}
	
	private static void revalidateDeco(L2PcInstance player)
	{
		ClanHall hall = ClanHallData.getClanHallByOwner(player.getClan());
		if (hall != null)
		{
			player.sendPacket(new ClanHallDecoration(hall));
		}
	}
	
	// MANAGE ------------------------------------------------------------------------------------------------
	
	private static void manageDeco(L2PcInstance player, L2Npc npc)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "deco.htm");
		
		boolean hallFunctionCurtains = (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS).getLvl() != 0);
		html.replace("%curtain%", hallFunctionCurtains ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS).getLvl()) : "0");
		html.replace("%curtainPrice%", hallFunctionCurtains ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS).getLease()) : "0");
		html.replace("%curtainDate%", hallFunctionCurtains ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS).getEndTime()) : "0");
		
		boolean hallFunctionPorch = (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM).getLvl() != 0);
		html.replace("%porch%", hallFunctionPorch ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM).getLvl()) : "0");
		html.replace("%porchPrice%", hallFunctionPorch ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM).getLease()) : "0");
		html.replace("%porchDate%", hallFunctionPorch ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM).getEndTime()) : "0");
		
		player.sendPacket(html);
	}
	
	private static void managePorchDeco(L2PcInstance player, L2Npc npc, int lvl)
	{
		int fee = 0;
		
		switch (lvl)
		{
			case 0:
				break;
			case 1:
				fee = Config.CH_FRONT1_FEE;
				break;
			default:
				fee = Config.CH_FRONT2_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.DECO_FRONTPLATEFORM, lvl, fee, Config.CH_FRONT_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_FRONT_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageCurtainsDeco(L2PcInstance player, L2Npc npc, int lvl)
	{
		int fee = 0;
		
		switch (lvl)
		{
			case 0:
				break;
			case 1:
				fee = Config.CH_CURTAIN1_FEE;
				break;
			default:
				fee = Config.CH_CURTAIN2_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.DECO_CURTAINS, lvl, fee, Config.CH_CURTAIN_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_CURTAIN_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.DECO_CURTAINS) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageOther(L2PcInstance player, L2Npc npc)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "edit_other" + npc.getClanHall().getGrade() + ".htm");
		
		boolean hallFunctionTeleport = (npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getLvl() != 0);
		html.replace("%tele%", hallFunctionTeleport ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getLvl()) : "0");
		html.replace("%telePrice%", hallFunctionTeleport ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getLease()) : "0");
		html.replace("%teleDate%", hallFunctionTeleport ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getEndTime()) : "0");
		
		boolean hallFunctionSupport = (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl() != 0);
		html.replace("%support%", hallFunctionSupport ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl()) : "0");
		html.replace("%supportPrice%", hallFunctionSupport ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLease()) : "0");
		html.replace("%supportDate%", hallFunctionSupport ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getEndTime()) : "0");
		
		boolean hallFunctionItemCreate = (npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE).getLvl() != 0);
		html.replace("%item%", hallFunctionItemCreate ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE).getLvl()) : "0");
		html.replace("%itemPrice%", hallFunctionItemCreate ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE).getLease()) : "0");
		html.replace("%itemDate%", hallFunctionItemCreate ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE).getEndTime()) : "0");
		
		player.sendPacket(html);
	}
	
	private static void manageOtherSupport(L2PcInstance player, L2Npc npc, int lvl)
	{
		int fee = 0;
		
		switch (lvl)
		{
			case 0:
				break;
			case 1:
				fee = Config.CH_SUPPORT1_FEE;
				break;
			case 2:
				fee = Config.CH_SUPPORT2_FEE;
				break;
			case 3:
				fee = Config.CH_SUPPORT3_FEE;
				break;
			case 4:
				fee = Config.CH_SUPPORT4_FEE;
				break;
			case 5:
				fee = Config.CH_SUPPORT5_FEE;
				break;
			case 6:
				fee = Config.CH_SUPPORT6_FEE;
				break;
			case 7:
				fee = Config.CH_SUPPORT7_FEE;
				break;
			default:
				fee = Config.CH_SUPPORT8_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.SUPPORT, lvl, fee, Config.CH_SUPPORT_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_SUPPORT_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageOtherTele(L2PcInstance player, L2Npc npc, int lvl)
	{
		int fee = 0;
		
		switch (lvl)
		{
			case 0:
				break;
			case 1:
				fee = Config.CH_TELE1_FEE;
				break;
			case 2:
				fee = Config.CH_TELE2_FEE;
			default:
				fee = Config.CH_TELE3_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.TELEPORT, lvl, fee, Config.CH_TELE_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_TELE_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageOtherItem(L2PcInstance player, L2Npc npc, int lvl)
	{
		int fee = 0;
		
		switch (lvl)
		{
			case 0:
				break;
			case 1:
				fee = Config.CH_ITEM1_FEE;
				break;
			case 2:
				fee = Config.CH_ITEM2_FEE;
				break;
			default:
				fee = Config.CH_ITEM3_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.ITEM_CREATE, lvl, fee, Config.CH_ITEM_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_ITEM_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageRecovery(L2PcInstance player, L2Npc npc)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "edit_recovery" + npc.getClanHall().getGrade() + ".htm");
		
		boolean restoreHP = (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP).getLvl() != 0);
		html.replace("%hp%", restoreHP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP).getLvl()) + "%" : "0%");
		html.replace("%hpPrice%", restoreHP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP).getLease()) : "0");
		html.replace("%hpDate%", restoreHP ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP).getEndTime()) : "0");
		
		boolean restoreEXP = (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP).getLvl() != 0);
		html.replace("%exp%", restoreEXP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP).getLvl()) + "%" : "0%");
		html.replace("%expPrice%", restoreEXP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP).getLease()) : "0");
		html.replace("%expDate%", restoreEXP ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP).getEndTime()) : "0");
		
		boolean restoreMP = (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP) != null) && (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP).getLvl() != 0);
		html.replace("%mp%", restoreMP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP).getLvl()) + "%" : "0%");
		html.replace("%mpPrice%", restoreMP ? String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP).getLease()) : "0");
		html.replace("%mpDate%", restoreMP ? dateFormat.format(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP).getEndTime()) : "0");
		
		player.sendPacket(html);
	}
	
	private static void manageExpRecovery(L2PcInstance player, L2Npc npc, int percent)
	{
		int fee = 0;
		
		switch (percent)
		{
			case 0:
				break;
			case 5:
				fee = Config.CH_EXPREG1_FEE;
				break;
			case 10:
				fee = Config.CH_EXPREG2_FEE;
				break;
			case 15:
				fee = Config.CH_EXPREG3_FEE;
				break;
			case 25:
				fee = Config.CH_EXPREG4_FEE;
				break;
			case 35:
				fee = Config.CH_EXPREG5_FEE;
				break;
			case 40:
				fee = Config.CH_EXPREG6_FEE;
				break;
			default:
				fee = Config.CH_EXPREG7_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.RESTORE_EXP, percent, fee, Config.CH_EXPREG_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_EXPREG_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageMpRecovery(L2PcInstance player, L2Npc npc, int percent)
	{
		int fee = 0;
		
		switch (percent)
		{
			case 0:
				break;
			case 5:
				fee = Config.CH_MPREG1_FEE;
				break;
			case 10:
				fee = Config.CH_MPREG2_FEE;
				break;
			case 15:
				fee = Config.CH_MPREG3_FEE;
				break;
			case 30:
				fee = Config.CH_MPREG4_FEE;
				break;
			default:
				fee = Config.CH_MPREG5_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.RESTORE_MP, percent, fee, Config.CH_MPREG_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_MPREG_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
	}
	
	private static void manageHpRecovery(L2PcInstance player, L2Npc npc, int percent)
	{
		int fee = 0;
		
		switch (percent)
		{
			case 0:
				break;
			case 20:
				fee = Config.CH_HPREG1_FEE;
				break;
			case 40:
				fee = Config.CH_HPREG2_FEE;
				break;
			case 80:
				fee = Config.CH_HPREG3_FEE;
				break;
			case 100:
				fee = Config.CH_HPREG4_FEE;
				break;
			case 120:
				fee = Config.CH_HPREG5_FEE;
				break;
			case 140:
				fee = Config.CH_HPREG6_FEE;
				break;
			case 160:
				fee = Config.CH_HPREG7_FEE;
				break;
			case 180:
				fee = Config.CH_HPREG8_FEE;
				break;
			case 200:
				fee = Config.CH_HPREG9_FEE;
				break;
			case 220:
				fee = Config.CH_HPREG10_FEE;
				break;
			case 240:
				fee = Config.CH_HPREG11_FEE;
				break;
			case 260:
				fee = Config.CH_HPREG12_FEE;
				break;
			default:
				fee = Config.CH_HPREG13_FEE;
				break;
		}
		
		if (!npc.getClanHall().updateFunctions(ClanHallFunctionType.RESTORE_HP, percent, fee, Config.CH_HPREG_FEE_RATIO, Calendar.getInstance().getTimeInMillis() + Config.CH_HPREG_FEE_RATIO, (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP) == null)))
		{
			player.sendPacket(SystemMessage.NOT_ENOUGH_ADENA_IN_CWH);
		}
		else
		{
			revalidateDeco(player);
		}
		
	}
	
	// FUNCTIONS ---------------------------------------------------------------------------------------------
	
	private static void function(L2PcInstance player, L2Npc npc)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "functions.htm");
		
		html.replace("%npcId%", npc.getId());
		html.replace("%xp_regen%", (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP) != null) ? (String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_EXP).getLvl()) + "%") : "0%");
		html.replace("%hp_regen%", (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP) != null) ? (String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_HP).getLvl()) + "%") : "0%");
		html.replace("%mp_regen%", (npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP) != null) ? (String.valueOf(npc.getClanHall().getFunction(ClanHallFunctionType.RESTORE_MP).getLvl()) + "%") : "0%");
		
		player.sendPacket(html);
	}
	
	private static void functionTele(L2PcInstance player, L2Npc npc)
	{
		if (npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT) == null)
		{
			return;
		}
		
		if (npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getLvl() == 0)
		{
			return;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "tele" + npc.getClanHall().getLocation() + npc.getClanHall().getFunction(ClanHallFunctionType.TELEPORT).getLvl() + ".htm");
		player.sendPacket(html);
	}
	
	private static void functionItemCreation(L2PcInstance player, L2Npc npc, int valBuy)
	{
		if (npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE) == null)
		{
			return;
		}
		
		npc.showBuyWindow(player, valBuy + (npc.getClanHall().getFunction(ClanHallFunctionType.ITEM_CREATE).getLvl() * 100000));
	}
	
	private static void functionSupport(L2PcInstance player, L2Npc npc)
	{
		if (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT) == null)
		{
			return;
		}
		
		if (npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl() == 0)
		{
			return;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "support" + npc.getClanHall().getFunction(ClanHallFunctionType.SUPPORT).getLvl() + ".htm");
		html.replace("%mp%", String.valueOf((int) npc.getCurrentMp()));
		player.sendPacket(html);
	}
}
