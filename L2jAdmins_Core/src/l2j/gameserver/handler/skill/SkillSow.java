package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.data.ManorData;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author l3x
 */
public class SkillSow implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SOW
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
		
		for (var object : targets)
		{
			if (!(object instanceof L2MonsterInstance))
			{
				continue;
			}
			
			var monster = (L2MonsterInstance) object;
			
			if (monster.isSeeded() || monster.isDead() || (monster.getSeeder() != player) || (monster.getSeedType() == 0))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			var item = player.getInventory().getItemById(monster.getSeedType());
			// Consuming used seed
			player.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
			
			SystemMessage sm = null;
			if (calcSuccess(player, monster, monster.getSeedType()))
			{
				player.playSound(PlaySoundType.SYS_SOW_SUCCES);
				monster.setSeeded();
				sm = new SystemMessage(SystemMessage.THE_SEED_WAS_SUCCESSFULLY_SOWN);
			}
			else
			{
				sm = new SystemMessage(SystemMessage.THE_SEED_WAS_NOT_SOWN);
			}
			
			if (player.getParty() == null)
			{
				player.sendPacket(sm);
			}
			else
			{
				player.getParty().broadcastToPartyMembers(sm);
			}
			
			// TODO: Mob should not aggro on player, this way doesn't work really nice
			monster.getAI().setIntention(CtrlIntentionType.IDLE);
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
	
	private static boolean calcSuccess(L2PcInstance player, L2MonsterInstance target, int seedId)
	{
		// TODO: check all the chances
		var basicSuccess = (ManorData.getInstance().isAlternative(seedId) ? 20 : 90);
		var minlevelSeed = ManorData.getInstance().getSeedMinLevel(seedId);
		var maxlevelSeed = ManorData.getInstance().getSeedMaxLevel(seedId);
		
		var levelPlayer = player.getLevel(); // Attacker Level
		var levelTarget = target.getLevel(); // target Level
		
		// 5% decrease in chance if player level
		// is more then +/- 5 levels to seed's_ level
		if (levelTarget < minlevelSeed)
		{
			basicSuccess -= 5;
		}
		if (levelTarget > maxlevelSeed)
		{
			basicSuccess -= 5;
		}
		
		// 5% decrease in chance if player level
		// is more than +/- 5 levels to target's_ level
		var diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		
		if (diff > 5)
		{
			basicSuccess -= 5 * (diff - 5);
		}
		
		// Chance can't be less than 1%
		if (basicSuccess < 1)
		{
			basicSuccess = 1;
		}
		
		return (Rnd.nextInt(99) < basicSuccess);
	}
}
