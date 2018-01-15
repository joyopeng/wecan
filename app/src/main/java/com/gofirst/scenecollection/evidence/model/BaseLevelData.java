package com.gofirst.scenecollection.evidence.model;

import java.util.List;

/**
 * @author maxiran
 */
public interface BaseLevelData <T> {

    String getName();
    int getCurrentLevel();
    List<T> getNextLevelList();
    String getTopListName();
}
