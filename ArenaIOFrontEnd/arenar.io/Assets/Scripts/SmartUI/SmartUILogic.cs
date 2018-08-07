using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
#if (UNITY_EDITOR)
using UnityEditor;
#endif

[ExecuteInEditMode]
public class SmartUILogic : MonoBehaviour {
    private static List<SmartUI> activeElements = new List<SmartUI>();

    public bool debugAnimations = false;
    // Update is called once per frame
    public void Update () {
        lock (activeElements) {
            if (activeElements.Count > 0) {
                if (debugAnimations) Debug.Log("Updating " + activeElements.Count + " elements with animations");
                for (int i = 0; i < activeElements.Count; i++) {
                    if (activeElements[i] != null) {

                        if (debugAnimations
                         && !activeElements[i].name.Equals("Swirl1")
                         && !activeElements[i].name.Equals("Swirl2")
                         && !activeElements[i].name.Equals("Swirl3")
                         && !activeElements[i].name.Equals("GlowL")
                         && !activeElements[i].name.Equals("LightL")
                         && !activeElements[i].name.Equals("GlowR")
                         && !activeElements[i].name.Equals("LightR")
                            )
                            Debug.Log(activeElements[i].name + " animation(s): " + activeElements[i].getAnimationString());

                        activeElements[i].tick();
                    } else {
                        activeElements.RemoveAt(i);
                        i--;
                    }
                }
            }
        }

#if (UNITY_EDITOR)
        if (EditorApplication.isPlaying) return;
        updateDPI();
#endif
    }

    public void updateDPI() {
        if (Canvas != null)
            Canvas.referencePixelsPerUnit = Screen.width / 10f;
    }

    public static void setActive(SmartUI o) {
        lock(activeElements)
            activeElements.Add(o);
    }

    public static void removeActive(SmartUI o) {
        lock (activeElements)
            activeElements.Remove(o);
    }
    
    public CanvasScaler Canvas;

    void Start() {
        /*
        float scale = Mathf.Min(Screen.width / Consts.refWidth, Screen.height / Consts.refHeight);
        foreach (Text t in Canvas.GetComponentsInChildren<Text>()) {
            t.fontSize = (int)(t.fontSize * scale);
        }
        */
        updateDPI();
    }
    
}
