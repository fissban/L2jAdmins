package main.holders.objects;

import l2j.gameserver.model.actor.L2Npc;
import main.enums.ChampionType;

/**
 * @author fissban
 */
public class NpcHolder extends CharacterHolder
{
	public NpcHolder(L2Npc npc)
	{
		super(npc);
	}
	
	/**
	 * Obtain the Npc instance
	 * @return -> Npc
	 */
	@Override
	public L2Npc getInstance()
	{
		return (L2Npc) super.getInstance();
	}
	
	public int getId()
	{
		return getInstance().getId();
	}
	
	// XXX CHAMPIONS ---------------------------------------------------------------------------------------------------
	
	private ChampionType championType = ChampionType.NONE;
	
	public boolean isChampion()
	{
		return championType != ChampionType.NONE;
	}
	
	public void setChampionType(ChampionType championType)
	{
		this.championType = championType;
	}
	
	public ChampionType getChampionType()
	{
		return championType;
	}
}
