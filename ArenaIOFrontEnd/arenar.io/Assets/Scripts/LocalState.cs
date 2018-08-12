using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
[Serializable]
class LocalState
{
    public static int ID = -1;
    public static int GunID = -1;
    public static int Color = 0;
    public static int Size = 0;
    public static string Name = "";
    public static int Health = 0;
    public static int MaxHealth = 0;
    public static int Armour = 0;
    public static int MaxArmour = 0;
    public static int Ammo = 0;
    public static int Magazine = 0;
    public static int MagazineSize =0;

    public static string dumpState()
    {
        return " ID " + ID + " Gun ID " + " Color " + Color + " Size " + Size + " Name " + Name + " Health " + Health + " Max Health " + MaxHealth + " Armour " + Armour
            + " Max Armour " + MaxArmour + " Ammo " + Ammo + " Magazine " + Magazine + " MagazineSize " + MagazineSize; 
    }
}