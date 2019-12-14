package l2j.gameserver.handler.command.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import main.data.xml.IconData;

/**
 * @author fissban
 */
public class AdminEditNpc implements IAdminCommandHandler
{
	private static final Logger LOG = Logger.getLogger(AdminEditNpc.class.getName());
	private final static int PAGE_LIMIT = 10;
	
	private static final String[] ADMINCOMMAND =
	{
		"admin_edit_npc",
		"admin_save_npc",
		"admin_show_droplist",
		"admin_showShop",
		"admin_showShopList",
		"admin_addShopItem",
		"admin_delShopItem",
		"admin_show_skilllist_npc",
		"admin_del_skill_npc",
		"admin_close_window"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_showShop"))
		{
			if (st.hasMoreTokens())
			{
				showShop(activeChar, Integer.parseInt(st.nextToken()));
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_showShopList"))
		{
			if (st.countTokens() == 2)
			{
				showShopList(activeChar, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			}
		}
		else if (event.equals("admin_addShopItem"))
		{
			final String[] args = command.split(" ");
			if (args.length > 1)
			{
				addShopItem(activeChar, args);
			}
		}
		else if (event.equals("admin_delShopItem"))
		{
			final String[] args = command.split(" ");
			if (args.length > 2)
			{
				delShopItem(activeChar, args);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_edit_npc"))
		{
			int npcId = 0;
			
			try
			{
				npcId = Integer.parseInt(st.nextToken());
			}
			catch (final Exception e)
			{
			}
			
			if (npcId > 0)
			{
				final NpcTemplate npc = NpcData.getInstance().getTemplate(npcId);
				showNpcProperty(activeChar, npc);
			}
			else
			{
				activeChar.sendMessage("Wrong usage: //edit_npc <npcId>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_show_droplist"))
		{
			try
			{
				final int npcId = Integer.parseInt(st.nextToken());
				int page = 1;
				if (st.hasMoreTokens())
				{
					page = Integer.parseInt(st.nextToken());
				}
				showNpcDropList(activeChar, npcId, page);
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //show_droplist <npc_id>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_show_skilllist_npc"))
		{
			try
			{
				final int npcId = Integer.parseInt(st.nextToken());
				int page = 1;
				if (st.hasMoreTokens())
				{
					page = Integer.parseInt(st.nextToken());
				}
				showNpcSkillList(activeChar, npcId, page);
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Usage: //admin_show_skillList <npc_id>");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_del_skill_npc"))
		{
			final int npcId = Integer.parseInt(st.nextToken());
			final int skillId = Integer.parseInt(st.nextToken());
			
			// Borramos de la DB los skills de ese npc
			try (Connection con = DatabaseManager.getConnection())
			{
				if (npcId > 0)
				{
					int updated = 0;
					if (Config.CUSTOM_NPC_SKILLS_TABLE)
					{
						try (PreparedStatement statement = con.prepareStatement("DELETE FROM custom_npc_skills WHERE npcid=? AND skillid=?"))
						{
							statement.setInt(1, npcId);
							statement.setInt(2, skillId);
							updated = statement.executeUpdate();
						}
					}
					if (updated == 0)
					{
						try (PreparedStatement statement2 = con.prepareStatement("DELETE FROM npc_skills WHERE npcid=? AND skillid=?"))
						{
							statement2.setInt(1, npcId);
							statement2.setInt(2, skillId);
							statement2.execute();
						}
					}
					
					// volvemos a actualizar la informacion leyenda la nueva desde la DB
					try (Connection con1 = DatabaseManager.getConnection())
					{
						final NpcTemplate npcData = NpcData.getInstance().getTemplate(npcId);
						
						Skill skillData = null;
						if (npcData.getSkills() != null)
						{
							npcData.getSkills().clear();
						}
						
						// without race
						try (PreparedStatement statement = con1.prepareStatement("SELECT skillid, level FROM npc_skills WHERE npcid=? AND skillid <> 4416"))
						{
							statement.setInt(1, npcId);
							try (ResultSet skillDataList = statement.executeQuery())
							{
								while (skillDataList.next())
								{
									final int idval = skillDataList.getInt("skillid");
									final int levelval = skillDataList.getInt("level");
									skillData = SkillData.getInstance().getSkill(idval, levelval);
									if (skillData != null)
									{
										npcData.addSkill(skillData);
									}
								}
							}
						}
						
						if (Config.CUSTOM_NPC_SKILLS_TABLE)
						{
							try (PreparedStatement statement2 = con1.prepareStatement("SELECT skillid, level FROM npc_skills WHERE npcid=? AND skillid <> 4416"))
							{
								statement2.setInt(1, npcId);
								try (ResultSet skillDataList2 = statement2.executeQuery())
								{
									while (skillDataList2.next())
									{
										final int idval = skillDataList2.getInt("skillid");
										final int levelval = skillDataList2.getInt("level");
										skillData = SkillData.getInstance().getSkill(idval, levelval);
										if (skillData != null)
										{
											npcData.addSkill(skillData);
										}
									}
								}
							}
						}
					}
					catch (final Exception e)
					{
						LOG.warning("Error while reloading npc skill list (" + npcId + "): " + e);
					}
					
					showNpcSkillList(activeChar, npcId, 0);
					
					activeChar.sendMessage("Deleted skill id " + skillId + " from npc id " + npcId + ".");
				}
			}
			catch (final Exception e)
			{
				activeChar.sendMessage("Could not delete npc skill!");
				LOG.warning("Error while deleting npc skill (" + npcId + ", " + skillId + "): " + e);
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_save_npc"))
		{
			try
			{
				saveNpcProperty(st.nextToken());
			}
			catch (final Exception e)
			{
			}
		}
		return true;
	}
	
	// ----------~ METODOS ~---------- //
	
	/**
	 * @param admin
	 * @param tradeListID
	 * @param page
	 */
	private static void showShopList(L2PcInstance admin, int tradeListID, int page)
	{
		final MerchantTradeList tradeList = TradeControllerData.getInstance().getBuyList(tradeListID);
		if ((page > ((tradeList.getItems().size() / PAGE_LIMIT) + 1)) || (page < 1))
		{
			return;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<font color=\"LEVEL\">Merchant Shop List Page: " + page + "</font>");
		sb.append("<br>Edit, add or delete entries in a merchantList.");
		sb.append("<table>");
		sb.append("<tr><td width=150>Item Name</td><td width=60>Price</td><td width=40>Delete</td></tr>");
		final int start = ((page - 1) * PAGE_LIMIT);
		final int end = Math.min(((page - 1) * PAGE_LIMIT) + (PAGE_LIMIT - 1), tradeList.getItems().size() - 1);
		
		for (final ItemInstance item : tradeList.getItems(start, end + 1))
		{
			sb.append("<tr><td><a action=\"bypass -h admin_editShopItem " + tradeList.getListId() + " " + item.getId() + "\">" + item.getItem().getName() + "</a></td>");
			sb.append("<td>" + item.getPriceToSell() + "</td>");
			sb.append("<td><button value=\"Del\" action=\"bypass -h admin_delShopItem " + tradeList.getListId() + " " + item.getId() + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			sb.append("</tr>");
		}
		sb.append("<tr>");
		final int min = 1;
		final int max = (tradeList.getItems().size() / PAGE_LIMIT) + 1;
		if (page > 1)
		{
			sb.append("<td><button value=\"Page" + (page - 1) + "\" action=\"bypass -h admin_showShopList " + tradeList.getListId() + " " + (page - 1) + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		}
		if (page < max)
		{
			if (page <= min)
			{
				sb.append("<td></td>");
			}
			sb.append("<td><button value=\"Page" + (page + 1) + "\" action=\"bypass -h admin_showShopList " + tradeList.getListId() + " " + (page + 1) + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		}
		sb.append("</tr><tr><td>.</td></tr>");
		sb.append("</table>");
		sb.append("<center>");
		sb.append("<button value=\"Add\" action=\"bypass -h admin_addShopItem " + tradeList.getListId() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal>");
		sb.append("<button value=\"Close\" action=\"bypass -h admin_close_window\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal>");
		sb.append("</center></body></html>");
		
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
		
	}
	
	/**
	 * @param admin
	 * @param merchantID
	 */
	private static void showShop(L2PcInstance admin, int merchantID)
	{
		final List<MerchantTradeList> tradeLists = getTradeLists(merchantID);
		if (tradeLists == null)
		{
			admin.sendMessage("Unknown npc template ID" + merchantID);
			return;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<center>");
		sb.append("<font color=\"LEVEL\">Merchant Shop Lists</font>");
		sb.append("<br>Select a list to view");
		sb.append("<table border=\"0\" width=\"100%\">");
		
		for (final MerchantTradeList tradeList : tradeLists)
		{
			if (tradeList != null)
			{
				sb.append("<tr><td align=center><img src=\"L2UI_CH3.questwndplusbtn_over\" width=16 height=16></td><td><a action=\"bypass -h admin_showShopList " + tradeList.getListId() + " 1\">Trade List " + tradeList.getListId() + "</a></td></tr>");
			}
		}
		
		sb.append("</table>");
		sb.append("<center>");
		sb.append("<button value=\"Close\" action=\"bypass -h admin_close_window\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal>");
		sb.append("</center></body></html>");
		
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
	}
	
	/**
	 * @param admin
	 * @param args
	 */
	private static void delShopItem(L2PcInstance admin, String[] args)
	{
		final int tradeListID = Integer.parseInt(args[1]);
		final int itemID = Integer.parseInt(args[2]);
		final MerchantTradeList tradeList = TradeControllerData.getInstance().getBuyList(tradeListID);
		
		if (tradeList.getPriceForItemId(itemID) < 0)
		{
			return;
		}
		
		if (args.length > 3)
		{
			final int order = findOrderTradeList(itemID, tradeList.getPriceForItemId(itemID), tradeListID);
			
			tradeList.removeItem(itemID);
			deleteTradeList(tradeListID, order);
			
			admin.sendMessage("Deleted " + ItemData.getInstance().getTemplate(itemID).getName() + " from Trade List " + tradeListID);
			showShopList(admin, tradeListID, 1);
			return;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Merchant Shop Item Delete</title>");
		sb.append("<body>");
		sb.append("<br>Delete entry in merchantList.");
		sb.append("<br>Item to Delete: " + ItemData.getInstance().getTemplate(itemID).getName());
		sb.append("<table>");
		sb.append("<tr><td width=100>Property</td><td width=100>Value</td></tr>");
		sb.append("<tr><td><br></td><td></td></tr>");
		sb.append("<tr><td>Price</td><td>" + tradeList.getPriceForItemId(itemID) + "</td></tr>");
		sb.append("</table>");
		sb.append("<center><br><br><br>");
		sb.append("<button value=\"Confirm\" action=\"bypass -h admin_delShopItem " + tradeListID + " " + itemID + " 1\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("<br><button value=\"Back\" action=\"bypass -h admin_showShopList " + tradeListID + " 1\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</center>");
		sb.append("</body></html>");
		
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
	}
	
	/**
	 * @param admin
	 * @param args
	 */
	private static void addShopItem(L2PcInstance admin, String[] args)
	{
		final int tradeListID = Integer.parseInt(args[1]);
		
		final MerchantTradeList tradeList = TradeControllerData.getInstance().getBuyList(tradeListID);
		if (tradeList == null)
		{
			admin.sendMessage("TradeList not found!");
			return;
		}
		
		if (args.length > 3)
		{
			final int order = tradeList.getItems().size() + 1; // last item order + 1
			final int itemID = Integer.parseInt(args[2]);
			final int price = Integer.parseInt(args[3]);
			
			final ItemInstance newItem = ItemData.getInstance().createDummyItem(itemID);
			newItem.setPriceToSell(price);
			newItem.setCount(-1);
			tradeList.addItem(newItem);
			storeTradeList(itemID, price, tradeListID, order);
			
			admin.sendMessage("Added " + newItem.getItem().getName() + " to Trade List " + tradeList.getListId());
			showShopList(admin, tradeListID, 1);
			return;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Merchant Shop Item Add</title>");
		sb.append("<body>");
		sb.append("<br>Add a new entry in merchantList.");
		sb.append("<table>");
		sb.append("<tr><td width=100>Property</td><td>Edit Field</td></tr>");
		sb.append("<tr><td><br></td><td></td></tr>");
		sb.append("<tr><td>ItemID</td><td><edit var=\"itemID\" width=80></td></tr>");
		sb.append("<tr><td>Price</td><td><edit var=\"price\" width=80></td></tr>");
		sb.append("</table>");
		sb.append("<center><br><br><br>");
		sb.append("<button value=\"Save\" action=\"bypass -h admin_addShopItem " + tradeListID + " $itemID $price\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("<br><button value=\"Back\" action=\"bypass -h admin_showShopList " + tradeListID + " 1\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</center>");
		sb.append("</body></html>");
		
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
	}
	
	/**
	 * @param  itemID
	 * @param  price
	 * @param  tradeListID
	 * @return
	 */
	private static int findOrderTradeList(int itemID, int price, int tradeListID)
	{
		int order = -1;
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM merchant_buylists WHERE `shop_id`='" + tradeListID + "' AND `item_id` ='" + itemID + "' AND `price` = '" + price + "'");
			ResultSet rs = stmt.executeQuery())
		{
			
			if (rs.first())
			{
				order = rs.getInt("order");
			}
			
			if ((order < 0) && Config.CUSTOM_MERCHANT_TABLES)
			{
				try (PreparedStatement stmt2 = con.prepareStatement("SELECT * FROM custom_merchant_buylists WHERE `shop_id`='" + tradeListID + "' AND `item_id` ='" + itemID + "' AND `price` = '" + price + "'");
					ResultSet rs2 = stmt2.executeQuery())
				{
					if (rs2.first())
					{
						order = rs2.getInt("order");
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not get order for (" + itemID + ", " + price + ", " + tradeListID + "): " + e);
		}
		return order;
	}
	
	/**
	 * @param tradeListID
	 * @param order
	 */
	private static void deleteTradeList(int tradeListID, int order)
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			int updated = 0;
			if (Config.CUSTOM_MERCHANT_TABLES)
			{
				try (PreparedStatement stmt = con.prepareStatement("DELETE FROM custom_merchant_buylists WHERE `shop_id`='" + tradeListID + "' AND `order`='" + order + "'"))
				{
					updated = stmt.executeUpdate();
				}
			}
			
			if (updated == 0)
			{
				try (PreparedStatement stmt = con.prepareStatement("DELETE FROM merchant_buylists WHERE `shop_id`='" + tradeListID + "' AND `order`='" + order + "'"))
				{
					stmt.executeUpdate();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not delete trade list (" + tradeListID + ", " + order + "): " + e);
		}
	}
	
	/**
	 * @param itemID
	 * @param price
	 * @param tradeListID
	 * @param order
	 */
	private static void storeTradeList(int itemID, int price, int tradeListID, int order)
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			String table = "merchant_buylists";
			if (Config.CUSTOM_MERCHANT_TABLES)
			{
				table = "custom_merchant_buylists";
			}
			
			try (PreparedStatement stmt = con.prepareStatement("INSERT INTO `" + table + "` (`item_id`,`price`,`shop_id`,`order`) values (" + itemID + "," + price + "," + tradeListID + "," + order + ")"))
			{
				stmt.execute();
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Could not store trade list (" + itemID + ", " + price + ", " + tradeListID + ", " + order + "): " + e);
		}
	}
	
	/**
	 * @param  merchantID
	 * @return
	 */
	private static List<MerchantTradeList> getTradeLists(int merchantID)
	{
		final String target = "npc_%objectId%_Buy";
		
		String content = HtmData.getInstance().getHtm("data/html/merchant/" + merchantID + ".htm");
		
		if (content == null)
		{
			content = HtmData.getInstance().getHtm("data/html/merchant/7001.htm");
			
			if (content == null)
			{
				return null;
			}
		}
		
		final List<MerchantTradeList> tradeLists = new ArrayList<>();
		
		final String[] lines = content.split("\n");
		int pos = 0;
		
		for (final String line : lines)
		{
			pos = line.indexOf(target);
			if (pos >= 0)
			{
				final int tradeListID = Integer.decode((line.substring(pos + target.length() + 1)).split("\"")[0]);
				
				tradeLists.add(TradeControllerData.getInstance().getBuyList(tradeListID));
			}
		}
		
		return tradeLists;
	}
	
	/**
	 * @param adminPlayer
	 * @param npc
	 */
	private static void showNpcProperty(L2PcInstance adminPlayer, NpcTemplate npc)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		final String content = HtmData.getInstance().getHtm("data/html/admin/editnpc.htm");
		
		if (content != null)
		{
			adminReply.setHtml(content);
			adminReply.replace("%NpcName%", npc.getName());
			adminReply.replace("%CollisionRadius%", String.valueOf(npc.getCollisionRadius()));
			adminReply.replace("%CollisionHeight%", String.valueOf(npc.getCollisionHeight()));
			adminReply.replace("%Level%", String.valueOf(npc.getLevel()));
			adminReply.replace("%Type%", String.valueOf(npc.getType()));
			adminReply.replace("%AttackRange%", String.valueOf(npc.getBaseAtkRange()));
			adminReply.replace("%MaxHp%", String.valueOf(npc.getBaseHpMax()));
			adminReply.replace("%MaxBaseMp%", String.valueOf(npc.getBaseMpMax()));
			adminReply.replace("%Exp%", String.valueOf(npc.getRewardExp()));
			adminReply.replace("%Sp%", String.valueOf(npc.getRewardSp()));
			adminReply.replace("%Patk%", String.valueOf(npc.getBasePAtk()));
			adminReply.replace("%Pdef%", String.valueOf(npc.getBasePDef()));
			adminReply.replace("%Matk%", String.valueOf(npc.getBaseMAtk()));
			adminReply.replace("%Mdef%", String.valueOf(npc.getBaseMDef()));
			adminReply.replace("%Atkspd%", String.valueOf(npc.getBasePAtkSpd()));
			adminReply.replace("%Aggro%", String.valueOf(npc.getAggroRange()));
			adminReply.replace("%Matkspd%", String.valueOf(npc.getBaseMAtkSpd()));
			adminReply.replace("%Rhand%", String.valueOf(npc.getRhand()));
			adminReply.replace("%Lhand%", String.valueOf(npc.getLhand()));
			adminReply.replace("%WalkSpeed%", String.valueOf(npc.getBaseWalkSpd()));
			adminReply.replace("%RunSpeed%", String.valueOf(npc.getBaseRunSpd()));
			adminReply.replace("%NpcId%", String.valueOf(npc.getId()));
		}
		else
		{
			adminReply.setHtml("<html><head><body>File not found: data/html/admin/editnpc.htm</body></html>");
			// do nothing.
		}
		
		adminPlayer.sendPacket(adminReply);
	}
	
	/**
	 * @param modifications
	 */
	private static void saveNpcProperty(String modifications)
	{
		final StatsSet npcData = new StatsSet();
		try
		{
			final StringTokenizer st = new StringTokenizer(modifications, ",");
			while (st.hasMoreTokens())
			{
				final StringTokenizer st2 = new StringTokenizer(st.nextToken().trim());
				if (st2.countTokens() != 2)
				{
					continue;
				}
				final String name = st2.nextToken().trim();
				final String value = st2.nextToken().trim();
				
				if (name.equals("id"))
				{
					npcData.set("npcId", Integer.parseInt(value));
				}
				else if (name.equals("CollisionRadius"))
				{
					npcData.set("collision_radius", Integer.parseInt(value));
				}
				else if (name.equals("CollisionHeight"))
				{
					npcData.set("collision_height", Integer.parseInt(value));
				}
				else if (name.equals("Level"))
				{
					npcData.set("level", Integer.parseInt(value));
				}
				else if (name.equals("Sex"))
				{
					if (value.equals("0"))
					{
						npcData.set("sex", "male");
					}
					else
					{
						npcData.set("sex", "female");
					}
				}
				else if (name.equals("Type"))
				{
					npcData.set("type", value);
				}
				else if (name.equals("AttackRange"))
				{
					npcData.set("attackrange", Integer.parseInt(value));
				}
				else if (name.equals("MaxHp"))
				{
					npcData.set("hp", Integer.parseInt(value));
				}
				else if (name.equals("MaxBaseMp"))
				{
					npcData.set("mp", Integer.parseInt(value));
				}
				else if (name.equals("Exp"))
				{
					npcData.set("exp", Integer.parseInt(value));
				}
				else if (name.equals("Sp"))
				{
					npcData.set("sp", Integer.parseInt(value));
				}
				else if (name.endsWith("Patk"))
				{
					npcData.set("patk", Integer.parseInt(value));
				}
				else if (name.endsWith("Pdef"))
				{
					npcData.set("pdef", Integer.parseInt(value));
				}
				else if (name.endsWith("Matk"))
				{
					npcData.set("matk", Integer.parseInt(value));
				}
				else if (name.endsWith("Mdef"))
				{
					npcData.set("mdef", Integer.parseInt(value));
				}
				else if (name.endsWith("Atkspd"))
				{
					npcData.set("atkspd", Integer.parseInt(value));
				}
				else if (name.endsWith("Aggro"))
				{
					npcData.set("aggro", Integer.parseInt(value));
				}
				else if (name.endsWith("Matkspd"))
				{
					npcData.set("matkspd", Integer.parseInt(value));
				}
				else if (name.endsWith("Rhand"))
				{
					npcData.set("rhand", Integer.parseInt(value));
				}
				else if (name.endsWith("Lhand"))
				{
					npcData.set("lhand", Integer.parseInt(value));
				}
				else if (name.endsWith("Armor"))
				{
					npcData.set("armor", Integer.parseInt(value));
				}
				else if (name.endsWith("RunSpeed"))
				{
					npcData.set("runspd", Integer.parseInt(value));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("save Npc data error");
		}
		
		// L2NpcTemplate template = new L2NpcTemplate(npcData);
		// NpcTable.getInstance().replaceTemplate(template);
		
		// save the npc data
		NpcData.getInstance().saveNpc(npcData);
		// and reload
		NpcData.getInstance().reload();
	}
	
	/**
	 * @param admin
	 * @param npcId
	 * @param page
	 */
	private static void showNpcDropList(L2PcInstance admin, int npcId, int page)
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		if (template == null)
		{
			admin.sendMessage("unknown npc template id" + npcId);
			return;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<br>");
		sb.append("NPC: " + template.getName() + "(" + template.getId() + ") 's drop view");
		
		var searchPage = PAGE_LIMIT * (page - 1);
		var count = 0;
		var color = 0;
		
		var actualCat = -2;
		
		for (DropCategory cat : template.getDropsCategory())
		{
			for (DropInstance drop : cat.getAllDrops())
			{
				count++;
				// min
				if (count < searchPage)
				{
					continue;
				}
				// max
				if (count >= (searchPage + PAGE_LIMIT))
				{
					continue;
				}
				
				if (actualCat != cat.getCategoryType())
				{
					actualCat = cat.getCategoryType();
					
					sb.append("<img src=L2UI_CH3.br_bar2_mp width=270 height=1>");
					sb.append("<table width=270 height=40 border=0 bgcolor=CC99FF>");
					sb.append("<tr>");
					sb.append("<td align=center>Category: " + actualCat + " Chance:" + (((cat.getCategoryChance() * 1.0) / DropInstance.MAX_CHANCE) * 100.0) + "%</td>");
					sb.append("</tr>");
					sb.append("</table>");
					sb.append("<img src=L2UI_CH3.br_bar2_mp width=270 height=1>");
				}
				
				sb.append("<table width=270 border=0 cellspacing=0 cellpadding=0" + (((color % 2) == 0) ? " bgcolor=000000>" : ">"));
				sb.append("<tr>");
				//
				sb.append("<td width=32 height=60><img src=" + IconData.getIconByItemId(drop.getItemId()) + " width=32 height=32></td>");
				//
				sb.append("<td width=238>");
				sb.append("<table width=238 border=0 cellspacing=0 cellpadding=0>");
				sb.append("<tr><td height=20><font color=LEVEL>* " + ItemData.getInstance().getTemplate(drop.getItemId()).getName() + "</font> [" + drop.getItemId() + "]</td></tr>");
				sb.append("<tr><td>* Drop Min:" + drop.getMinDrop() + " Max:" + drop.getMaxDrop() + " Chance:" + ((float) (((drop.getChance() * 1.0) / DropInstance.MAX_CHANCE) * 100.0)) + "%</td></tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<img src=L2UI.SquareGray width=264 height=1>");
				color++;
			}
		}
		
		sb.append("<center>");
		
		sb.append("<table bgcolor=CC99FF>");
		sb.append("<tr>");
		
		var currentPage = 1;
		
		for (int i = 0; i < count; i++)
		{
			if ((i % PAGE_LIMIT) == 0)
			{
				sb.append("<td width=18 align=center><a action=\"bypass -h admin_show_droplist " + npcId + " " + currentPage + "\">" + currentPage + "</a></td>");
				currentPage++;
			}
		}
		
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<img src=L2UI.SquareGray width=264 height=1>");
		sb.append("</center>");
		sb.append("</body></html>");
		
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
	}
	
	/**
	 * Mostramos la lista de skills de un npc
	 * @param admin
	 * @param npcId
	 * @param page
	 */
	private static void showNpcSkillList(L2PcInstance admin, int npcId, int page)
	{
		final NpcTemplate npcData = NpcData.getInstance().getTemplate(npcId);
		if (npcData == null)
		{
			admin.sendMessage("Template id unknown: " + npcId);
			return;
		}
		
		final Map<Integer, Skill> skills = new HashMap<>(npcData.getSkills());
		final int skillsize = skills.size();
		
		final int maxSkillsPerPage = PAGE_LIMIT;
		int maxPages = skillsize / maxSkillsPerPage;
		if (skillsize > (maxSkillsPerPage * maxPages))
		{
			maxPages++;
		}
		
		if (page > maxPages)
		{
			page = maxPages;
		}
		
		final int skillsStart = maxSkillsPerPage * page;
		int skillsEnd = skillsize;
		if ((skillsEnd - skillsStart) > maxSkillsPerPage)
		{
			skillsEnd = skillsStart + maxSkillsPerPage;
		}
		
		final StringBuilder sb = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		sb.append(npcData.getName());
		sb.append(" (");
		sb.append(npcData.getId());
		sb.append("): ");
		sb.append(skillsize);
		sb.append(" skills</font></center><table width=300 bgcolor=\"000000\"><tr>");
		
		for (int x = 0; x < maxPages; x++)
		{
			final int pagenr = x + 1;
			if (page == x)
			{
				sb.append("<td>Page ");
				sb.append(pagenr);
				sb.append("</td>");
			}
			else
			{
				sb.append("<td><a action=\"bypass -h admin_show_skilllist_npc ");
				sb.append(npcData.getId());
				sb.append(" ");
				sb.append(x);
				sb.append("\"> Page ");
				sb.append(pagenr);
				sb.append(" </a></td>");
			}
		}
		sb.append("</tr></table><table width=\"100%\" border=0><tr><td>Skill name [skill id-skill lvl]</td><td>Delete</td></tr>");
		final Iterator<Skill> skillite = skills.values().iterator();
		
		for (int i = 0; i < skillsStart; i++)
		{
			if (skillite.hasNext())
			{
				skillite.next();
			}
		}
		
		int cnt = skillsStart;
		Skill sk;
		while (skillite.hasNext())
		{
			cnt++;
			if (cnt > skillsEnd)
			{
				break;
			}
			
			sk = skillite.next();
			sb.append("<tr><td width=240>");
			if (sk.getSkillType() == SkillType.NOTDONE)
			{
				sb.append("<font color=\"777777\">" + sk.getName() + "</font>");
			}
			else
			{
				sb.append(sk.getName());
			}
			sb.append(" [");
			sb.append(sk.getId());
			sb.append("-");
			sb.append(sk.getLevel());
			sb.append("]</td><td width=60><a action=\"bypass -h admin_del_skill_npc ");
			sb.append(npcData.getId());
			sb.append(" ");
			sb.append(sk.getId());
			sb.append("\">Delete</a></td></tr>");
		}
		sb.append("</table><br><center><button value=\"Add Skill\" action=\"bypass -h admin_add_skill_npc ");
		sb.append(npcId);
		sb.append("\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal><button value=\"Close\" action=\"bypass -h admin_close_window\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></center></body></html>");
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(sb.toString());
		admin.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
