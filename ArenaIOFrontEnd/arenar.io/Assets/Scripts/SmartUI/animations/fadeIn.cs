using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class fadeIn : UIAnimation {
    long startTime;
    float target;
    float initial;
    float duration;
    SmartUI.TweenTypes type;

    public fadeIn(float duration, float targetOpacity) {
        target = targetOpacity;
        this.duration = duration * 1000;
    }
    public fadeIn(float duration) {
        target = 1;
        this.duration = duration * 1000;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(fadeOut));
        initial = sui.opacity;
    }

    public override bool tick() {
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;

        float progress = (currentPosition / duration);

        sui.opacity = initial + ((target-initial) * progress);
        sui.updateOpacity();
        if (currentPosition >= duration) {
            sui.opacity = target;
            sui.updateOpacity();
            return true;
        }
        return false;
    }
}
