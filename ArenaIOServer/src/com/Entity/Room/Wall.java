package com.Entity.Room;

import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.vec2;

public class Wall {
	public byte atPoint;
	public byte orientation;
	public boolean open; // Is door
	
	public Wall(byte atPoint,byte orientation, boolean open){
		this.atPoint = atPoint;
		this.orientation = orientation;
		this.open = open;
	}
	
	public void setIsOpen(boolean open){
		this.open = open;
	}
	
	/*//Maybe send
	 * byte[] getSpawnPacket() throws Exception {
		// Don't need to send the coords because we will send them in order
		// ORDER: TL TM TR, ML MM MR, BL BM BR
		return PacketHelper.bytesFromParams(
				orientation,
				open
				); 
	}*/	
}
