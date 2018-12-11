package l2j.gameserver.model.holder;

import l2j.gameserver.model.items.ItemHenna;

/**
 * This class represents a Non-Player-Character in the world. it can be a monster or a friendly character. it also uses a template to fetch some static values. the templates are hardcoded in the client, so we can rely on them.
 * @version $Revision$ $Date$
 */
public class HennaHolder
{
	private final ItemHenna template;
	private int symbolId;
	private int itemIdDye;
	private int price;
	private int cancel_fee;
	private int statINT;
	private int statSTR;
	private int statCON;
	private int statMEN;
	private int statDEX;
	private int statWIT;
	private int amountDyeRequire;
	
	public HennaHolder(ItemHenna template)
	{
		this.template = template;
		symbolId = template.getSymbolId();
		itemIdDye = template.getDyeId();
		amountDyeRequire = template.getAmountDyeRequire();
		price = template.getPrice();
		cancel_fee = template.getCancelFee();
		statINT = template.getStatINT();
		statSTR = template.getStatSTR();
		statCON = template.getStatCON();
		statMEN = template.getStatMEN();
		statDEX = template.getStatDEX();
		statWIT = template.getStatWIT();
	}
	
	public String getName()
	{
		String res = "";
		if (statINT > 0)
		{
			res = res + "INT +" + statINT;
		}
		else if (statSTR > 0)
		{
			res = res + "STR +" + statSTR;
		}
		else if (statCON > 0)
		{
			res = res + "CON +" + statCON;
		}
		else if (statMEN > 0)
		{
			res = res + "MEN +" + statMEN;
		}
		else if (statDEX > 0)
		{
			res = res + "DEX +" + statDEX;
		}
		else if (statWIT > 0)
		{
			res = res + "WIT +" + statWIT;
		}
		
		if (statINT < 0)
		{
			res = res + ", INT " + statINT;
		}
		else if (statSTR < 0)
		{
			res = res + ", STR " + statSTR;
		}
		else if (statCON < 0)
		{
			res = res + ", CON " + statCON;
		}
		else if (statMEN < 0)
		{
			res = res + ", MEN " + statMEN;
		}
		else if (statDEX < 0)
		{
			res = res + ", DEX " + statDEX;
		}
		else if (statWIT < 0)
		{
			res = res + ", WIT " + statWIT;
		}
		
		return res;
	}
	
	public ItemHenna getTemplate()
	{
		return template;
	}
	
	public int getSymbolId()
	{
		return symbolId;
	}
	
	public void setSymbolId(int SymbolId)
	{
		symbolId = SymbolId;
	}
	
	public int getItemIdDye()
	{
		return itemIdDye;
	}
	
	public void setItemIdDye(int ItemIdDye)
	{
		itemIdDye = ItemIdDye;
	}
	
	public int getAmountDyeRequire()
	{
		return amountDyeRequire;
	}
	
	public void setAmountDyeRequire(int AmountDyeRequire)
	{
		amountDyeRequire = AmountDyeRequire;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public void setPrice(int Price)
	{
		price = Price;
	}
	
	public int getStatINT()
	{
		return statINT;
	}
	
	public void setStatINT(int StatINT)
	{
		statINT = StatINT;
	}
	
	public int getStatSTR()
	{
		return statSTR;
	}
	
	public void setStatSTR(int StatSTR)
	{
		statSTR = StatSTR;
	}
	
	public int getStatCON()
	{
		return statCON;
	}
	
	public void setStatCON(int StatCON)
	{
		statCON = StatCON;
	}
	
	public int getStatMEN()
	{
		return statMEN;
	}
	
	public void setStatMEN(int StatMEN)
	{
		statMEN = StatMEN;
	}
	
	public int getStatDEX()
	{
		return statDEX;
	}
	
	public void setStatDEX(int StatDEX)
	{
		statDEX = StatDEX;
	}
	
	public int getStatWIT()
	{
		return statWIT;
	}
	
	public void setStatWIT(int StatWIT)
	{
		statWIT = StatWIT;
	}
	
	public int getCancelFee()
	{
		return cancel_fee;
	}
	
	public void setCancelFee(int fee)
	{
		cancel_fee = fee;
	}
}
