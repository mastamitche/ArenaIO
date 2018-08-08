using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

public class PlayerWeaponController : MonoBehaviour {
    public static string[] gunNames = new string[]
    {
        "",
        "ShotGun",
        "Chainsaw",
        "Saw Gun"
    };


    GameObject currentWeapon;
    public GameObject gunLocation;

	// Use this for initialization
	void Start () {
	    
	}
	
	// Update is called once per frame
	void Update () {
	}

    public void swapWeapon(GameObject newWeapon)
    {
        currentWeapon = newWeapon;
        newWeapon.transform.position = gunLocation.transform.position;
        newWeapon.transform.rotation = gunLocation.transform.rotation;
        newWeapon.transform.SetParent(gunLocation.transform.parent);
    }

}
