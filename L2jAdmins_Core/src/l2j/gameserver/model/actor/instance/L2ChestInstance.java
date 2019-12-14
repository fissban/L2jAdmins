package l2j.gameserver.model.actor.instance;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.util.Rnd;

/**
 * @author Julian This class manages all chest.
 */
public final class L2ChestInstance extends L2MonsterInstance
{
	private volatile boolean isInteracted;
	private volatile boolean specialDrop;
	
	public L2ChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2ChestInstance);
		isInteracted = false;
		specialDrop = false;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		isInteracted = false;
		specialDrop = false;
		
		setMustRewardExpSp(true);
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	public synchronized boolean isInteracted()
	{
		return isInteracted;
	}
	
	public synchronized void setInteracted()
	{
		isInteracted = true;
	}
	
	public synchronized boolean isSpecialDrop()
	{
		return specialDrop;
	}
	
	public synchronized void setSpecialDrop()
	{
		specialDrop = true;
	}
	
	@Override
	public void doItemDrop(NpcTemplate npcTemplate, L2Character lastAttacker)
	{
		int id = getTemplate().getId();
		
		if (specialDrop)
		{
			if ((id >= 1801) && (id <= 1822))
			{
				id += 11299;
			}
			
			switch (id)
			{
				case 1671:
					id = 13213;
					break;
				case 1694:
					id = 13215;
					break;
				case 1717:
					id = 13217;
					break;
				case 1740:
					id = 13219;
					break;
				case 1763:
					id = 13221;
					break;
				case 1786:
					id = 13223;
					break;
			}
		}
		
		super.doItemDrop(NpcData.getInstance().getTemplate(id), lastAttacker);
		
	}
	
	// cast - trap chest
	public void chestTrap(L2Character player)
	{
		int trapSkillId = 0;
		int rnd = Rnd.get(120);
		
		if (getTemplate().getLevel() >= 61)
		{
			if (rnd >= 90)
			{
				trapSkillId = 4139; // explosion
			}
			else if (rnd >= 50)
			{
				trapSkillId = 4118; // area paralysis
			}
			else if (rnd >= 20)
			{
				trapSkillId = 1167; // poison cloud
			}
			else
			{
				trapSkillId = 223; // sting
			}
		}
		else if (getTemplate().getLevel() >= 41)
		{
			if (rnd >= 90)
			{
				trapSkillId = 4139; // explosion
			}
			else if (rnd >= 60)
			{
				trapSkillId = 96; // bleed
			}
			else if (rnd >= 20)
			{
				trapSkillId = 1167; // poison cloud
			}
			else
			{
				trapSkillId = 4118; // area paralysis
			}
		}
		else if (getTemplate().getLevel() >= 21)
		{
			if (rnd >= 80)
			{
				trapSkillId = 4139; // explosion
			}
			else if (rnd >= 50)
			{
				trapSkillId = 96; // bleed
			}
			else if (rnd >= 20)
			{
				trapSkillId = 1167; // poison cloud
			}
			else
			{
				trapSkillId = 129; // poison
			}
		}
		else
		{
			if (rnd >= 80)
			{
				trapSkillId = 4139; // explosion
			}
			else if (rnd >= 50)
			{
				trapSkillId = 96; // bleed
			}
			else
			{
				trapSkillId = 129; // poison
			}
		}
		
		handleCast(player, trapSkillId);
	}
	
	private boolean handleCast(L2Character player, int skillId)
	{
		int skillLevel = 1;
		
		int lvl = getTemplate().getLevel();
		if ((lvl > 20) && (lvl <= 40))
		{
			skillLevel = 3;
		}
		else if ((lvl > 40) && (lvl <= 60))
		{
			skillLevel = 5;
		}
		else if (lvl > 60)
		{
			skillLevel = 6;
		}
		
		Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		
		if (player.getEffect(skill) == null)
		{
			skill.getEffects(this, player);
			broadcastPacket(new MagicSkillUse(this, player, skill.getId(), skillLevel, skill.getHitTime(), 0));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		if (super.isMovementDisabled())
		{
			return true;
		}
		if (isInteracted())
		{
			return false;
		}
		return true;
	}
}
