using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
[ExecuteInEditMode]
public class ScreenHeightText : MonoBehaviour {
    public float percentage = 20f;

    private Text text;
    private float lastPercentage = 0;
    private float lastScreenHeight = 0;

	// Use this for initialization
	void Start () {
        text = GetComponent<Text>();
        setFontSize();
    }
    private void setFontSize()
    {
        text.fontSize = (int)Mathf.Ceil(Screen.height / percentage);
    }

#if (UNITY_EDITOR)
    // Update is called once per frame
    public void Update () {
		if(percentage != lastPercentage || lastScreenHeight != Screen.height)
        {
            //print("Updating to " + percentage);
            setFontSize();
            lastPercentage = percentage;
            lastScreenHeight = Screen.height;
        }
	}
#endif
}
