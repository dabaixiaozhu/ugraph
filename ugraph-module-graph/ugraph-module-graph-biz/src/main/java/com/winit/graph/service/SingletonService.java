package com.winit.graph.service;

import java.util.List;

/**
 * 存储单例
 */
public interface SingletonService {

    List<String> getIsGeneratingVersionGraphRelation();

    void addIsGeneratingVersionGraphRelation(String appName);

    void removeIsGeneratingVersionGraphRelation(String appName);

    List<String> getIsGeneratingTrunkGraphRelation();

    void addIsGeneratingTrunkGraphRelation(String appName);

    void removeIsGeneratingTrunkGraphRelation(String appName);

    boolean getIsInitGeneratingTrunkGraphRelation();

    void setIsInitGeneratingTrunkGraphRelation(boolean flag);
}
