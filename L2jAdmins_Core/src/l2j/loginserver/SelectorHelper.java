package l2j.loginserver;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import l2j.loginserver.network.LoginClient;
import l2j.loginserver.network.external.server.Init;
import l2j.mmocore.IAcceptFilter;
import l2j.mmocore.IClientFactory;
import l2j.mmocore.IMMOExecutor;
import l2j.mmocore.MMOConnection;
import l2j.mmocore.ReceivablePacket;
import l2j.util.IPv4Filter;

public class SelectorHelper implements IMMOExecutor<LoginClient>, IClientFactory<LoginClient>, IAcceptFilter
{
	private final ThreadPoolExecutor generalPacketsThreadPool;
	
	private final IPv4Filter ipv4filter;
	
	public SelectorHelper()
	{
		generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		ipv4filter = new IPv4Filter();
	}
	
	@Override
	public void execute(ReceivablePacket<LoginClient> packet)
	{
		generalPacketsThreadPool.execute(packet);
	}
	
	@Override
	public LoginClient create(MMOConnection<LoginClient> con)
	{
		var client = new LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public boolean accept(SocketChannel sc)
	{
		return ipv4filter.accept(sc) && !LoginController.getInstance().isBannedAddress(sc.socket().getInetAddress());
	}
}