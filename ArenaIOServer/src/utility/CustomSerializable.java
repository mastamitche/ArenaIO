package utility;

import java.io.DataOutputStream;

public interface CustomSerializable {
	public void writeData(LittleEndianOutputStream dos) throws Exception;
	public void writeDataJava(DataOutputStream dos) throws Exception;
}
