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
public class CBNpcClanManager extends AbstractMod
{
	private static final int NPC = 70001;
	
	public CBNpcClanManager()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder npc)
	{
		if (Util.areObjectType(L2Npc.class, npc) && (((NpcHolder) npc).getId() == NPC))
		{
			EngineModsManager.onCommunityBoard(ph.getInstance(), "_bbsclan");
			return true;
		}
		return false;
	}
}
