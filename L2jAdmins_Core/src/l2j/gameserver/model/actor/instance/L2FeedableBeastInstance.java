package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;

// This class is here mostly for convinience and for avoidance of hardcoded IDs.
// It refers to Beast (mobs) that can be attacked but can also be fed
// For example, the Beast Farm's Alpen Buffalo.
// This class is only trully used by the handlers in order to check the correctness
// of the target. However, no additional tasks are needed, since they are all
// handled by scripted AI.
public class L2FeedableBeastInstance extends L2MonsterInstance
{
	public L2FeedableBeastInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2FeedableBeastInstance);
	}
}
