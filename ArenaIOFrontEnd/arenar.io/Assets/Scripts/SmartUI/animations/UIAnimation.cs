using System.Collections;
using System;
using System.Collections.Generic;
using UnityEngine;

public abstract class UIAnimation {
    public SmartUI sui;
    public abstract bool tick();
    public abstract void init();
    public Action callback;
}
