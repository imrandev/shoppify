package com.nerdgeeks.shop.Util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.nerdgeeks.shop.Model.Product;
import com.nerdgeeks.shop.Model.Stock;
import com.nerdgeeks.shop.Model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {

    public static DatabaseReference firebaseDatabase;

    public DatabaseUtil(){
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    //find the firebase database JSON config file
    private InputStream findFile() {

        String fileName = "shop-9c056-firebase-adminsdk-z5fb7-3c23768f33.json";

        // this is the path within the jar file
        InputStream input = getClass().getResourceAsStream("/resources/" + fileName);
        if (input == null) {
            // this is how we load file within editor (EX: Eclipse, Intellij IDE)
            input = getClass().getClassLoader().getResourceAsStream(fileName);
        }
        return input;
    }

    //connect the application to firebase database
    public boolean ConnectFirebase() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(findFile()))
                    .setDatabaseUrl("https://shop-9c056.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);

            //Initialize the firebase database
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    //get firebase database node value for realtime
    public static void getDataValueEvent(String databaseNodeName, OnGetDataListener dataListener) {

        firebaseDatabase.child(databaseNodeName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataListener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataListener.onFailure(databaseError);
            }
        });
    }

    //get firebase database node value for single time
    public static void getDataForSingleValueEvent(String databaseNodeName, OnGetDataListener dataListener) {

        firebaseDatabase.child(databaseNodeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataListener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataListener.onFailure(databaseError);
            }
        });
    }

    //remove supplier from database
    public static void delSupplier(String supplierId) {
        firebaseDatabase.child(AppConstant.SUPPLIERS_DATABASE_NODE_NAME)
                .child(supplierId)
                .removeValue((databaseError, databaseReference) -> {
                });
    }

    //remove products from database
    public static void delProduct(String supplierName, String productCategory, String productId) {
        firebaseDatabase.child(AppConstant.PRODUCTS_DATABASE_NODE_NAME)
                .child(supplierName)
                .child(productCategory)
                .child(productId)
                .removeValue((databaseError, databaseReference) -> {
                });
    }

    //remove purchase record from database
    public static void delPurchaseRecord(String purchaseMonth, String purchaseDate, String purchaseSupplier, String purchaseId) {
        firebaseDatabase.child(AppConstant.INVOICES_DATABASE_NODE_NAME)
                .child(purchaseMonth)
                .child(purchaseDate)
                .child(AppConstant.PURCHASE_DATABASE_NODE_NAME)
                .child(purchaseSupplier)
                .child(purchaseId)
                .removeValue((databaseError, databaseReference) -> {
                });
    }

    //Get all supplier name
    public static ObservableList getSupplierName() {

        ObservableList<String> supplierName = FXCollections.observableArrayList();

        firebaseDatabase.child(AppConstant.SUPPLIERS_DATABASE_NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Supplier supplier = dataSnapshot1.getValue(Supplier.class);
                    supplierName.add(supplier.getSupplierName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return supplierName;
    }

    //get all product Name by supplier
    public static ObservableList getProductDetailsForSupplier(String SupplierName, String productChild){

        ObservableList<String> productChildDetails = FXCollections.observableArrayList();

        firebaseDatabase.child(AppConstant.PRODUCTS_DATABASE_NODE_NAME).child(SupplierName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot rootCategory: dataSnapshot.getChildren()){
                    for(DataSnapshot rootProduct: rootCategory.getChildren()) {

                        Product product = rootProduct.getValue(Product.class);
                        switch(productChild) {
                            case "productName" :
                                productChildDetails.add(product.getProductName());
                                break;
                            case "productId" :
                                productChildDetails.add(product.getProductId());
                                break;
                            case "productBuyingPrice" :
                                productChildDetails.add(product.getBuyingPrice());
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return productChildDetails;
    }

    // get all stock quantity with product details
    public static ObservableList<Stock> getStockDataWithProductDetails(){

        ObservableList<Stock> stockData = FXCollections.observableArrayList();

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(stockData.size()>0){
                    stockData.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.child(AppConstant.STOCK_DATABASE_NODE_NAME).getChildren()){

                    Stock stock = snapshot.getValue(Stock.class);

                    //Now get product details using product id from database
                    for(DataSnapshot rootSupplier: dataSnapshot.child(AppConstant.PRODUCTS_DATABASE_NODE_NAME).getChildren()){
                        for(DataSnapshot rootCategory: rootSupplier.getChildren()){
                            for(DataSnapshot rootProduct: rootCategory.getChildren()){
                                if (rootProduct.getKey().equals(snapshot.getKey())){
                                    Product product = rootProduct.getValue(Product.class);
                                    Stock stockWithProductDetails = new Stock(product.getProductId(),product.getProductName(),product.getProductCategory(),product.getProductSupplier(),stock.getInStock());
                                    stockData.add(stockWithProductDetails);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return stockData;
    }


}

