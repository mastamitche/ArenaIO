using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class PlayerController : MonoBehaviour
{
    public GameObject playerCircle;

    public float acceleration = 1;

    Vector3 mousePos;
    Vector3 objectPos;
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
        onMoveOrRotate();
    }


    Vector3 lastMove = new Vector3();
    float lastAngle = 0;
    void onMoveOrRotate()
    {
        Vector3 pos = transform.position;
        float rot = transform.rotation.z;
        if (lastMove == pos && lastAngle == rot) return;
        lastMove = pos;
        lastAngle = rot;
        Networking.sendPlayerMove(new Vector2(pos.x, pos.y), rot);
    }

    void rotateTowardsMouse()
    {
        mousePos = Input.mousePosition;
        objectPos = Camera.main.WorldToScreenPoint(playerCircle.transform.position);
        mousePos.x = mousePos.x - objectPos.x;
        mousePos.y = mousePos.y - objectPos.y;
        angle = Mathf.Atan2(mousePos.y, mousePos.x) * Mathf.Rad2Deg;
        playerCircle.transform.rotation = Quaternion.Euler(new Vector3(0, 0, angle));
    }

}
