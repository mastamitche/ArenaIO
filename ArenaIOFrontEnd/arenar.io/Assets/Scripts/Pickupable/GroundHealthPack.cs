using UnityEngine;
using System.Collections;

public class GroundHealthPack : Pickupable {
    public int healAmount = 0;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public override bool pickUP(GameObject player)
    {
        PlayerInfo pi = player.GetComponent<PlayerInfo>();
        pi.heal(healAmount);
        return true;
    }
}
