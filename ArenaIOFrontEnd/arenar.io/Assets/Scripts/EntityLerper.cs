using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class EntityLerper : MonoBehaviour {
    Vector3 start;
    Vector3 end;

    float startAngle;
    float endAngle;
    
    public float speed = 1F;
    private long startTime;
    private long startTimeAngle;
    private float len;
    private float lenAngle;

    private bool hasReachedEnd = true;
    private bool hasReachedEndAngle = true;

    void Start()
    {
    }

    public void Lerp(Vector3 pos)
    {
        if (pos == transform.position) return;
        startTime = System.DateTime.Now.Ticks;
        start = transform.position;
        end = pos;
        len = Vector3.Distance(start, end);
        hasReachedEnd = false;
    }
    public void Lerp(float angle)
    {
        if (angle == transform.rotation.z) return;
        startTimeAngle = System.DateTime.Now.Ticks;
        startAngle = transform.rotation.z;
        endAngle = angle;
        lenAngle = Mathf.Abs(startAngle - endAngle);
        hasReachedEndAngle = false;
    }

    // Follows the target position like with a spring
    void Update()
    {
        if (!hasReachedEndAngle)
        {
            float dist = (System.DateTime.Now.Ticks - startTimeAngle) * speed;
            float percent = dist / lenAngle;
            
            transform.rotation = Quaternion.Euler(new Vector3(0, 0, startAngle + endAngle * percent));
            if (transform.rotation.z == endAngle)
                hasReachedEndAngle = true;
        }

        if (!hasReachedEnd)
        {
            float dist = (System.DateTime.Now.Ticks  - startTime) ;
            float percent = dist / len;
            //print("Percent " + percent + " Dist and len " + dist + " " + len + "start " + start  + " end " + end + " time " + startTime);
            transform.position = Vector3.Lerp(start, end, percent);
            if (transform.position == end)
                hasReachedEnd = true;
        }
    }
}
