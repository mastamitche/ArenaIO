using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

public class PlayerWeaponController : MonoBehaviour {
    GameObject currentWeapon;
    Weapon currentWeaponScript;
    public GameObject weaponText;
    public GameObject currentAmmoText;
    public GameObject ReloadText;
    public GameObject gunLocation;

	// Use this for initialization
	void Start () {
	    
	}
	
	// Update is called once per frame
	void Update () {
        checkControls();
	}

    public void checkControls()
    {
        if (Input.GetMouseButton(0) && currentWeapon != null)
            currentWeaponScript.attemptToShoot();
        /*if (Input.GetKey(KeyCode.R))
            currentWeapon.reload();*/
    }

    public int pickUpAmmo(int amountPickedUp)
    {
        int difference = currentWeaponScript.addCurrentHoldAmmo(amountPickedUp);
        return difference <= 0 ? 0 : difference ;//get left over ammo to leave on the ground
    }

    public void swapWeapon(GameObject newWeapon)
    {
        currentWeapon = newWeapon;
        currentWeaponScript = newWeapon.GetComponent<Weapon>();
        currentWeaponScript.pickedUp();
        newWeapon.transform.position = gunLocation.transform.position;
        newWeapon.transform.rotation = gunLocation.transform.rotation;
        newWeapon.transform.SetParent(gunLocation.transform.parent);
        if (currentWeapon != null)
        {
            weaponText.SetActive(true);
            currentAmmoText.SetActive(true);
        }
        else
        {
            weaponText.SetActive(false);
            currentAmmoText.SetActive(false);
        }
        Weapon.OnInfoChange += updateCurrentAmmoText;
    }

    public void updateCurrentAmmoText(int[] info)
    {
        updateCurrentAmmoText(info[0], info[1], info[2]);
    }

    public void updateCurrentAmmoText(int _currentAmmo,int _currentMaxAmmo,int _currentHoldAmmo)
    {
        /*if (_currentAmmo <= 0)
        {
            ReloadText.SetActive(true);
        }
        else
        {
            ReloadText.SetActive(false);
        }*/
        Text text = currentAmmoText.GetComponent<Text>();
        text.text = _currentAmmo + " / " + _currentMaxAmmo;// + " : " + _currentHoldAmmo;
    }
}
