using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SmartProgressBar : MonoBehaviour {
    public GameObject barToChange;
    public GameObject barTotalSpace;

    //can be null
    public GameObject displayText;
    public DisplayType displayType;
    public int displayValueAmount = 0;
    public string displayFormat = "{0} %";
   
    public enum DisplayType { Percent, Value}

    SmartUI barToChangeSUI;
    SmartUI barTotalSpaceSUI;
    Text displayTextText;

	// Use this for initialization
	void Start () {
        if (barToChange != null)
            barToChangeSUI = barToChange.GetComponent<SmartUI>();
        else
            Debug.LogError("SmartUIProgress bar : " + this + " reference GameObject barToChange missing");

        if(barTotalSpace != null)
            barToChangeSUI = barToChange.GetComponent<SmartUI>();
        else
            Debug.LogError("SmartUIProgress bar : " + this + " reference GameObject barToChange missing");

        if (displayText != null)
            displayTextText = displayText.GetComponent<Text>();
    }

    public void updateValues(float _value, float _max)
    {
        float percent = _value / _max;
        barToChangeSUI.position.width = barTotalSpaceSUI.position.width * percent;
        barToChangeSUI.setElementToRect();
        if(displayTextText != null)
        {
            float value1 = 0;
            float value2 = 0;
            if(displayType == DisplayType.Percent)
            {
                value1 = percent * 100;
                value2 = 100;
            }else if(displayType == DisplayType.Value)
            {
                value1 = _value;
                value2 = _max;
            }
            if (displayValueAmount == 1)
                displayTextText.text = string.Format(displayFormat, value1);
            else if (displayValueAmount == 2)
                displayTextText.text = string.Format(displayFormat, value2);
        }
        
    }
	
	// Update is called once per frame
	void Update () {
		
	}
}
