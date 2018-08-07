using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SmartUITextGroup : MonoBehaviour {
    public static Dictionary<string,SmartUITextGroup> children = new Dictionary<string,SmartUITextGroup>();

    public string GroupFor;
    public int size = 20;
    public int minSize = 10;
    public int maxSize = 300;
    public bool bestFit;
    
    private List<Text> TextGroup = new List<Text>();
    // Use this for initialization
    private List<float> Sizes = new List<float>();

    private int currentSize = 0;
    private int nFrame = 0;
    public static void AddBeforeInit(string id,GameObject withText)
    {
        SmartUITextGroup s = null;
        children.TryGetValue(id, out s);
        if (withText.GetComponent<Text>() != null)
        {
            Text t = withText.GetComponent<Text>();
            s.TextGroup.Add(t);
            s.Sizes.Add(getSize(t));
        }
    }
    public static void AddTextObj(string ID, GameObject withText)
    {
        SmartUITextGroup s = null;
        children.TryGetValue(ID, out s);
        if (withText.GetComponent<Text>() != null && s != null)
        {
            s.AddTextObj(withText);
        }
    }
    public void AddTextObj(GameObject withText)
    {
        if (withText.GetComponent<Text>() != null)
        {
            Text t = withText.GetComponent<Text>();
            TextGroup.Add(t);
            Sizes.Add(getSize(t));
        }
        reCalc();
    }
    public void Empty()
    {
        TextGroup.Clear();
        Sizes.Clear();
    }
    void Awake()
    {
        if (!children.ContainsKey(GroupFor))
            children.Add(GroupFor,this);
    }
    

    // Update is called once per frame
    int recalcOneEvery = 60;
	void Update () {
        if (nFrame >= 3 && (nFrame % recalcOneEvery == 0 || nFrame == 3)) {
            for (int i = 0; i < TextGroup.Count; i++)
            {
                var size = getSize(TextGroup[i]);
                if (size !=0 && size != Sizes[i])
                {
                    reCalc();
                    break;
                    
                }
            }
        }
        if (nFrame == 2)
            reCalc();

        nFrame++;
    }

    private void reCalc()
    {
        int currentSmallest = 99999999;
        for (int k = 0; k < TextGroup.Count; k++)
        {
            Text t = TextGroup[k];
            if (t.cachedTextGenerator.fontSizeUsedForBestFit == 0) continue;
            if (t.cachedTextGenerator.fontSizeUsedForBestFit < currentSmallest)
                currentSmallest = getSize(t) < currentSmallest ? getSize(t) : currentSmallest;
        }
        currentSize = currentSmallest;
        int i = 0;
        while (i < TextGroup.Count)
        {
            Text t = TextGroup[i];
            if (t)
                t.resizeTextMaxSize = currentSize;
            else
            {
                TextGroup.RemoveAt(i);
            }
            i++;
        }
        for (int j = 0; j < Sizes.Count; j++)
            Sizes[j] = currentSize;
    }
    public static void recalcID(string ID)
    {

        SmartUITextGroup s = null;
        children.TryGetValue(ID, out s);
        s.reCalc();
    }
    //util
    private static int getSize(Text t) {
        return t.cachedTextGenerator.fontSizeUsedForBestFit;
    }
}
