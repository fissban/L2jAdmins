package l2j.gameserver.network.external.server;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.multisell.MultisellHolder;
import l2j.gameserver.model.multisell.MultisellItemHolder;
import l2j.gameserver.model.multisell.ProductHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class MultiSellList extends AServerPacket
{
	private final int page, finished;
	private final MultisellHolder list;
	
	public MultiSellList(MultisellHolder list, int page, int finished)
	{
		this.list = list;
		this.page = page;
		this.finished = finished;
	}
	
	@Override
	public void writeImpl()
	{
		// [ddddd] [dchh] [hdhdh] [hhdh]
		
		writeC(0xd0);
		writeD(list.getListId()); // list id
		writeD(page); // page
		writeD(finished); // finished
		writeD(0x28); // size of pages
		writeD(list.getEntries().size()); // list length
		
		if (list != null)
		{
			for (MultisellItemHolder ent : list.getEntries())
			{
				writeD(ent.getEntryId());
				writeC(1);
				writeH(ent.getProducts().size());
				writeH(ent.getIngredients().size());
				
				for (ProductHolder i : ent.getProducts())
				{
					writeH(i.getItemId());
					writeD(ItemData.getInstance().getTemplate(i.getItemId()).getBodyPart().getMask());
					writeH(ItemData.getInstance().getTemplate(i.getItemId()).getType2().ordinal());
					writeD((int) i.getItemCount());
					writeH(i.getEnchantmentLevel()); // enchant lvl
				}
				
				for (ProductHolder i : ent.getIngredients())
				{
					writeH(i.getItemId()); // ID
					writeH(ItemData.getInstance().getTemplate(i.getItemId()).getType2().ordinal());
					writeD((int) i.getItemCount()); // Count
					writeH(i.getEnchantmentLevel()); // Enchant Level
				}
			}
		}
	}
}
