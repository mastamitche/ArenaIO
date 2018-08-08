using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

class GamePrefabBatcher {

    private static Dictionary<GameObject, List<GameObject>> pools = new Dictionary<GameObject, List<GameObject>>(); // prefab -> gameObject[]
    private static Dictionary<List<GameObject>, List<GameObject>> availableGameObjects = new Dictionary<List<GameObject>, List<GameObject>>(); // pool -> available entries in it
    private static Dictionary<GameObject, List<GameObject>> gameObjectToPoolMap = new Dictionary<GameObject, List<GameObject>>(); // gameObject -> pool

    private static GameObject addToList(GameObject prefab, bool willBeAvailable) {
        GameObject b = MonoBehaviour.Instantiate(prefab);
        if (!pools.ContainsKey(prefab)) pools[prefab] = new List<GameObject>(); // Make sure pool exists
        List<GameObject> pool = pools[prefab];
        if (!availableGameObjects.ContainsKey(pool)) availableGameObjects[pool] = new List<GameObject>(); // Make sure availability exists
        pools[prefab].Add(b);
        if (willBeAvailable) // If we aren't gonna use this object, add it to available (pre-cache purposes)
            availableGameObjects[pool].Add(b);
        gameObjectToPoolMap[b] = pools[prefab];
        return b;
    }
    
    public static GameObject GetInstance(GameObject prefab,Transform parent) {
        return GetInstance(prefab, parent, null);
    }

    public static GameObject GetInstance(GameObject prefab,Transform parent, Vector3? newPos) {
        //Check storage
        GameObject b = getNextAvailable(prefab);
        if(b == null)
            b = addToList(prefab, false);
        b.transform.SetParent(parent);
        //Need to do these here, because we need to set position before we turn back on the particle effects or you get a smoothing effect
        if (newPos != null)
            b.transform.position = (Vector3)newPos;

        b.SetActive(true);
        //Nothing available, need new
        return b;
    }
    public static void Destroy(GameObject go) {
        if (gameObjectToPoolMap.ContainsKey(go)) {
            // Move to container if it isn't already
            go.transform.SetParent(Consts.instance.Entities.transform);

            List<GameObject> pool = gameObjectToPoolMap[go];
            ParticleSystem[] particleSystems = go.GetComponentsInChildren<ParticleSystem>();
            foreach (ParticleSystem p in particleSystems)
                p.Clear();
            availableGameObjects[pool].Add(go);
            go.SetActive(false);
            return;
        }
        // Not one of ours, destroy it
        Debug.Log("Dont know object " + go.name + " deleting");
        GameObject.Destroy(go);
    }
    public static void Clear() {
        foreach (KeyValuePair<GameObject, List<GameObject>> l in pools)
            foreach (GameObject b in l.Value)
                GameObject.Destroy(b);
        pools.Clear();
        availableGameObjects.Clear();
        gameObjectToPoolMap.Clear();
    }

    private static GameObject getNextAvailable(GameObject prefab) {
        List<GameObject> pool = null;
        if (pools.TryGetValue(prefab, out pool)) {
            if (availableGameObjects[pool].Count == 0) return null;
            GameObject toReturn = availableGameObjects[pool].First();
            availableGameObjects[pool].RemoveAt(0);

            List<ParticleSystem> particles = new List<ParticleSystem>(); // enable all emission
            foreach (ParticleSystem ps in particles) {
                var x = ps.emission;
                x.enabled = true;
            }

            return toReturn;
        }
        return null;
    }

}
