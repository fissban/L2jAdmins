package l2j.gameserver.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import l2j.gameserver.model.holder.ExtractableItemHolder;
import l2j.gameserver.model.holder.ExtractableProductItemHolder;
import l2j.util.UtilPrint;

/**
 * @author FBIagent
 */
public class ExtractableItemsData
{
	private static final Logger LOG = Logger.getLogger(ExtractableItemsData.class.getName());
	
	private final Map<Integer, ExtractableItemHolder> items = new HashMap<>();
	
	public ExtractableItemsData()
	{
		try (Scanner s = new Scanner(new File("./data/extractable_items.csv")))
		{
			int lineCount = 0;
			
			while (s.hasNextLine())
			{
				lineCount++;
				
				String line = s.nextLine();
				
				if (line.startsWith("#"))
				{
					continue;
				}
				else if (line.isEmpty())
				{
					continue;
				}
				
				String[] lineSplit = line.split(";");
				boolean ok = true;
				int itemID = 0;
				
				try
				{
					itemID = Integer.parseInt(lineSplit[0]);
				}
				catch (Exception e)
				{
					LOG.warning("Extractable items data: Error in line " + lineCount + " -> invalid item id or wrong seperator after item id!");
					LOG.warning("		" + line);
					ok = false;
				}
				
				if (!ok)
				{
					continue;
				}
				
				List<ExtractableProductItemHolder> productTemp = new ArrayList<>();
				
				for (int i = 0; i < (lineSplit.length - 1); i++)
				{
					ok = true;
					
					String[] lineSplit2 = lineSplit[i + 1].split(",");
					
					if (lineSplit2.length != 3)
					{
						LOG.warning("Extractable items data: Error in line " + lineCount + " -> wrong seperator!");
						LOG.warning("		" + line);
						ok = false;
					}
					
					if (!ok)
					{
						continue;
					}
					
					int production = 0, amount = 0, chance = 0;
					
					try
					{
						production = Integer.parseInt(lineSplit2[0]);
						amount = Integer.parseInt(lineSplit2[1]);
						chance = Integer.parseInt(lineSplit2[2]);
					}
					catch (Exception e)
					{
						LOG.warning("Extractable items data: Error in line " + lineCount + " -> incomplete/invalid production data or wrong seperator!");
						LOG.warning("		" + line);
						ok = false;
					}
					
					if (!ok)
					{
						continue;
					}
					
					ExtractableProductItemHolder product = new ExtractableProductItemHolder(production, amount, chance);
					productTemp.add(product);
				}
				
				int fullChances = 0;
				
				for (ExtractableProductItemHolder Pi : productTemp)
				{
					fullChances += Pi.getChance();
				}
				
				if (fullChances > 100)
				{
					LOG.warning("Extractable items data: Error in line " + lineCount + " -> all chances together are more then 100!");
					LOG.warning("		" + line);
					continue;
				}
				
				items.put(itemID, new ExtractableItemHolder(itemID, productTemp));
			}
			
			UtilPrint.result("ExtractableItemsData", "Loaded extractable items", items.size());
		}
		catch (Exception e)
		{
			LOG.warning("Extractable items data: Can not find './data/extractable_items.csv'");
			return;
		}
	}
	
	public ExtractableItemHolder getExtractableItem(int itemID)
	{
		return items.get(itemID);
	}
	
	public int[] itemIDs()
	{
		int size = items.size();
		int[] result = new int[size];
		int i = 0;
		for (ExtractableItemHolder ei : items.values())
		{
			result[i] = ei.getItemId();
			i++;
		}
		return result;
	}
	
	public static ExtractableItemsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ExtractableItemsData INSTANCE = new ExtractableItemsData();
	}
}
