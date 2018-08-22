using Assets.Scripts.Networking.ADT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

class Room
{
    Vector2 coordinates = new Vector2();
    Tile[] tiles = new Tile[9];
    GameObject go;

    public Room(Vector2 coords,Tile[] tiles)
    {
        coordinates = coords;  
        this.tiles = tiles;
        go = createGameObject();
    }

    public void changeDoorSate(int vec, int coord, bool open)
    {
        tiles[vec].changeDoorSate(coord, open);
    }

    public GameObject createGameObject()
    {
        GameObject go = GameObject.Instantiate(Consts.instance.RoomPrefab);
        Transform t = go.transform.Find("Background");
        for(int i=0;i< tiles.Length;i++)
        {
            Tile tile = tiles[i];
            Wall[] walls = tile.walls;
            Transform block = t.GetChild(i);
            Transform wallParent = block.Find("Walls");
            for(int j =0; j < walls.Length; j++)
            {
                Wall wall = walls[j];
                GameObject wallGo = wallParent.GetChild(j).gameObject;
                wallGo.SetActive(wall.open);
            }
        }
        return go;
    }
}