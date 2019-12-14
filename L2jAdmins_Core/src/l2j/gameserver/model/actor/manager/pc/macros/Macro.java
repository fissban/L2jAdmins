package l2j.gameserver.model.actor.manager.pc.macros;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsHolder;
import l2j.gameserver.model.actor.manager.pc.shortcuts.ShortCutsType;
import l2j.gameserver.network.external.server.SendMacroList;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/02 15:38:41 $
 */
public class Macro
{
	private static final Logger LOG = Logger.getLogger(Macro.class.getName());
	// SQL
	private static final String REGISTER_MACRO = "INSERT INTO character_macroses (char_obj_id, id, icon, name, descr, acronym, commands) values(?,?,?,?,?,?,?)";
	private static final String DELETE_MACRO = "DELETE FROM character_macroses WHERE char_obj_id=? AND id=?";
	private static final String RESTORE_MACRO = "SELECT char_obj_id, id, icon, name, descr, acronym, commands FROM character_macroses WHERE char_obj_id=?";
	
	private final L2PcInstance player;
	private int revision;
	private int macroId;
	private final Map<Integer, MacroHolder> macroses = new LinkedHashMap<>();
	
	public Macro(L2PcInstance owner)
	{
		player = owner;
		revision = 1;
		macroId = 1000;
	}
	
	public int getRevision()
	{
		return revision;
	}
	
	/**
	 * List of all macros L2PcInstance.
	 * @return List<Macro>
	 */
	public List<MacroHolder> getAllMacroses()
	{
		return allMacrosOrdered();
	}
	
	/**
	 * Get a specific macro from its id.
	 * @param  id
	 * @return
	 */
	public MacroHolder getMacro(int id)
	{
		return macroses.get(id - 1);
	}
	
	/**
	 * Add a Macro to the L2PcInstance macroses.
	 * @param macro -> Macro
	 */
	public void registerMacro(MacroHolder macro)
	{
		if (macro.getId() == 0)
		{
			macro.setId(macroId++);
			
			while (macroses.get(macro.getId()) != null)
			{
				macro.setId(macroId++);
			}
			
			macroses.put(macro.getId(), macro);
			
			registerMacroInDb(macro);
		}
		else
		{
			MacroHolder old = macroses.put(macro.getId(), macro);
			if (old != null)
			{
				deleteMacroFromDb(old);
			}
			registerMacroInDb(macro);
		}
		sendUpdate();
	}
	
	/**
	 * Delete the L2Macro corresponding to the Identifier from the L2PcInstance macroses.
	 * @param id
	 */
	public void deleteMacro(int id)
	{
		MacroHolder toRemove = macroses.get(id);
		if (toRemove != null)
		{
			deleteMacroFromDb(toRemove);
		}
		
		macroses.remove(id);
		
		for (ShortCutsHolder sc : player.getShortCuts().getAllShortCuts())
		{
			if (sc == null)
			{
				continue;
			}
			
			if ((sc.getId() == id) && (sc.getType() == ShortCutsType.MACRO))
			{
				player.getShortCuts().deleteShortCut(sc.getSlot(), sc.getPage());
			}
		}
		
		sendUpdate();
	}
	
	/**
	 * Update macroList from player -> SendMacroList()
	 */
	public void sendUpdate()
	{
		revision++;
		List<MacroHolder> allMacros = getAllMacroses();
		if (allMacros.isEmpty())
		{
			player.sendPacket(new SendMacroList(revision, allMacros.size(), null));
		}
		else
		{
			for (MacroHolder m : allMacros)
			{
				player.sendPacket(new SendMacroList(revision, allMacros.size(), m));
			}
		}
	}
	
	private void registerMacroInDb(MacroHolder macro)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(REGISTER_MACRO))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, macro.getId());
			ps.setInt(3, macro.getIcon());
			ps.setString(4, macro.getName());
			ps.setString(5, macro.getDescription());
			ps.setString(6, macro.getAcronym());
			StringBuilder sb = new StringBuilder();
			for (MacroCmdHolder cmd : macro.getCommands())
			{
				sb.append(cmd.getType()).append(',');
				sb.append(cmd.getSkillId()).append(',');
				sb.append(cmd.getShortCutId());
				if ((cmd.getCmd() != null) && (cmd.getCmd().length() > 0))
				{
					sb.append(',').append(cmd.getCmd());
				}
				sb.append(';');
			}
			ps.setString(7, sb.toString());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("Macrolist: Could not register macro:" + e);
		}
	}
	
	/**
	 * @param macro
	 * @param shortcut
	 */
	private void deleteMacroFromDb(MacroHolder macro)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MACRO))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, macro.getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("Macrolist: Could not delete macro:" + e);
		}
	}
	
	public void restore()
	{
		macroses.clear();
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_MACRO))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int id = rset.getInt("id");
					int icon = rset.getInt("icon");
					String name = rset.getString("name");
					String descr = rset.getString("descr");
					String acronym = rset.getString("acronym");
					List<MacroCmdHolder> commands = new ArrayList<>();
					StringTokenizer st1 = new StringTokenizer(rset.getString("commands"), ";");
					while (st1.hasMoreTokens())
					{
						StringTokenizer st = new StringTokenizer(st1.nextToken(), ",");
						if (st.countTokens() < 3)
						{
							continue;
						}
						String type = st.nextToken();
						int d1 = Integer.parseInt(st.nextToken());
						int d2 = Integer.parseInt(st.nextToken());
						String cmd = "";
						if (st.hasMoreTokens())
						{
							cmd = st.nextToken();
						}
						
						commands.add(new MacroCmdHolder(commands.size(), MacroType.valueOf(type), d1, d2, cmd));
					}
					
					macroses.put(id, new MacroHolder(id, icon, name, descr, acronym, commands));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Macrolist: Could not store shortcuts:" + e);
		}
	}
	
	// MISC ==============================================================================================//
	/**
	 * Realizamos un mapa ordenado con los macros
	 * @return
	 */
	private List<MacroHolder> allMacrosOrdered()
	{
		List<MacroHolder> allMacros = new ArrayList<>();
		
		for (Entry<Integer, MacroHolder> shortCut : macroses.entrySet())
		{
			allMacros.add(shortCut.getValue());
		}
		
		return allMacros;
	}
}
