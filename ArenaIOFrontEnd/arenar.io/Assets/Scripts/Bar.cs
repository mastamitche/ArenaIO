using UnityEngine;
using System.Collections;

public class Bar : MonoBehaviour {
    public GameObject displayBar;
	// Use this for initialization
	void Start () {
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    public void updateDisplayBar(int hp, int maxHP)
    {
        Vector3 currScale = displayBar.transform.localScale;
        displayBar.transform.localScale=new Vector3((hp+0.0f)/(maxHP+0.0f), currScale.y, currScale.z);
    }
}
