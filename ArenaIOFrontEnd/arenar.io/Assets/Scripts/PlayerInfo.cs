using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class PlayerInfo : MonoBehaviour {

    public Text ammoText;
    public Text ReloadText;
    public Text GunName;

	// Use this for initialization
	void Start () {
        ammoText.gameObject.SetActive(false);
        ReloadText.gameObject.SetActive(false);
        GunName.gameObject.SetActive(false);
        Networking.PlayerInfo += changeAmmo;
        Networking.PlayerGun += changeGunName;
    }
	
    public void changeGunName()
    {
        if (LocalState.GunID == -1) return;
        GunName.text = PlayerWeaponController.gunNames[LocalState.GunID];
        ammoText.gameObject.SetActive(true);
        GunName.gameObject.SetActive(true);
    }


    public void changeAmmo()
    {
        if (LocalState.GunID == -1) return;
        ammoText.text = LocalState.Magazine + " / " + LocalState.MagazineSize + " : " + LocalState.Ammo;
        if(LocalState.Magazine == 0)
        {
            ReloadText.gameObject.SetActive(true);
        }else
        {
            ReloadText.gameObject.SetActive(false);
        }
    }

}
