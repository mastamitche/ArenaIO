// Odessa 2016

package utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

// This provides the ability to create byte streams out of objects/primitives
// Incredibly useful for sending packets
public class PacketHelper {
    public static boolean debug = false;
    
    
    public static void writeObjToJavaStream(DataOutputStream dos, Object o){
		try {
			if (debug){
				if (o instanceof Object[])
		            System.out.println("Object[]");
				else if (o instanceof byte[])
		            System.out.println("byte[]");
		        else if (o instanceof Integer)
		            System.out.println("Integer");
		        else if (o instanceof Float)
		            System.out.println("Float");
		        else if (o instanceof Byte)
		            System.out.println("Byte");
		        else if (o instanceof Short)
		            System.out.println("Short");
		        else if (o instanceof String){
		            System.out.println("String");
		        }else if (o instanceof Double)
		            System.out.println("Double");
		        else if (o instanceof Boolean)
		            System.out.println("bool");
		        else if (o instanceof Long)
		            System.out.println("long " + o);
				else{
		            System.out.println("UNKNOWN DATA TYPE " + o.getClass() + " " + o.toString());
				}
			}
			
			
	        if (o instanceof Object[])
	            for (Object p : (Object[])o)
	                writeObjToJavaStream(dos, p); // Array, we're going deeper!
	        else if (o instanceof CustomSerializable)
	            ((CustomSerializable)o).writeDataJava(dos);
	        else if (o instanceof List)
	        	synchronized(o){
	        		for (Object p : (List)o)
	        			writeObjToJavaStream(dos, p); // Array, we're going deeper!
	        	}
	        else if (o instanceof byte[])
	            for (Byte p : (byte[])o)
	            	writeObjToJavaStream(dos, p); // Array, we're going deeper!
	        else if (o instanceof HashMap){  // Hashmap, we're going deeper!
	        	Iterator<Entry<Object, Object>> iterator = ((HashMap)o).entrySet().iterator();
	        	while(iterator.hasNext()){
		        	Entry<Object, Object> entry = iterator.next();
		        	writeObjToJavaStream(dos, entry.getKey());
		        	writeObjToJavaStream(dos, entry.getValue());
		        }
	        }else if (o instanceof Integer)
	            dos.writeInt((int)o);
	        else if (o instanceof Long)
	            dos.writeLong((long)o);
	        else if (o instanceof Float)
	            dos.writeFloat((float)o);
	        else if (o instanceof Byte)
	            dos.write((byte)o);
	        else if (o instanceof Short)
	            dos.writeShort((short)o);
	        else if (o instanceof String){
	            dos.writeUTF((String)o);
	        }else if (o instanceof Double)
	            dos.writeDouble((Double)o);
	        else if (o instanceof Boolean)
				dos.writeBoolean((Boolean)o);
	        else if (o instanceof Vector2){
	        	Vector2 p = (Vector2)o;
	        	dos.writeFloat(p.x);
	        	dos.writeFloat(p.y);
	        }else{
	            try {
					throw new Exception("derp");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }

    public static byte[] bytesFromParamsJava(Object... args) {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	DataOutputStream dos = new DataOutputStream(out);
    	
        for (Object o : args)
            writeObjToJavaStream(dos, o);
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
    
    
    
    public static void writeObjToStream(LittleEndianOutputStream dos, Object o) throws Exception{
		
    	if (debug){
			if (o instanceof Object[])
	            System.out.println("Object[]");
			else if (o instanceof int[])
	            System.out.println("int[]");
			else if (o instanceof ArrayList)
	            System.out.println("ArrayList");
			else if (o instanceof byte[])
	            System.out.println("byte[]");
	        else if (o instanceof Integer)
	            System.out.println("Integer");
	        else if (o instanceof Long)
	            System.out.println("long " + o);
	        else if (o instanceof Float)
	            System.out.println("Float");
	        else if (o instanceof Byte)
	            System.out.println("Byte");
	        else if (o instanceof Short)
	            System.out.println("Short");
	        else if (o instanceof String){
	            System.out.println("String");
	        }else if (o instanceof Double)
	            System.out.println("Double");
	        else if (o instanceof Boolean)
	            System.out.println("bool");
			else{
	            System.out.println("UNKNOWN DATA TYPE " + o.getClass() + " " + o.toString());
			}
		}
    	
        if (o instanceof Object[])
            for (Object p : (Object[])o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof CustomSerializable)
            ((CustomSerializable)o).writeData(dos);
        else if (o instanceof ArrayList)
            for (Object p : (ArrayList)o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof byte[])
            for (Byte p : (byte[])o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof int[])
            for (int p : (int[])o)
                writeObjToStream(dos, p); // Array, we're going deeper!
        else if (o instanceof HashMap){  // Hashmap, we're going deeper!
        	Iterator<Entry<Object, Object>> iterator = ((HashMap)o).entrySet().iterator();
        	while(iterator.hasNext()){
	        	Entry<Object, Object> entry = iterator.next();
	        	writeObjToStream(dos, entry.getKey());
	        	writeObjToStream(dos, entry.getValue());
	        }
        }else if (o instanceof Integer){
            dos.writeInt((int)o);
        }else if (o instanceof Float)
            dos.writeFloat((float)o);
        else if (o instanceof Long)
            dos.writeLong((long)o);
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
        }else if (o instanceof Vector2){
        	Vector2 p = (Vector2)o;
        	dos.writeFloat(p.x);
        	dos.writeFloat(p.y);
        }else{
            System.out.println("UNKNOWN DATA TYPE " + o.getClass() + " " + o.toString());
			throw new Exception("derp " + o);
		}
    }
    
    public static LittleEndianOutputStream newStream(){
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	return new LittleEndianOutputStream(out);
    }
    public static byte[] streamToBytes(LittleEndianOutputStream dos){
    	try {
			dos.close();
	        byte[] a = ((ByteArrayOutputStream)dos.out).toByteArray();
	        dos.out.close();
	        return a;
		} catch (IOException e) { e.printStackTrace(); }
        return new byte[0];
    }
    
    public static byte[] bytesFromParams(Object... args) {
    	try {
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
    	} catch (Exception e){
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
