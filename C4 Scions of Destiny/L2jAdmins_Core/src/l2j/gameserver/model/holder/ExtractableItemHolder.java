package l2j.gameserver.model.holder;

import java.util.List;

/**
 * @author -Nemesiss-
 */
public class ExtractableItemHolder
{
	private final int itemId;
	private final List<ExtractableProductItemHolder> products;
	
	public ExtractableItemHolder(int itemId, List<ExtractableProductItemHolder> products)
	{
		this.itemId = itemId;
		this.products = products;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public List<ExtractableProductItemHolder> getProductItems()
	{
		return products;
	}
}
