using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class slideY : UIAnimation{
    long startTime;
    float initialY;
    float targetY;
    float duration;
    SmartUI.TweenTypes type;

    public slideY(float initialY, float targetY, float duration, SmartUI.TweenTypes type)
    {
        this.initialY = initialY;
        this.targetY = targetY;
        this.duration = duration * 1000;
        this.type = type;
    }

    public override void init() {
        startTime = System.DateTime.Now.Ticks / 10000L;
    }

    public override bool tick(){
        float currentPosition = (System.DateTime.Now.Ticks / 10000L) - startTime;
        float newY = -1;
        
        switch (type)
        {
            case SmartUI.TweenTypes.elasticEnd:
                newY = LibTween.easeOutElastic(currentPosition, initialY, targetY - initialY, duration, null, null);
                break;
            case SmartUI.TweenTypes.elasticBoth:
                newY = LibTween.easeInOutElastic(currentPosition, initialY, targetY - initialY, duration, null, null);
                break;
            case SmartUI.TweenTypes.easeStart:
                newY = LibTween.easeInCubic(currentPosition, initialY, targetY - initialY, duration);
                break;
            case SmartUI.TweenTypes.easeEnd:
                newY = LibTween.easeOutCubic(currentPosition, initialY, targetY - initialY, duration);
                break;
            case SmartUI.TweenTypes.easeBoth:
                newY = LibTween.easeInOutQuad(currentPosition, initialY, targetY - initialY, duration);
                break;
            case SmartUI.TweenTypes.linear:
                newY = LibTween.linear(currentPosition, initialY, targetY - initialY, duration);
                break;
        }

        sui.position.y = newY;
        if (currentPosition >= duration){
            sui.position.y = targetY;
            return true;
        }
        return false;
    }
}
