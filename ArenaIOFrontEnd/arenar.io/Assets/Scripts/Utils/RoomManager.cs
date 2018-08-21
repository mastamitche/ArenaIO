using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

public class RoomManager : MonoBehaviour {
    public static Room LoadRoom(File f)
    {
        try
        {
            ByteBuffer arr = ByteBuffer.wrap(Files.readAllBytes(f.toPath()));
            int version = arr.getInt();
            if (version == 1)
            {
                Tile[] tiles = new Tile[9];
                for (byte i = 0; i < 9; i++)
                {
                    Wall[] walls = new Wall[]{
                        new Wall (i,(byte)0, byteIntepreter(arr.get())) ,
                        new Wall (i,(byte)1, byteIntepreter(arr.get())) ,
                        new Wall (i,(byte)2, byteIntepreter(arr.get())) ,
                        new Wall (i,(byte)3, byteIntepreter(arr.get()))
                    };
                    tiles[i] = new Tile(walls, i);
                }
                Room room = new Room((byte)0, (byte)0);
                room.tiles = tiles;
                return room;
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed to load File " + f.getName());
            e.printStackTrace();
        }
        return null;
    }

    // Use this for initialization
    void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
