
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

public class Room
{
    static Dictionary<GameObject, Room> mappedRooms = new Dictionary<GameObject, Room>();
    public static Dictionary<int, Room> typeToRoom = new Dictionary<int, Room>();
    public static Dictionary<int, Room> IDToRoom = new Dictionary<int, Room>();
    Vector2 coordinates = new Vector2();
    Tile[] tiles = new Tile[9];
    GameObject go;
    int type;
    int orientation;

    override
    public string ToString()
    {
        return "ROOM Type: " + type + " Orientation: " + orientation;
    }

    public static byte[] RoomToByteStream(GameObject go)
    {
        Transform t = go.transform.Find("Background");
        if (t != null) {
            List<byte> bytes = new List<byte>();
            for (int i = 0; i < t.childCount; i++)
            {
                Transform tile = t.GetChild(i);
                Transform walls = tile.Find("Walls");
                for (int j = 0; j < walls.childCount; j++)
                {
                    GameObject wall = walls.GetChild(j).gameObject;
                    bytes.Add((byte)(wall.active ? 1 : 0));
                }
            }
            return bytes.ToArray();
        }
        throw new Exception("Not a valid Room GameObject");
    }

    public Room(Vector2 coords,Tile[] tiles, int orientation, int type)
    {
        coordinates = coords;  
        this.tiles = tiles;
        this.type = type;
        typeToRoom.Add(type, this);
        this.orientation = orientation;
    }
    public Room(Tile[] tiles, int orientation, int type)
    {
        this.tiles = tiles;
        this.type = type;
        typeToRoom.Add(type, this);
        this.orientation = orientation;
    }

    public void changeDoorSate(int vec, int coord, bool open)
    {
        tiles[vec].changeDoorSate(coord, open);
    }

    public byte[] getBytes()
    {
        List<byte> bytes = new List<byte>();
        foreach(Tile t in tiles)
            bytes.AddRange(t.getBytes());
        return bytes.ToArray();
    }

    public Room clone()
    {
        Room r = new Room(tiles, orientation, type);
        if(coordinates != null)
         r.coordinates = coordinates;
        if (go != null)
            r.go = go;
        return r;
    }

    public GameObject createGameObject(Transform parent,Vector2 pos, int ID)
    {
        GameObject go = GameObject.Instantiate(Consts.instance.RoomPrefab, parent);
        go.SetActive(true);
        Room r = clone();
        r.coordinates = pos;
        go.transform.position = pos;
        mappedRooms.Add(go, r);
        IDToRoom.Add(ID, r);
        Transform t = go.transform.Find("Background");
        for(int i=0;i< tiles.Length;i++)
        {
            Tile tile = tiles[i];
            Wall[] walls = tile.walls;
            Transform block = t.GetChild(i);
            tile.setReferenceObject(block.gameObject);
            Transform wallParent = block.Find("Walls");
            for(int j =0; j < walls.Length; j++)
            {
                Wall wall = walls[j];
                GameObject wallGo = wallParent.GetChild(j).gameObject;
                wall.setReferenceObject(wallGo, wall.open);
            }
        }
        return go;
    }
}