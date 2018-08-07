using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FloatyTextManager : MonoBehaviour {

    public static GameObject Template;
    public GameObject Prefab;
    // Use this for initialization
    void Start()
    {
        Template = Instantiate(Prefab) as GameObject;
        //if (Template != null)
        //    Template.SetActive(false);
    }

    // Update is called once per frame
    void Update () {
		
	}
}
