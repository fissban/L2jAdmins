package l2j.gameserver.model.zone.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	protected static final Logger LOG = Logger.getLogger(EffectZone.class.getName());
	
	private int initialDelay;
	private int reuse;
	public boolean enabled;
	protected boolean _bypassConditions;
	protected volatile Map<Integer, Integer> _skills;
	private Future<?> task;
	
	public EffectZone(int id)
	{
		super(id);
		initialDelay = 0;
		reuse = 30000;
		enabled = true;
		_bypassConditions = false;
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
		else if (name.equals("bypassSkillConditions"))
		{
			_bypassConditions = Boolean.parseBoolean(value);
		}
		else if (name.equals("skillIdLvl"))
		{
			String[] propertySplit = value.split(";");
			_skills = new ConcurrentHashMap<>(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split("-");
				if (skillSplit.length != 2)
				{
					LOG.log(Level.WARNING, "invalid config property -> skillsIdLvl" + skill, "\"");
				}
				else
				{
					try
					{
						_skills.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							LOG.log(Level.WARNING, "invalid config property -> skillsIdLvl" + skillSplit[0], "\"" + skillSplit[1]);
						}
					}
				}
			}
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
						task = ThreadPoolManager.scheduleAtFixedRate(new ApplySkill(), initialDelay, reuse);
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
	
	protected Skill getSkill(int skillId, int skillLvl)
	{
		return SkillData.getInstance().getSkill(skillId, skillLvl);
	}
	
	public void addSkill(int skillId, int skillLvL)
	{
		if (skillLvL < 1) // remove skill
		{
			removeSkill(skillId);
			return;
		}
		
		if (_skills == null)
		{
			synchronized (this)
			{
				if (_skills == null)
				{
					_skills = new ConcurrentHashMap<>(3);
				}
			}
		}
		_skills.put(skillId, skillLvL);
	}
	
	public void removeSkill(int skillId)
	{
		if (_skills != null)
		{
			_skills.remove(skillId);
		}
	}
	
	public void clearSkills()
	{
		if (_skills != null)
		{
			_skills.clear();
		}
	}
	
	public int getSkillLevel(int skillId)
	{
		final Map<Integer, Integer> skills = _skills;
		return skills != null ? skills.getOrDefault(skillId, 0) : 0;
	}
	
	private final class ApplySkill implements Runnable
	{
		protected ApplySkill()
		{
			if (_skills == null)
			{
				throw new IllegalStateException("No skills defined.");
			}
		}
		
		@Override
		public void run()
		{
			if (enabled)
			{
				for (L2Character temp : getCharacterList())
				{
					if ((temp != null) && !temp.isDead())
					{
						for (Entry<Integer, Integer> e : _skills.entrySet())
						{
							Skill skill = getSkill(e.getKey(), e.getValue());
							if ((skill != null) && (_bypassConditions || skill.checkCondition(temp, false)))
							{
								if (skill != null)
								{
									skill.getEffects(temp, temp);
								}
							}
						}
					}
				}
			}
		}
		
	}
	
}
