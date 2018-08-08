using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TextSetter : MonoBehaviour {
    TextMesh textMesh;

    void Start()
    {
        textMesh = GetComponent<TextMesh>();
    }

    public void setText(string text)
    {
        if (textMesh == null) return;
        textMesh.text = text;
    }
}
