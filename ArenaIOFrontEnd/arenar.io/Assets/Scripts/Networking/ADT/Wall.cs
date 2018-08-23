using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

public class Wall
{
    public int orientation = -1;
    public int coord;
    public bool open = false;
    GameObject reference;

    public Wall( int coord, int orientation, bool open)
    {
        this.coord = coord;
        this.orientation = orientation;
        this.open = open;
    }

    public void setReferenceObject(GameObject go, bool open)
    {
        reference = go;
        changeDoorState(open);
    }

    public void changeDoorState( bool open)
    {
        this.open = open;
        if(reference)
            reference.SetActive(open);
    }
    public byte[] getBytes()
    {
        return new byte[]
        {
            (byte)(open? 1:0)
        };
    }
}
