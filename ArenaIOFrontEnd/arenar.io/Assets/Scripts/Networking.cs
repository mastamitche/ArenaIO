using UnityEngine;
using System.Collections;
using WebSocketSharp;
using System.IO;
using System;
using System.Collections.Generic;

public class Networking {
    // To server
    public static byte s_spawn = 0;
    public static byte s_playerMove = 1;
    public static byte s_playerPickUp = 2;
    public static byte s_playerFire = 3;
    public static byte s_ping = 4;
    public static byte s_nameReq = 5;
    public static byte s_reload = 6;
    
    // To client
    public static byte c_serverParams = 0;
    public static byte c_gameState = 1;
    public static byte c_setEntityPosition = 2;
    public static byte c_entitySpawn = 3;
    public static byte c_entityDespawn = 4;
    public static byte c_playerStats = 5;
    public static byte c_playerEquip = 6;
    public static byte c_pong = 8;
    public static byte c_name = 9;


    public delegate void _PlayerInfo(int ID, int health, int armour, int ammo, int magazine, int magazineSize);
    public static event _PlayerInfo PlayerInfo;

    public delegate void _PlayerGun(int ID, int gunID);
    public static event _PlayerGun PlayerGun;

    static WebSocket socket;

    public void Start()
    {
        socket = new WebSocket("ws://192.168.1.12:32320");
        socket.OnOpen += (sender,e) => OnOpen();
        socket.OnClose += (sender, e) => OnClose();
        socket.OnError += (sender, e) => OnError();
        socket.OnMessage += (sender, e) => UnityMainThreadDispatcher.Instance().Enqueue(OnMessage(e));
        socket.Connect();
    }
    
    void OnError()
    {

    }

    IEnumerator OnMessage(MessageEventArgs e)
    {
        if (e.RawData.Length > 0)
            OnPacket(e.RawData);
        /*if (e.Data.Length > 0)
            Debug.Log(e.Data);*/
        yield return 0;
    }

    void OnOpen()
    {
        Debug.Log("Connection open");
        send(s_ping);
        send(s_spawn);
    }

    void OnClose()
    {

    }

    void OnPacket(byte[] data)
    {
        using (MemoryStream stream = new MemoryStream(data))
        {
            using (BinaryReader reader = new BinaryReader(stream))
            {
                int category = reader.ReadByte();
                /*if (Master.debugPackets)
                    print("Got Category " + category);*/

                if(category == c_serverParams)
                {

                }
                else if (category == c_gameState)
                {
                    byte gamestate = reader.ReadByte();
                    int myID = reader.ReadInt32();
                    LocalState.ID = myID;
                }
                else if (category == c_setEntityPosition)
                {
                    int ID = reader.ReadInt32();
                    if (Master.debugPackets)
                    {
                        /*print("Setting position of " + ID);
                        foreach (KeyValuePair<int, Entity> en in Entity.Entities)
                            print("Entities " + en.Key);*/
                    }
                    float x = reader.ReadSingle();
                    float y = reader.ReadSingle();
                    float angle = NetworkingUtility.getAngleFromByte(reader.ReadByte());
                    if (Entity.Entities.ContainsKey(ID))
                    {
                        Entity ent = Entity.Entities[ID];
                        if (ID != LocalState.ID)
                        {
                            EntityLerper entLerp = ent.gameObject.GetComponent<EntityLerper>();
                            if (entLerp != null)
                            {
                                entLerp.Lerp(new Vector3(x, y));
                            }
                        }
                        else
                        {
                            PlayerController player = Consts.instance.MainPlayer.transform.Find("Player").GetComponent<PlayerController>();
                            player.moveLayer(new Vector3(x, y));
                        }
                    }
                    else
                    {
                        /*if (Master.debugPackets)
                            print("Trying to move not registered Entity with ID : " + ID);*/
                    }
                }
                else if (category == c_entitySpawn)
                {
                    EntityTypes EntityType = (EntityTypes)reader.ReadByte();
                    int ID = reader.ReadInt32();
                    if (Master.debugPackets)
                        print("Spawning " + ID + " of Type " + EntityType);

                    if (EntityType == EntityTypes.TYPE_PLAYER) {
                        //( id, color, pos, PacketHelper.getAngleByte(angle), size, name, health, armour, ammo, magazine, magazineSize, gunID);

                        int color = reader.ReadByte();
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        float angle = NetworkingUtility.getAngleFromByte(reader.ReadByte());
                        int size = reader.ReadInt32();
                        string name = NetworkingUtility.ReadString(reader);
                        int health = reader.ReadInt32();
                        int maxHealth = reader.ReadInt32();
                        int armour = reader.ReadInt32();
                        int maxArmour = reader.ReadInt32();
                        int ammo = reader.ReadInt32();
                        int magazine = reader.ReadInt32();
                        int magazineSize = reader.ReadInt32();
                        int gunID = reader.ReadInt32();

                        if (ID != LocalState.ID)
                        {
                            GameObject player = GamePrefabBatcher.GetInstance(Consts.instance.playerPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                            player.GetComponent<Entity>().SetEnt(ID);
                        }else
                        {
                            print("Setting player ID " + ID);
                            GameObject player = Consts.instance.MainPlayer.transform.Find("Player").gameObject;
                            player.GetComponent<Entity>().SetEnt(ID);
                        }
                        PlayerInfo(ID, health, armour, ammo, magazine, magazineSize);
                        Entity ent = Entity.Entities[ID];
                        ent.gameObject.transform.Find("InfoBars/Health").GetComponent<Bar>().updateDisplayBar(health, maxHealth);
                        ent.gameObject.transform.Find("InfoBars/Armour").GetComponent<Bar>().updateDisplayBar(armour, maxArmour);
                    } else if (EntityType == EntityTypes.TYPE_SHOTGUN)
                    {
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        GameObject shotgun = GamePrefabBatcher.GetInstance(Consts.instance.shotGunPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                        shotgun.GetComponent<Pickupable>().SetEnt(ID);
                    }
                    else if (EntityType == EntityTypes.TYPE_BULLET)
                    {
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        GameObject bullet = GamePrefabBatcher.GetInstance(Consts.instance.bulletPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                        bullet.GetComponent<Pickupable>().SetEnt(ID);
                    }
                    else if (EntityType == EntityTypes.TYPE_AMMO)
                    {
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        int amount = reader.ReadInt32();
                        GameObject ammo = GamePrefabBatcher.GetInstance(Consts.instance.ammoPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                        ammo.GetComponent<Pickupable>().SetEnt(ID);
                        ammo.transform.Find("Text").GetComponent<TextSetter>().setText(amount + "");
                    }
                    else if (EntityType == EntityTypes.TYPE_ARMOUR)
                    {
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        int amount = reader.ReadInt32();
                        GameObject armour = GamePrefabBatcher.GetInstance(Consts.instance.armourPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                        armour.GetComponent<Pickupable>().SetEnt(ID);
                        armour.transform.Find("Text").GetComponent<TextSetter>().setText(amount + "");
                    }
                    else if (EntityType == EntityTypes.TYPE_HEALTHPACK)
                    {
                        float x = reader.ReadSingle();
                        float y = reader.ReadSingle();
                        int amount = reader.ReadInt32();
                        GameObject healthPack = GamePrefabBatcher.GetInstance(Consts.instance.healthPackPrefab, Consts.instance.Entities.transform, new Vector3(x, y));
                        healthPack.GetComponent<Pickupable>().SetEnt(ID);
                        healthPack.transform.Find("Text").GetComponent<TextSetter>().setText(amount + "");
                    }

                }
                else if (category == c_entityDespawn)
                {
                    int ID = reader.ReadInt32();
                    if (Entity.Entities.ContainsKey(ID))
                    {
                        if (Master.debugPackets)
                            print("DeSpawning " + ID + " of Type " + ID);
                        GamePrefabBatcher.Destroy(Entity.Destroy(ID));
                    }else
                    {
                        if(Master.debugPackets)
                            print("Trying to despawn ID " + ID);
                    }
                }
                else if (category == c_playerStats)
                {
                    int ID = reader.ReadInt32();
                    int health = reader.ReadInt32();
                    int maxHealth = reader.ReadInt32();
                    int armour = reader.ReadInt32();
                    int maxArmour = reader.ReadInt32();
                    int ammo = reader.ReadInt32();
                    int magazine = reader.ReadInt32();
                    int magazineSize = reader.ReadInt32();
                    PlayerInfo(ID,health, armour, ammo, magazine, magazineSize);
                    Entity ent = Entity.Entities[ID];
                    ent.gameObject.transform.Find("InfoBars/Health").GetComponent<Bar>().updateDisplayBar(health, maxHealth);
                    ent.gameObject.transform.Find("InfoBars/Armour").GetComponent<Bar>().updateDisplayBar(armour, maxArmour);
                }
                else if (category == c_playerEquip)
                {
                    int id = reader.ReadInt32();
                    int gunID = reader.ReadInt32();
                    PlayerGun(id, gunID);
                }
                else if (category == c_pong)
                {

                }
                else
                    print("Couldn't find Category " + category);

            }
        }
    }

    public static void sendPlayerMove(Vector2 v2, float angle)
    {
        send(s_playerMove, v2.x, v2.y, NetworkingUtility.getAngleByte(angle));
    }

    public static void onPlayerFire()
    {
        send(s_playerFire);
    }
    public static void onPlayerReload()
    {
        send(s_reload);
    }
    public static void onPlayerPickup(int ID)
    {
        send(s_playerPickUp, ID);
    }

    public static void send(params object[] args)
    {
        try
        {
            socket.Send(NetworkingUtility.bytesFromParams(args));
        }
        catch (Exception e)
        {
            print("Error");
            print(e.StackTrace);
        }
    }
    public static void print(String s)
    {
        Debug.Log(s);
    }
}
