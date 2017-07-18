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

   // private static DataProvider instance = null;
    private static ArrayList<GeneralCategory> generalCategories;
    private static ArrayList<SubCategory> subCategories;
    private static ArrayList<Transaction> transactions;
    private static RootCategory[] rootCategories;// = {new RootCategory("Income"), new RootCategory("Expenses")};
    private static KeyPackage keyPackage;

//    private DataProvider() {
//        generalCategories = new ArrayList<>();
//        subCategories = new ArrayList<>();
//        transactions = new ArrayList<>();
//    }
//
//    public static DataProvider getInstance() {
//        if (instance == null) {
//            System.out.println("INSTANCE IS NULL");
//            instance = new DataProvider();
//        }
//        return instance;
//    }

    public static void loadData(ArrayList<GeneralCategory> generalCategories,
                                ArrayList<SubCategory> subCategories,
                                ArrayList<Transaction> transactions) {

        rootCategories = new RootCategory[2];
        rootCategories[0] = new RootCategory("Income");
        rootCategories[1] = new RootCategory("Expenses");

        DataProvider.generalCategories = generalCategories;
        DataProvider.subCategories = subCategories;
        DataProvider.transactions = transactions;

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

//    public void setGeneralCategories(ArrayList<GeneralCategory> generalCategories) {
//        this.generalCategories = generalCategories;
//    }

    public static ArrayList<GeneralCategory> getGeneralCategories() {
        return generalCategories;
    }

//    public ArrayList<GeneralCategory> getGeneralCategories() {
//
//        System.out.println("-----------------------------------------------------");
//
//        for (GeneralCategory g : generalCategories) {
//            System.out.println(g.toString());
//            for (SubCategory sc : g.getSubCategories()) {
//                System.out.println(sc.toString());
//            }
//        }
//
//        System.out.println("-----------------------------------------------------");
//
//        ArrayList<Transaction> t = new ArrayList<>(this.transactions);
//        ArrayList<SubCategory> sc = new ArrayList<>(this.subCategories);
//        ArrayList<GeneralCategory> gc = new ArrayList<>(this.generalCategories);
//
//        for (Transaction transaction : t) {
//            for (SubCategory subCategory : sc) {
//                if (transaction.getSubCategoryID() == subCategory.getId()) {
//                    subCategory.getTransactions().add(transaction);
//                    break;
//                }
//            }
//        }
//
//        for (SubCategory subCategory : sc) {
//            for (GeneralCategory generalCategory : gc) {
//                if (subCategory.getGeneralCategoryID() == generalCategory.getId()) {
//                    generalCategory.getSubCategories().add(subCategory);
//                    break;
//                }
//            }
//        }
//
//        for (GeneralCategory g : gc) {
////            for (SubCategory s : g.getSubCategories()) {
////                System.out.println(s.toString());
////            }
//            Collections.sort(g.getSubCategories(), new SubCategoryComparator());
//        }
//
//        Collections.sort(gc, new GeneralCategoryComparator());
//
//        return gc;
//    }

//    public ArrayList<SubCategory> getSubCategories() {
//        return subCategories;
//    }
//
//    public void setSubCategories(ArrayList<SubCategory> subCategories) {
//        this.subCategories = subCategories;
//    }
//
//    public void setTransactions(ArrayList<Transaction> transactions) {
//        this.transactions = transactions;
//    }

    public static void setKeyPackage(KeyPackage keyPackage) {
        DataProvider.keyPackage = keyPackage;
    }

    public static KeyPackage getKeyPackage() {
        return keyPackage;
    }

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }


//    public RootCategory[] getRootCategories() {
//
//        ArrayList<Transaction> transactions = this.transactions;
//        ArrayList<SubCategory> subCategories = this.subCategories;
//        ArrayList<GeneralCategory> generalCategories = this.generalCategories;
//
//        for (Transaction transaction : transactions) {
//            for (SubCategory subCategory : subCategories) {
//                if (transaction.getSubCategoryID() == subCategory.getId()) {
//                    subCategory.getTransactions().add(transaction);
//                    break;
//                }
//            }
//        }
//
//        for (SubCategory subCategory : subCategories) {
//            for (GeneralCategory generalCategory : generalCategories) {
//                if (subCategory.getGeneralCategoryID() == generalCategory.getId()) {
//                    generalCategory.getSubCategories().add(subCategory);
//                    break;
//                }
//            }
//        }
//
//        RootCategory[] rootCategories = {new RootCategory("Income"), new RootCategory("Expenses")};
//
//        for (GeneralCategory generalCategory : generalCategories) {
//            for (int i = 0; i < rootCategories.length; i++) {
//                if (generalCategory.getRootCategory().equals(rootCategories[i].getCategoryName())) {
//                    rootCategories[i].getGeneralCategories().add(generalCategory);
//                    break;
//                }
//            }
//        }
//
//        return rootCategories;
//
//    }

//    public RootCategory[] getRootCategories() {
//        return rootCategories;
//    }
//
//    public void setRootCategories(RootCategory[] rootCategories) {
//        this.rootCategories = rootCategories;
//    }

//    public ArrayList<GeneralCategory> getGeneralCategories() {
//        ArrayList<GeneralCategory> categories = new ArrayList<>();
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                Collections.sort(gc.getSubCategories(), new SubCategoryComparator());
//                categories.add(gc);
//            }
//        }
//
//        Collections.sort(categories, new GeneralCategoryComparator());
//        return categories;
//    }

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

//    public boolean checkDuplicateGeneralCategory(String categoryName) {
//        ArrayList<String> generalCategories = new ArrayList<>();
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                generalCategories.add(gc.getCategoryName());
//            }
//        }
//        if (generalCategories.contains(categoryName)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public ArrayList<Transaction> getTransactions() {
//        return transactions;
//    }

//    public ArrayList<Transaction> getTransactions() {
//
//        ArrayList<Transaction> transactions = new ArrayList<>(this.transactions);
//        ArrayList<SubCategory> subCategories = new ArrayList<>(this.subCategories);
//
//        for (Transaction transaction : transactions) {
//            for (SubCategory subCategory : subCategories) {
//                if (transaction.getSubCategoryID() == subCategory.getId()) {
//                    transaction.setSubCategoryName(subCategory.getCategoryName());
//                    for (GeneralCategory generalCategory : generalCategories) {
//                        if (subCategory.getGeneralCategoryID() == generalCategory.getId()) {
//                            transaction.setRootCategoryType(generalCategory.getRootCategory());
//                            transaction.setCategoryIcon(generalCategory.getIconFile());
//                            break;
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//
//        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
//
//        return transactions;
//
//    }

//    public ArrayList<Transaction> getTransactions() {
//        ArrayList<Transaction> transactions = new ArrayList<>();
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                for (SubCategory sc: gc.getSubCategories()) {
//                    for (Transaction t : sc.getTransactions()) {
//                        transactions.add(t);
//                    }
//                }
//            }
//        }
//
//        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
//        return transactions;
//    }

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
    }

//    public void addTransaction(Transaction transaction) {
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                for (SubCategory sc : gc.getSubCategories()) {
//                    if (transaction.getSubCategoryID() == sc.getId()) {
//                        transaction.setSubCategoryName(sc.getCategoryName());
//                        transaction.setCategoryIcon(gc.getIconFile());
//                        transaction.setRootCategoryType(gc.getRootCategory());
//                        sc.getTransactions().add(transaction);
//                        return;
//                    }
//                }
//            }
//        }
//    }

    public static void updateTransaction(Transaction transaction) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == transaction.getId()) {
                transactions.set(i, transaction);
                break;
            }
        }
    }

//    public void updateTransaction(Transaction transaction) {
//        deleteTransaction(transaction);
//        addTransaction(transaction);
//    }

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

    public static void deleteTransaction(Transaction transactionToDelete) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == transactionToDelete.getId()) {
                transactions.remove(transaction);
                break;
            }
        }
    }

//    public void deleteTransaction(Transaction transaction) {
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
//                for (SubCategory sc : gc.getSubCategories()) {
//                    for (Transaction t : sc.getTransactions()) {
//                        if (transaction.getId() == t.getId()) {
//                            sc.getTransactions().remove(t);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    }

    public static void addGeneralCategory(GeneralCategory generalCategory) {
        generalCategories.add(generalCategory);
    }

//    public void addGeneralCategory(GeneralCategory generalCategory) {
//        for (int i = 0; i < rootCategories.length; i++) {
//            if (rootCategories[i].getCategoryName().equals(generalCategory.getRootCategory())) {
//                rootCategories[i].getGeneralCategories().add(generalCategory);
//                return;
//            }
//        }
//    }

}
