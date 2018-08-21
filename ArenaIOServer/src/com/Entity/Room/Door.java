package com.Entity.Room;

import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Door implements java.io.Serializable{
	
	public vec2 coordinates;
	public byte orientation;
	public boolean open;
	public boolean enabled;
	
	
	public Door(vec2 pos, byte orientation, boolean open){
		this.coordinates = pos;
		this.orientation = orientation;
		this.open = open;
	}
	
	public void setIsOpen(boolean open){
		this.open = open;
	}
	byte[] getSpawnPacket() throws Exception {
		// Don't need to send the coords because we will send them in order
		// ORDER: TL TM TR, ML MM MR, BL BM BR
		return PacketHelper.bytesFromParams(
				enabled,
				orientation,
				open
				); 
	}
	
}
