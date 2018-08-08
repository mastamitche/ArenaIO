using UnityEngine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public class Entity: MonoBehaviour
{
    public static Dictionary<int, Entity> Entities = new Dictionary<int, Entity>();
    public int ID = -1;

    public void SetEnt(int ID)
    {
        this.ID = ID;
        Entities.Remove(ID);
        Entities.Add(ID, this);
    }

    public static GameObject Destroy(int ID)
    {
        Entity en = Entities[ID];
        bool destroyed = Entities.Remove(ID);
        if (destroyed)
            return en.gameObject;
        else
            print("Could not find Entity " + ID);
        return null;
    }
}

public enum EntityTypes
{
    TYPE_PLAYER = 0,
    TYPE_SHOTGUN = 1,
    TYPE_BULLET = 2,
    TYPE_AMMO = 3,
    TYPE_ARMOUR = 4,
    TYPE_HEALTHPACK = 5
}
