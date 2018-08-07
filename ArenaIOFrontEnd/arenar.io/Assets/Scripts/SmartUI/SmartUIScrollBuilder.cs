using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
[ExecuteInEditMode]
public class SmartUIScrollBuilder : MonoBehaviour {
    public ScrollType scrollType;
    public float itemSpacing = 0.0f;
    public float scrollToAnimTime = 0.1f;
    public BuildDirection direction = BuildDirection.Down;

    private GameObject ViewportContent;
    private SmartUI ViewSUI;
    private int OldChildCount = 0;
    private float lastScreenHeight = 0;
    private float lastItemSpacing = 0;

    private float contentOffset = 0;

	// Use this for initialization
	void Start () {
        ViewportContent = transform.Find("Viewport").Find("Content").gameObject;
        ViewSUI = ViewportContent.GetComponent<SmartUI>();
        GetComponent<ScrollRect>().onValueChanged.AddListener(onScroll);
	}
	
	// Update is called once per frame
	void Update () {
        if (OldChildCount != getChildCount() || lastItemSpacing != itemSpacing || lastScreenHeight != Screen.height)
        {
            rebuildContent();
            OldChildCount = getChildCount();
            lastItemSpacing = itemSpacing;
            lastScreenHeight = Screen.height;
        }
    }

    public void forceRebuild()
    {
        rebuildContent();
    }

    private void onScroll(Vector2 val)
    {
        float change = val.y;
        // 1 is top
        // 0 bottom
        contentOffset = ViewSUI.position.height * (1 - change);
        ViewSUI.position.y =- contentOffset;
    }

    private int getChildCount()
    {
        int childCount = 0;
        foreach (Transform child in ViewportContent.transform)
            if (child.gameObject.activeSelf)
                childCount++;
        return childCount;
    }

    private void rebuildContent()
    {
        float finalHeight = 0;
        foreach (Transform child in ViewportContent.transform)
        {
            if (!child.gameObject.activeSelf) continue;
            SmartUI sui = child.GetComponent<SmartUI>();
            Vector2 sizePixels = sui.getSize();
            sui.position.y = sui.convertHeightPixelToPercent(finalHeight);
            sui.setElementToRect();
            finalHeight += (sizePixels.y + itemSpacing*Screen.height);
            /*if (direction == BuildDirection.Up)
            {
                sui.VAlign = SmartUI.VAligns.bottom;
            }*/
        }
        float heightPercent = ViewSUI.convertHeightPixelToPercent(finalHeight);
        if (direction == BuildDirection.Up)
        {
            ViewSUI.VAlign = SmartUI.VAligns.bottom;
        }
        ViewSUI.position.height = heightPercent;
        ViewSUI.setElementToRect();
    }

    public void scrollTo(float value, ScrollToType type)
    {
        switch (type)
        {
            case ScrollToType.PIXEL:
                scrollTo(value);
                break;

            case ScrollToType.PERCENT:
                scrollTo(ViewSUI.position.height * value);
                break;
        }
    }
    public void scrollTo(GameObject go)
    {
        for(int i=0;i< ViewportContent.transform.childCount; i++)
        {
            GameObject child = ViewportContent.transform.GetChild(i).gameObject;
            if (go == child)
            {
                scrollTo(ViewportContent.transform.GetChild(i).gameObject.GetComponent<SmartUI>().position.y);
                return;
            }
        }
    }
    private void scrollTo(float yPos)
    {
        ViewSUI.addAnimation(new slideY(ViewSUI.position.y, -yPos, scrollToAnimTime, SmartUI.TweenTypes.elasticBoth),null);
    }

    public enum ScrollToType { PIXEL, PERCENT };
    public enum ScrollType {Vertical, HorizontalNotBuiltYet };
    public enum BuildDirection { Up, Down };
}
