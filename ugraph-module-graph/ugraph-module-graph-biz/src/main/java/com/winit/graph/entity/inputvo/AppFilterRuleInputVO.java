package com.winit.graph.entity.inputvo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.winit.graph.entity.vo.AppFilterRuleVO;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 对应 graph_app_filter_rule
 */
public class AppFilterRuleInputVO {

    /**
     * app编号
     */
    @JsonProperty(value = "app_name")
    @NotEmpty(message = "app_name缺失")
    private String appName;

    @JsonProperty(value = "filter_list")
    private List<AppFilterRuleSaveVO> filterList;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<AppFilterRuleSaveVO> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<AppFilterRuleSaveVO> filterList) {
        this.filterList = filterList;
    }
}
