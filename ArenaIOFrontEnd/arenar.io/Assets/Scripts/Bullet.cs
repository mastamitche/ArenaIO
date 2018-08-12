using UnityEngine;
using System.Collections;

public class Bullet : Entity {

    Vector3 velocity;

    // Use this for initialization
    void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if(velocity != null)
        {
            transform.position += velocity * Time.fixedDeltaTime;
        }
	}

    public void setVelocity( Vector3 v3)
    {
        velocity = v3;
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
