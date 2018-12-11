package l2j.gameserver.network.external.client;

import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public class RequestGetItemFromPet extends AClientPacket
{
	private int objectId;
	private int amount;
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		amount = readD();
		unknown = readD();// = 0 for most trades
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((player == null) || (player.getPet() == null) || !(player.getPet() instanceof L2PetInstance))
		{
			return;
		}
		L2PetInstance pet = (L2PetInstance) player.getPet();
		
		if (amount < 0)
		{
			IllegalAction.report(player, "[RequestGetItemFromPet] count < 0! ban! oid: " + objectId + " owner: " + player.getName());
			return;
		}
		else if (amount == 0)
		{
			return;
		}
		
		if (pet.transferItem("Transfer", objectId, amount, player.getInventory(), player, pet) == null)
		{
			LOG.warning("Invalid Item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}
}
