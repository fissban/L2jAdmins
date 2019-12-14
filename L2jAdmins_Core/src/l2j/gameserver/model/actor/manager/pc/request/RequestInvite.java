package l2j.gameserver.model.actor.manager.pc.request;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class manages requests (transactions) between two L2PcInstance.<br>
 * <li>JoinParty</li>
 * <li>JoinAlly</li>
 * <li>JoinParty</li>
 * <li>JoinPledge</li>
 */
public class RequestInvite
{
	private static final int REQUEST_TIMEOUT = 10; // in seconds
	
	private final L2PcInstance player;
	private L2PcInstance partner;
	
	protected boolean isRequestor;
	protected boolean isAnswerer;
	
	private Future<?> requestTime;
	
	private RequestPacketType requestPacketType = RequestPacketType.NONE;
	
	public RequestInvite(L2PcInstance player)
	{
		this.player = player;
	}
	
	protected void clear()
	{
		partner = null;
		requestPacketType = RequestPacketType.NONE;
		isRequestor = false;
		isAnswerer = false;
	}
	
	/**
	 * Set the L2PcInstance member of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).<BR>
	 * @param partner
	 */
	private synchronized void setPartner(L2PcInstance partner)
	{
		this.partner = partner;
	}
	
	/**
	 * Return the L2PcInstance member of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 * @return
	 */
	public L2PcInstance getPartner()
	{
		return partner;
	}
	
	/**
	 * Set the packet income from requester.
	 * @param packet
	 */
	public synchronized void setRequestPacket(RequestPacketType packet)
	{
		requestPacketType = packet;
	}
	
	/**
	 * @param  packet
	 * @return        True if original packet equals "packet".
	 */
	public boolean isRequestPacket(RequestPacketType packet)
	{
		return requestPacketType.equals(packet);
	}
	
	/**
	 * Checks if request can be made and in success case puts both PC on request state.
	 * @param  partner
	 * @param  packet
	 * @return
	 */
	public synchronized boolean startRequest(L2PcInstance partner, RequestPacketType packet)
	{
		if (partner == null)
		{
			player.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return false;
		}
		
		if (partner.getRequestInvite().isProcessingRequest())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1_IS_BUSY_TRY_LATER).addString(partner.getName()));
			return false;
		}
		
		if (isProcessingRequest())
		{
			// Waiting another reply.
			player.sendPacket(SystemMessage.WAITING_FOR_ANOTHER_REPLY);
			return false;
		}
		
		this.partner = partner;
		requestPacketType = packet;
		startRequestTimer(true);
		partner.getRequestInvite().setPartner(player);
		partner.getRequestInvite().setRequestPacket(packet);
		partner.getRequestInvite().startRequestTimer(false);
		return true;
	}
	
	/**
	 * Clears PC request state. Should be called after answer packet receive.
	 */
	public void endRequest()
	{
		if (partner != null)
		{
			partner.getRequestInvite().endRequestTimer();
			partner.getRequestInvite().clear();
		}
		
		endRequestTimer();
		clear();
	}
	
	/**
	 * @param isRequestor
	 */
	private void startRequestTimer(boolean isRequestor)
	{
		isRequestor = isRequestor ? true : false;
		isAnswerer = isRequestor ? false : true;
		
		requestTime = ThreadPoolManager.schedule(() -> clear(), REQUEST_TIMEOUT * 1000);
	}
	
	private void endRequestTimer()
	{
		if (requestTime != null)
		{
			requestTime.cancel(true);
			requestTime = null;
		}
	}
	
	/**
	 * Return True if a transaction is in progress.
	 * @return
	 */
	public boolean isProcessingRequest()
	{
		return partner != null;
	}
}
