package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * MagicEffectIcons format h (dhd)
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public class MagicEffectIcons extends AServerPacket
{
	private final List<Effect> effects;
	
	class Effect
	{
		int skillId;
		int level;
		int duration;
		
		public Effect(int skillId, int level, int duration)
		{
			this.skillId = skillId;
			this.level = level;
			this.duration = duration;
		}
	}
	
	public MagicEffectIcons()
	{
		effects = new ArrayList<>();
	}
	
	public void addEffect(int skillId, int level, int duration)
	{
		effects.add(new Effect(skillId, level, duration));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x7f);
		
		writeH(effects.size());
		
		for (Effect temp : effects)
		{
			writeD(temp.skillId);
			writeH(temp.level);
			
			if (temp.duration == -1)
			{
				writeD(-1);
			}
			else
			{
				writeD(temp.duration / 1000);
			}
		}
	}
}
