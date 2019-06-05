package l2j.gameserver.model.zone.type;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.EtcStatusUpdate;

/**
 * Effect zones give entering players a special effect
 * @author durgus
 */
public class EffectZone extends Zone
{
	private int initialDelay;
	private int reuse;
	private boolean enabled;
	private int skillId;
	private int skillLevel;
	private Future<?> task;
	
	public EffectZone(int id)
	{
		super(id);
		initialDelay = 0;
		reuse = 30000;
		enabled = true;
		skillLevel = 1;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("InitialDelay"))
		{
			initialDelay = Integer.parseInt(value);
		}
		else if (name.equals("Reuse"))
		{
			reuse = Integer.parseInt(value);
		}
		else if (name.equals("EnabledByDefault"))
		{
			enabled = Boolean.parseBoolean(value);
		}
		else if (name.equals("SkillId"))
		{
			skillId = Integer.parseInt(value);
		}
		else if (name.equals("SkillLevel"))
		{
			skillLevel = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			if (!enabled)
			{
				return;
			}
			
			if (task == null)
			{
				synchronized (this)
				{
					if (task == null)
					{
						task = ThreadPoolManager.scheduleAtFixedRate(() ->
						{
							for (L2Character cha : characterList.values())
							{
								if (cha == null)
								{
									continue;
								}
								
								// ignore if character get effect
								if (cha.getEffect(skillId) != null)
								{
									continue;
								}
								
								Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
								
								if (skill != null)
								{
									skill.getEffects(cha, cha);
								}
							}
						}, initialDelay, reuse);
					}
				}
			}
			
			character.setInsideZone(ZoneType.EFFECT, true);
			character.sendPacket(new EtcStatusUpdate((L2PcInstance) character));
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (characterList.isEmpty() && (task != null))
		{
			task.cancel(false);
			task = null;
		}
		
		if (character instanceof L2PcInstance)
		{
			if (!enabled)
			{
				return;
			}
			
			character.setInsideZone(ZoneType.EFFECT, false);
			character.sendPacket(new EtcStatusUpdate((L2PcInstance) character));
		}
	}
}
