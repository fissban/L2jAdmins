package l2j.gameserver.model.request;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.network.external.server.ConfirmDlg;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class PcRequestRevive
{
	private final L2PcInstance player;
	
	private boolean reviveRequested = false;
	private double revivePower = 0;
	private boolean revivePet = false;
	
	public PcRequestRevive(L2PcInstance player)
	{
		this.player = player;
	}
	
	public void reviveRequest(L2PcInstance reviver, Skill skill, boolean isPet)
	{
		if (reviveRequested)
		{
			if (revivePet == isPet)
			{
				reviver.sendPacket(SystemMessage.RES_HAS_ALREADY_BEEN_PROPOSED);
			}
			else
			{
				if (isPet)
				{
					reviver.sendPacket(SystemMessage.CANNOT_RES_PET2);
				}
				else
				{
					reviver.sendPacket(SystemMessage.MASTER_CANNOT_RES);
				}
			}
			return;
		}
		
		if ((isPet && (player.getPet() != null) && player.getPet().isDead()) || (!isPet && player.isDead()))
		{
			reviveRequested = true;
			revivePower = Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), reviver);
			revivePet = isPet;
			player.sendPacket(new ConfirmDlg(SystemMessage.RESSURECTION_REQUEST_BY_C1_FOR_S2_XP, reviver.getName()));
		}
	}
	
	public void reviveAnswer(int answer)
	{
		if ((!reviveRequested) || (!player.isDead() && !revivePet) || (revivePet && (player.getPet() != null) && !player.getPet().isDead()))
		{
			return;
		}
		
		if (answer == 1)
		{
			if (!revivePet)
			{
				if (revivePower != 0)
				{
					player.doRevive(revivePower);
				}
				else
				{
					player.doRevive();
				}
			}
			else if (player.getPet() != null)
			{
				if (revivePower != 0)
				{
					player.getPet().doRevive(revivePower);
				}
				else
				{
					player.getPet().doRevive();
				}
			}
		}
		
		removeReviving();
	}
	
	public boolean isReviveRequested()
	{
		return (reviveRequested);
	}
	
	public boolean isRevivingPet()
	{
		return revivePet;
	}
	
	public void removeReviving()
	{
		reviveRequested = false;
		revivePower = 0;
	}
}
