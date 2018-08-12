using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Master : MonoBehaviour
{

    public static Master instance;

    void Awake()
    {
        if (instance != null)
        {
            Destroy(this);
            return;
        }
        DontDestroyOnLoad(transform.gameObject);
        instance = this;
        mono = this;
        network = new Networking();
        runNetworking();
    }

    public static void runNetworking()
    {
        // Break this out so we can init with loading screen before we connect
        network.Start();
    }

    public static string URLOverride = "192.168.1.12";//My local IP
    public static int PortOverride = 32320;
    public static bool isDev = false;
    public static bool debugPackets = false;
    public static Networking network;

    public static MonoBehaviour mono; // So that back-end tasks can call coroutines and such
}