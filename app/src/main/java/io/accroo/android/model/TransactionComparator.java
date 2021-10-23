package io.accroo.android.model;

import java.util.Comparator;

/**
 * Created by oscar on 7/06/17.
 */

public class TransactionComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction t1, Transaction t2) {
        // Temporary fix for incorrect sort order.
        if (t1.getDate().equals(t2.getDate())) {
            return -1;
        }
        return t1.getDate().compareTo(t2.getDate());
    }
}
