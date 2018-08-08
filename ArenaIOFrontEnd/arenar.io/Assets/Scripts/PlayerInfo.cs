using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class PlayerInfo : MonoBehaviour {

    public Text ammoText;
    public Text ReloadText;
    public Text GunName;

    private int gunID = -1;

	// Use this for initialization
	void Start () {
        ammoText.gameObject.SetActive(false);
        ReloadText.gameObject.SetActive(false);
        GunName.gameObject.SetActive(false);
        Networking.PlayerInfo += InfoCross;
    }
    void InfoCross(int id, int health, int armour, int ammo, int magazine, int magazineSize)
    {
        if (id != LocalState.ID) return;
        changeAmmo(magazine, magazineSize, ammo);
    }
	
    public void changeGunName(int ID)
    {
        if (ID == -1) return;
        gunID = ID;
        GunName.text = PlayerWeaponController.gunNames[ID];
        ammoText.gameObject.SetActive(true);
        GunName.gameObject.SetActive(true);
    }


    public void changeAmmo(int magazine, int magazineSize, int ammo)
    {
        if (gunID == -1) return;
        ammoText.text = magazine + " / " + magazineSize + " : " + ammo;
        if(magazine == 0)
        {
            ReloadText.gameObject.SetActive(true);
        }else
        {
            ReloadText.gameObject.SetActive(false);
        }
    }

}
