package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;

public class L2RiftInvaderInstance extends L2MonsterInstance
{
	public L2RiftInvaderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2RiftInvaderInstance);
	}
}
