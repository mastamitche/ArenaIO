using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SmartUITextGroupItem : MonoBehaviour {
    public string TextGroupParentName;

	// Use this for initialization
	void Start () {
        SmartUITextGroup.AddBeforeInit(TextGroupParentName,gameObject);
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
