package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 * @author  godson
 */
public class ExOlympiadSpelledInfo extends AServerPacket
{
	private final L2PcInstance player;
	private final List<Effect> effects;
	
	class Effect
	{
		protected int skillId;
		protected int dat;
		protected int duration;
		
		public Effect(int pSkillId, int pDat, int pDuration)
		{
			skillId = pSkillId;
			dat = pDat;
			duration = pDuration;
		}
	}
	
	public ExOlympiadSpelledInfo(L2PcInstance player)
	{
		effects = new ArrayList<>();
		this.player = player;
	}
	
	public void addEffect(int skillId, int dat, int duration)
	{
		effects.add(new Effect(skillId, dat, duration));
	}
	
	@Override
	public void writeImpl()
	{
		if (player == null)
		{
			return;
		}
		
		writeC(0xfe);
		writeH(0x2a);
		writeD(player.getObjectId());
		writeD(effects.size());
		
		for (Effect temp : effects)
		{
			writeD(temp.skillId);
			writeH(temp.dat);
			writeD(temp.duration / 1000);
		}
	}
}
