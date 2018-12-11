package l2j.mmocore;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<T extends MMOClient<?>>
{
	public ByteBuffer buff;
	
	public T networClient;
	
	public final T getClient()
	{
		return networClient;
	}
}