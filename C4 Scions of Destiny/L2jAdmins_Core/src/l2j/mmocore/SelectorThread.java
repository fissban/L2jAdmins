package l2j.mmocore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

import l2j.gameserver.network.external.server.KeyPacket;
import l2j.loginserver.network.external.server.Init;

public final class SelectorThread<T extends MMOClient<?>> extends Thread
{
	// default BYTE_ORDER
	private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	
	// default HEADER_SIZE
	private static final int HEADER_SIZE = 2;
	
	// Configurations
	private final int HELPER_BUFFER_SIZE;
	private final int HELPER_BUFFER_COUNT;
	private final int MAX_SEND_PER_PASS;
	private final int MAX_READ_PER_PASS;
	private final long SLEEP_TIME;
	public boolean TCP_NODELAY;
	
	// String Buffer
	// private final NioNetStringBuffer STRING_BUFFER;
	
	// Selector
	private final Selector selector;
	
	// Implementations
	private final IPacketHandler<T> packetHandler;
	private final IMMOExecutor<T> executor;
	private final IClientFactory<T> clientFactory;
	private final IAcceptFilter acceptFilter;
	
	// Main Buffers
	private final ByteBuffer directWriteBuffer;
	private final ByteBuffer writeBuffer;
	private final ByteBuffer readBuffer;
	
	// ByteBuffers General Purpose Pool
	private final LinkedList<ByteBuffer> bufferPool;
	
	// Pending Close
	private final NioNetStackList<MMOConnection<T>> pendingClose;
	
	private boolean shutdown;
	
	public SelectorThread(SelectorConfig sc, IMMOExecutor<T> executor, IPacketHandler<T> packetHandler, IClientFactory<T> clientFactory, IAcceptFilter acceptFilter) throws IOException
	{
		super.setName("SelectorThread-" + super.getId());
		
		HELPER_BUFFER_SIZE = sc.HELPER_BUFFER_SIZE;
		HELPER_BUFFER_COUNT = sc.HELPER_BUFFER_COUNT;
		MAX_SEND_PER_PASS = sc.MAX_SEND_PER_PASS;
		MAX_READ_PER_PASS = sc.MAX_READ_PER_PASS;
		
		SLEEP_TIME = sc.SLEEP_TIME;
		TCP_NODELAY = sc.TCP_NODELAY;
		
		directWriteBuffer = ByteBuffer.allocateDirect(sc.WRITE_BUFFER_SIZE).order(BYTE_ORDER);
		writeBuffer = ByteBuffer.wrap(new byte[sc.WRITE_BUFFER_SIZE]).order(BYTE_ORDER);
		readBuffer = ByteBuffer.wrap(new byte[sc.READ_BUFFER_SIZE]).order(BYTE_ORDER);
		
		// STRING_BUFFER = new NioNetStringBuffer(64 * 1024);
		
		this.pendingClose = new NioNetStackList<>();
		this.bufferPool = new LinkedList<>();
		
		for (int i = 0; i < HELPER_BUFFER_COUNT; i++)
		{
			bufferPool.addLast(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER));
		}
		
		this.acceptFilter = acceptFilter;
		this.packetHandler = packetHandler;
		this.clientFactory = clientFactory;
		this.executor = executor;
		this.selector = Selector.open();
	}
	
	public final void openServerSocket(InetAddress address, int tcpPort) throws IOException
	{
		var selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);
		
		var ss = selectable.socket();
		
		if (address == null)
		{
			ss.bind(new InetSocketAddress(tcpPort));
		}
		else
		{
			ss.bind(new InetSocketAddress(address, tcpPort));
		}
		
		selectable.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	final ByteBuffer getPooledBuffer()
	{
		if (bufferPool.isEmpty())
		{
			return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER);
		}
		
		return bufferPool.removeFirst();
	}
	
	final void recycleBuffer(final ByteBuffer buf)
	{
		if (bufferPool.size() < HELPER_BUFFER_COUNT)
		{
			buf.clear();
			bufferPool.addLast(buf);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final void run()
	{
		int selectedKeysCount = 0;
		
		SelectionKey key;
		MMOConnection<T> con;
		
		Iterator<SelectionKey> selectedKeys;
		
		while (!shutdown)
		{
			try
			{
				selectedKeysCount = selector.selectNow();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (selectedKeysCount > 0)
			{
				selectedKeys = selector.selectedKeys().iterator();
				
				while (selectedKeys.hasNext())
				{
					key = selectedKeys.next();
					selectedKeys.remove();
					
					con = (MMOConnection<T>) key.attachment();
					
					switch (key.readyOps())
					{
						case SelectionKey.OP_CONNECT:
							finishConnection(key, con);
							break;
						
						case SelectionKey.OP_ACCEPT:
							acceptConnection(key, con);
							break;
						
						case SelectionKey.OP_READ:
							readPacket(key, con);
							break;
						
						case SelectionKey.OP_WRITE:
							writePacket(key, con);
							break;
						
						case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
							writePacket(key, con);
							if (key.isValid())
							{
								readPacket(key, con);
							}
							break;
					}
				}
			}
			
			synchronized (pendingClose)
			{
				while (!pendingClose.isEmpty())
				{
					try
					{
						con = pendingClose.removeFirst();
						writeClosePacket(con);
						closeConnectionImpl(con.getSelectionKey(), con);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		closeSelectorThread();
	}
	
	private final void finishConnection(final SelectionKey key, final MMOConnection<T> con)
	{
		try
		{
			((SocketChannel) key.channel()).finishConnect();
		}
		catch (IOException e)
		{
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
		
		// key might have been invalidated on finishConnect()
		if (key.isValid())
		{
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
		}
	}
	
	private final void acceptConnection(final SelectionKey key, MMOConnection<T> con)
	{
		var ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		
		try
		{
			while ((sc = ssc.accept()) != null)
			{
				if ((acceptFilter == null) || acceptFilter.accept(sc))
				{
					sc.configureBlocking(false);
					var clientKey = sc.register(selector, SelectionKey.OP_READ);
					con = new MMOConnection<>(this, sc.socket(), clientKey, TCP_NODELAY);
					con.setClient(clientFactory.create(con));
					clientKey.attach(con);
				}
				else
				{
					sc.socket().close();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private final void readPacket(final SelectionKey key, final MMOConnection<T> con)
	{
		if (!con.isClosed())
		{
			ByteBuffer buf;
			if ((buf = con.getReadBuffer()) == null)
			{
				buf = readBuffer;
			}
			
			// if we try to to do a read with no space in the buffer it will read 0 bytes going into infinite loop
			if (buf.position() == buf.limit())
			{
				closeConnectionImpl(key, con);
				return;
			}
			
			int result = -2;
			
			try
			{
				result = con.read(buf);
			}
			catch (IOException e)
			{
				// error handling goes bellow
			}
			
			if (result > 0)
			{
				buf.flip();
				
				final T client = con.getClient();
				
				for (int i = 0; i < MAX_READ_PER_PASS; i++)
				{
					if (!tryReadPacket(key, client, buf, con))
					{
						return;
					}
				}
				
				// only reachable if MAX_READ_PER_PASS has been reached
				// check if there are some more bytes in buffer
				// and allocate/compact to prevent content lose.
				if (buf.remaining() > 0)
				{
					// did we use the READ_BUFFER ?
					if (buf == readBuffer)
					{
						// move the pending byte to the connections READ_BUFFER
						allocateReadBuffer(con);
					}
					else
					{
						// move the first byte to the beginning :)
						buf.compact();
					}
				}
			}
			else
			{
				switch (result)
				{
					case 0:
					case -1:
						closeConnectionImpl(key, con);
						break;
					case -2:
						con.getClient().onForcedDisconnection();
						closeConnectionImpl(key, con);
						break;
				}
			}
		}
	}
	
	private final boolean tryReadPacket(final SelectionKey key, final T client, final ByteBuffer buf, final MMOConnection<T> con)
	{
		switch (buf.remaining())
		{
			case 0:
				// buffer is full nothing to read
				return false;
			
			case 1:
				// we don`t have enough data for header so we need to read
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				
				// did we use the READ_BUFFER ?
				if (buf == readBuffer)
				{
					// move the pending byte to the connections READ_BUFFER
					allocateReadBuffer(con);
				}
				else
				{
					// move the first byte to the beginning :)
					buf.compact();
				}
				return false;
			
			default:
				// data size excluding header size :>
				var dataPending = (buf.getShort() & 0xFFFF) - HEADER_SIZE;
				
				// do we got enough bytes for the packet?
				if (dataPending <= buf.remaining())
				{
					// avoid parsing dummy packets (packets without body)
					if (dataPending > 0)
					{
						final int pos = buf.position();
						parseClientPacket(pos, buf, dataPending, client);
						buf.position(pos + dataPending);
					}
					
					// if we are done with this buffer
					if (!buf.hasRemaining())
					{
						if (buf != readBuffer)
						{
							con.setReadBuffer(null);
							recycleBuffer(buf);
						}
						else
						{
							readBuffer.clear();
						}
						return false;
					}
					return true;
				}
				
				// we don`t have enough bytes for the dataPacket so we need
				// to read
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				
				// did we use the READ_BUFFER ?
				if (buf == readBuffer)
				{
					// move it`s position
					buf.position(buf.position() - HEADER_SIZE);
					// move the pending byte to the connections READ_BUFFER
					allocateReadBuffer(con);
				}
				else
				{
					buf.position(buf.position() - HEADER_SIZE);
					buf.compact();
				}
				return false;
		}
	}
	
	private final void allocateReadBuffer(final MMOConnection<T> con)
	{
		con.setReadBuffer(getPooledBuffer().put(readBuffer));
		readBuffer.clear();
	}
	
	private final void parseClientPacket(final int pos, final ByteBuffer buf, final int dataSize, final T client)
	{
		var ret = client.decrypt(buf, dataSize);
		
		if (ret && buf.hasRemaining())
		{
			// apply limit
			var limit = buf.limit();
			buf.limit(pos + dataSize);
			var cp = packetHandler.handlePacket(buf, client);
			
			if (cp != null)
			{
				cp.buff = buf;
				// cp.sbuf = STRING_BUFFER;
				cp.networClient = client;
				
				if (cp.read())
				{
					executor.execute(cp);
				}
				
				cp.buff = null;
				// cp.sbuf = null;
			}
			buf.limit(limit);
		}
	}
	
	private final void writeClosePacket(final MMOConnection<T> con)
	{
		SendablePacket<T> sp;
		synchronized (con.getSendQueue())
		{
			if (con.getSendQueue().isEmpty())
			{
				return;
			}
			
			while ((sp = con.getSendQueue().removeFirst()) != null)
			{
				writeBuffer.clear();
				
				putPacketIntoWriteBuffer(con.getClient(), sp);
				
				writeBuffer.flip();
				
				try
				{
					con.write(writeBuffer);
				}
				catch (IOException e)
				{
					// error handling goes on the if bellow
				}
			}
		}
	}
	
	protected final void writePacket(final SelectionKey key, final MMOConnection<T> con)
	{
		if (!prepareWriteBuffer(con))
		{
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			return;
		}
		
		directWriteBuffer.flip();
		
		var size = directWriteBuffer.remaining();
		
		var result = -1;
		
		try
		{
			result = con.write(directWriteBuffer);
		}
		catch (IOException e)
		{
			// error handling goes on the if bellow
		}
		
		// check if no error happened
		if (result >= 0)
		{
			// check if we written everything
			if (result == size)
			{
				// complete write
				synchronized (con.getSendQueue())
				{
					if (con.getSendQueue().isEmpty() && !con.hasPendingWriteBuffer())
					{
						key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
					}
				}
			}
			else
			{
				// incomplete write
				con.createWriteBuffer(directWriteBuffer);
			}
		}
		else
		{
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
	}
	
	private final boolean prepareWriteBuffer(final MMOConnection<T> con)
	{
		var hasPending = false;
		directWriteBuffer.clear();
		
		// if there is pending content add it
		if (con.hasPendingWriteBuffer())
		{
			con.movePendingWriteBufferTo(directWriteBuffer);
			hasPending = true;
		}
		
		if ((directWriteBuffer.remaining() > 1) && !con.hasPendingWriteBuffer())
		{
			var sendQueue = con.getSendQueue();
			var client = con.getClient();
			SendablePacket<T> sp;
			
			for (int i = 0; i < MAX_SEND_PER_PASS; i++)
			{
				synchronized (con.getSendQueue())
				{
					if (sendQueue.isEmpty())
					{
						sp = null;
					}
					else
					{
						sp = sendQueue.removeFirst();
					}
				}
				
				if (sp == null)
				{
					break;
				}
				
				hasPending = true;
				
				// put into WriteBuffer
				putPacketIntoWriteBuffer(client, sp);
				
				writeBuffer.flip();
				
				if (directWriteBuffer.remaining() >= writeBuffer.limit())
				{
					directWriteBuffer.put(writeBuffer);
				}
				else
				{
					con.createWriteBuffer(writeBuffer);
					break;
				}
			}
		}
		return hasPending;
	}
	
	private final void putPacketIntoWriteBuffer(final T client, final SendablePacket<T> sp)
	{
		writeBuffer.clear();
		
		// reserve space for the size
		var headerPos = writeBuffer.position();
		var dataPos = headerPos + HEADER_SIZE;
		writeBuffer.position(dataPos);
		
		// set the write buffer
		sp.buff = writeBuffer;
		// set the client.
		sp.networClient = client;
		// write content to buffer
		sp.write();
		// delete the write buffer
		sp.buff = null;
		
		// size (inclusive header)
		int dataSize = writeBuffer.position() - dataPos;
		writeBuffer.position(dataPos);
		
		if (!((sp instanceof Init) || (sp instanceof KeyPacket)))
		{
			client.encrypt(writeBuffer, dataSize);
			
			// recalculate size after encryption
			dataSize = writeBuffer.position() - dataPos;
		}
		writeBuffer.position(headerPos);
		// write header
		writeBuffer.putShort((short) (dataSize + HEADER_SIZE));
		writeBuffer.position(dataPos + dataSize);
	}
	
	final void closeConnection(final MMOConnection<T> con)
	{
		synchronized (pendingClose)
		{
			pendingClose.addLast(con);
		}
	}
	
	private final void closeConnectionImpl(final SelectionKey key, final MMOConnection<T> con)
	{
		try
		{
			// notify connection
			con.getClient().onDisconnection();
		}
		finally
		{
			try
			{
				// close socket and the SocketChannel
				con.close();
			}
			catch (IOException e)
			{
				// ignore, we are closing anyway
			}
			finally
			{
				con.releaseBuffers();
				// clear attachment
				key.attach(null);
				// cancel key
				key.cancel();
			}
		}
	}
	
	public final void shutdown()
	{
		shutdown = true;
	}
	
	protected void closeSelectorThread()
	{
		for (var key : selector.keys())
		{
			try
			{
				key.channel().close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		
		try
		{
			selector.close();
		}
		catch (IOException e)
		{
			// Ignore
		}
	}
}