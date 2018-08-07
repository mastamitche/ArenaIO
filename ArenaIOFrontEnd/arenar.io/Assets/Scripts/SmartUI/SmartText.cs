using UnityEngine;
using UnityEngine.UI;

// Helps with all things text
public class SmartText : MonoBehaviour {
    public float outlineScaleX = .08f;
    public float outlineScaleY = .08f;

    public void run() {
        // outline scaling
        Outline outline = GetComponent<Outline>();
        if (outline != null) {
            int fontSize = GetComponent<Text>().cachedTextGenerator.fontSizeUsedForBestFit;
            outline.effectDistance = new Vector2(fontSize * outlineScaleX, fontSize * outlineScaleY);
        }
    }
    
    public void Update() {
        if (Time.frameCount % 100 != 0) return;
        run();
    }

}
