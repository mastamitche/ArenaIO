using UnityEngine;
using System.Collections;

public class Bullet : Entity {

    // Use this for initialization
    void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	}

    void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.gameObject.layer == LayerMask.NameToLayer("Player") || collision.gameObject.layer == LayerMask.NameToLayer("Background"))
        {
            Networking.onPlayerPickup(ID);
            Destroy(gameObject);
        }
    }
}
