using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

class Wall
{
    public int orientation = -1;
    public bool open = false;
    public GameObject reference;

    public Wall(int orientation, bool open)
    {
        this.orientation = orientation;
        this.open = open;
    }
    public void changeDoorSate( bool open)
    {
        this.open = open;
        reference.SetActive(open);
    }
}
