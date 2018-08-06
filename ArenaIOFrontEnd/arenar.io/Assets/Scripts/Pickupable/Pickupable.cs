using UnityEngine;
using System.Collections;

public class Pickupable : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
    void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.gameObject.layer == LayerMask.NameToLayer("Player"))
            if(pickUP(collision.gameObject))
                Destroy(gameObject);
    }
    public virtual bool pickUP(GameObject player)
    {
        return true;
    }
}
