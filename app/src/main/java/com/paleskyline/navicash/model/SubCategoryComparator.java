package com.paleskyline.navicash.model;

import java.util.Comparator;

/**
 * Created by oscar on 12/06/17.
 */

public class SubCategoryComparator implements Comparator<SubCategory> {
    @Override
    public int compare(SubCategory sc1, SubCategory sc2) {
        return sc1.getCategoryName().compareTo(sc2.getCategoryName());
    }
}
