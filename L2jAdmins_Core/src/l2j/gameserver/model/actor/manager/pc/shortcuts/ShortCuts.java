package l2j.gameserver.model.actor.manager.pc.shortcuts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ExAutoSoulShot;
import l2j.gameserver.network.external.server.ExAutoSoulShot.AutoSoulShotType;
import l2j.gameserver.network.external.server.ShortCutInit;
import l2j.gameserver.network.external.server.ShortCutRegister;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:33 $
 */
public class ShortCuts
{
	private static final Logger LOG = Logger.getLogger(ShortCuts.class.getName());
	// SQL
	private static final String RESTORE_SHORTCUT = "SELECT char_obj_id, slot, page, type, shortcut_id, level FROM character_shortcuts WHERE char_obj_id=? AND class_index=?";
	private static final String REGISTER_SHORTCUT = "REPLACE INTO character_shortcuts (char_obj_id, slot, page,type, shortcut_id, level, class_index) values(?,?,?,?,?,?,?)";
	private static final String DELETE_SHORTCUT = "DELETE FROM character_shortcuts WHERE char_obj_id=? AND slot=? AND page=? AND type=? AND shortcut_id=? AND level=? AND class_index=?";
	
	private final L2PcInstance player;
	private final Map<Integer, ShortCutsHolder> shortCuts = new TreeMap<>();
	
	public ShortCuts(L2PcInstance owner)
	{
		player = owner;
	}
	
	/**
	 * @return a list containing all L2ShortCut of the L2PcInstance.
	 */
	public List<ShortCutsHolder> getAllShortCuts()
	{
		return allShortCutOrdered();
	}
	
	/**
	 * @param  slot The slot in which the shortCuts is equipped
	 * @param  page The page of shortCuts containing the slot
	 * @return      the L2ShortCut of the L2PcInstance corresponding to the position (page-slot).
	 */
	public ShortCutsHolder getShortCut(int slot, int page)
	{
		ShortCutsHolder sc = shortCuts.get(slot + (page * 12));
		// verify shortcut
		if ((sc != null) && (sc.getType() == ShortCutsType.ITEM))
		{
			if (player.getInventory().getItemByObjectId(sc.getId()) == null)
			{
				deleteShortCut(sc.getSlot(), sc.getPage());
				sc = null;
			}
		}
		
		return sc;
	}
	
	/**
	 * Add a L2shortCut to the L2PcInstance shortCuts.
	 * @param shortcut
	 * @param sendPacket : true -> sendPacket(new ShortCutRegister());
	 */
	public synchronized void registerShortCut(ShortCutsHolder shortcut, boolean sendPacket)
	{
		if (sendPacket)
		{
			player.sendPacket(new ShortCutRegister(shortcut));
		}
		shortCuts.put(shortcut.getSlot() + (shortcut.getPage() * 12), shortcut);
		
		registerShortCutFromDb(shortcut);
	}
	
	/**
	 * Delete the L2ShortCut corresponding to the position (page-slot) from the L2PcInstance shortCuts.
	 * @param slot
	 * @param page
	 */
	public synchronized void deleteShortCut(int slot, int page)
	{
		ShortCutsHolder old = shortCuts.remove(slot + (page * 12));
		if (old == null)
		{
			return;
		}
		
		deleteShortCutFromDb(old);
		
		if (player == null)
		{
			return;
		}
		
		if (old.getType() == ShortCutsType.ITEM)
		{
			ItemInstance item = player.getInventory().getItemByObjectId(old.getId());
			if ((item != null) && (item.getType() == EtcItemType.SHOT))
			{
				player.removeAutoSoulShot(item.getId());
				player.sendPacket(new ExAutoSoulShot(item.getId(), AutoSoulShotType.DESACTIVE));
			}
		}
		
		player.sendPacket(new ShortCutInit(player));
		
		for (int shotId : player.getAutoSoulShot())
		{
			player.sendPacket(new ExAutoSoulShot(shotId, AutoSoulShotType.ACTIVE));
		}
	}
	
	/**
	 * Remove a ShortCut of the L2PcInstance shortCuts
	 * @param objectId
	 */
	public synchronized void removeItemFromShortCut(int objectId)
	{
		for (ShortCutsHolder shortcut : getAllShortCuts())
		{
			if ((shortcut.getType() == ShortCutsType.ITEM) && (shortcut.getId() == objectId))
			{
				deleteShortCut(shortcut.getSlot(), shortcut.getPage());
				break;
			}
		}
	}
	
	/**
	 * @param shortCut
	 */
	private void deleteShortCutFromDb(ShortCutsHolder shortCut)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_SHORTCUT))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, shortCut.getSlot());
			ps.setInt(3, shortCut.getPage());
			ps.setInt(4, shortCut.getType().getMask());
			ps.setInt(5, shortCut.getId());
			ps.setInt(6, shortCut.getLevel());
			ps.setInt(7, player.getClassIndex());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("ShortCuts: Could not delete character shortcut: " + e);
		}
	}
	
	private void registerShortCutFromDb(ShortCutsHolder shortcut)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(REGISTER_SHORTCUT))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, shortcut.getSlot());
			ps.setInt(3, shortcut.getPage());
			ps.setInt(4, shortcut.getType().getMask());
			ps.setInt(5, shortcut.getId());
			ps.setInt(6, shortcut.getLevel());
			ps.setInt(7, player.getClassIndex());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("ShortCuts: Could not store character shortcut: " + e);
		}
	}
	
	public void restore()
	{
		shortCuts.clear();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_SHORTCUT))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, player.getClassIndex());
			
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int slot = rset.getInt("slot");
					int page = rset.getInt("page");
					int type = rset.getInt("type");
					int id = rset.getInt("shortcut_id");
					int level = rset.getInt("level");
					
					if (level > -1)
					{
						level = player.getSkillLevel(id);
					}
					ShortCutsType typeEnum = null;
					switch (type)
					{
						case 1:
							typeEnum = ShortCutsType.ITEM;
							break;
						case 2:
							typeEnum = ShortCutsType.SKILL;
							break;
						case 3:
							typeEnum = ShortCutsType.ACTION;
							break;
						case 4:
							typeEnum = ShortCutsType.MACRO;
							break;
						case 5:
							typeEnum = ShortCutsType.RECIPE;
							break;
						default:
							continue;
					}
					
					shortCuts.put(slot + (page * 12), new ShortCutsHolder(slot, page, typeEnum, id, level, 1));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("ShortCuts: Could not restore character shortcuts: " + e);
		}
		
		// verify shortcuts
		for (ShortCutsHolder sc : getAllShortCuts())
		{
			if (sc == null)
			{
				continue;
			}
			
			if (sc.getType() == ShortCutsType.ITEM)
			{
				if (player.getInventory().getItemByObjectId(sc.getId()) == null)
				{
					deleteShortCut(sc.getSlot(), sc.getPage());
				}
			}
		}
	}
	
	// MISC ========================================================================================//
	/**
	 * Realizamos un mapa ordenado con los shortCuts
	 * @return
	 */
	private List<ShortCutsHolder> allShortCutOrdered()
	{
		List<ShortCutsHolder> allShortCuts = new ArrayList<>();
		
		for (Entry<Integer, ShortCutsHolder> shortCut : shortCuts.entrySet())
		{
			allShortCuts.add(shortCut.getValue());
		}
		
		return allShortCuts;
	}
}
