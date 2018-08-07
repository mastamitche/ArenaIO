using System.Collections;
using System.Collections.Generic;
#if (UNITY_EDITOR)
using UnityEditor;
#endif
using UnityEngine;
using UnityEngine.UI;

[ExecuteInEditMode]

public class SmartLayoutGroup : MonoBehaviour {
    //public float spacing = 0;
    public HorizontalAlignments horizontalAlignment = HorizontalAlignments.Center;
    public VerticalAlignments verticalAlignment = VerticalAlignments.Center;
    public OverflowOption overflowOption = OverflowOption.Wrap;
    public enum HorizontalAlignments { Left, Center,Right};
    public enum VerticalAlignments { Top, Center, Bottom };
    public enum OverflowOption { Overflow, Wrap };
    public int childWidthDetectionDelay = -1; // -1 means never run, 0/1 is always run, >1 means delayed checking for performance
    public bool minSize100Percent = false;
    public float childLerpTime = 0f;
    public float paddingV = 0f;
    public float paddingH = 0f;

    int oldChildCount = 0;
    Vector2 oldSize = Vector2.zero;
    Vector2[] oldSizes = new Vector2[0];
    float lastPaddingH = 0;
    float lastPaddingV = 0;

    // Use this for initialization
    void Start () {
        lastPaddingH = paddingH;
        lastPaddingV = paddingV;

    }


    public void updateElements() {
        // set sizes and child count for next cycle
        oldChildCount = transform.childCount;
        oldSize = GetComponent<RectTransform>().rect.size;
        oldSizes = new Vector2[oldChildCount];
        for (int i = 0; i < transform.childCount; i++)
            oldSizes[i] = transform.GetChild(i).GetComponent<RectTransform>().rect.size;

        // Work out sizes of elements and how they wrap
        float parentWidth = transform.parent.GetComponent<RectTransform>().rect.size.x;
        float parentHeight = transform.parent.GetComponent<RectTransform>().rect.size.y;
        float myWidth = GetComponent<RectTransform>().rect.size.x;
        float myHeight = GetComponent<RectTransform>().rect.size.y;
        if (minSize100Percent) {
            myWidth = Mathf.Max(myWidth, transform.parent.GetComponent<RectTransform>().rect.width);
            myHeight = Mathf.Max(myHeight, transform.parent.GetComponent<RectTransform>().rect.height);
        }
        if (overflowOption == OverflowOption.Wrap)
            myWidth = transform.parent.GetComponent<RectTransform>().rect.width;

        List<float> rowWidths = new List<float>();
        List<float> rowHeights = new List<float>();
        List<int> childrenInRows = new List<int>();
        int row = -1;
        int lastRow = row;
        for (int i = 0; i < transform.childCount; i++) {
            if (!transform.GetChild(i).GetComponent<SmartUI>().collidable()) continue;
            Vector2 size = transform.GetChild(i).GetComponent<RectTransform>().rect.size;
            size.x += paddingH;
            if (row == -1 || (rowWidths[row] + size.x > myWidth + .1f && overflowOption == OverflowOption.Wrap)) {
                row++;
                rowWidths.Add(0);
                rowHeights.Add(0);
                childrenInRows.Add(0);
            }
            size.x -= lastRow!=row ? paddingH : 0;
            rowWidths[row] += size.x;
            //Debug.Log(size.x);
            if (size.y > rowHeights[row]) rowHeights[row] = size.y;
            childrenInRows[row]++;
            lastRow = row;
        }

        // Work out vertical alignments
        float totalHeight = paddingV;//Add to top to give top spacing
        foreach (float f in rowHeights) {
            totalHeight += f + paddingV;
        }

        float YOffset = 0;
        if (verticalAlignment == VerticalAlignments.Center) YOffset = (myHeight - totalHeight) / 2f;
        else if (verticalAlignment == VerticalAlignments.Bottom) YOffset = myHeight - totalHeight;

        // Start setting element positions. Horizontal alignments are performed per-row

        row = 0;
        int childCountOnRow = 0;
        float curX = 0;
        float curY = paddingV;
        for (int i = 0; i < transform.childCount; i++) {
            SmartUI sui = transform.GetChild(i).GetComponent<SmartUI>();
            if (!sui.collidable()) continue;
            if (sui == null) {
                Debug.LogError("Child does not have SmartUI!");
                return;
            }
            Vector2 size = sui.getSize();
            sui.unitTypes[0] = sui.unitTypes[1] = 0; // set to pixel x/y positioning

            if (childCountOnRow + 1 > childrenInRows[row]) {
                curY += rowHeights[row] ;
                row++;
                curX = 0;
                childCountOnRow = 0;
            }

            float XOffset = 0;
            if (rowWidths[row] < parentWidth) {
                if (horizontalAlignment == HorizontalAlignments.Center) XOffset = (parentWidth - rowWidths[row]) / 2f;// + 
                if (horizontalAlignment == HorizontalAlignments.Right) XOffset = parentWidth - rowWidths[row];
            }

            XOffset += paddingH * (i % childrenInRows[row] );

            if (XOffset < 0) XOffset = 0;

            if (childLerpTime > 0)
                sui.addAnimation(new slideX(sui.position.x, curX + XOffset, childLerpTime, SmartUI.TweenTypes.easeBoth), null);
            else
                sui.position.x = curX + XOffset;

#if (UNITY_EDITOR)
            if (!EditorApplication.isPlaying)
                sui.position.x = curX + XOffset;
#endif

            sui.position.y = curY + YOffset + paddingV * row;
            sui.setElementToRect();
            curX += size.x;
            childCountOnRow++;
        }

        if (rowWidths[row] < parentWidth) { // move scroll to left if we have can't scroll anymore
            Vector2 pos = GetComponent<RectTransform>().anchoredPosition;
            pos.x = 0;
            GetComponent<RectTransform>().anchoredPosition = pos;
        }


        // Set our size so we know how to position elements
        if (overflowOption == OverflowOption.Overflow) {
            SmartUI sui = GetComponent<SmartUI>();
            if (sui == null) {
                float minWidth = minSize100Percent ? transform.parent.GetComponent<RectTransform>().rect.width : 0;
                float minHeight = minSize100Percent ? transform.parent.GetComponent<RectTransform>().rect.height : 0;
                Vector2 size = GetComponent<RectTransform>().sizeDelta;
                size.x = Mathf.Max(curX, minWidth);
                GetComponent<RectTransform>().sizeDelta = size;// new Vector2(, Mathf.Max(GetComponent<RectTransform>().rect.size.y, minHeight));
            } else {
                sui.unitTypes[2] = 0;
                sui.position.width = curX;
                if (minSize100Percent) {
                    if (sui.position.width < transform.parent.GetComponent<RectTransform>().rect.size.x)
                        sui.position.width = transform.parent.GetComponent<RectTransform>().rect.size.x;
                }
                sui.setElementToRect();
            }
        }

        Vector2 mysize = GetComponent<RectTransform>().sizeDelta;
        if (minSize100Percent && myHeight > totalHeight) totalHeight = myHeight;
        mysize.y = totalHeight - parentHeight;
        //print(parentHeight);
        GetComponent<RectTransform>().localPosition = new Vector3(0, 0, 0);
        GetComponent<RectTransform>().sizeDelta = mysize;
    }

    
    public void LateUpdate() {
        bool changed = false;

        // Detect change by child count
        if (oldChildCount != transform.childCount) changed = true;
        if (!oldSize.Equals(GetComponent<RectTransform>().rect.size)) changed = true;
        if (!oldSize.Equals(GetComponent<RectTransform>().rect.size)) changed = true;
        if (lastPaddingH != paddingH) changed = true;
        if (lastPaddingV != paddingV) changed = true;


        // Detect change in child width change
        if (childWidthDetectionDelay >= 0 && (childWidthDetectionDelay == 0 || Time.frameCount % childWidthDetectionDelay == 0) && !changed) {
            for (int i = 0; i < transform.childCount; i++) {
                RectTransform rt = transform.GetChild(i).GetComponent<RectTransform>();
                if (!oldSizes[i].Equals(rt.rect.size)) {
                    changed = true;
                    break;
                }
            }
        }
#if (UNITY_EDITOR)
        if (!EditorApplication.isPlaying)
            changed = true;
#endif

        if (!changed) return;

        updateElements();
        lastPaddingH = paddingH;
        lastPaddingV = paddingV;
    }
}
