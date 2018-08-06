using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class PlayerController : MonoBehaviour
{
    public GameObject playerCircle;

    public float acceleration = 1;

    Vector3 mouse_pos;
    Vector3 object_pos;
    float angle;
    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        rotateTowardsMouse();
        Vector3 vecToMove = Vector3.zero;
        if (Input.GetKey(KeyCode.A) || Input.GetKey(KeyCode.LeftArrow)) vecToMove += new Vector3(-1, 0, 0);
        if (Input.GetKey(KeyCode.D) || Input.GetKey(KeyCode.RightArrow)) vecToMove += new Vector3(1, 0, 0);
        if (Input.GetKey(KeyCode.W) || Input.GetKey(KeyCode.UpArrow)) vecToMove += new Vector3(0, 1, 0);
        if (Input.GetKey(KeyCode.S) || Input.GetKey(KeyCode.DownArrow)) vecToMove += new Vector3(0, -1, 0);
        if (vecToMove.sqrMagnitude > 0)
            gameObject.GetComponent<Rigidbody2D>().AddForce(vecToMove.normalized * acceleration);
    }

    void rotateTowardsMouse()
    {
        mouse_pos = Input.mousePosition;
        object_pos = Camera.main.WorldToScreenPoint(playerCircle.transform.position);
        mouse_pos.x = mouse_pos.x - object_pos.x;
        mouse_pos.y = mouse_pos.y - object_pos.y;
        angle = Mathf.Atan2(mouse_pos.y, mouse_pos.x) * Mathf.Rad2Deg;
        playerCircle.transform.rotation = Quaternion.Euler(new Vector3(0, 0, angle));
    }

}
