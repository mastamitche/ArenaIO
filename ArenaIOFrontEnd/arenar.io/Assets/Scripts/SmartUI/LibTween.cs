using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LibTween : MonoBehaviour {

    public static float linear(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return valueChange * positionCurrent / positionEnd + valueStart;
    }
    
    public static float easeInQuad(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return valueChange * positionCurrent * positionCurrent / positionEnd + valueStart;
    }
    
    public static float easeOutQuad(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        float positionCurrentByPositionEnd = positionCurrent / positionEnd;
        return (valueChange * -1) * positionCurrentByPositionEnd * (positionCurrent - 2) + valueStart;
    }
    
    public static float easeInOutQuad(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        float positionCurrentByPositionEnd = positionCurrent / (positionEnd / 2);
        float positionCurrentDecrement = positionCurrentByPositionEnd - 1;
        if (positionCurrentByPositionEnd < 1)
            return valueChange / 2 * positionCurrentByPositionEnd * positionCurrentByPositionEnd + valueStart;
        return (valueChange * -1) / 2 * (positionCurrentDecrement * (positionCurrentDecrement - 2) - 1) + valueStart;
    }
    
    // Speeds up, stops abruptly
    public static float easeInCubic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return valueChange * Mathf.Pow(positionCurrent / positionEnd, 3) + valueStart;
    }

    // abruptly starts fast, slows to target
    public static float easeOutCubic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return valueChange * (Mathf.Pow(positionCurrent / positionEnd - 1, 3) + 1) + valueStart;
    }
    
    // Starts and ends slow, speeds up in the center
    public static float easeInOutCubic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        float positionCurrentByPositionEnd = positionCurrent / (positionEnd / 2);
        float positionCurrentDecrement = positionCurrentByPositionEnd - 2;
        if (positionCurrentByPositionEnd < 1)
            return valueChange / 2 * Mathf.Pow(positionCurrentByPositionEnd, 3) + valueStart;
        return valueChange / 2 * (Mathf.Pow(positionCurrentDecrement, 3) + 2) + valueStart;
    }

    // Speeds up, stops abruptly (spends longer in slow part than cubic)
    public static float easeInQuartic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return valueChange * Mathf.Pow(positionCurrent / positionEnd, 4) + valueStart;
    }

    // abruptly starts fast, slows to target
    public static float easeOutQuartic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        return (valueChange * -1) * (Mathf.Pow(positionCurrent / positionEnd - 1, 4) - 1) + valueStart;
    }

    // Starts and ends slow, speeds up in the center
    public static float easeInOutQuartic(float positionCurrent, float valueStart, float valueChange, float positionEnd){
        float positionCurrentByPositionEnd = positionCurrent / (positionEnd / 2);
        float positionCurrentDecrement = positionCurrentByPositionEnd - 2;
		if (positionCurrentByPositionEnd < 1)
            return valueChange / 2 * Mathf.Pow(positionCurrentByPositionEnd, 4) + valueStart;
		return (valueChange* -1) / 2 * (Mathf.Pow(positionCurrentDecrement, 4) - 2) + valueStart;
    }

    // Quickly shoots toward target position, before rocking a little when it gets there
    public static float easeOutElastic(float positionCurrent, float valueStart, float valueChange, float positionEnd, float? amplitude, float? period){
        float? unknownNum3 = null;
        float positionCurrentByPositionEnd = positionCurrent / positionEnd;
        if (positionCurrent == 0) return valueStart;
        if (positionCurrentByPositionEnd == 1) return valueStart + valueChange;
        if (!period.HasValue) period = positionEnd * 0.3f;

        if (!amplitude.HasValue || amplitude < Mathf.Abs(valueChange)) {
            amplitude = valueChange;
            unknownNum3 = period / 4;
        } else
            unknownNum3 = period / (2 * Mathf.PI) * Mathf.Asin(valueChange / amplitude.Value);
        return amplitude.Value * Mathf.Pow(2, -10 * positionCurrentByPositionEnd) * Mathf.Sin((positionCurrentByPositionEnd * positionEnd - unknownNum3.Value) * (2 * Mathf.PI) / period.Value) + valueChange + valueStart;
    }

    
    // Rocks a bit before bouncing towards target
    public static float easeInOutElastic(float positionCurrent, float valueStart, float valueChange, float positionEnd, float? amplitude, float? period){
        float? unknownNum3 = null;
        float positionCurrentByPositionEnd = positionCurrent / (positionEnd / 2);
        float positionCurrentDecrement = positionCurrentByPositionEnd - 1;

        if (positionCurrent == 0) return valueStart;
        if (positionCurrentByPositionEnd == 2) return valueStart + valueChange;
        if (!period.HasValue) period = positionEnd * 0.45f;//(.3 * 1.5)

		if (!amplitude.HasValue || amplitude < Mathf.Abs(valueChange)){
            amplitude = valueChange;
            unknownNum3 = period / 4;
		}else
			unknownNum3 = period / (2 * Mathf.PI) * Mathf.Asin(valueChange / amplitude.Value);
		
		if (positionCurrentByPositionEnd < 1)
			return -0.5f * (amplitude.Value * Mathf.Pow(2, 10 * positionCurrentDecrement) * Mathf.Sin( (positionCurrentDecrement* positionEnd - unknownNum3.Value) * (2 * Mathf.PI) / period.Value)) + valueStart;
		
		return amplitude.Value * Mathf.Pow(2, -10 * positionCurrentDecrement) * Mathf.Sin( (positionCurrentDecrement* positionEnd - unknownNum3.Value) * (2 * Mathf.PI) / period.Value) * 0.5f + valueChange + valueStart;

    }


}
