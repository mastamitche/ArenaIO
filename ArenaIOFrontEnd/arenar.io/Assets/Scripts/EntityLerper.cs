using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class EntityLerper : MonoBehaviour {
    Vector3 start;
    Vector3 end;
    
    public float speed = .1F;
    private float startTime;
    private float len;

    private bool hasReachedEnd = true;

    void Start()
    {
    }

    public void Lerp(Vector3 pos)
    {
        startTime = Time.time;
        start = transform.position;
        end = pos;
        len = Vector3.Distance(start, end);
        hasReachedEnd = false;
    }

    // Follows the target position like with a spring
    void Update()
    {
        if (hasReachedEnd) return;
        float dist = (Time.time - startTime) * speed;
        float percent = dist / len;
        
        transform.position = Vector3.Lerp(start, end, percent);
        if(transform.position == end)
            hasReachedEnd = true;
    }
}
