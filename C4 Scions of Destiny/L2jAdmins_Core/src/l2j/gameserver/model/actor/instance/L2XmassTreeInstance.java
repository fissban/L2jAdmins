package l2j.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.util.Rnd;

/**
 * @author Drunkard Zabb0x Lets drink2code!
 */
public class L2XmassTreeInstance extends L2Npc
{
	private ScheduledFuture<?> aiTask;
	
	public L2XmassTreeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2XmassTreeInstance);
		aiTask = ThreadPoolManager.scheduleAtFixedRate(new XmassAI(this), 3000, 3000);
	}
	
	class XmassAI implements Runnable
	{
		L2XmassTreeInstance caster;
		
		protected XmassAI(L2XmassTreeInstance caster)
		{
			this.caster = caster;
		}
		
		@Override
		public void run()
		{
			getKnownList().getObjectType(L2PcInstance.class).forEach(player -> handleCast(player, (4262 + Rnd.nextInt(3))));
		}
		
		private boolean handleCast(L2PcInstance player, int skillId)
		{
			Skill skill = SkillData.getInstance().getSkill(skillId, 1);
			
			if (player.getEffect(skill) == null)
			{
				setTarget(player);
				doCast(skill);
				
				broadcastPacket(new MagicSkillUse(caster, player, skill.getId(), 1, skill.getHitTime(), 0));
				return true;
			}
			
			return false;
		}
	}
	
	@Override
	public void deleteMe()
	{
		if (aiTask != null)
		{
			aiTask.cancel(true);
			aiTask = null;
		}
		
		super.deleteMe();
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}
