using System;
using System.Collections.Generic;
#if (UNITY_EDITOR)
using UnityEditor;
#endif
using UnityEngine;
using UnityEngine.UI;

[ExecuteInEditMode]
public class SmartUI : MonoBehaviour {

    public static Rect RectTransformToScreenSpace(RectTransform transform) {
        Vector2 size = Vector2.Scale(transform.rect.size, transform.lossyScale);
        Rect rect = new Rect(transform.position.x, transform.position.y, size.x, size.y);
        rect.x -= (transform.pivot.x * size.x);
        rect.y -= ((1.0f - transform.pivot.y) * size.y);
        return rect;
    }
    private static int IDAssign = 0;
    [HideInInspector]
    public int ID = 0;

    public HAligns HAlign = HAligns.left;
    public VAligns VAlign = VAligns.top;
    public Rect position;
    public UnitTypes[] unitTypes = new UnitTypes[4] { UnitTypes.percentOfParent, UnitTypes.percentOfParent, UnitTypes.percentOfParent, UnitTypes.percentOfParent };

    public Rect maxSize = new Rect(-1, -1, -1, -1);

    public float angle;
    public float opacity = 1;
    public bool inheritAngle = false;
    public bool inheritOpacity = false;
    public bool flipX = false;
    public bool flipY = false;

    public AspectTypes aspectType = AspectTypes.none;
    public float aspect = -1;

    public bool dontResetAnchors = false;

    public bool debug = false;

    Vector2 initialSize;

    public enum AspectTypes { none, expand, shrink, forceX, forceY };
    public enum UnitTypes { pixel, percentOfParent, percentOfScreenWidth, percentOfScreenHeight };
    public enum TweenTypes { linear, easeStart, easeEnd, easeBoth, elasticEnd, elasticBoth };

    public enum HAligns { left, center, right };
    public enum VAligns { top, center, bottom };

    private List<UIAnimation> curAnimations = new List<UIAnimation>();

    public Vector2 screenPosition() {
        Canvas c = GetComponentInParent<Canvas>();
        Vector3[] corners = new Vector3[4];
        c.GetComponent<RectTransform>().GetWorldCorners(corners);
        Rect r = new Rect(corners[0].x, corners[0].y, corners[2].x - corners[0].x, corners[2].y - corners[0].y);
        
        Vector2 corner = transform.position - corners[0];
        corner = new Vector2(corner.x / r.size.x, corner.y / r.size.y);
        corner = new Vector2(corner.x * Screen.width, corner.y * Screen.height);
        corner.y = Screen.height - corner.y;

        RectTransform t = GetComponent<RectTransform>();

        corner.x -= t.pivot.x * t.rect.width;
        corner.y -= t.pivot.y * t.rect.height;

        //print(name + ": " + corner);

        return corner;
    }

    public bool collidable() {
        if (!gameObject.activeSelf) return false;
        if (opacity == 0 && !hasAnimationOfType(typeof(fadeIn))) return false;
        if (hasAnimationOfType(typeof(fadeOut))) return false;
        return true;
    }

    // automated utils
    SmartText st = null;

    /*
    public void bounce(){
        addAnimation(new bounceIn(initialSize.x, initialSize.y, 400, 0.05f, 150, 0.85f, 0.15f),null);
    }
    public void slide(float targetX){
        addAnimation(new slideX(position.x, targetX, 1000, TweenTypes.elasticEnd),null);
    }
    public void slideY(float targetY){
        addAnimation(new slideY(position.y, targetY, 1000, TweenTypes.elasticEnd),null);
    }
    */

    public void Start() {
        initElement();
        setElementToRect();
        initialSize = new Vector2(position.size.x, position.size.y);
        st = GetComponent<SmartText>();
        ID = IDAssign;
        IDAssign++;
    }

    public int getAnimationCount() {
        return curAnimations.Count;
    }
    public String getAnimationString() {
        String s = "";
        foreach (UIAnimation a in curAnimations)
            s += " " + a.GetType().Name;
        return curAnimations.Count + s;
    }

    public void removeAnimationsOfType(Type t) {
        int i = curAnimations.Count;
        while (i-- > 0) { // remove duplicates
            if (curAnimations[i].GetType().Equals(t))
                curAnimations.RemoveAt(i);
        }
    }
    public bool hasAnimationOfType(Type t) {
        foreach(UIAnimation a in curAnimations)
            if (a.GetType().Equals(t))
                return true;
        return false;
    }
    public UIAnimation getAnimationOfType(Type t) {
        foreach (UIAnimation a in curAnimations)
            if (a.GetType().Equals(t))
                return a;
        return null;
    }

    public void addAnimation(UIAnimation a) {
        addAnimation(a, null);
    }
    public void addAnimation(UIAnimation a,Action callback) {
        a.sui = this;
        removeAnimationsOfType(a.GetType());
        a.init();
        if (callback != null)
            a.callback = callback;
        curAnimations.Add(a);
        SmartUILogic.setActive(this);
    }

    public float getOpacity() {
        if (!inheritOpacity) return opacity;
        SmartUI sui = transform.parent.GetComponent<SmartUI>();
        if (sui != null)
            return sui.getOpacity() * opacity;
        else {
            //Debug.Log(transform.parent.name + " " + transform.parent.GetComponent<RectTransform>().rect.size);
            return 1;
        }
    }

    public void tick() {
        int i = curAnimations.Count;
        while (i-- > 0)
        {
            if (curAnimations[i] != null && curAnimations[i].tick())
            {
                if (curAnimations[i].callback != null)
                    curAnimations[i].callback(); 
                curAnimations.RemoveAt(i);
            }
            //print(position);
        }
        if (curAnimations.Count == 0)
            SmartUILogic.removeActive(this);

        setElementToRect();
    }

    public void initElement() {
        if (!dontResetAnchors) {
            RectTransform rt = GetComponent<RectTransform>();
            //rt.pivot = new Vector2(0, 1);
            rt.anchorMin = new Vector2(0, 1);
            rt.anchorMax = new Vector2(0, 1);
        }
    }

    //static int refWidth = 1080;
    //static int refHeight = 1920;

    //static CanvasScaler scaler;


    public Vector2 getParentSize() {
        SmartUI sui = transform.parent.GetComponent<SmartUI>();
        if (sui != null)
            return sui.getSize();
        else {
            if (transform.parent.GetComponent<SmartLayoutGroup>() != null) { // If a layout group, check it's parent instead
                sui = transform.parent.parent.GetComponent<SmartUI>();
                if (sui != null)
                    return sui.getSize();
                else
                    return transform.parent.parent.GetComponent<RectTransform>().rect.size;
            }
            return transform.parent.GetComponent<RectTransform>().rect.size;
        }
    }

    public Vector2 getSize() {
        Vector2 parentSize = getParentSize();
        Vector2 sizePixels = new Vector2(
            pixelsFromUnit(unitTypes[2], position.size.x, parentSize.x, maxSize.width, false),
            pixelsFromUnit(unitTypes[3], position.size.y, parentSize.y, maxSize.height, true)
        );

        // Aspect
        if (aspectType == AspectTypes.forceX)
            sizePixels.x = sizePixels.y * aspect;
        else if (aspectType == AspectTypes.forceY)
            sizePixels.y = sizePixels.x / aspect;
        else if (aspectType == AspectTypes.expand) {
            if (sizePixels.x / aspect > sizePixels.y) // x is too large
                sizePixels.y = sizePixels.x / aspect; // expand y to fit x
            else
                sizePixels.x = sizePixels.y * aspect; // expand x to fit y
        } else if (aspectType == AspectTypes.shrink)
            if (sizePixels.x / aspect > sizePixels.y) // x is too large
                sizePixels.x = sizePixels.y * aspect; // shrink x to fit y
            else
                sizePixels.y = sizePixels.x / aspect; // shrink y to fit x

        return sizePixels;
    }

    public static float pixelsFromUnit(UnitTypes type, float value, float parentValue, float max, bool isHeight) {
        float found = -1;

        //if (scaler == null) scaler = GameObject.Find("Canvas").GetComponent<CanvasScaler>();

        //float scale = isHeight ? Screen.height / Consts.refHeight : Screen.width / Consts.refWidth;

        switch (type) {
            //case UnitTypes.pixel: return isHeight ? (Screen.height / (float)refHeight) * value : (Screen.width / (float)refWidth) * value;
            case UnitTypes.pixel: return value;
            case UnitTypes.percentOfParent:
                found = (value / 100f) *  parentValue;
                break;
            case UnitTypes.percentOfScreenWidth:
                found = (value / 100f) * Screen.width;
                break;
            case UnitTypes.percentOfScreenHeight:
                found = (value / 100f) * Screen.height;
                break;
            //case UnitTypes.pixelOffsetFromParent:
            //    found = parentValue + value;
            //    break;
        }
        if (max >= 0) found = Mathf.Min(found, max);
        
        return found;
    }
    
    public void OnEnable() {
        //print("Enabled! " + name);
        // This is ineffecient, it goes through every smartUI in the heirachy to adjust it.. this should be done better
        setElementToRect();
        foreach (SmartUI sui in GetComponentsInChildren<SmartUI>())
            sui.setElementToRect();
    }

    public void setElementToRect()
    {
        if (debug) {
            try { throw new Exception(); } catch (Exception e) { print(position); print(e.StackTrace); }
        }
        try {
            // Validation
            angle = angle % 360;
            if (angle < 0) angle = 360 + angle;
            opacity = Mathf.Clamp01(opacity);

            Vector2 posPixels = new Vector2(position.x, -position.y);
            Vector2 sizePixels = position.size;
            if (transform.parent == null) {
                Debug.Log(gameObject.name);
                return;
            }
            
            Vector2 parentSize = getParentSize();

            // translating the 4 different numbers into pixels, using up to 4 units
            if (unitTypes.Length > 0) posPixels.x = pixelsFromUnit(unitTypes[0], posPixels.x, parentSize.x, maxSize.x, false);
            if (unitTypes.Length > 1) posPixels.y = pixelsFromUnit(unitTypes[1], posPixels.y, parentSize.y, maxSize.y, true);
            sizePixels = getSize();
            /*
            if (unitTypes.Length > 2) sizePixels.x = pixelsFromUnit(unitTypes[2], sizePixels.x, parentSize.x, maxSize.width, false);
            if (unitTypes.Length > 3) sizePixels.y = pixelsFromUnit(unitTypes[3], sizePixels.y, parentSize.y, maxSize.height, true);

            // Aspect
            if (aspectType == AspectTypes.forceX)
                sizePixels.x = sizePixels.y * aspect;
            else if (aspectType == AspectTypes.forceY)
                sizePixels.y = sizePixels.x / aspect;
            else if (aspectType == AspectTypes.expand) {
                if (sizePixels.x / aspect > sizePixels.y) // x is too large
                    sizePixels.y = sizePixels.x / aspect; // expand y to fit x
                else
                    sizePixels.x = sizePixels.y * aspect; // expand x to fit y
            }else if (aspectType == AspectTypes.shrink)
                if (sizePixels.x / aspect > sizePixels.y) // x is too large
                    sizePixels.x = sizePixels.y * aspect; // shrink x to fit y
                else
                    sizePixels.y = sizePixels.x / aspect; // shrink y to fit x
            */


            if (HAlign == HAligns.center) posPixels.x += parentSize.x / 2 - sizePixels.x / 2;
            else if (HAlign == HAligns.right) posPixels.x += parentSize.x - sizePixels.x;

            if (VAlign == VAligns.center) posPixels.y -= parentSize.y / 2 - sizePixels.y / 2;
            else if (VAlign == VAligns.bottom) posPixels.y -= parentSize.y - sizePixels.y;
        
            RectTransform rt = GetComponent<RectTransform>();

            posPixels.x += sizePixels.x * rt.pivot.x;
            posPixels.y += sizePixels.y * (rt.pivot.y - 1);


            if (inheritAngle)
                rt.localEulerAngles = new Vector3(0, 0, angle);
            else
                rt.eulerAngles = new Vector3(0, 0, angle);

            rt.anchoredPosition = posPixels;
            rt.sizeDelta = sizePixels;
            rt.localScale = new Vector3(flipX ? -1 : 1, flipY ? -1 : 1, 1);

            updateOpacity();

            if (st != null) st.run();
            
        } catch (Exception e){
            Debug.LogError(e);
        }
    }

    public void updateSize(bool first) {
        if (!first && unitTypes[2] != UnitTypes.percentOfParent && unitTypes[3] != UnitTypes.percentOfParent && unitTypes[0] != UnitTypes.percentOfParent && unitTypes[1] != UnitTypes.percentOfParent) return;
        setElementToRect();
        foreach (SmartUI sui in GetComponentsInChildren<SmartUI>()) {
            sui.setElementToRect();
            //print(sui.name);
            //sui.updateSize(false);
        }
    }

    public void updateSizeRecursive(bool first) {
        if (!first && unitTypes[2] != UnitTypes.percentOfParent && unitTypes[3] != UnitTypes.percentOfParent && unitTypes[0] != UnitTypes.percentOfParent && unitTypes[1] != UnitTypes.percentOfParent) return;
        setElementToRect();
        foreach (SmartUI sui in GetComponentsInChildren<SmartUI>())
            sui.updateSizeRecursive(false);
    }

    float lastUsedOpacity = -1;
    bool disableOpacityUpdate = false;
    public void updateOpacity() {
        if (disableOpacityUpdate) return;
        float curOpacity = getOpacity();
        if (curOpacity == lastUsedOpacity) return;
        lastUsedOpacity = curOpacity;

        Image img = GetComponent<Image>();
        if (img != null)
            if (img.color.a != curOpacity)
                img.color = new Color(img.color.r, img.color.g, img.color.b, curOpacity);
        Text txt = GetComponent<Text>();
        if (txt != null)
            if (txt.color.a != curOpacity)
                txt.color = new Color(txt.color.r, txt.color.g, txt.color.b, curOpacity);

        foreach (SmartUI sui in GetComponentsInChildren<SmartUI>())
            if (sui.inheritOpacity)
                sui.updateOpacity();
    }

    //Util functions
    public Rect getPhysicalRect()
    {
        return GetComponent<RectTransform>().rect;
    }
    //convert from pixel to %
    public float convertWidthPixelToPercent(float pixel)
    {
        Rect rect = getPhysicalRect();
        if (unitTypes[2] == UnitTypes.percentOfScreenWidth)
        {
            return (pixel / Screen.width ) * 100;
        }
        else if (unitTypes[2] == UnitTypes.percentOfParent)
        {
            return (pixel / getSize().x) * 100;
        }
        return (pixel / rect.width) * 100;
    }
    public float convertHeightPixelToPercent(float pixel)
    {
        Rect rect = getPhysicalRect();
        if(unitTypes[3] == UnitTypes.percentOfScreenHeight)
        {
            return (pixel / Screen.height ) * 100;
        }else if (unitTypes[3] == UnitTypes.percentOfParent)
        {
            return (pixel / getSize().y) * 100;
        }
        return (pixel/ rect.height) * 100;
    }
    public bool contains(Vector2 pos)
    {
        Rect rect = getPhysicalRect();

        if (pos.x > transform.position.x - rect.width/2 &&
            pos.x < transform.position.x + rect.width/2 &&
            pos.y > transform.position.y - rect.height/2 &&
            pos.y < transform.position.y + rect.height/2
            )
            return true;
        return false;
    }

#if (UNITY_EDITOR)
    public void OnDrawGizmosSelected() {
        // Display the explosion radius when selected
        int distToSelected = 0;
        Transform cur = transform;
        while (cur != Selection.activeGameObject.transform) {
            distToSelected++;
            if (distToSelected > 4) return;
            if (cur.parent == null) return; // Not a parent of this item?
            cur = cur.parent;
        }

        //Gizmos.color = new Color(0, 1, 0, distToSelected == 0 ? 1 : ((float)Math.Pow(0.75f, distToSelected) * 0.6f) + 0.1f);

        //Gizmos.color = new Color(0, 1, 0, distToSelected == 0 ? 1 : ((float)Math.Pow(0.5f, distToSelected) * 0.3f) + 0.1f);
        //Gizmos.color = new Color(0, 1, 0, 0.75f);

        if (distToSelected == 0)
            Gizmos.color = new Color(0, 1, 0, 0.1f);
        else
            Gizmos.color = new Color(0, 0, 1, (float)Math.Pow(0.95f, distToSelected) * 0.1f);

        Canvas c = GetComponentInParent<Canvas>();
        RectTransform rt = GetComponent<RectTransform>();
        Vector2 size = RectTransformToScreenSpace(rt).size;
        Vector3 offset = new Vector3((0.5f - rt.pivot.x) * size.x, (0.5f - rt.pivot.y) * size.y);
        int lines = 6 - distToSelected;
        if (distToSelected != 0) lines -= 3;


        for (int x = -lines; x <= lines; x++)
            for (int y = -lines; y <= lines; y++)
                Gizmos.DrawWireCube(rt.position + (offset) + new Vector3(x/20f, y/20f), new Vector3(size.x, size.y, 0));
        //Gizmos.DrawWireCube(rt.position.AddXY(offset), new Vector3(size.x, size.y, 0));


    }
#endif


#if (UNITY_EDITOR)
    public void Update() {
        if (EditorApplication.isPlaying) return;
        st = GetComponent<SmartText>();
        setElementToRect();
    }
#endif
    
}
