package l2j.gameserver.handler.action;

import java.util.List;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.MoveToPawn;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.ValidateLocation;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;
import main.EngineModsManager;

/**
 * @author fissban
 */
public class NpcOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2Npc npc = (L2Npc) target;
		
		if (!npc.canTarget(player))
		{
			return false;
		}
		
		player.setLastTalkNpc(npc);
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (target != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(target);
			
			// Check if the player is attackable (without a forced attack)
			if (target.isAutoAttackable(player))
			{
				// wake up ai
				npc.getAI();
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(target.getObjectId());
				su.addAttribute(StatusUpdateType.CUR_HP, (int) npc.getCurrentHp());
				su.addAttribute(StatusUpdateType.MAX_HP, npc.getStat().getMaxHp());
				player.sendPacket(su);
			}
		}
		else if (interact)
		{
			player.sendPacket(new ValidateLocation(npc));
			// Check if the player is attackable (without a forced attack) and isn't dead
			if (target.isAutoAttackable(player))
			{
				if (!npc.isAlikeDead())
				{
					// Set the L2PcInstance Intention to ATTACK
					player.getAI().setIntention(CtrlIntentionType.ATTACK, target);
				}
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, (L2Character) target, L2Npc.INTERACTION_DISTANCE));
					// Notify the L2PcInstance AI with FOLLOW
					player.getAI().setIntention(CtrlIntentionType.FOLLOW, target);
				}
			}
			else
			{
				// Calculate the distance between the L2PcInstance and the L2NpcInstance
				if (!npc.canInteract(player))
				{
					// Notify the L2PcInstance AI with INTERACT
					player.getAI().setIntention(CtrlIntentionType.INTERACT, target);
				}
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, (L2Character) target, L2Npc.INTERACTION_DISTANCE));
					
					// TODO hardcode
					// These npc, make no animation and q are bodies lying on the ground.
					switch (npc.getId())
					{
						case 7675:
						case 7761:
						case 7762:
						case 7763:
						case 7980:
						case 8665:
						case 8752:
							break;
						default:
							SocialActionType[] type = new SocialActionType[]
							{
								SocialActionType.NPC_ANIMATION, // 1
								SocialActionType.HELLO, // 2
								SocialActionType.VICTORY, // 3
								SocialActionType.CHARGE, // 4
								SocialActionType.NO, // 5
								SocialActionType.YES, // 6
								SocialActionType.BOW, // 7
								SocialActionType.UNAWARE, // 8
							};
							npc.broadcastPacket(new SocialAction(npc.getObjectId(), type));
					}
					
					if (EngineModsManager.onInteract(player, npc))
					{
						return false;
					}
					
					// Open a chat window on client with the text of the L2NpcInstance
					List<Script> qlsa = npc.getTemplate().getEventScript(ScriptEventType.QUEST_START);
					List<Script> qlst = npc.getTemplate().getEventScript(ScriptEventType.ON_FIRST_TALK);
					
					if ((qlsa != null) && (!qlsa.isEmpty()))
					{
						player.setLastQuestNpcObject(npc.getObjectId());
					}
					if ((qlst != null) && (qlst.size() == 1))
					{
						qlst.get(0).notifyFirstTalk(npc, player);
					}
					else
					{
						npc.showChatWindow(player);
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
