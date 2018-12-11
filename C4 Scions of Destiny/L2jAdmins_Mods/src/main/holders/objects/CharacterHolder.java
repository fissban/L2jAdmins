package main.holders.objects;

import main.data.ObjectData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;

/**
 * @author fissban
 */
public class CharacterHolder extends ObjectHolder
{
	public CharacterHolder(L2Character character)
	{
		super(character);
	}
	
	@Override
	public L2Character getInstance()
	{
		return (L2Character) super.getInstance();
	}
	
	// TODO
	// cuando contruyamos PlayableHolder, SummonHolder etc etc podemos expandir este sistema.
	public PlayerHolder getActingPlayer()
	{
		var owner = getInstance().getActingPlayer();
		if (owner != null)
		{
			return ObjectData.get(PlayerHolder.class, owner.getObjectId());
		}
		return null;
	}
	
	/**
	 * Obtengo el team del objecto
	 * @return -> <b>TeamType</b>
	 */
	public TeamType getTeam()
	{
		return getInstance().getTeam();
	}
	
	/**
	 * Define el team del personaje:
	 * @param team -> <b>TeamType</b>
	 */
	public void setTeam(TeamType team)
	{
		getInstance().setTeam(team);
		
		if (getInstance() != null && getInstance() instanceof L2PcInstance)
		{
			((L2PcInstance) getInstance()).broadcastUserInfo();
		}
	}
}
