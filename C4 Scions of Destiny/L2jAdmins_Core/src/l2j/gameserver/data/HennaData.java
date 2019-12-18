package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.holder.HennaHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class HennaData extends XmlParser
{
	private final static Map<Integer, List<HennaHolder>> hennaInfo = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/hennas.xml");
	}
	
	@Override
	protected void parseFile()
	{
		int count = 0;
		for (Node n : getNodes("henna"))
		{
			// se lee todas la informacion de los diferentes hennas
			HennaHolder henna = new HennaHolder();
			
			int symbol_id = 0;
			int dye_id = 0;
			int dye_amount = 0;
			int price = 0;
			int cancel_fee = 0;
			int stat_str = 0;
			int stat_dex = 0;
			int stat_con = 0;
			int stat_wit = 0;
			int stat_int = 0;
			int stat_men = 0;
			List<Integer> availableClassList = new ArrayList<>();
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				NamedNodeMap attrs = c.getAttributes();
				
				switch (c.getNodeName())
				{
					case "symbol_id":
						symbol_id = parseInt(attrs, "val");
						break;
					case "dye_id":
						dye_id = parseInt(attrs, "val");
						break;
					case "dye_amount":
						dye_amount = parseInt(attrs, "val");
						break;
					case "price":
						price = parseInt(attrs, "val");
						break;
					case "cancel_fee":
						cancel_fee = parseInt(attrs, "val");
						break;
					case "stat":
						stat_str = parseInt(attrs, "str");
						stat_dex = parseInt(attrs, "dex");
						stat_con = parseInt(attrs, "con");
						stat_wit = parseInt(attrs, "wit");
						stat_int = parseInt(attrs, "int");
						stat_men = parseInt(attrs, "men");
						break;
					case "availableClass":
						String availableClass = parseString(attrs, "val");
						
						for (String p : availableClass.split(" "))
						{
							availableClassList.add(Integer.valueOf(p));
						}
						break;
				}
			}
			
			henna.setSymbolId(symbol_id);
			henna.setDyeId(dye_id);
			henna.setDyeAmount(dye_amount);
			henna.setPrice(price);
			henna.setCancelFee(cancel_fee);
			
			henna.setStatSTR(stat_str);
			henna.setStatDEX(stat_dex);
			henna.setStatCON(stat_con);
			
			henna.setStatINT(stat_int);
			henna.setStatWIT(stat_wit);
			henna.setStatMEN(stat_men);
			
			// Segun la clase se van almacenando en la memoria
			for (int ac : availableClassList)
			{
				if (!hennaInfo.containsKey(Integer.valueOf(ac)))
				{
					hennaInfo.put(ac, new ArrayList<>());
				}
				
				hennaInfo.get(ac).add(henna);
			}
			
			System.out.println("load symbol id " + symbol_id);
			count++;
		}
		
		UtilPrint.result("HennaData", "Loaded all henna info", count);
	}
	
	/**
	 * Get all symbols for one class id.
	 * @param  id
	 * @return
	 */
	public static List<HennaHolder> getByClass(int id)
	{
		return hennaInfo.get(id);
	}
	
	/**
	 * Get symbol by id
	 * @param  id
	 * @return
	 */
	public static HennaHolder getById(int id)
	{
		for (List<HennaHolder> listHenna : hennaInfo.values())
		{
			for (HennaHolder hh : listHenna)
			{
				if (hh.getSymbolId() == id)
				{
					return hh;
				}
			}
		}
		
		return null;
	}
	
	public static HennaData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaData INSTANCE = new HennaData();
	}
}
