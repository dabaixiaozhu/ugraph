package com.winit.graph.common.entity;


public enum CallTypeEnum {
    CTE_ITF("ITF", "INTERFACE"),
    CTE_LM("LM", "LAMBDA"),
    CTE_ST("ST", "STREAM"),
    CTE_RIR("RIR", "RUNNABLE_INIT_RUN"),
    CTE_CIC("CIC", "CALLABLE_INIT_CALL"),
    CTE_TSR("TSR", "THREAD_START_RUN"),
    CTE_SCC("SCC", "SUPER_CALL_CHILD"),
    CTE_CCS("CCS", "CHILD_CALL_SUPER"),
    CTE_MA("MA", "MANUAL_ADD"),
    CTE_ILLEGAL("ILLEGAL", "ILLEGAL"),
    ;

    private final String type;

    private final String desc;

    CallTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CallTypeEnum getFromType(String type) {
        for (CallTypeEnum callTypeEnum : CallTypeEnum.values()) {
            if (callTypeEnum.getType().equals(type)) {
                return callTypeEnum;
            }
        }
        return CallTypeEnum.CTE_ILLEGAL;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return type + "-" + desc;
    }
}
