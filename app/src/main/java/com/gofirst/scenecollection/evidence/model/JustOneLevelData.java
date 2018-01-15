package com.gofirst.scenecollection.evidence.model;

import java.util.List;

/**
 * @author maxiran
 */
public abstract class JustOneLevelData implements BaseLevelData<String>{

    public int getCurrentLevel(){return 1;};
    public List<String> getNextLevelList(){return null;};
    public String getTopListName(){return null;};
}
