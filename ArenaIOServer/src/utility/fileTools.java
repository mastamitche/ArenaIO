package utility;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class fileTools {

	public static String store(InputStream is) throws IOException {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
		StringWriter sw = new StringWriter();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) break;
			sw.write(line+"\n");
		}
		is.close();
		return sw.toString();
	}

	public static String store(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		return store(fis);
	}
	public static String store(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		return store(fis);
	}
	
	public static void writeDataToFile(String file, byte[] data) {
		File f = new File(file);
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public static byte[] readDataFromFile(String file) {
		Path path = Paths.get(file);
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
    }
	
}
