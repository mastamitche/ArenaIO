#if ISERVER
using Ayx.BitIO;
#endif
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Security.Cryptography;
using System.Text;

public class NetworkingUtility {
    
    public static void log(String s) {
        UnityEngine.Debug.Log(s);
    }

    void printBytes(byte[] bytes){
        String s = "";
        foreach (byte b in bytes)
            s += b + " ";
        NetworkingUtility.log(s);
    }


    public static void writeObjToStream(BinaryWriter writer, object o){
        if (o is object[]) // primitive array
            foreach (object p in (object[])o)
                writeObjToStream(writer, p); // Array, we're going deeper!
        else if (o is Object[]) // Object array
            foreach (Object p in (Object[])o)
                writeObjToStream(writer, p); // Array, we're going deeper!
        else if (o is Byte[]) // Byte array
            foreach (Byte p in (Byte[])o)
                writeObjToStream(writer, p); // Array, we're going deeper!
        else if (o is int)
            writer.Write((int)o);
        else if (o is long)
            writer.Write((long)o);
        else if (o is float)
            writer.Write((float)o);
        else if (o is byte)
            writer.Write((byte)o);
        else if (o is short)
            writer.Write((short)o);
        else if (o is string)
            WriteString(writer, (string)o);
        else if (o is double)
            writer.Write((double)o);
        else if (o is UnityEngine.Vector3)
        {
            writer.Write(((UnityEngine.Vector3)o).x);
            writer.Write(((UnityEngine.Vector3)o).y);
            writer.Write(((UnityEngine.Vector3)o).z);
        }
        else if (o is bool)
            writer.Write((bool)o);
        else
            NetworkingUtility.log("UNKNOWN DATA TYPE " + o.GetType());
    }

    public static byte[] bytesFromParams(params object[] args)
    {
        using (MemoryStream stream = new MemoryStream())
            using (BinaryWriter writer = new BinaryWriter(stream))
            {
                foreach (object o in args)
                    writeObjToStream(writer, o);
                return stream.ToArray();
            }
    }


    public static double log2(int n){
        return (Math.Log(n) / Math.Log(2));
    }


    public static string ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.Length * 2);
        foreach (byte b in ba)
            hex.AppendFormat("{0:x2}", b);
        return hex.ToString();
    }

    public static String createSHA(String s) {
        var data = Encoding.UTF8.GetBytes(s);
        using (SHA512 shaM = new SHA512Managed()) {
            return ByteArrayToString(shaM.ComputeHash(data));
        }
    }

    public static String ReadString(BinaryReader br) {
        String s = "";
        while (true) {
            byte b = br.ReadByte();
            if (b == 0) break;
            s += (char)b;
        }
        return s;
    }
    public static void WriteString(BinaryWriter bw, String s) {
        bw.Write(Encoding.ASCII.GetBytes(s));
        bw.Write((byte)0);
    }

}
