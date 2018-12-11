package l2j.gameserver.network.external.server;

import java.util.List;
import java.util.stream.Collectors;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TimeStampHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author KenM
 */
public class SkillCoolTime extends AServerPacket
{
	public List<TimeStampHolder> reuseTimeStamps;
	
	public SkillCoolTime(L2PcInstance cha)
	{
		reuseTimeStamps = cha.getReuseTimeStamps().stream().filter(r -> r.hasNotPassed()).collect(Collectors.toList());
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xc1);
		writeD(reuseTimeStamps.size()); // list size
		for (TimeStampHolder ts : reuseTimeStamps)
		{
			writeD(ts.getSkill().getId());
			writeD(ts.getSkill().getLevel());
			writeD((int) ts.getReuse() / 1000);
			writeD((int) ts.getRemaining() / 1000);
		}
	}
}
