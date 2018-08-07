using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class wobble : UIAnimation {
    long startTime;
    float amplitude;
    float period;
    float endAfter;

    public wobble(float amplitude, float period, float endAfter) {
        this.period = period * 1000;
        this.amplitude = amplitude;
        this.endAfter = endAfter;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(resetAngle));
        sui.removeAnimationsOfType(typeof(rotate));
    }

    public override bool tick()
    {
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;
        float progress = Mathf.PI * 2 * (currentPosition / period);
        float angle = Mathf.Cos(progress) * amplitude;

        sui.angle = angle;
        if (endAfter != -1)
            if ((startTime + endAfter) < (System.DateTime.Now.Ticks / 10000L) )
            {
                sui.addAnimation(new resetAngle((period/1000)/(2.0f)), null);
                return false;
            }
        return false;
    }
}
