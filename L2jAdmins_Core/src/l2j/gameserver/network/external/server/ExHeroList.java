package l2j.gameserver.network.external.server;

import java.util.Collection;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.entity.Hero;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.network.AServerPacket;

/**
 * Format: (ch) d [SdSdSdd] d: size [ S: hero name d: hero class ID S: hero clan name d: hero clan crest id S: hero ally name d: hero Ally id d: count ]
 * @author -Wooden- Format from KenM Re-written by godson
 */
public class ExHeroList extends AServerPacket
{
	private final Collection<StatsSet> heroList;
	
	public ExHeroList()
	{
		heroList = Hero.getInstance().getHeroes().values();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x23);
		writeD(heroList.size());
		
		for (StatsSet hero : heroList)
		{
			writeS(hero.getString(Olympiad.CHAR_NAME));
			writeD(hero.getInteger(Olympiad.CLASS_ID));
			writeS(hero.getString(Hero.CLAN_NAME, ""));
			writeD(hero.getInteger(Hero.CLAN_CREST, 0));
			writeS(hero.getString(Hero.ALLY_NAME, ""));
			writeD(hero.getInteger(Hero.ALLY_CREST, 0));
			writeD(hero.getInteger(Hero.COUNT));
		}
	}
}
