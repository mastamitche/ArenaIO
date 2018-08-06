using UnityEngine;
using System.Collections;

public class GroundArmor : Pickupable {
    public int armorAmount;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public override bool pickUP(GameObject player)
    {
        PlayerInfo pi = player.GetComponent<PlayerInfo>();
        //refil armor
        return true;
    }
}
