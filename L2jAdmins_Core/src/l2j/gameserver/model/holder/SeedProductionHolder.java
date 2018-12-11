package l2j.gameserver.model.holder;

/**
 * @author fissban
 */
public class SeedProductionHolder
{
	private final int seedId;
	private int residual;
	private final int price;
	private int sales;
	
	public SeedProductionHolder(int id)
	{
		seedId = id;
		sales = 0;
		price = 0;
		sales = 0;
	}
	
	public SeedProductionHolder(int id, int amount, int price, int sales)
	{
		seedId = id;
		residual = amount;
		this.price = price;
		this.sales = sales;
	}
	
	public int getId()
	{
		return seedId;
	}
	
	public int getCanProduce()
	{
		return residual;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getStartProduce()
	{
		return sales;
	}
	
	public void setCanProduce(int amount)
	{
		residual = amount;
	}
}
