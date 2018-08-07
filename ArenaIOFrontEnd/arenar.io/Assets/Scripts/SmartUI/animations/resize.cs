using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class resize : UIAnimation {
    long startTime;
    float initialW;
    float targetW;
    float initialH;
    float targetH;
    float duration;
    SmartUI.TweenTypes type;

    public resize(float initialW, float targetW, float initialH, float targetH, float duration, SmartUI.TweenTypes type) {
        this.initialW = initialW;
        this.targetW = targetW;
        this.initialH = initialH;
        this.targetH = targetH;
        this.duration = duration * 1000;
        this.type = type;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
    }

    public override bool tick() {
        if (initialW == targetW && initialH == targetH)
            return true;
        //Debug.Log(sui.gameObject.name + " w: " + initialW + " " + targetW + " h: " + initialH + " " + targetH);


        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;
        float newW = -1;
        float newH = -1;

        switch (type) {
            case SmartUI.TweenTypes.elasticEnd:
                newW = LibTween.easeOutElastic(currentPosition, initialW, targetW - initialW, duration, null, null);
                newH = LibTween.easeOutElastic(currentPosition, initialH, targetH - initialH, duration, null, null);
                break;
            case SmartUI.TweenTypes.elasticBoth:
                newW = LibTween.easeInOutElastic(currentPosition, initialW, targetW - initialW, duration, null, null);
                newH = LibTween.easeInOutElastic(currentPosition, initialH, targetH - initialH, duration, null, null);
                break;
            case SmartUI.TweenTypes.easeStart:
                newW = LibTween.easeInQuad(currentPosition, initialW, targetW - initialW, duration);
                newH = LibTween.easeInQuad(currentPosition, initialH, targetH - initialH, duration);
                break;
            case SmartUI.TweenTypes.easeEnd:
                newW = LibTween.easeOutQuad(currentPosition, initialW, targetW - initialW, duration);
                newH = LibTween.easeOutQuad(currentPosition, initialH, targetH - initialH, duration);
                break;
            case SmartUI.TweenTypes.easeBoth:
                newW = LibTween.easeInOutQuad(currentPosition, initialW, targetW - initialW, duration);
                newH = LibTween.easeInOutQuad(currentPosition, initialH, targetH - initialH, duration);
                break;
            case SmartUI.TweenTypes.linear:
                newW = LibTween.linear(currentPosition, initialW, targetW - initialW, duration);
                newH = LibTween.linear(currentPosition, initialH, targetH - initialH, duration);
                break;
        }

        sui.position.width = newW;
        sui.position.height = newH;
        
        if (currentPosition >= duration) {
            sui.position.width = targetW;
            sui.position.height = targetH;
            sui.updateSize(true);
            return true;
        }
        sui.updateSize(true);
        return false;
    }
}
