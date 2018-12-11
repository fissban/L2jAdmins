package l2j.mmocore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;

public class MMOConnection<T extends MMOClient<?>>
{
	private final SelectorThread<T> selectorThread;
	private final Socket socket;
	private final InetAddress address;
	
	private final ReadableByteChannel readableByteChannel;
	private final WritableByteChannel writableByteChannel;
	
	private final int port;
	
	private final NioNetStackList<SendablePacket<T>> sendQueue;
	
	private final SelectionKey selectionKey;
	
	private ByteBuffer readBuffer;
	private ByteBuffer primaryWriteBuffer;
	private ByteBuffer secondaryWriteBuffer;
	
	private volatile boolean pendingClose;
	
	private T client;
	
	public MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey key, boolean tcpNoDelay)
	{
		this.selectorThread = selectorThread;
		this.socket = socket;
		this.address = socket.getInetAddress();
		
		this.readableByteChannel = socket.getChannel();
		this.writableByteChannel = socket.getChannel();
		
		this.port = socket.getPort();
		this.selectionKey = key;
		
		this.sendQueue = new NioNetStackList<>();
		
		try
		{
			this.socket.setTcpNoDelay(tcpNoDelay);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setClient(final T client)
	{
		this.client = client;
	}
	
	public T getClient()
	{
		return client;
	}
	
	public void sendPacket(final SendablePacket<T> sp)
	{
		sp.networClient = client;
		
		if (pendingClose)
		{
			return;
		}
		
		synchronized (getSendQueue())
		{
			sendQueue.addLast(sp);
		}
		
		if (!sendQueue.isEmpty())
		{
			try
			{
				selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
			}
			catch (CancelledKeyException e)
			{
				// ignore
			}
		}
	}
	
	public SelectionKey getSelectionKey()
	{
		return selectionKey;
	}
	
	public InetAddress getInetAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void close() throws IOException
	{
		socket.close();
	}
	
	public int read(final ByteBuffer buf) throws IOException
	{
		return readableByteChannel.read(buf);
	}
	
	public int write(final ByteBuffer buf) throws IOException
	{
		return writableByteChannel.write(buf);
	}
	
	public void createWriteBuffer(final ByteBuffer buf)
	{
		if (primaryWriteBuffer == null)
		{
			primaryWriteBuffer = selectorThread.getPooledBuffer();
			primaryWriteBuffer.put(buf);
		}
		else
		{
			final ByteBuffer temp = selectorThread.getPooledBuffer();
			temp.put(buf);
			
			final int remaining = temp.remaining();
			primaryWriteBuffer.flip();
			final int limit = primaryWriteBuffer.limit();
			
			if (remaining >= primaryWriteBuffer.remaining())
			{
				temp.put(primaryWriteBuffer);
				selectorThread.recycleBuffer(primaryWriteBuffer);
				primaryWriteBuffer = temp;
			}
			else
			{
				primaryWriteBuffer.limit(remaining);
				temp.put(primaryWriteBuffer);
				primaryWriteBuffer.limit(limit);
				primaryWriteBuffer.compact();
				secondaryWriteBuffer = primaryWriteBuffer;
				primaryWriteBuffer = temp;
			}
		}
	}
	
	public boolean hasPendingWriteBuffer()
	{
		return primaryWriteBuffer != null;
	}
	
	public void movePendingWriteBufferTo(final ByteBuffer dest)
	{
		primaryWriteBuffer.flip();
		dest.put(primaryWriteBuffer);
		selectorThread.recycleBuffer(primaryWriteBuffer);
		primaryWriteBuffer = secondaryWriteBuffer;
		secondaryWriteBuffer = null;
	}
	
	public void setReadBuffer(final ByteBuffer buf)
	{
		readBuffer = buf;
	}
	
	public final ByteBuffer getReadBuffer()
	{
		return readBuffer;
	}
	
	public final boolean isClosed()
	{
		return pendingClose;
	}
	
	public NioNetStackList<SendablePacket<T>> getSendQueue()
	{
		return sendQueue;
	}
	
	public void close(final SendablePacket<T> sp)
	{
		if (pendingClose)
		{
			return;
		}
		
		synchronized (getSendQueue())
		{
			if (!pendingClose)
			{
				pendingClose = true;
				sendQueue.clear();
				sendQueue.addLast(sp);
			}
		}
		
		try
		{
			selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
		}
		catch (CancelledKeyException e)
		{
		}
		
		selectorThread.closeConnection(this);
	}
	
	public void releaseBuffers()
	{
		if (primaryWriteBuffer != null)
		{
			selectorThread.recycleBuffer(primaryWriteBuffer);
			primaryWriteBuffer = null;
			
			if (secondaryWriteBuffer != null)
			{
				selectorThread.recycleBuffer(secondaryWriteBuffer);
				secondaryWriteBuffer = null;
			}
		}
		
		if (readBuffer != null)
		{
			selectorThread.recycleBuffer(readBuffer);
			readBuffer = null;
		}
	}
}