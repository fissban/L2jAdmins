package l2j.loginserver.network.internal.gameserver;

import java.util.ArrayList;
import java.util.List;

import l2j.loginserver.network.AClientPacket;

public class PlayerInGame extends AClientPacket
{
	private final List<String> accounts = new ArrayList<>();
	
	public PlayerInGame(byte[] decrypt)
	{
		super(decrypt);
		
		int size = readH();
		for (int i = 0; i < size; i++)
		{
			accounts.add(readS());
		}
	}
	
	public List<String> getAccounts()
	{
		return accounts;
	}
}