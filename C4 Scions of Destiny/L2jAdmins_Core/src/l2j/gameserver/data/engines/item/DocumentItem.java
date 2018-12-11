package l2j.gameserver.data.engines.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import l2j.gameserver.data.engines.AbstractDocumentBase;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemEtcItem;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.enums.WeaponType;

/**
 * @author mkizub
 */
public final class DocumentItem extends AbstractDocumentBase
{
	private DocumentItemHolder currentItem = null;
	private final List<Item> itemsInFile = new ArrayList<>();
	private Map<Integer, DocumentItemHolder> itemData = new HashMap<>();
	
	/**
	 * @param pItemData
	 * @param file
	 */
	public DocumentItem(Map<Integer, DocumentItemHolder> pItemData, File file)
	{
		super(file);
		itemData = pItemData;
	}
	
	/**
	 * @param item
	 */
	private void setCurrentItem(DocumentItemHolder item)
	{
		currentItem = item;
	}
	
	@Override
	protected StatsSet getStatsSet()
	{
		return currentItem.set;
	}
	
	@Override
	protected String getTableValue(String name)
	{
		return tables.get(name)[currentItem.currentLevel];
	}
	
	@Override
	protected String getTableValue(String name, int idx)
	{
		return tables.get(name)[idx - 1];
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("item".equalsIgnoreCase(d.getNodeName()))
					{
						setCurrentItem(new DocumentItemHolder());
						parseItem(d);
						itemsInFile.add(currentItem.item);
						resetTable();
					}
				}
			}
			else if ("item".equalsIgnoreCase(n.getNodeName()))
			{
				setCurrentItem(new DocumentItemHolder());
				parseItem(n);
				itemsInFile.add(currentItem.item);
			}
		}
	}
	
	protected void parseItem(Node n)
	{
		int itemId = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
		String itemName = n.getAttributes().getNamedItem("name").getNodeValue();
		
		currentItem.id = itemId;
		currentItem.name = itemName;
		currentItem.set = itemData.get(currentItem.id).set;
		currentItem.type = itemData.get(currentItem.id).type;
		
		Node first = n.getFirstChild();
		for (n = first; n != null; n = n.getNextSibling())
		{
			if ("table".equalsIgnoreCase(n.getNodeName()))
			{
				parseTable(n);
			}
		}
		for (n = first; n != null; n = n.getNextSibling())
		{
			if ("set".equalsIgnoreCase(n.getNodeName()))
			{
				parseBeanSet(n, itemData.get(currentItem.id).set, 1);
			}
		}
		for (n = first; n != null; n = n.getNextSibling())
		{
			if ("for".equalsIgnoreCase(n.getNodeName()))
			{
				makeItem();
				parseTemplate(n, currentItem.item);
			}
		}
	}
	
	private void makeItem()
	{
		if (currentItem.item != null)
		{
			return;
		}
		if (currentItem.type instanceof ArmorType)
		{
			currentItem.item = new ItemArmor((ArmorType) currentItem.type, currentItem.set);
		}
		else if (currentItem.type instanceof WeaponType)
		{
			currentItem.item = new ItemWeapon((WeaponType) currentItem.type, currentItem.set);
		}
		else if (currentItem.type instanceof EtcItemType)
		{
			currentItem.item = new ItemEtcItem((EtcItemType) currentItem.type, currentItem.set);
		}
		else
		{
			throw new Error("Unknown item type " + currentItem.type);
		}
	}
	
	/**
	 * @return
	 */
	public List<Item> getItemList()
	{
		return itemsInFile;
	}
}
