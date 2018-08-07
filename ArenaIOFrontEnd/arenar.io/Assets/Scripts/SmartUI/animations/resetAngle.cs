using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class resetAngle : UIAnimation {
    long startTime;
    float duration;
    float initial;

    public resetAngle(float duration) {
        this.duration = duration*1000;
    }

    public override void init() {
        initial = sui.angle % 360;
        if (initial > 180) initial -= 360;
        if (initial < -180) initial += 360;
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(wobble));
    }

    public override bool tick() {
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;

        float progress = currentPosition / duration;
        sui.angle = initial * (1-progress);
        
        if (progress >= 1) {
            sui.angle = 0;
            return true;
        }

        return false;
    }
}
