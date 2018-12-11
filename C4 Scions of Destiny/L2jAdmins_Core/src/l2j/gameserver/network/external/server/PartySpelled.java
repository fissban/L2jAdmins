package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartySpelled extends AServerPacket
{
	private final List<Effect> effects;
	private final L2Character cha;
	
	class Effect
	{
		int skillId;
		int dat;
		int duration;
		
		public Effect(int pSkillId, int pDat, int pDuration)
		{
			skillId = pSkillId;
			dat = pDat;
			duration = pDuration;
		}
	}
	
	public PartySpelled(L2Character cha)
	{
		effects = new ArrayList<>();
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		if (cha == null)
		{
			return;
		}
		
		writeC(0xee);
		writeD(cha instanceof L2SummonInstance ? 2 : cha instanceof L2PetInstance ? 1 : 0);
		writeD(cha.getObjectId());
		writeD(effects.size());
		for (Effect temp : effects)
		{
			writeD(temp.skillId);
			writeH(temp.dat);
			writeD(temp.duration / 1000);
		}
	}
	
	public void addPartySpelledEffect(int skillId, int dat, int duration)
	{
		// C4 doesn't support more than 20 effects
		// in party window, so limiting them makes no diff
		// in fact, more effects could cause client errors :)
		if (effects.size() < 20)
		{
			effects.add(new Effect(skillId, dat, duration));
		}
	}
}
