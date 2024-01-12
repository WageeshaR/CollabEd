package com.collabed.core.service.intel.criteria;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CriteriaTarget {
    private final TargetType targetType;
    private List<Object> targets;
    public CriteriaTarget(TargetType targetType) {
        this.targetType = targetType;
        this.targets = new ArrayList<>();
    }

    public void addTargets(Object target) {
        this.targets.add(target);
    }

    public enum TargetType {
        DB_FETCH,
        SUPPLIED,
        OUTPUT
    }

}
