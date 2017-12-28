package com.paleskyline.accroo.services;

import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.GeneralCategoryComparator;
import com.paleskyline.accroo.model.KeyPackage;
import com.paleskyline.accroo.model.RootCategory;
import com.paleskyline.accroo.model.SubCategory;
import com.paleskyline.accroo.model.SubCategoryComparator;
import com.paleskyline.accroo.model.Transaction;
import com.paleskyline.accroo.model.TransactionComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by oscar on 27/05/17.
 */

public class DataProvider {

    private static ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private static ArrayList<SubCategory> subCategories = new ArrayList<>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static RootCategory[] rootCategories;
    private static KeyPackage keyPackage;
    private static Date startDate, endDate;

    public static void loadData(ArrayList<GeneralCategory> generalCategories,
                                ArrayList<SubCategory> subCategories,
                                ArrayList<Transaction> transactions) {

        DataProvider.generalCategories = generalCategories;
        DataProvider.subCategories = subCategories;
        DataProvider.transactions = transactions;

        sortData();
    }

    private static void sortData() {
        // Clear existing child objects

        for (GeneralCategory generalCategory : generalCategories) {
            generalCategory.getSubCategories().clear();
        }

        for (SubCategory subCategory : subCategories) {
            subCategory.getTransactions().clear();
        }

        rootCategories = new RootCategory[2];
        rootCategories[0] = new RootCategory("Income");
        rootCategories[1] = new RootCategory("Expense");

        for (Transaction transaction : transactions) {
            for (SubCategory subCategory : subCategories) {
                if (transaction.getSubCategoryID() == subCategory.getId()) {
                    transaction.setParent(subCategory);
                    subCategory.getTransactions().add(transaction);
                    break;
                }
            }
        }

        for (SubCategory subCategory : subCategories) {
            for (GeneralCategory generalCategory : generalCategories) {
                if (subCategory.getGeneralCategoryID() == generalCategory.getId()) {
                    subCategory.setParent(generalCategory);
                    generalCategory.getSubCategories().add(subCategory);
                    break;
                }
            }
        }

        for (GeneralCategory generalCategory : generalCategories) {
            Collections.sort(generalCategory.getSubCategories(), new SubCategoryComparator());
            for (int i = 0; i < rootCategories.length; i++) {
                if (generalCategory.getRootCategory().equals(rootCategories[i].getCategoryName())) {
                    rootCategories[i].getGeneralCategories().add(generalCategory);
                    break;
                }
            }
        }

        Collections.sort(generalCategories, new GeneralCategoryComparator());
        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
    }

    public static RootCategory[] getRootCategories() {
        return rootCategories;
    }

    public static ArrayList<GeneralCategory> getGeneralCategories() {
        return generalCategories;
    }

    public static void setKeyPackage(KeyPackage keyPackage) {
        DataProvider.keyPackage = keyPackage;
    }

    public static KeyPackage getKeyPackage() {
        return keyPackage;
    }

    public static void setStartDate(Date startDate) {
        DataProvider.startDate = startDate;
    }

    public static void setEndDate(Date endDate) {
        DataProvider.endDate = endDate;
    }

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static boolean checkDuplicateGeneralCategory(String categoryName) {
        ArrayList<String> categoryNames = new ArrayList<>();

        for (GeneralCategory generalCategory : generalCategories) {
            categoryNames.add(generalCategory.getCategoryName());
        }

        if (categoryNames.contains(categoryName)) {
            return true;
        }

        return false;
    }

    public static boolean checkDuplicateSubCategory(String categoryName) {
        ArrayList<String> categoryNames = new ArrayList<>();

        for (SubCategory subCategory : subCategories) {
            categoryNames.add(subCategory.getCategoryName());
        }

        if (categoryNames.contains(categoryName)) {
            return true;
        }

        return false;
    }

    public static void addTransaction(Transaction transaction) {
        if (!transaction.getDate().before(startDate) && !transaction.getDate().after(endDate)) {
            transactions.add(transaction);
            for (SubCategory subCategory : subCategories) {
                if (transaction.getSubCategoryID() == subCategory.getId()) {
                    transaction.setParent(subCategory);
                    subCategory.getTransactions().add(transaction);
                    break;
                }
            }
            Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
        }
    }

    public static void updateTransaction(Transaction transaction) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == transaction.getId()) {
                if (!transaction.getDate().before(startDate) && !transaction.getDate().after(endDate)) {
                    transactions.set(i, transaction);
                } else {
                    transactions.remove(i);
                }
                break;
            }
        }
        sortData();

    }

    public static void deleteTransaction(Transaction transactionToDelete) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == transactionToDelete.getId()) {
                transactions.remove(transaction);
                break;
            }
        }

        for (SubCategory subCategory : subCategories) {
            for (Transaction transaction : subCategory.getTransactions()) {
                if (transaction.getId() == transactionToDelete.getId()) {
                    subCategory.getTransactions().remove(transaction);
                    break;
                }
            }
        }

    }

    public static void addGeneralCategory(GeneralCategory generalCategory) {
        generalCategories.add(generalCategory);
        sortData();
    }

    public static void updateGeneralCategory(GeneralCategory generalCategory) {
        for (int i = 0; i < generalCategories.size(); i++) {
            if (generalCategories.get(i).getId() == generalCategory.getId()) {
                generalCategories.set(i, generalCategory);
                break;
            }
        }
        sortData();
    }

    public static void deleteGeneralCategory(GeneralCategory categoryToDelete) {
        Iterator<GeneralCategory> generalCategoryIterator = generalCategories.listIterator();
        while (generalCategoryIterator.hasNext()) {
            int generalCategoryID = generalCategoryIterator.next().getId();
            if (generalCategoryID == categoryToDelete.getId()) {

                Iterator<SubCategory> subCategoryIterator = subCategories.listIterator();

                while (subCategoryIterator.hasNext()) {
                    SubCategory subCategory = subCategoryIterator.next();
                    if (((GeneralCategory) subCategory.getParent()).getId() == generalCategoryID) {

                        Iterator<Transaction> transactionIterator = transactions.listIterator();

                        while (transactionIterator.hasNext()) {
                            Transaction transaction = transactionIterator.next();
                            if (((SubCategory) transaction.getParent()).getId() == subCategory.getId()) {
                                transactionIterator.remove();
                            }
                        }
                        subCategoryIterator.remove();
                    }
                }
                generalCategoryIterator.remove();
                break;
            }
        }
        sortData();
    }

    public static void addSubCategory(SubCategory subCategory) {
        subCategories.add(subCategory);
        sortData();
    }

    public static void updateSubCategory(SubCategory subCategory) {
        for (int i = 0; i < subCategories.size(); i++) {
            if (subCategories.get(i).getId() == subCategory.getId()) {
                subCategories.set(i, subCategory);
                break;
            }
        }
        sortData();
    }

    public static void deleteSubCategory(SubCategory categoryToDelete) {
        Iterator<SubCategory> subCategoryIterator = subCategories.listIterator();
        while (subCategoryIterator.hasNext()) {
            int subCategoryID = subCategoryIterator.next().getId();
            if (subCategoryID == categoryToDelete.getId()) {
                Iterator<Transaction> transactionIterator = transactions.listIterator();
                while (transactionIterator.hasNext()) {
                    Transaction transaction = transactionIterator.next();
                    if (((SubCategory) transaction.getParent()).getId() == subCategoryID) {
                        transactionIterator.remove();
                    }
                }
                subCategoryIterator.remove();
                break;
            }
        }
        sortData();
    }

}
