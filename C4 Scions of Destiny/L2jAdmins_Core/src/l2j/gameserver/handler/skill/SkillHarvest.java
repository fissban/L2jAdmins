package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author l3x, zarie
 */
public class SkillHarvest implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.HARVEST
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		var player = (L2PcInstance) activeChar;
		
		for (var object : skill.getTargetList(activeChar))
		{
			if (!(object instanceof L2MonsterInstance))
			{
				continue;
			}
			
			var manor = (L2MonsterInstance) object;
			
			if (player != manor.getSeeder())
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST));
				continue;
			}
			
			var send = false;
			var total = 0;
			var cropId = 0;
			
			// TODO: check items and amount of items player harvest
			if (manor.isSeeded())
			{
				if (calcSuccess(player, manor))
				{
					var items = manor.takeHarvest();
					if ((items != null) && (items.size() > 0))
					{
						for (var ritem : items)
						{
							cropId = ritem.getId(); // always got 1 type of crop as reward
							if (player.isInParty())
							{
								player.getParty().distributeItem(player, ritem, true, manor);
							}
							else
							{
								player.getInventory().addItem("Manor", ritem.getId(), ritem.getCount(), player, object);
								
								send = true;
								total += ritem.getCount();
							}
							manor.setSeeded(false);
						}
						
						if (send)
						{
							player.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_S1_S2).addNumber(total).addItemName(cropId));
							
							if (player.getParty() != null)
							{
								player.getParty().broadcastToPartyMembers(player, new SystemMessage(SystemMessage.C1_HARVESTED_S3_S2S).addString(player.getName()).addNumber(total).addItemName(cropId));
							}
						}
					}
				}
				else
				{
					player.sendPacket(SystemMessage.THE_HARVEST_HAS_FAILED);
				}
			}
			else
			{
				player.sendPacket(SystemMessage.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
	
	private boolean calcSuccess(L2PcInstance player, L2MonsterInstance target)
	{
		var basicSuccess = 100;
		var levelPlayer = player.getLevel();
		var levelTarget = target.getLevel();
		
		var diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		
		// apply penalty, target <=> player levels
		// 5% penalty for each level
		if (diff > 5)
		{
			basicSuccess -= (diff - 5) * 5;
		}
		
		// success rate cant be less than 1%
		if (basicSuccess < 1)
		{
			basicSuccess = 1;
		}
		
		if (Rnd.nextInt(99) < basicSuccess)
		{
			return true;
		}
		
		return false;
	}
}
