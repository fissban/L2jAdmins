package l2j.mmocore;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection<?>>
{
	private final T con;
	
	public MMOClient(final T con)
	{
		this.con = con;
	}
	
	public T getConnection()
	{
		return con;
	}
	
	public abstract boolean decrypt(final ByteBuffer buf, final int size);
	
	public abstract boolean encrypt(final ByteBuffer buf, final int size);
	
	public abstract void onDisconnection();
	
	public abstract void onForcedDisconnection();
}