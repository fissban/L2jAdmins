package l2j.gameserver.network.external.server;

import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.entity.clanhalls.ClanHallFunction;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Steuf
 */
public class ClanHallDecoration extends AServerPacket
{
	private final ClanHall clanHall;
	
	public ClanHallDecoration(ClanHall clanHall)
	{
		this.clanHall = clanHall;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf7);
		writeD(clanHall.getId()); // clanhall id
		
		// FUNC_RESTORE_HP
		ClanHallFunction function = clanHall.getFunction(ClanHallFunctionType.RESTORE_HP);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (((clanHall.getGrade() == 0) && (function.getLvl() < 220)) || ((clanHall.getGrade() == 1) && (function.getLvl() < 160)) || ((clanHall.getGrade() == 2) && (function.getLvl() < 260)) || ((clanHall.getGrade() == 3) && (function.getLvl() < 300)))
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		
		// FUNC_RESTORE_MP
		function = clanHall.getFunction(ClanHallFunctionType.RESTORE_MP);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
			writeC(0);
		}
		else if ((((clanHall.getGrade() == 0) || (clanHall.getGrade() == 1)) && (function.getLvl() < 25)) || ((clanHall.getGrade() == 2) && (function.getLvl() < 30)) || ((clanHall.getGrade() == 3) && (function.getLvl() < 40)))
		{
			writeC(1);
			writeC(1);
		}
		else
		{
			writeC(2);
			writeC(2);
		}
		
		// FUNC_RESTORE_EXP
		function = clanHall.getFunction(ClanHallFunctionType.RESTORE_EXP);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (((clanHall.getGrade() == 0) && (function.getLvl() < 25)) || ((clanHall.getGrade() == 1) && (function.getLvl() < 30)) || ((clanHall.getGrade() == 2) && (function.getLvl() < 40)) || ((clanHall.getGrade() == 3) && (function.getLvl() < 50)))
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		
		// FUNC_TELEPORT
		function = clanHall.getFunction(ClanHallFunctionType.TELEPORT);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (function.getLvl() < 2)
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		writeC(0);
		
		// CURTAINS
		function = clanHall.getFunction(ClanHallFunctionType.DECO_CURTAINS);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (function.getLvl() <= 1)
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		
		// FUNC_ITEM_CREATE
		function = clanHall.getFunction(ClanHallFunctionType.ITEM_CREATE);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (((clanHall.getGrade() == 0) && (function.getLvl() < 2)) || (function.getLvl() < 3))
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		
		// FUNC_SUPPORT
		function = clanHall.getFunction(ClanHallFunctionType.SUPPORT);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
			writeC(0);
		}
		else if (((clanHall.getGrade() == 0) && (function.getLvl() < 2)) || ((clanHall.getGrade() == 1) && (function.getLvl() < 4)) || ((clanHall.getGrade() == 2) && (function.getLvl() < 5)) || ((clanHall.getGrade() == 3) && (function.getLvl() < 8)))
		{
			writeC(1);
			writeC(1);
		}
		else
		{
			writeC(2);
			writeC(2);
		}
		
		// Front Platform
		function = clanHall.getFunction(ClanHallFunctionType.DECO_FRONTPLATEFORM);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (function.getLvl() <= 1)
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		
		// FUNC_ITEM_CREATE
		function = clanHall.getFunction(ClanHallFunctionType.ITEM_CREATE);
		if ((function == null) || (function.getLvl() == 0))
		{
			writeC(0);
		}
		else if (((clanHall.getGrade() == 0) && (function.getLvl() < 2)) || (function.getLvl() < 3))
		{
			writeC(1);
		}
		else
		{
			writeC(2);
		}
		writeD(0);
		writeD(0);
	}
}
