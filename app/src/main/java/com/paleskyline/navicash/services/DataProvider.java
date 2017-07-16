package com.paleskyline.navicash.services;

import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.GeneralCategoryComparator;
import com.paleskyline.navicash.model.KeyPackage;
import com.paleskyline.navicash.model.RootCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.SubCategoryComparator;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.model.TransactionComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by oscar on 27/05/17.
 */

public class DataProvider {

    private static DataProvider instance = null;
    private RootCategory[] rootCategories;
    private KeyPackage keyPackage;

    private DataProvider() {
        rootCategories = new RootCategory[2];
        rootCategories[0] = new RootCategory("Income");
        rootCategories[1] = new RootCategory("Expenses");
    }

    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }

    public void setKeyPackage(KeyPackage keyPackage) {
        this.keyPackage = keyPackage;
    }

    public KeyPackage getKeyPackage() {
        return keyPackage;
    }

    public RootCategory[] getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(RootCategory[] rootCategories) {
        this.rootCategories = rootCategories;
    }

    public ArrayList<GeneralCategory> getGeneralCategories() {
        ArrayList<GeneralCategory> categories = new ArrayList<>();
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                Collections.sort(gc.getSubCategories(), new SubCategoryComparator());
                categories.add(gc);
            }
        }

        Collections.sort(categories, new GeneralCategoryComparator());
        return categories;
    }

    public boolean checkDuplicateGeneralCategory(String categoryName) {
        ArrayList<String> generalCategories = new ArrayList<>();
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                generalCategories.add(gc.getCategoryName());
            }
        }
        if (generalCategories.contains(categoryName)) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                for (SubCategory sc: gc.getSubCategories()) {
                    for (Transaction t : sc.getTransactions()) {
                        transactions.add(t);
                    }
                }
            }
        }

        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                for (SubCategory sc : gc.getSubCategories()) {
                    if (transaction.getSubCategoryID() == sc.getId()) {
                        transaction.setSubCategoryName(sc.getCategoryName());
                        transaction.setCategoryIcon(gc.getIconFile());
                        transaction.setRootCategoryType(gc.getRootCategory());
                        sc.getTransactions().add(transaction);
                        return;
                    }
                }
            }
        }
    }

    public void updateTransaction(Transaction transaction) {
        deleteTransaction(transaction);
        addTransaction(transaction);
    }

//    public void updateTransaction(Transaction transaction) {
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                for (SubCategory sc : gc.getSubCategories()) {
//                    for (Transaction t : sc.getTransactions()) {
//                        if (transaction.getId() == t.getId()) {
//                            t.setAmount(transaction.getAmount());
//                            t.setDateString(transaction.getDateString());
//                            t.setDescription(transaction.getDescription());
//                            if (transaction.getSubCategoryID() != t.getSubCategoryID()) {
//
//                            } else {
//                                return;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//        for (Transaction t : sc.getTransactions()) {
//            if (transaction.getId() == t.getId()) {
//                t.setAmount(transaction.getAmount());
//                t.setSubCategoryID(transaction.getSubCategoryID());
//                t.setSubCategoryName(transaction.getSubCategoryName());
//                t.setCategoryIcon(transaction.getCategoryIcon());
//                t.setRootCategoryType(transaction.getRootCategoryType());
//                t.setDateString(transaction.getDateString());
//                t.setDescription(transaction.getDescription());
//                return;
//            }
//        }
//    }

    public void deleteTransaction(Transaction transaction) {
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                for (SubCategory sc : gc.getSubCategories()) {
                    for (Transaction t : sc.getTransactions()) {
                        if (transaction.getId() == t.getId()) {
                            sc.getTransactions().remove(t);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void addGeneralCategory(GeneralCategory generalCategory) {
        for (int i = 0; i < rootCategories.length; i++) {
            if (rootCategories[i].getCategoryName().equals(generalCategory.getRootCategory())) {
                rootCategories[i].getGeneralCategories().add(generalCategory);
                return;
            }
        }
    }

}
