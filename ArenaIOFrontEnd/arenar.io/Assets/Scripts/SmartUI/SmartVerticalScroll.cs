using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class SmartVerticalScroll : MonoBehaviour{
    public float spacing = 0;
    public float scrollSpeed = 0;
    SmartUI sui = null;
    Vector2 startDrag = new Vector2();

    private float totalHeight = 0;
    private float scrollPos = 0;

    void Start()
    {
        sui = GetComponent<SmartUI>();
        rebuildAll(); rebuildAll();
    }

    void Update()
    {
        //if (!Input.GetMouseButton(0) || Input.touchCount == 0 || transform.childCount == 0) return;
        float change = 0;
#if (UNITY_EDITOR)

        //if (!Input.GetMouseButton(0)) return;//!sui.contains(Input.mousePosition)) return;
        if (Input.GetMouseButtonDown(0))
        {
            startDrag = Input.mousePosition;
        }
        if (Input.GetMouseButton(0))
        {
            change = startDrag.y - Input.mousePosition.y;
            startDrag = Input.mousePosition;
        }

#endif
        /* 
         * change = startDrag - Input.GetTouch(0).position;
         * causes
         * Cannot implicitly convert type `UnityEngine.Vector2' to `float'
         * 
    #if (UNITY_ANDROID || UNITY_IOS) && !UNITY_EDITOR
            if (Input.touchCount == 0) return;
            if (!sui.contains(Input.GetTouch(0).position))
                return;
            TouchPhase currPhase = Input.GetTouch(0).phase;

            if (currPhase == TouchPhase.Began)
                startDrag = Input.GetTouch(0).position;

            if (currPhase == TouchPhase.Moved)
                change = startDrag - Input.GetTouch(0).position;
    #endif
            setPos(change);
            
        */
    }
    public void rebuildAll()
    {
        SmartUI sui = GetComponent<SmartUI>();
        float cumulativeHeight = 0;
        foreach(Transform child in transform)
        {
            SmartUI item = child.GetComponent<SmartUI>();
            if (item != null )
            {
                item.position.y = sui.convertHeightPixelToPercent(Mathf.FloorToInt(cumulativeHeight));
                item.setElementToRect();
                cumulativeHeight += item.getPhysicalRect().height + spacing;
                item.setElementToRect();
            }
        }
        totalHeight = cumulativeHeight;
    }

    public void setPos(float currentOffest)
    {
        if(currentOffest == 0) return;
        //Error starting from same position every stroke
        float cumulativeHeight = scrollPos + currentOffest*scrollSpeed;
        cumulativeHeight = cumulativeHeight > 0 ? 0 : cumulativeHeight;
        var rect = GetComponent<SmartUI>().getPhysicalRect();
        cumulativeHeight = (cumulativeHeight+totalHeight) < (rect.y + rect.height*1.5) ? scrollPos : cumulativeHeight;
        scrollPos = cumulativeHeight;
        foreach (Transform child in transform)
        {
            SmartUI item = child.GetComponent<SmartUI>();
            if (item != null)
            {
                item.position.y = sui.convertHeightPixelToPercent(cumulativeHeight);
                item.setElementToRect();
                cumulativeHeight += item.getPhysicalRect().height + spacing;
            }

        }
    }

    /*
    public float scrollDragThreshold = 0;
    public float spacing = 0;
    public float speedscalar = 1;
    public DragDirection buildDirection = DragDirection.Down;
    float currentPos = 0;
    float deltaPos = 0;
    bool dragging = false;
    Vector2 startDrag;
    SmartUI sui;

    int childCount = 0;
    
    public enum DragDirection { Up, Down };

    // Use this for initialization
    void Start() {
        for (int i = 0; i < transform.childCount; i++)
        {
            Transform child = transform.GetChild(i);
            SmartUI suic = child.GetComponent<SmartUI>();
            if (suic != null && child.gameObject.activeInHierarchy)
            {
                if (buildDirection == DragDirection.Down)
                {
                    suic.VAlign = SmartUI.VAligns.top;
                }
                else
                {
                    suic.VAlign = SmartUI.VAligns.bottom;
                }
            }

        }
        sui = gameObject.GetComponent<SmartUI>();
        checkUpChildren();
    }
    public void checkUpChildren()
    {
        updatePositions(currentPos);
        childCount = transform.childCount;
    }

    // Update is called once per frame
    void Update()
    {
        if (!Input.GetMouseButtonDown(0) || Input.touchCount == 0) return;
        sui = GetComponent<SmartUI>();
        if (childCount != transform.childCount) checkUpChildren();
        if (childCount == 0) return;
        float change = 0;
#if (UNITY_EDITOR)
       
        if (!sui.contains(Input.mousePosition)) return;
        if (Input.GetMouseButtonDown(0))
        {
            startDrag = Input.mousePosition;
            deltaPos = Input.mousePosition.y;
        }

        if (Input.GetMouseButton(0))
        {
            change = (Input.mousePosition.y - deltaPos) * speedscalar;
            deltaPos = Input.mousePosition.y;
            if (Mathf.Abs(startDrag.y - Input.mousePosition.y) > scrollDragThreshold)
                dragging = true;
        }

        if (Input.GetMouseButtonUp(0))
        {
            dragging = false;
            deltaPos = 0;
        }

        /*if (dragging)
        {
            Debug.Log("Size : " + Mathf.Abs(startDrag.y - Input.mousePosition.y));
            Debug.Log(scrollDragThreshold);
        }
#endif
#if (UNITY_ANDROID || UNITY_IOS) && !UNITY_EDITOR
        if (Input.touchCount == 0) return;
        if (!sui.contains(Input.GetTouch(0).position))
            return;
        TouchPhase currPhase = Input.GetTouch(0).phase;

        if(currPhase == TouchPhase.Began)
            startDrag = Input.GetTouch(0).position;

        change = Input.GetTouch(0).deltaPosition.y * speedscalar;

        if(currPhase == TouchPhase.Moved)
            if(Mathf.Abs(startDrag.y - Input.GetTouch(0).position.y) > scrollDragThreshold)
                dragging = true;

        if (currPhase == TouchPhase.Ended)
            dragging = false;
#endif
        if (!dragging) return;
        currentPos -= change;
        updatePositions(currentPos);
        if (buildDirection == DragDirection.Down)
        {
            if (change > 0)
            {
                Transform child = transform.GetChild(transform.childCount - 1);
                SmartUI sui = child.GetComponent<SmartUI>();
                SmartUI thisSUI = transform.GetComponent<SmartUI>();
                if ((sui.position.height + sui.position.y) < thisSUI.position.height)
                {
                    currentPos += change;
                    updatePositions(currentPos);
                }
            }
            else if (change < 0)
            {
                //create limiters so you don't go over
                Transform child = transform.GetChild(0);
                SmartUI sui = child.GetComponent<SmartUI>();
                if (sui.position.y > 0)
                {
                    currentPos += change;
                    updatePositions(currentPos);
                }

            }
        }
        else
        {
            //Working for now however will need to add a limiter to check if the  y is greater then find the difference and set it to that so that it doesn't have the weird edge thing where it needs to be pulled up twice to reach the edge

            //build up Scroll
            if (change > 0)
            {
                //create limiters so you don't go over
                Transform child = transform.GetChild(transform.childCount - 1);
                SmartUI sui = child.GetComponent<SmartUI>();
                SmartUI thisSUI = transform.GetComponent<SmartUI>();
                if (sui.position.y < 0)
                {
                    currentPos += change;
                    updatePositions(currentPos);
                }
            }
            else if (change < 0)
            {
                Transform child = transform.GetChild(0);
                SmartUI sui = child.GetComponent<SmartUI>();
                SmartUI thisSUI = transform.GetComponent<SmartUI>();
                if ((sui.position.y - sui.position.height) > -thisSUI.position.height)
                {
                    currentPos += change;
                    updatePositions(currentPos);
                }

            }
        }
    }

    public void updatePositions(float startPos)
    {
        float currentHeight = startPos;
        bool buildUp = buildDirection == DragDirection.Up;
        int i = buildUp ? (transform.childCount - 1) : 0;
        bool finished = false;
        while (!finished)
        {
            if (transform.childCount == 0) break;
            Transform child = transform.GetChild(i);
            SmartUI sui = child.GetComponent<SmartUI>();
            if (sui != null && child.gameObject.activeInHierarchy)
            {
                if (buildDirection == DragDirection.Up)
                    sui.VAlign = SmartUI.VAligns.bottom;
                else
                    sui.VAlign = SmartUI.VAligns.top;

                sui.position.y = currentHeight;
                sui.setElementToRect();
                Debug.Log("Current height : " + currentHeight);
                Debug.Log("Physical " + sui.getPhysicalRect());
                currentHeight += (buildUp? -1:1) * (sui.position.height + spacing);
            }
            finished = buildUp ? i <= 0 : i >= (transform.childCount - 1);
            i += buildUp ? -1 : 1;
        }
        GetComponent<SmartUI>().position.height = currentHeight;
        GetComponent<SmartUI>().setElementToRect();
    }*/
    }
