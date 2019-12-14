package l2j.gameserver.model.actor.manager.pc.fishing.task;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.actor.manager.pc.fishing.FishingHolder;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishAnimationType;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishLureType;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishModeType;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.ExFishingHpRegen;
import l2j.gameserver.network.external.server.ExFishingStartCombat;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class StartCombatForFish implements Runnable
{
	private L2PcInstance fisher;
	private int time;
	private int stop = 0;
	private int goodUse = 0;
	private FishAnimationType animation = FishAnimationType.NONE;
	private FishModeType mode;
	
	private Future<?> fishAItask;
	private boolean thinking;
	// Fish datas
	private final int fishId;
	private final int fishMaxHp;
	private int fishCurHP;
	private final double regenHp;
	
	public StartCombatForFish(L2PcInstance fisher, FishingHolder fish, FishLureType lureType)
	{
		this.fisher = fisher;
		
		fishMaxHp = fish.getHP();
		fishCurHP = fishMaxHp;
		regenHp = fish.getHpRegen();
		fishId = fish.getId();
		time = fish.getCombatTime() / 1000;
		mode = Rnd.get(100) >= 80 ? FishModeType.FIGHTING : FishModeType.RESTING;
		fisher.broadcastPacket(new ExFishingStartCombat(fisher, time, fishMaxHp, mode, lureType));
		
		// Succeeded in getting a bite
		fisher.sendPacket(SystemMessage.GOT_A_BITE);
		
		if (fishAItask == null)
		{
			fishAItask = ThreadPoolManager.scheduleAtFixedRate(this, 1000, 1000);
		}
	}
	
	@Override
	public void run()
	{
		if (fishCurHP >= (fishMaxHp * 2))
		{
			// The fish got away
			fisher.sendPacket(SystemMessage.BAIT_STOLEN_BY_FISH);
			doDie(false);
		}
		else if (time <= 0)
		{
			// Time is up, so that fish got away
			fisher.sendPacket(SystemMessage.FISH_SPIT_THE_HOOK);
			doDie(false);
		}
		else
		{
			aiTask();
		}
	}
	
	public void changeHp(int hp, int pen)
	{
		fishCurHP -= hp;
		if (fishCurHP < 0)
		{
			fishCurHP = 0;
		}
		
		fisher.broadcastPacket(new ExFishingHpRegen(fisher, time, fishCurHP, mode, goodUse, animation, pen));
		
		animation = FishAnimationType.NONE;
		
		if (fishCurHP > (fishMaxHp * 2))
		{
			fishCurHP = fishMaxHp * 2;
			doDie(false);
			return;
		}
		else if (fishCurHP == 0)
		{
			doDie(true);
			return;
		}
	}
	
	public synchronized void doDie(boolean win)
	{
		if (fishAItask != null)
		{
			fishAItask.cancel(false);
			fishAItask = null;
		}
		
		if (fisher == null)
		{
			return;
		}
		
		if (win)
		{
			int check = Rnd.get(100);
			if (check <= 5)
			{
				penaltyMonster();
			}
			else
			{
				fisher.sendPacket(SystemMessage.YOU_CAUGHT_SOMETHING);
				fisher.getInventory().addItem("Fishing", fishId, 1, null, true);
			}
		}
		
		fisher.getFishing().endFishing(win);
		fisher = null;
	}
	
	protected void aiTask()
	{
		if (thinking)
		{
			return;
		}
		
		thinking = true;
		time--;
		
		try
		{
			if (mode == FishModeType.FIGHTING)
			{
				fishCurHP += (int) regenHp;
			}
			
			if (stop == 0)
			{
				stop = 1;
				
				if ((Rnd.get(100) >= 70) && (mode == FishModeType.RESTING))
				{
					mode = FishModeType.FIGHTING;
				}
			}
			else
			{
				stop--;
			}
		}
		finally
		{
			thinking = false;
			ExFishingHpRegen efhr = new ExFishingHpRegen(fisher, time, fishCurHP, mode, 0, animation, 0);
			
			if (animation != FishAnimationType.NONE)
			{
				fisher.broadcastPacket(efhr);
			}
			else
			{
				fisher.sendPacket(efhr);
			}
		}
	}
	
	public void useRealing(int dmg, int pen)
	{
		animation = FishAnimationType.PUMPING;
		if (Rnd.get(100) > 90)
		{
			fisher.sendPacket(SystemMessage.FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN);
			goodUse = 0;
			changeHp(0, pen);
			return;
		}
		
		if (fisher == null)
		{
			return;
		}
		
		if (mode == FishModeType.FIGHTING)
		{
			// Reeling is successful, Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.REELING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
			if (pen == 50)
			{
				fisher.sendPacket(new SystemMessage(SystemMessage.REELING_SUCCESSFUL_PENALTY_S1).addNumber(pen));
			}
			goodUse = 1;
			changeHp(dmg, pen);
		}
		else
		{
			// Reeling failed, Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.FISH_RESISTED_REELING_S1_HP_REGAINED).addNumber(dmg));
			goodUse = 2;
			changeHp(-dmg, pen);
		}
	}
	
	public void usePumping(int dmg, int pen)
	{
		animation = FishAnimationType.REELING;
		if (Rnd.get(100) > 90)
		{
			fisher.sendPacket(SystemMessage.FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN);
			goodUse = 0;
			changeHp(0, pen);
			return;
		}
		
		if (fisher == null)
		{
			return;
		}
		
		if (mode == FishModeType.RESTING)
		{
			// Pumping is successful. Damage: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
			
			if (pen == 50)
			{
				fisher.sendPacket(new SystemMessage(SystemMessage.PUMPING_SUCCESSFUL_PENALTY_S1).addNumber(pen));
			}
			goodUse = 1;
			changeHp(dmg, pen);
		}
		else
		{
			// Pumping failed, Regained: $s1
			fisher.sendPacket(new SystemMessage(SystemMessage.FISH_RESISTED_PUMPING_S1_HP_REGAINED).addNumber(dmg));
			goodUse = 2;
			changeHp(-dmg, pen);
			
		}
	}
	
	private void penaltyMonster()
	{
		int lvl = (int) Math.round(fisher.getLevel() * 0.1);
		int npcid;
		
		fisher.sendPacket(SystemMessage.YOU_CAUGHT_SOMETHING_SMELLY_THROW_IT_BACK);
		
		switch (lvl)
		{
			case 0:
			case 1:
				npcid = 13245;
				break;
			case 2:
				npcid = 13246;
				break;
			case 3:
				npcid = 13247;
				break;
			case 4:
				npcid = 13248;
				break;
			case 5:
				npcid = 13249;
				break;
			case 6:
				npcid = 13250;
				break;
			case 7:
				npcid = 13251;
				break;
			case 8:
				npcid = 13252;
				break;
			default:
				npcid = 13245;
				break;
		}
		
		NpcTemplate template = NpcData.getInstance().getTemplate(npcid);
		
		try
		{
			Spawn spawn = new Spawn(template);
			spawn.setX(fisher.getFishing().getLoc().getX());
			spawn.setY(fisher.getFishing().getLoc().getY());
			spawn.setZ(fisher.getFishing().getLoc().getZ());
			spawn.setAmount(1);
			spawn.setHeading(fisher.getHeading());
			spawn.stopRespawn();
			
			ScriptState qs = fisher.getScriptState("FishCaught");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("SetPlayerToKill", spawn.doSpawn(), fisher);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
