// Odessa 2016

package com.Odessa.utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.Odessa.utility.LittleEndianOutputStream;

// This provides the ability to create byte streams out of objects/primitives
// Incredibly useful for sending packets
public class PacketHelper {
    public static boolean debug = false;
    public static void writeObjToStream(LittleEndianOutputStream dos, Object o) throws Exception{
		
        if (o instanceof Object[])
            for (Object p : (Object[])o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof ArrayList)
            for (Object p : (ArrayList)o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof byte[])
            for (Byte p : (byte[])o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof Integer)
            dos.writeInt((int)o);
        else if (o instanceof Float)
            dos.writeFloat((float)o);
        else if (o instanceof Byte)
            dos.write((byte)o);
        else if (o instanceof Boolean)
            dos.writeBoolean((Boolean)o);
        else if (o instanceof Short)
            dos.writeShort((short)o);
        else if (o instanceof String){
            dos.writeBytes((String)o);
            dos.write(0);
        }else if (o instanceof Double)
            dos.writeDouble((Double)o);
        else if (o instanceof Boolean){
			dos.writeBoolean((Boolean)o);
        //else if (o instanceof shortvec2){
        //	shortvec2 p = (shortvec2)o;
        //	dos.writeShort(p.x);
        //	dos.writeShort(p.y);
        }else if (o instanceof vec2){
        	vec2 p = (vec2)o;
        	dos.writeFloat(p.x);
        	dos.writeFloat(p.y);
        }else{
			throw new Exception("derp " + o);
		}
    }
    
    public static byte[] bytesFromParams(Object... args) throws Exception {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	LittleEndianOutputStream dos = new LittleEndianOutputStream(out);
    	
    	
        for (Object o : args)
            writeObjToStream(dos, o);
        try {
			dos.close();
	        byte[] a = out.toByteArray();
	        out.close();
	        return a;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new byte[0];
    }
	
	// Utility functions
	public static final float RADIANSTOBYTE = 40.58f;
	public static byte getAngleByte(float angle){
		return (byte)((angle % 6.2832f) * RADIANSTOBYTE);
	}
	
}
