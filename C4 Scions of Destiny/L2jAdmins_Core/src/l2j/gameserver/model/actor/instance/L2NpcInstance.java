package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;

public class L2NpcInstance extends L2Npc
{
	public L2NpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2NpcInstance);
	}
}
