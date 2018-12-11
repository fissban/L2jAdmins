package l2j.gameserver.network.external.client;

import l2j.gameserver.data.PetNameData;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/04/06 16:13:48 $
 */
public class RequestChangePetName extends AClientPacket
{
	private String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2Summon pet = activeChar.getPet();
		if (pet == null)
		{
			return;
		}
		
		if (pet.getName() != null)
		{
			activeChar.sendPacket(SystemMessage.NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET);
			return;
		}
		
		if (PetNameData.getInstance().doesPetNameExist(name, pet.getTemplate().getId()))
		{
			activeChar.sendPacket(SystemMessage.NAMING_ALREADY_IN_USE_BY_ANOTHER_PET);
			return;
		}
		
		if ((name.length() < 1) || (name.length() > 16))
		{
			activeChar.sendMessage("Your pet's name can be up to 16 characters.");
			return;
		}
		
		if (!Util.isValidNameTitle(name))
		{
			activeChar.sendPacket(SystemMessage.NAMING_PETNAME_CONTAINS_INVALID_CHARS);
			return;
		}
		
		pet.setName(name);
		pet.updateAndBroadcastStatus(1);
	}
}
