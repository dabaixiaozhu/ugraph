package com.winit.graph.service.impl;

import com.winit.graph.service.SingletonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SingletonServiceImpl implements SingletonService {

    /**
     * 是否正在初始化 true表示正在生成，false表示已结束
     */
    private volatile boolean isInitGeneratingTrunkGraphRelation = false;

    /**
     * trunk是否正常生成调用关系
     */
    private final CopyOnWriteArrayList<String> isGeneratingTrunkGraphRelation = new CopyOnWriteArrayList<>();

    /**
     * version是否正常生成调用关系
     */
    private final CopyOnWriteArrayList<String> isGeneratingVersionGraphRelation = new CopyOnWriteArrayList<>();


    @Override
    public List<String> getIsGeneratingVersionGraphRelation() {
        return isGeneratingVersionGraphRelation;
    }

    @Override
    public void addIsGeneratingVersionGraphRelation(String appName) {
        isGeneratingVersionGraphRelation.add(appName);
    }

    @Override
    public void removeIsGeneratingVersionGraphRelation(String appName) {
        isGeneratingVersionGraphRelation.remove(appName);
    }

    @Override
    public List<String> getIsGeneratingTrunkGraphRelation() {
        return isGeneratingTrunkGraphRelation;
    }

    @Override
    public void addIsGeneratingTrunkGraphRelation(String appName) {
        isGeneratingTrunkGraphRelation.add(appName);
    }

    @Override
    public void removeIsGeneratingTrunkGraphRelation(String appName) {
        isGeneratingTrunkGraphRelation.remove(appName);
    }

    @Override
    public boolean getIsInitGeneratingTrunkGraphRelation() {
        return isInitGeneratingTrunkGraphRelation;
    }

    @Override
    public void setIsInitGeneratingTrunkGraphRelation(boolean flag) {
        isInitGeneratingTrunkGraphRelation = flag;
    }
}
