package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.CharSelectInfoHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.external.server.CharSelected;

/**
 * This class ...
 * @version $Revision: 1.5.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterSelected extends AClientPacket
{
	// cd
	private int charSlot;
	
	@SuppressWarnings("unused")
	private int unk1; // new in C4
	@SuppressWarnings("unused")
	private int unk2; // new in C4
	@SuppressWarnings("unused")
	private int unk3; // new in C4
	@SuppressWarnings("unused")
	private int unk4; // new in C4
	
	@Override
	protected void readImpl()
	{
		charSlot = readD();
		unk1 = readH();
		unk2 = readD();
		unk3 = readD();
		unk4 = readD();
	}
	
	@Override
	public void runImpl()
	{
		// if there is a playback.dat file in the current directory, it will
		// be sent to the client instead of any regular packets
		// to make this work, the first packet in the playback.dat has to
		// be a [S]0x21 packet
		// after playback is done, the client will not work correct and need to exit
		// playLogFile(getConnection()); // try to play log file
		
		// we should always be abble to acquire the lock
		// but if we cant lock then nothing should be done (ie repeated packet)
		if (getClient().getActiveCharLock().tryLock())
		{
			try
			{
				// should always be null
				// but if not then this is repeated packet and nothing should be done here
				if (getClient().getActiveChar() == null)
				{
					// The L2PcInstance must be created here, so that it can be attached to the L2GameClient
					final CharSelectInfoHolder info = getClient().getCharSelectSlot(charSlot);
					if (info == null)
					{
						return;
					}
					
					// Selected character is banned. Acts like if nothing occured...
					if (info.getAccessLevel() < 0)
					{
						return;
					}
					
					// load up character from disk
					L2PcInstance cha = getClient().loadCharFromDisk(charSlot);
					if (cha == null)
					{
						return;
					}
					
					cha.setClient(getClient());
					getClient().setActiveChar(cha);
					cha.setOnlineStatus(true);
					// nProtect.getInstance().sendRequest(getClient());
					getClient().setState(GameClientState.IN_GAME);
					
					cha.setClient(getClient());
					CharNameData.getInstance().addName(cha);
					sendPacket(new CharSelected(cha, getClient().getSessionId().playOkID1));
				}
			}
			finally
			{
				getClient().getActiveCharLock().unlock();
			}
		}
	}
}
