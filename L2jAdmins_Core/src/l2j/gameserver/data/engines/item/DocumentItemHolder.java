package l2j.gameserver.data.engines.item;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.items.Item;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class DocumentItemHolder
{
	// TODO pasar a private y crear metiodos GET y SET y aplicar la nomenclatura para las variables
	public int id;
	public Enum<?> type;
	public String name;
	public StatsSet set;
	public int currentLevel;
	public Item item;
}
