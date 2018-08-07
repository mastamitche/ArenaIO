using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class FloatyText : MonoBehaviour {
    public float travelDistanceP = 10.0f;
    public float animationTime = 2.0f;

    private float startTime = 0;
    private static Dictionary<GameObject, float> currentlyDisplayed = new Dictionary<GameObject, float>();
    private SmartUI parentSUI;

    void Start()
    {
        parentSUI = GetComponent<SmartUI>();
    }
	
	// Update is called once per frame
	void Update ()
    {
        if (currentlyDisplayed.Count > 0)
        {
            List<GameObject> toRemove = new List<GameObject>();//This is for the error while removing from live access dictionary
            foreach (KeyValuePair<GameObject, float> kv in currentlyDisplayed)
                if (kv.Value < Time.fixedTime)
                {
                    kv.Key.SetActive(false);
                    toRemove.Add(kv.Key);
                }
            int i = 0;
            while (i < toRemove.Count)
            {
                currentlyDisplayed.Remove(toRemove[i]);
                Destroy(toRemove[i]);
                i++;
            }
        }
	}

    public void Error(string text)
    {
        Text(text, Color.red);
    }
    public void Normal(string text)
    {
        Text(text, Color.black);
    }
    public void Success(string text)
    {
        Text(text, Color.green);
    }
    public void Warning(string text)
    {
        Text(text, new Color(255, 165, 0));
    }
    private bool Contains(string text)
    {
        foreach(KeyValuePair<GameObject,float> kv in currentlyDisplayed)
            if (kv.Key.GetComponentInChildren<Text>().text == text)
                return true;
        return false;
    }
    private void Text(string text, Color color)
    {
        if(FloatyTextManager.Template == null)
        {
            Debug.Log("Floaty text tempalte null");
            return;
        }
        if (Contains(text))
            return;
        float timeToReach = Time.fixedTime + animationTime;
        GameObject go = Instantiate(FloatyTextManager.Template, this.transform);

        go.SetActive(true);
        GameObject childText = go.transform.Find("Text").gameObject;
        Text t = childText.GetComponent<Text>();
        t.color = color;
        t.text = text;
        SmartUI sui = go.GetComponent<SmartUI>();
        childText.GetComponent<SmartUI>().addAnimation(new fadeOut(animationTime),null);
        sui.addAnimation(new slideY(-100+sui.position.height, -100-(sui.position.height*((travelDistanceP/100f)) + 1),animationTime*0.8f, SmartUI.TweenTypes.easeBoth),null);//80% of the anim time is spent floating up 
        currentlyDisplayed.Add(go, timeToReach); 
    }
}
