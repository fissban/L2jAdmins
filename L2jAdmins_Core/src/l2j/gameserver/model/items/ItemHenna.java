package l2j.gameserver.model.items;

import l2j.gameserver.model.StatsSet;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class ItemHenna
{
	private final int symbolId;
	private final int dye;
	private final int price;
	private final int cancel_fee;
	private final int amount;
	private final int statINT;
	private final int statSTR;
	private final int statCON;
	private final int statMEN;
	private final int statDEX;
	private final int statWIT;
	
	public ItemHenna(StatsSet set)
	{
		symbolId = set.getInteger("symbol_id");
		dye = set.getInteger("dye");
		price = set.getInteger("price");
		cancel_fee = set.getInteger("cancel_fee");
		amount = set.getInteger("amount");
		statINT = set.getInteger("stat_INT");
		statSTR = set.getInteger("stat_STR");
		statCON = set.getInteger("stat_CON");
		statMEN = set.getInteger("stat_MEN");
		statDEX = set.getInteger("stat_DEX");
		statWIT = set.getInteger("stat_WIT");
	}
	
	public int getSymbolId()
	{
		return symbolId;
	}
	
	public int getDyeId()
	{
		return dye;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getCancelFee()
	{
		return cancel_fee;
	}
	
	public int getAmountDyeRequire()
	{
		return amount;
	}
	
	public int getStatINT()
	{
		return statINT;
	}
	
	public int getStatSTR()
	{
		return statSTR;
	}
	
	public int getStatCON()
	{
		return statCON;
	}
	
	public int getStatMEN()
	{
		return statMEN;
	}
	
	public int getStatDEX()
	{
		return statDEX;
	}
	
	public int getStatWIT()
	{
		return statWIT;
	}
}
