package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.model.items.ItemHenna;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class HennaTreeData
{
	private static final Logger LOG = Logger.getLogger(HennaTreeData.class.getName());
	private static final HennaTreeData INSTANCE = new HennaTreeData();
	private final Map<ClassId, List<HennaHolder>> hennaTrees = new HashMap<>();
	
	private HennaTreeData()
	{
		hennaTrees.clear();
		int classId = 0;
		int count = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT class_name, id, parent_id FROM class_list ORDER BY id");
			ResultSet classlist = ps.executeQuery())
		{
			while (classlist.next())
			{
				List<HennaHolder> list = new ArrayList<>();
				
				classId = classlist.getInt("id");
				
				try (PreparedStatement statement2 = con.prepareStatement("SELECT class_id, symbol_id FROM henna_trees where class_id=? ORDER BY symbol_id"))
				{
					statement2.setInt(1, classId);
					
					try (ResultSet hennatree = statement2.executeQuery())
					{
						while (hennatree.next())
						{
							int id = hennatree.getInt("symbol_id");
							ItemHenna template = HennaData.getInstance().getTemplate(id);
							if (template == null)
							{
								return;
							}
							HennaHolder henna = new HennaHolder(template);
							henna.setSymbolId(id);
							henna.setItemIdDye(template.getDyeId());
							henna.setAmountDyeRequire(template.getAmountDyeRequire());
							henna.setPrice(template.getPrice());
							henna.setCancelFee(template.getCancelFee());
							henna.setStatINT(template.getStatINT());
							henna.setStatSTR(template.getStatSTR());
							henna.setStatCON(template.getStatCON());
							henna.setStatMEN(template.getStatMEN());
							henna.setStatDEX(template.getStatDEX());
							henna.setStatWIT(template.getStatWIT());
							
							list.add(henna);
						}
						hennaTrees.put(ClassId.getById(classId), list);
						
						count += list.size();
						LOG.fine("Henna Tree for Class: " + classId + " has " + list.size() + " Henna Templates.");
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while creating henna tree for classId " + classId + "  " + e);
			e.printStackTrace();
		}
		
		UtilPrint.result("HennaTreeTable", "Loaded henna template", count);
	}
	
	public List<HennaHolder> getAvailableHenna(ClassId classId)
	{
		if (hennaTrees.get(classId) == null)
		{
			// the hennaTree for this class is undefined, so we give an empty list
			LOG.warning("Hennatree for class " + classId + " is not defined !");
			return Collections.emptyList();
		}
		
		return hennaTrees.get(classId);
	}
	
	public static HennaTreeData getInstance()
	{
		return INSTANCE;
	}
}
