using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class slideX : UIAnimation{
    long startTime;
    float initialX;
    float targetX;
    float duration;
    SmartUI.TweenTypes type;

    public slideX(float initialX, float targetX, float duration, SmartUI.TweenTypes type)
    {
        this.initialX = initialX;
        this.targetX = targetX;
        this.duration = duration*1000;
        this.type = type;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
    }

    public override bool tick(){
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;
        float newX = -1;

        switch (type)
        {
            case SmartUI.TweenTypes.elasticEnd:
                newX = LibTween.easeOutElastic(currentPosition, initialX, targetX - initialX, duration, null, null);
                break;
            case SmartUI.TweenTypes.elasticBoth:
                newX = LibTween.easeInOutElastic(currentPosition, initialX, targetX - initialX, duration, null, null);
                break;
            case SmartUI.TweenTypes.easeStart:
                newX = LibTween.easeInQuad(currentPosition, initialX, targetX - initialX, duration);
                break;
            case SmartUI.TweenTypes.easeEnd:
                newX = LibTween.easeOutQuad(currentPosition, initialX, targetX - initialX, duration);
                break;
            case SmartUI.TweenTypes.easeBoth:
                newX = LibTween.easeInOutQuad(currentPosition, initialX, targetX - initialX, duration);
                break;
            case SmartUI.TweenTypes.linear:
                newX = LibTween.linear(currentPosition, initialX, targetX - initialX, duration);
                break;
        }

        sui.position.x = newX;
        if (currentPosition >= duration){
            sui.position.x = targetX;
            return true;
        }
        return false;
    }
}
