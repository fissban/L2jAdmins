package l2j.gameserver.data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.task.continuous.GameTimeTaskManager;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * This class load, holds and calculates the hit condition bonuses.
 * @author Nik
 */
public final class HitConditionBonusData extends XmlParser
{
	private int frontBonus = 0;
	private int sideBonus = 0;
	private int backBonus = 0;
	private int highBonus = 0;
	private int lowBonus = 0;
	private int darkBonus = 0;
	private int rainBonus = 0;
	
	@Override
	public void load()
	{
		loadFile("data/xml/stats/hitConditionBonus.xml");
		UtilPrint.result("HitConditionBonusData", "Loaded Hit Condition bonuses", "");
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes())
		{
			NamedNodeMap attrs = n.getAttributes();
			switch (n.getNodeName())
			{
				case "front":
					frontBonus = parseInt(attrs, "val");
					break;
				case "side":
					sideBonus = parseInt(attrs, "val");
					break;
				case "back":
					backBonus = parseInt(attrs, "val");
					break;
				case "high":
					highBonus = parseInt(attrs, "val");
					break;
				case "low":
					lowBonus = parseInt(attrs, "val");
					break;
				case "dark":
					darkBonus = parseInt(attrs, "val");
					break;
				case "rain":
					rainBonus = parseInt(attrs, "val");
					break;
			}
		}
	}
	
	/**
	 * Gets the condition bonus.
	 * @param  attacker the attacking character.
	 * @param  target   the attacked character.
	 * @return          the bonus of the attacker against the target.
	 */
	public double getConditionBonus(L2Character attacker, L2Character target)
	{
		double mod = 100;
		
		// Get high or low bonus
		if ((attacker.getZ() - target.getZ()) > 50)
		{
			mod += highBonus;
		}
		else if ((attacker.getZ() - target.getZ()) < -50)
		{
			mod += lowBonus;
		}
		
		// Get weather bonus
		if (GameTimeTaskManager.getInstance().isNight())
		{
			mod += darkBonus;
			// else if () No rain support yet.
			// chance += hitConditionBonus.rainBonus;
		}
		
		// Get side bonus
		if (attacker.isBehindTarget())
		{
			mod += backBonus;
		}
		else if (attacker.isInFrontOfTarget())
		{
			mod += frontBonus;
		}
		else
		{
			mod += sideBonus;
		}
		
		// If (mod / 100) is less than 0, return 0, because we can't lower more than 100%.
		return Math.max(mod / 100, 0);
	}
	
	/**
	 * Gets the single instance of HitConditionBonus.
	 * @return single instance of HitConditionBonus
	 */
	public static HitConditionBonusData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HitConditionBonusData INSTANCE = new HitConditionBonusData();
	}
}
