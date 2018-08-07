using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class rotate : UIAnimation {
    long startTime;
    float speed;
    float endAfter;

    public rotate(float speed, float endAfter) {
        this.speed = speed ;
        this.endAfter = endAfter;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
        sui.removeAnimationsOfType(typeof(resetAngle));
        sui.removeAnimationsOfType(typeof(rotate));
        sui.removeAnimationsOfType(typeof(wobble));
    }

    public override bool tick()
    {
        sui.angle = (sui.angle+ speed) % 360.0f;
        if (endAfter != -1)
        {
            if ((System.DateTime.Now.Ticks / 10000L) - startTime > endAfter)
                return true;
        }
        return false;
    }
}
