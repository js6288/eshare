package com.shu.eshare.common;

/**
 * 每个等级所需的经验
 * lv1 200经验
 * lv2 600经验
 * lv3 1000经验
 * lv4 2800经验
 * lv5 4800经验
 */
public class LevelExperience {

    public final static Integer LV1 = 1;
    public final static Integer LV2 = 2;
    public final static Integer LV3 = 3;
    public final static Integer LV4 = 4;
    public final static Integer LV5 = 5;
    public final static Integer LV6 = 6;

    public final static Integer LV1MAX = 200;
    public final static Integer LV2MAX = 600;
    public final static Integer LV3MAX = 1000;
    public final static Integer LV4MAX = 2800;
    public final static Integer LV5MAX = 4800;

    /**
     * 根据等级获取最大经验
     * @param level 等级
     */
    public static Integer getMaxExperienceByLevel(Integer level){
        switch (level){
            case 1:
                return LV1MAX;
            case 2:
                return LV2MAX;
            case 3:
                return LV3MAX;
            case 4:
                return LV4MAX;
            case 5:
                return LV5MAX;
            default: return Integer.MAX_VALUE;
        }
    }
}
