package com.paleskyline.accroo.model;

import java.util.Comparator;

/**
 * Created by oscar on 7/06/17.
 */

public class TransactionComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction t1, Transaction t2) {
        int value1 = t1.getDate().compareTo(t2.getDate());
        if (value1 == 0) {
            return ((Integer) t1.getId()).compareTo((Integer) t2.getId());
        }
        return value1;
    }
}
