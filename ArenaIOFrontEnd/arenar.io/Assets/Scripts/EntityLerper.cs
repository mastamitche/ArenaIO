using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class EntityLerper : MonoBehaviour {
    Vector3 start;
    Vector3 end;

    float startAngle;
    float endAngle;
    
    public float speed = 0.2f;
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
        end = pos;
    }
    public void Lerp(float angle)
    {
        angle *= Mathf.PI / 180;
        if (angle == transform.eulerAngles.z) return;
        endAngle = angle;
    }

    // Follows the target position like with a spring
    void Update()
    {
        transform.rotation = Quaternion.Euler(new Vector3(0, 0, transform.eulerAngles.z + (endAngle - transform.eulerAngles.z * speed)));
        
        transform.position = Vector3.Lerp(transform.position, end, speed);
    }
}
