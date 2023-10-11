package cn.ccsu.cecs.common.utils;

import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BonusUtils {

    private static Map<Integer, BonusConstant.CategoryWeightEnum> weightEnumMap = new LinkedHashMap<>() {{
        put(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode(),
                BonusConstant.CategoryWeightEnum.BASE_SCORE);
        put(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode(),
                BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE);
        put(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode(),
                BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE);
        put(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode(),
                BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE);
    }};

    public static Map<Integer, BonusConstant.CategoryWeightEnum> getCategoryWeightEnums() {
        return weightEnumMap;
    }

    public static BonusConstant.CategoryWeightEnum getCategoryWeightEnum(Integer code) {
        return weightEnumMap.get(code);
    }


}
