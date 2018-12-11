package l2j.mmocore;

public interface IClientFactory<T extends MMOClient<?>>
{
	public T create(final MMOConnection<T> con);
}