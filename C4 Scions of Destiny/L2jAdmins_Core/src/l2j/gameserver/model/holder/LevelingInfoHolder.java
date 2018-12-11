package l2j.gameserver.model.holder;

/**
 * This class stores Soul Crystal leveling infos related to NPCs, notably:
 * <ul>
 * <li>AbsorbCrystalType which can be LAST_HIT, FULL_PARTY or PARTY_ONE_RANDOM ;</li>
 * <li>If the item cast on monster is required or not ;</li>
 * <li>The chance of break (base 1000) ;</li>
 * <li>The chance of success (base 1000) ;</li>
 * <li>The list of allowed crystals levels.</li>
 * </ul>
 */
public final class LevelingInfoHolder
{
	public enum AbsorbCrystalType
	{
		LAST_HIT,
		FULL_PARTY,
		PARTY_ONE_RANDOM
	}
	
	private final AbsorbCrystalType absorbCrystalType;
	private final boolean skillRequired;
	private final int chanceStage;
	private final int chanceBreak;
	private final int[] levelList;
	
	public LevelingInfoHolder(String absorbType, boolean skill, int chanceStage, int chanceBreak, String levelList)
	{
		absorbCrystalType = AbsorbCrystalType.valueOf(absorbType);
		skillRequired = skill;
		this.chanceStage = chanceStage;
		this.chanceBreak = chanceBreak;
		String[] list = levelList.split(";");
		this.levelList = new int[list.length];
		for (int i = 0; i < this.levelList.length; i++)
		{
			this.levelList[i] = Integer.parseInt(list[i]);
		}
	}
	
	public AbsorbCrystalType getAbsorbCrystalType()
	{
		return absorbCrystalType;
	}
	
	public boolean isSkillRequired()
	{
		return skillRequired;
	}
	
	public int getChanceStage()
	{
		return chanceStage;
	}
	
	public int getChanceBreak()
	{
		return chanceBreak;
	}
	
	public int[] getLevelList()
	{
		return levelList;
	}
}
