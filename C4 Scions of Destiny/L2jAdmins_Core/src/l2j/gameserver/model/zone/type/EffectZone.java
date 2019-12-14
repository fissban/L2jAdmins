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
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
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
	protected Map<Integer, Integer> skills;
	private Future<?> task;
	
	public EffectZone(int id)
	{
		super(id);
		initialDelay = 0;
		reuse = 30000;
		enabled = true;
		setTargetType(InstanceType.L2Character);// default all chars
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
		else if (name.equals("SkillIdLvl"))
		{
			try
			{
				skills = new ConcurrentHashMap<>();
				
				for (String skill : value.trim().split(";"))
				{
					var id = Integer.parseInt(skill.split("-")[0]);
					var lvl = Integer.parseInt(skill.split("-")[1]);
					
					skills.put(id, lvl);
				}
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "length: invalid config property -> SkillIdLvl: " + value, "\"");
				e.printStackTrace();
			}
		}
		else if (name.equals("TargetClass"))
		{
			setTargetType(Enum.valueOf(InstanceType.class, value));
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
			
			// check if affected object type
			if (!character.isInstanceTypes(getTargetType()))
			{
				return;
			}
			
			System.out.println("character: " + character.getName() + " enter: " + getId());
			
			if (task == null)
			{
				task = ThreadPoolManager.scheduleAtFixedRate(() -> applySkill(), initialDelay, reuse);
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
		
		if (skills == null)
		{
			skills = new ConcurrentHashMap<>(2);
		}
		skills.put(skillId, skillLvL);
	}
	
	public void removeSkill(int skillId)
	{
		if (skills != null)
		{
			skills.remove(skillId);
		}
	}
	
	public void clearSkills()
	{
		if (skills != null)
		{
			skills.clear();
		}
	}
	
	public int getSkillLevel(int skillId)
	{
		return skills != null ? skills.getOrDefault(skillId, 0) : 0;
	}
	
	public void applySkill()
	{
		if (enabled)
		{
			for (L2Character cha : getCharacterList())
			{
				if ((cha != null) && !cha.isDead())
				{
					for (Entry<Integer, Integer> e : skills.entrySet())
					{
						Skill sk = getSkill(e.getKey(), e.getValue());
						if (sk != null && cha.getEffect(sk) != null)
						{
							sk.getEffects(cha, cha);
						}
					}
				}
			}
		}
	}
}
