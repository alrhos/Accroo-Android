package io.accroo.android.model;

import java.util.Comparator;

/**
 * Created by oscar on 12/06/17.
 */

public class GeneralCategoryComparator implements Comparator<GeneralCategory> {
    @Override
    public int compare(GeneralCategory gc1, GeneralCategory gc2) {
        return gc1.getCategoryName().compareTo(gc2.getCategoryName());
    }
}
