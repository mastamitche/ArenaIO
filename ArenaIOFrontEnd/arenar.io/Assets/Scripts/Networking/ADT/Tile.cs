using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

class Tile
{
    public Wall[] walls = new Wall[4];
    public int coord = -1;
    public GameObject reference;

    public Tile (Wall[] walls, int coord)
    {
        this.walls = walls;
        this.coord = coord;
    }

    public void changeDoorSate(int door, bool open)
    {
        if (door < walls.Length) {
            walls[door].changeDoorState(open);
        }
    }
