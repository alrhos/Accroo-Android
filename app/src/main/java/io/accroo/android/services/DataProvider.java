package io.accroo.android.services;

import org.joda.time.DateTime;

import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.GeneralCategoryComparator;
import io.accroo.android.model.Key;
import io.accroo.android.model.RootCategory;
import io.accroo.android.model.SessionData;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.SubCategoryComparator;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.TransactionComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by oscar on 27/05/17.
 */

public class DataProvider {

    public static ArrayList<SessionData> sessions = new ArrayList<>();
    private static ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private static ArrayList<SubCategory> subCategories = new ArrayList<>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static RootCategory[] rootCategories;
    private static Key key;
    private static DateTime startDate, endDate;

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
                if (transaction.getSubCategoryId().equals(subCategory.getId())) {
                    transaction.setParent(subCategory);
                    subCategory.getTransactions().add(transaction);
                    break;
                }
            }
        }

        for (SubCategory subCategory : subCategories) {
            for (GeneralCategory generalCategory : generalCategories) {
                if (subCategory.getGeneralCategoryId().equals(generalCategory.getId())) {
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

    public static ArrayList<SessionData> getSessions() {
        return sessions;
    }

    public static void setSessions(ArrayList<SessionData> sessions) {
        DataProvider.sessions = sessions;
    }

    public static RootCategory[] getRootCategories() {
        return rootCategories;
    }

    public static ArrayList<GeneralCategory> getGeneralCategories() {
        return generalCategories;
    }

    public static void setKey(Key key) {
        DataProvider.key = key;
    }

    public static Key getKey() {
        return key;
    }

    public static void setStartDate(DateTime startDate) {
        DataProvider.startDate = startDate;
    }

    public static void setEndDate(DateTime endDate) {
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

        return categoryNames.contains(categoryName);
    }

    public static boolean checkDuplicateSubCategory(String categoryName) {
        ArrayList<String> categoryNames = new ArrayList<>();

        for (SubCategory subCategory : subCategories) {
            categoryNames.add(subCategory.getCategoryName());
        }

        return categoryNames.contains(categoryName);
    }

    public static void addTransaction(Transaction transaction) {
        if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
            transactions.add(transaction);
            for (SubCategory subCategory : subCategories) {
                if (transaction.getSubCategoryId().equals(subCategory.getId())) {
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
            if (transactions.get(i).getId().equals(transaction.getId())) {
                if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
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
            if (transaction.getId().equals(transactionToDelete.getId())) {
                transactions.remove(transaction);
                break;
            }
        }

        for (SubCategory subCategory : subCategories) {
            for (Transaction transaction : subCategory.getTransactions()) {
                if (transaction.getId().equals(transactionToDelete.getId())) {
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
            if (generalCategories.get(i).getId().equals(generalCategory.getId())) {
                generalCategories.set(i, generalCategory);
                break;
            }
        }
        sortData();
    }

    public static void deleteGeneralCategory(GeneralCategory categoryToDelete) {
        Iterator<GeneralCategory> generalCategoryIterator = generalCategories.listIterator();
        while (generalCategoryIterator.hasNext()) {
            UUID generalCategoryID = generalCategoryIterator.next().getId();
            if (generalCategoryID.equals(categoryToDelete.getId())) {

                Iterator<SubCategory> subCategoryIterator = subCategories.listIterator();

                while (subCategoryIterator.hasNext()) {
                    SubCategory subCategory = subCategoryIterator.next();
                    if (((GeneralCategory) subCategory.getParent()).getId().equals(generalCategoryID)) {

                        Iterator<Transaction> transactionIterator = transactions.listIterator();

                        while (transactionIterator.hasNext()) {
                            Transaction transaction = transactionIterator.next();
                            if (((SubCategory) transaction.getParent()).getId().equals(subCategory.getId())) {
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
            if (subCategories.get(i).getId().equals(subCategory.getId())) {
                subCategories.set(i, subCategory);
                break;
            }
        }
        sortData();
    }

    public static void deleteSubCategory(SubCategory categoryToDelete) {
        Iterator<SubCategory> subCategoryIterator = subCategories.listIterator();
        while (subCategoryIterator.hasNext()) {
            UUID subCategoryID = subCategoryIterator.next().getId();
            if (subCategoryID.equals(categoryToDelete.getId())) {
                Iterator<Transaction> transactionIterator = transactions.listIterator();
                while (transactionIterator.hasNext()) {
                    Transaction transaction = transactionIterator.next();
                    if (((SubCategory) transaction.getParent()).getId().equals(subCategoryID)) {
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
