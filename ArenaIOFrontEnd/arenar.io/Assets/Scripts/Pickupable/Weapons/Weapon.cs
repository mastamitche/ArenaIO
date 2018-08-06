using UnityEngine;
using System.Collections;

public class Weapon : MonoBehaviour {
    public GameObject bullet;
    public GameObject firingLocation;
    int magazineAmmo =1;
    public int maxMagazineAmmo = 0;
    int currentHoldAmmo = 0;
    public int shotAmount = 1;
    public int maxHoldAmmo = 0;
    float lastFire = 0;
    public float fireRate = 0; // time before you can shoot again
    public float reloadRate = 0;
    float lastReload = 0;
    public float bulletsize = 1;
    public float bulletSpeed = 1;
    public float maxSpreadAngle = 15;
    bool isEquipped = false;

    public string name = "Weapon";
    // Use this for initialization
    void Start () {
        magazineAmmo = maxMagazineAmmo;
    }
    
    public string getName()
    {
        return name;
    }
    
    public float getFireRate()
    {
        return fireRate;
    }

    public void setMaxHoldAmmo(int val)
    {
        maxHoldAmmo = val;
    }

    public int getMaxHoldAmmo()
    {
        return maxHoldAmmo;
    }

    public int getCurrentHoldAmmo()
    {
        return currentHoldAmmo;
    }

    public void pickedUp()
    {
        magazineAmmo = maxMagazineAmmo;
    }

    public int addCurrentHoldAmmo(int amount)
    {
        currentHoldAmmo += amount;
        int val = currentHoldAmmo;
        if (currentHoldAmmo > maxHoldAmmo)
        {
            currentHoldAmmo = maxHoldAmmo;
        }
        int dif = Mathf.Abs( val - currentHoldAmmo );
        OnInfoChange(returnDisplayInfo());
        return dif;
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    public void attemptToShoot()
    {
        Debug.Log(magazineAmmo);
        Debug.Log(shotAmount);
        if ((lastFire + fireRate) < Time.fixedTime && shotAmount <= magazineAmmo)
        {
            Debug.Log("Shooting");
            lastFire = Time.fixedTime;
            Shoot();
        }
    }

    public virtual void Shoot()
    {
        //fire
        for(int i=0; i< shotAmount; i++)
        {
            GameObject go = Instantiate(bullet) as GameObject;
            Debug.Log(firingLocation);
            go.transform.position = firingLocation.transform.position;
            go.GetComponent<Rigidbody2D>().AddForce(Vector2FromAngle(createSpread(gameObject.transform.parent.eulerAngles.z)).normalized* bulletSpeed);
        }
        magazineAmmo -= shotAmount;
        OnInfoChange(returnDisplayInfo());
    }

    public float createSpread(float initAngle)
    {
        float randomAngle = Random.RandomRange(-0.5f*maxSpreadAngle, 0.5f*maxSpreadAngle);
        return initAngle + randomAngle;
    }

    public static Vector2 Vector2FromAngle(float a)
    {
        a *= Mathf.Deg2Rad;
        return new Vector2(Mathf.Cos(a), Mathf.Sin(a));
    }

    public void reload()
    {
        if ((lastReload + fireRate) < Time.fixedTime)
        {
            lastReload = Time.fixedTime;
            //reload 
            int amountToReload = maxMagazineAmmo - magazineAmmo;
            if (currentHoldAmmo > amountToReload)
            {
                currentHoldAmmo -= amountToReload;
                magazineAmmo = maxMagazineAmmo;
            }
            else if (currentHoldAmmo > 0)
            {
                maxMagazineAmmo = maxMagazineAmmo + currentHoldAmmo;
                currentHoldAmmo = 0;
            }
        }
        OnInfoChange(returnDisplayInfo());
    }

    public int[] returnDisplayInfo()
    {
        return new int[3] { magazineAmmo, maxMagazineAmmo, currentHoldAmmo };
    }

    public void displayShot()
    {

    }

    void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.gameObject.layer == LayerMask.NameToLayer("Player"))
        {
            PlayerWeaponController pwc = collision.gameObject.GetComponent<PlayerWeaponController>();
            pwc.swapWeapon(gameObject);
        }
    }

    public delegate void _OnInfoChange(int[] info);
    public static event _OnInfoChange OnInfoChange;
}
