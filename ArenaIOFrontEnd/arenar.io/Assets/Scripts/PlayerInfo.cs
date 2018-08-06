using UnityEngine;
using System.Collections;

public class PlayerInfo : MonoBehaviour {
    public int maxArmor = 0;
    int currentArmor = 0;
    public int maxHP = 0;
    int currentHp = 0;

    //testing
    bool set = false;
	// Use this for initialization
	void Start () {
        currentHp = maxHP;
	}
	
	// Update is called once per frame
	void Update () {
	    if(Time.fixedTime > 10 && !set)
        {
            set = true;
            takeDamage(maxHP/4);
        }    
	}
    public int getMaxArmor()
    {
        return maxArmor;
    }
    public int getMaxHP()
    {
        return maxHP;
    }
    public int getHP()
    {
        return currentHp;
    }
    public int getArmor()
    {
        return currentArmor;
    }
    
    public void heal(int amount)
    {
        currentHp += amount;
        if (currentHp > maxHP)
            currentHp = maxHP;
        OnHPChange(currentHp, maxHP);
    }
    public void takeDamage(int amount)
    {
        currentHp -= amount;
        if (currentHp < 0)
            currentHp = 0;
        //if (currentHp == 0)
            //OnDeath(true);
        OnHPChange(currentHp, maxHP);
    }

    public delegate void _OnHPChange(int _currentHP,int _maxHP);
    public static event _OnHPChange OnHPChange;

    public delegate void _OnDeath(bool _death);
    public static event _OnDeath OnDeath;
}
