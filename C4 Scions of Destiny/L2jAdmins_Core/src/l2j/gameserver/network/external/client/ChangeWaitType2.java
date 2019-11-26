package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ChairSit;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class ChangeWaitType2 extends AClientPacket
{
	private boolean typeStand;
	
	@Override
	protected void readImpl()
	{
		typeStand = readD() == 1;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((getClient() != null) && (player != null))
		{
			if (player.isOutOfControl())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (player.isMounted())
			{
				return;
			}
			
			L2Object target = player.getTarget();
			
			if ((target != null) && !player.isSitting() && (target instanceof L2StaticObjectInstance) && (((L2StaticObjectInstance) target).getType() == 1) && (CastleData.getInstance().getCastle(target) != null)
				&& player.isInsideRadius(target, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
			{
				player.sitDown();
				player.broadcastPacket(new ChairSit(player, ((L2StaticObjectInstance) target).getStaticObjectId()));
			}
			
			if (typeStand)
			{
				player.standUp();
			}
			else
			{
				if (!player.isPendingSitting())
				{
					if (player.isMoving())
					{
						player.setIsPendingSitting(true);
					}
					else
					{
						if (player.cantAttack() || player.isAttackingNow() || player.isImmobilized() || player.isCastingNow())
						{
							return;
						}
						
						player.sitDown();
					}
				}
			}
		}
	}
}
