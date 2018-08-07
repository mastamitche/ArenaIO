using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class fadePulse : UIAnimation {
    long startTime;
    float targetLow;
    float targetHigh;
    float duration;
    bool reverse = true;
    SmartUI.TweenTypes type;

    public fadePulse(float duration, float targetOpacityLow, float targetOpacityHigh) {
        targetLow = targetOpacityLow;
        targetHigh = targetOpacityHigh;
        this.duration = duration * 1000;
    }
    public fadePulse(float duration)
    {
        targetLow = 0;
        targetHigh = 1;
        this.duration = duration * 1000;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(fadeOut));
        sui.removeAnimationsOfType(typeof(fadeIn));
        sui.removeAnimationsOfType(typeof(fadePulse));
    }

    public override bool tick() {
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;

        float progress = (currentPosition / duration);
        //fade out 
        if (reverse)
            sui.opacity = targetHigh - ((targetHigh - targetLow) * progress);
        else
            sui.opacity = targetLow + ((targetHigh - targetLow) * progress);
        //fade in
        if (currentPosition >= duration) {
            reverse = !reverse;
            startTime = System.DateTime.Now.Ticks / 10000L;
        }
        return false;
    }
}
