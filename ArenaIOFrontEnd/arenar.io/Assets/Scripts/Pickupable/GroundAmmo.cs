using UnityEngine;
using System.Collections;

public class GroundAmmo : Pickupable {
    public int ammoAmount = 0;
    GameObject textMesh;
    
	// Use this for initialization
	void Start () {
        textMesh = transform.Find("Text").gameObject;
        updateTextAmount(ammoAmount);
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    public void updateTextAmount(int amount)
    {
        ammoAmount = amount;
        TextMesh tm = textMesh.GetComponent<TextMesh>();
        tm.text = amount + "";
    }

    public override bool pickUP(GameObject player)
    {
        Debug.Log("Picking up ammo. Amount : " + ammoAmount);
        PlayerWeaponController pwc = player.GetComponent<PlayerWeaponController>();
        if (pwc == null) return false;
        int retAmount = pwc.pickUpAmmo(ammoAmount);
        updateTextAmount(retAmount);
        Debug.Log("Ammo left over : " + retAmount);
        if (retAmount <= 0)
            return true;
        return false;
    }
}
