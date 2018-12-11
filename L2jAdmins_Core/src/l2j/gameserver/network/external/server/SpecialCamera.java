package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class SpecialCamera extends AServerPacket
{
	private final int id;
	private final int dist;
	private final int yaw;
	private final int pitch;
	private final int time;
	private final int duration;
	private final int turn;
	private final int rise;
	private final int widescreen;
	private final int unknown;
	
	public SpecialCamera(int id, int dist, int yaw, int pitch, int time, int duration)
	{
		this.id = id;
		this.dist = dist;
		this.yaw = yaw;
		this.pitch = pitch;
		this.time = time;
		this.duration = duration;
		turn = 0;
		rise = 0;
		widescreen = 0;
		unknown = 0;
	}
	
	public SpecialCamera(int id, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		this.id = id;
		this.dist = dist;
		this.yaw = yaw;
		this.pitch = pitch;
		this.time = time;
		this.duration = duration;
		this.turn = turn;
		this.rise = rise;
		this.widescreen = widescreen;
		unknown = unk;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xc7);
		writeD(id);
		writeD(dist);
		writeD(yaw);
		writeD(pitch);
		writeD(time);
		writeD(duration);
		writeD(turn);
		writeD(rise);
		writeD(widescreen);
		writeD(unknown);
	}
}
