using UnityEngine;
using System.Collections;

public class Bullet : MonoBehaviour {
    public float initalSpeed;
    public int damageAmount = 0;

    Vector3 velocity;
	// Use this for initialization
	void Start () {
	
	}

    public void setDamageAmount(int _dmg)
    {
        damageAmount = _dmg;
    }
	
	// Update is called once per frame
	void Update () {
	}

    void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.gameObject.layer == LayerMask.NameToLayer("Player") || collision.gameObject.layer == LayerMask.NameToLayer("Background"))// || collision.gameObject.layer == LayerMask.NameToLayer("Environment"))
        {
            if(collision.gameObject.layer == LayerMask.NameToLayer("Player"))
                if (collision.gameObject.name != "MainPlayer")
                {
                    PlayerInfo pi = collision.gameObject.GetComponent<PlayerInfo>();
                    pi.takeDamage(damageAmount);
                }
            Destroy(gameObject);
        }
    }
}
