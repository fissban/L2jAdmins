package l2j.gameserver.model.holder;

/**
 * @author fissban
 */
public class CropProcureHolder
{
	private final int cropId;
	private int buyResidual;
	private final int rewardType;
	private final int buy;
	private final int price;
	
	public CropProcureHolder(int id)
	{
		cropId = id;
		buyResidual = 0;
		rewardType = 0;
		buy = 0;
		price = 0;
	}
	
	public CropProcureHolder(int id, int amount, int type, int buy, int price)
	{
		cropId = id;
		buyResidual = amount;
		rewardType = type;
		this.buy = buy;
		this.price = price;
	}
	
	public int getReward()
	{
		return rewardType;
	}
	
	public int getId()
	{
		return cropId;
	}
	
	public int getAmount()
	{
		return buyResidual;
	}
	
	public int getStartAmount()
	{
		return buy;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public void setAmount(int amount)
	{
		buyResidual = amount;
	}
}
