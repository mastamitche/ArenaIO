using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class fadeOut : UIAnimation {
    long startTime;
    float initial;
    float duration;
    SmartUI.TweenTypes type;

    public fadeOut(float duration) {
        this.duration = duration * 1000;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(fadeIn));
        initial = sui.opacity;
    }

    public override bool tick() {
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;

        float progress = (currentPosition / duration);
        
        sui.opacity = initial * (1.0f - progress);
        sui.updateOpacity();
        if (currentPosition >= duration) {
            sui.opacity = 0;
            sui.updateOpacity();
            return true;
        }
        return false;
    }
}
