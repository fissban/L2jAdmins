package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.zone.enums.ZoneType;

public class L2SiegeSummonInstance extends L2SummonInstance
{
	public L2SiegeSummonInstance(int objectId, NpcTemplate template, L2PcInstance owner, Skill skill)
	{
		super(objectId, template, owner, skill);
		
		setInstanceType(InstanceType.L2SiegeSummonInstance);
		if (skill.getId() == 13)
		{
			isSiegeGolem = true;
		}
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (!isInsideZone(ZoneType.SIEGE))
		{
			unSummon();
		}
	}
}
