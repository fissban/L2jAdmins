package l2j.gameserver.model.request;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class PcRequestTrade
{
	private static final int REQUEST_TIMEOUT = 10; // in seconds
	
	private final L2PcInstance player;
	private L2PcInstance partner;
	
	protected boolean isRequestor;
	protected boolean isAnswerer;
	
	private Future<?> requestTime;
	
	public PcRequestTrade(L2PcInstance player)
	{
		this.player = player;
	}
	
	protected void clear()
	{
		partner = null;
		isRequestor = false;
		isAnswerer = false;
	}
	
	/**
	 * Set the L2PcInstance member of a transaction TradeRequest.
	 * @param partner
	 */
	private synchronized void setPartner(L2PcInstance partner)
	{
		this.partner = partner;
	}
	
	/**
	 * Return the L2PcInstance member of a transaction TradeRequest.
	 * @return
	 */
	public L2PcInstance getPartner()
	{
		return partner;
	}
	
	/**
	 * Checks if request can be made and in success case puts both PC on request state.
	 * @param  partner
	 * @return
	 */
	public synchronized boolean startRequest(L2PcInstance partner)
	{
		if (partner == null)
		{
			return false;
		}
		
		if (partner.getRequestTrade().isProcessingRequest())
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
		
		setPartner(partner);
		startRequestTimer(true);
		
		partner.getRequestTrade().setPartner(player);
		partner.getRequestTrade().startRequestTimer(false);
		return true;
	}
	
	/**
	 * Clears PC request state. Should be called after answer packet receive.
	 */
	public void endRequest()
	{
		if (partner != null)
		{
			partner.getRequestTrade().endRequestTimer();
			partner.getRequestTrade().clear();
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
