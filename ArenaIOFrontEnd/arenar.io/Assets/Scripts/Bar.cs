using UnityEngine;
using System.Collections;

public class Bar : MonoBehaviour {
    GameObject displayBar;
	// Use this for initialization
	void Start () {
        displayBar = transform.Find("Foreground").gameObject;
        PlayerInfo.OnHPChange += updateDisplayBar;
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    public void updateDisplayBar(int currentHp, int maxHP)
    {
        Vector3 currScale = displayBar.transform.localScale;
        Debug.Log("Current Hp : " + currentHp);
        Debug.Log("Max HP : " + maxHP);
        displayBar.transform.localScale=new Vector3((currentHp+0.0f)/(maxHP+0.0f), currScale.y, currScale.z);
    }
}
