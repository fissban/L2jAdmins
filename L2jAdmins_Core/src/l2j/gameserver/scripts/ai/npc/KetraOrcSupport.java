package l2j.gameserver.scripts.ai.npc;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.WareHouseWithdrawalList;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class KetraOrcSupport extends Script
{
	private static final int KADUN = 8370; // Hierarch
	private static final int WAHKAN = 8371; // Messenger
	private static final int ASEFA = 8372; // Soul Guide
	private static final int ATAN = 8373; // Grocer
	private static final int JAFF = 8374; // Warehouse Keeper
	private static final int JUMARA = 8375; // Trader
	private static final int KURFA = 8376; // Gate Keeper
	
	private static final int HORN = 7186;
	
	private static final List<GetSupport> BUFFS = new ArrayList<>();
	{
		BUFFS.add(new GetSupport(4359, 1, 2)); // Focus: Requires 2 Buffalo Horns
		BUFFS.add(new GetSupport(4360, 1, 2)); // Death Whisper: Requires 2 Buffalo Horns
		BUFFS.add(new GetSupport(4345, 1, 3)); // Might: Requires 3 Buffalo Horns
		BUFFS.add(new GetSupport(4355, 1, 3)); // Acumen: Requires 3 Buffalo Horns
		BUFFS.add(new GetSupport(4352, 1, 3)); // Berserker: Requires 3 Buffalo Horns
		BUFFS.add(new GetSupport(4354, 1, 3)); // Vampiric Rage: Requires 3 Buffalo Horns
		BUFFS.add(new GetSupport(4356, 1, 6)); // Empower: Requires 6 Buffalo Horns
		BUFFS.add(new GetSupport(4357, 1, 6)); // Haste: Requires 6 Buffalo Horns
	}
	
	private static final String HTML_PATH = "data/html/ketraOrcSupport/";
	
	public KetraOrcSupport()
	{
		super(-1, "ai/npc");
		
		addFirstTalkId(KADUN, WAHKAN, ASEFA, ATAN, JAFF, JUMARA, KURFA);
		addTalkId(ASEFA, JAFF, KURFA);
		addStartNpc(JAFF, KURFA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (Util.isDigit(event))
		{
			final int eventId = Integer.parseInt(event);
			if ((eventId >= 0) && (eventId <= 7))
			{
				if (st.getItemsCount(HORN) >= BUFFS.get(eventId - 1).getPrice())
				{
					st.takeItems(HORN, BUFFS.get(eventId - 1).getPrice());
					npc.setTarget(player);
					npc.doCast(BUFFS.get(eventId - 1).getBuff());
					npc.setCurrentHpMp(npc.getStat().getMaxHp(), npc.getStat().getMaxMp());
					htmltext = "8372-4.htm";
				}
			}
		}
		else if (event.equals("Withdraw"))
		{
			if (player.getWarehouse().getSize() == 0)
			{
				htmltext = "8374-0.htm";
			}
			else
			{
				player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.PRIVATE));
				// Send a Server->Client packet ActionFailed to the L2PcInstance
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (event.equals("Teleport"))
		{
			switch (player.getAllianceWithVarkaKetra())
			{
				case 4:
					htmltext = "8376-4.htm";
					break;
				case 5:
					htmltext = "8376-5.htm";
					break;
			}
		}
		
		return HTML_PATH + htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			st = newState(player);
		}
		
		final int allianceLevel = player.getAllianceWithVarkaKetra();
		
		switch (npc.getId())
		{
			case KADUN:
				if (allianceLevel > 0)
				{
					htmltext = "8370-friend.htm";
				}
				else
				{
					htmltext = "8370-no.htm";
				}
				break;
			
			case WAHKAN:
				if (allianceLevel > 0)
				{
					htmltext = "8371-friend.htm";
				}
				else
				{
					htmltext = "8371-no.htm";
				}
				break;
			case ASEFA:
				st.setState(ScriptStateType.STARTED);
				if (allianceLevel < 1)
				{
					htmltext = "8372-3.htm";
				}
				else if ((allianceLevel < 3) && (allianceLevel > 0))
				{
					htmltext = "8372-1.htm";
				}
				else if (allianceLevel > 2)
				{
					if (st.hasItems(HORN))
					{
						htmltext = "8372-4.htm";
					}
					else
					{
						htmltext = "8372-2.htm";
					}
				}
				break;
			case ATAN:
				if (player.getKarma() >= 1)
				{
					htmltext = "8373-pk.htm";
				}
				else if (allianceLevel <= 0)
				{
					htmltext = "8373-no.htm";
				}
				else if ((allianceLevel == 1) || (allianceLevel == 2))
				{
					htmltext = "8373-1.htm";
				}
				else
				{
					htmltext = "8373-2.htm";
				}
				break;
			case JAFF:
				switch (allianceLevel)
				{
					case 1:
						htmltext = "8374-1.htm";
						break;
					case 2:
					case 3:
						htmltext = "8374-2.htm";
						break;
					default:
						if (allianceLevel <= 0)
						{
							htmltext = "8374-no.htm";
						}
						else if (player.getWarehouse().getSize() == 0)
						{
							htmltext = "8374-3.htm";
						}
						else
						{
							htmltext = "8374-4.htm";
						}
						break;
				}
				break;
			case JUMARA:
				switch (allianceLevel)
				{
					case 2:
						htmltext = "8375-1.htm";
						break;
					case 3:
					case 4:
						htmltext = "8375-2.htm";
						break;
					case 5:
						htmltext = "8375-3.htm";
						break;
					default:
						htmltext = "8375-no.htm";
						break;
				}
				break;
			case KURFA:
				if (allianceLevel <= 0)
				{
					htmltext = "8376-no.htm";
				}
				else if ((allianceLevel > 0) && (allianceLevel < 4))
				{
					htmltext = "8376-1.htm";
				}
				else if (allianceLevel == 4)
				{
					htmltext = "8376-2.htm";
				}
				else
				{
					htmltext = "8376-3.htm";
				}
				break;
		}
		
		return HTML_PATH + htmltext;
	}
	
	private class GetSupport
	{
		private final SkillHolder buff;
		private final int horn;
		
		public GetSupport(int skillLvl, int skillId, int horn)
		{
			buff = new SkillHolder(skillLvl, skillId);
			this.horn = horn;
		}
		
		public Skill getBuff()
		{
			return buff.getSkill();
		}
		
		public int getPrice()
		{
			return horn;
		}
	}
}
