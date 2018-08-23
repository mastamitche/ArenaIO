using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEditor;
using UnityEngine;

public class RoomManager : MonoBehaviour {
    public static byte Version = 1;
    public static byte RoomTypeCount = 0;
    public static List<Room> ROOMS = new List<Room>();
    public GameObject RoomEditor;
    public Transform parentForRooms;

    public static RoomManager instance;

    static bool byteIntepreter(byte b)
    {
        return b == 1 ? true : false;
    }

    void Start()
    {
        instance = this;
        RoomEditor.SetActive(false);
        LoadRooms();
    }
    [MenuItem("RoomTools/Reload Rooms")]
    public static void LoadRooms()
    {
        ROOMS.Clear();
        RoomTypeCount = 0;
        print("Loading Rooms from files...");
        foreach (string file in Directory.GetFiles(Directory.GetCurrentDirectory() + "/Assets/scripts/Rooms/"))
        {
            Room room = LoadRoom(file);
            if (room != null)
            {
                ROOMS.Add(room);
                RoomTypeCount++;
                print("Loaded " + RoomTypeCount);
            }
        }
        print("Room Loading Complete. Total Rooms "+ RoomTypeCount);
    }

    [MenuItem("RoomTools/Save Room")]
    public static void SaveRoom()
    {
        GameObject selected =GameObject.Find("RoomBlock");
        if (selected != null)
        {
            print("Saving " + RoomTypeCount + "... ");
            try
            {
                string path = Directory.GetCurrentDirectory() + "/Assets/scripts/Rooms/" + (RoomTypeCount) + ".room";
                string pathToServer = Path.GetFullPath(Path.Combine(Directory.GetCurrentDirectory(), @"..\..\")) + "/ArenaIOServer/rooms/" + (RoomTypeCount++) + ".room";
                FileStream f = File.Create(path);
                FileStream fServer = File.Create(pathToServer);
                List<byte> bytes = new List<byte>();
                bytes.Add(Version);
                bytes.AddRange(Room.RoomToByteStream(selected));
                MemoryStream stream = new MemoryStream(bytes.ToArray());
                stream.WriteTo(f);
                stream.WriteTo(fServer);
                print("Saved successfully");
            }catch(Exception e)
            {
                Debug.LogError("Error writing to file "+ selected.name);
                print(e.Message);
            }
        }
    }
    [MenuItem("RoomTools/Dump Rooms")]
    public static void DumpRooms()
    {
        foreach (Room room in ROOMS)
            print(room.ToString());
    }
    public GameObject CreateRoom(int type, Vector2 position, int ID)
    {
        Room room = Room.typeToRoom[type];
        return room.createGameObject(parentForRooms, position, ID);
    }

    public static Room LoadRoom(string path)
    {
        try
        {
            using (MemoryStream stream = new MemoryStream(File.ReadAllBytes(path)))
            {
                using (BinaryReader reader = new BinaryReader(stream))
                {
                    int version = reader.ReadByte();
                    if (version == 1)
                    {

                        Tile[] tiles = new Tile[9];
                        for (byte i = 0; i < 9; i++)
                        {
                            Wall[] walls = new Wall[]{
                                new Wall (i,(byte)0, byteIntepreter(reader.ReadByte())) ,
                                new Wall (i,(byte)1, byteIntepreter(reader.ReadByte())) ,
                                new Wall (i,(byte)2, byteIntepreter(reader.ReadByte())) ,
                                new Wall (i,(byte)3, byteIntepreter(reader.ReadByte()))
                            };
                            tiles[i] = new Tile(walls, i);
                        }
                        //Tiles, orientation, type
                        Room room = new Room(tiles,(byte)0, (byte)0);
                        return room;
                    }
                }
            }
        }
        catch (IOException e)
        {
            print("Failed to load File " + new FileInfo(path).Name);
            print(e.StackTrace);
        }
        return null;
    }
}
