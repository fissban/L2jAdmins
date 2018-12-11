package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * A mother-trees zone
 * @author durgus
 */
public class MotherTreeZone extends Zone
{
	public MotherTreeZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			
			if (player.isInParty())
			{
				for (L2PcInstance member : player.getParty().getMembers())
				{
					if (member.getRace() != Race.ELF)
					{
						return;
					}
				}
			}
			
			player.setInsideZone(ZoneType.MOTHERTREE, true);
			player.sendPacket(SystemMessage.ENTER_SHADOW_MOTHER_TREE);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if ((character instanceof L2PcInstance) && character.isInsideZone(ZoneType.MOTHERTREE))
		{
			character.setInsideZone(ZoneType.MOTHERTREE, false);
			((L2PcInstance) character).sendPacket(SystemMessage.EXIT_SHADOW_MOTHER_TREE);
		}
	}
}
