package com.manifest.data;

import com.common.PrintUtil;

/**
 *
 * Created by xueqiulxq on 20/07/2017.
 */

public class AttributeType {


    public static String getAttributeType(AttributeEntry entry) {
//        if (entry.data == ATTR_STRING) {
//
//        }
        return "--" + entry.type;
    }

    public static String getAttributeData(AttributeEntry entry) {
        return "--" + PrintUtil.hex4(entry.data);
    }
}
