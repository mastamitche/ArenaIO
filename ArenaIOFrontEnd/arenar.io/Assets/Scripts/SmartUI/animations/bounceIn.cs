using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class bounceIn : UIAnimation{
    long startTime;
    float initialWidth;
    float initialHeight;
    float duration;
    float amplitude;
    float period;
    float startPos;
    float posMod;

    public bounceIn(float initialWidth, float initialHeight, float duration, float amplitude, float period, float startPos, float posMod)
    {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.duration = duration*1000;
        this.amplitude = amplitude;
        this.period = period;
        this.startPos = startPos;
        this.posMod = posMod;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
    }

    public override bool tick(){
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;
        float scaleMod = LibTween.easeOutElastic(currentPosition, startPos, posMod, duration, amplitude, period);

        sui.position.width = initialWidth * scaleMod;
        sui.position.height = initialHeight * scaleMod;
        if (currentPosition >= duration) {
            sui.position.width = initialWidth;
            sui.position.height = initialHeight;
            return true;
        }
        return false;
    }
}
