using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

public class Tile
{
    public Wall[] walls = new Wall[4];
    public int coord = -1;
    GameObject reference;

    public Tile(Wall[] walls, int coord)
    {
        this.walls = walls;
        this.coord = coord;
    }

    public void setReferenceObject(GameObject go)
    {
        reference = go;
    }


    public void changeDoorSate(int door, bool open)
    {
        if (door < walls.Length)
            walls[door].changeDoorState(open);
    }

    public byte[] getBytes()
    {
        List<byte> bytes = new List<byte>();
        foreach(Wall wall in walls)
            bytes.AddRange(wall.getBytes());
        return bytes.ToArray();
    }
}