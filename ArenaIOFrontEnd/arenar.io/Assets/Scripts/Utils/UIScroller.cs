using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class UIScroller : MonoBehaviour, IDragHandler, IEndDragHandler, IBeginDragHandler{
    public static int focus = 0;
    float percentToTrackTo = .5f;
    public float threshold = 0;
    public float lerpMultiplier = 10;
    bool dragging = false;
    Vector2 startDrag;

    //Requires SmartUI attached to the container

    void Start() {
        float pixelToTrackTo = pixelFromPercent(percentToTrackTo);
        transform.transform.position = new Vector3(pixelToTrackTo, 0, 0);
    }
    
    // Update is called once per frame
    void Update () {
        if (!dragging) {
            float pixelToTrackTo = pixelFromPercent(percentToTrackTo);
            float lerped = (transform.transform.position.x * lerpMultiplier + pixelToTrackTo) / (lerpMultiplier + 1);
            SmartUI sui = GetComponent<SmartUI>();
            sui.position.x = lerped;
            sui.setElementToRect();
        }
	}
    
    public void OnBeginDrag(PointerEventData eventData)
    {
        startDrag = eventData.position;
        //dragging = true;
    }
    
    private float getTotalWidth() {
        return GetComponent<RectTransform>().rect.width; //* Screen.width / 600f; // Need this if scaling with screen size
    }
    private float getPercent() {
        return -GetComponent<RectTransform>().position.x / getMaxLeft();
    }
    private float getMaxLeft() {
        return getTotalWidth() * ((transform.childCount - 1f) / transform.childCount);
    }
    private float pixelFromPercent(float percent) {
        return -percent * getMaxLeft();
    }
    private float getClosestScreenPercent(float percent) { // This one is compex, but, it's just mathy-mathy stuff
        float eachScreenPercent = 1/(transform.childCount - 1f);
        return Mathf.Floor((percent + eachScreenPercent / 2) * (transform.childCount - 1f)) / (transform.childCount - 1f);
    }

    public void OnDrag(PointerEventData eventData){
        if  (Mathf.Abs(startDrag.x - eventData.position.x) > threshold 
            //|| Mathf.Abs(startDrag.y - eventData.position.y) > threshold
            )
        {
            dragging = true;
            float change = eventData.delta.x;
            transform.Translate(new Vector3(change, 0, 0));
            // Lock into the area
            if (transform.transform.position.x > 0) transform.transform.position = new Vector3(0, 0, 0);
            if (transform.transform.position.x < -getMaxLeft()) transform.transform.position = new Vector3(-getMaxLeft(), 0, 0);
        }
    }

    public void OnEndDrag(PointerEventData eventData){
        dragging = false;
        float percent = getPercent();
        percentToTrackTo = getClosestScreenPercent(percent);
    }

    public void setScreenToTrackTo(int screen){
        percentToTrackTo = screen / (transform.childCount-1f);
        focus = screen;
    }
}
