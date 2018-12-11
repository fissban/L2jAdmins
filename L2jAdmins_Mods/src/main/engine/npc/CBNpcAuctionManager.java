package main.engine.npc;

import main.EngineModsManager;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import l2j.gameserver.model.actor.L2Npc;

/**
 * @author fissban
 */
public class CBNpcAuctionManager extends AbstractMod
{
	private static final int NPC = 70002;
	
	public CBNpcAuctionManager()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder npc)
	{
		if (Util.areObjectType(L2Npc.class, npc) && ((NpcHolder) npc).getId() == NPC)
		{
			EngineModsManager.onCommunityBoard(ph.getInstance(), "_bbsmemo");
			return true;
		}
		return false;
	}
}
