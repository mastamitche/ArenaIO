﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Consts : MonoBehaviour {

    public List<Color> playercolors = new List<Color>();
    public GameObject bulletPrefab;
    public GameObject playerPrefab;
    public GameObject healthPackPrefab;
    public GameObject armourPrefab;
    public GameObject ammoPrefab;
    public GameObject shotGunPrefab;
    public GameObject Entities;
    public GameObject MainPlayer;

    public static Consts instance;
	// Use this for initialization
	void Start () {
        instance = this;
	}
}