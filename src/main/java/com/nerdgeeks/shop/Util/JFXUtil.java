package com.nerdgeeks.shop.Util;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JFXUtil {

    private static double xOffset = 0, yOffset = 0;

    //set the table column name and initialize the table column with the database column
    public static void setTableData(TableView table, String[] colName, ObservableList tableData){

        table.getColumns().clear();

        String capitalizeColumnNameFirstLatter;
        String name;
        TableColumn column;
        String buying = "buying", selling = "selling", supplier= "supplier", product = "product";

        //Initialize the table Column and column value
        for(String aColName : colName) {

            if(aColName.contains(buying)){
                capitalizeColumnNameFirstLatter = buying.substring(0, 1).toUpperCase() + buying.substring(1);
                name = aColName.replace(buying, capitalizeColumnNameFirstLatter + " ");
            } else if(aColName.contains(selling)){
                capitalizeColumnNameFirstLatter = selling.substring(0, 1).toUpperCase() + selling.substring(1);
                name = aColName.replace(selling, capitalizeColumnNameFirstLatter + " ");
            } else if (aColName.contains(supplier)){
                capitalizeColumnNameFirstLatter = supplier.substring(0, 1).toUpperCase() + supplier.substring(1);
                name = aColName.replace(supplier, capitalizeColumnNameFirstLatter + " ");
            } else{
                capitalizeColumnNameFirstLatter = product.substring(0, 1).toUpperCase() + product.substring(1);
                name = aColName.replace(product, capitalizeColumnNameFirstLatter + " ");
            }

            column = new TableColumn(name);
            column.setCellValueFactory(new PropertyValueFactory(aColName)); //indicate the table column to model column
            table.getColumns().add(column);
        }
        table.setItems(tableData); //Set All data to table
    }

    // show the popup windows
    public static void popUpWindows(String FXMLPath) {
        try {
            URL myFxmlURL = ClassLoader.getSystemResource(FXMLPath);
            Parent root = FXMLLoader.load(myFxmlURL);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showAlertBox(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("All Field Required!");
        alert.setHeaderText("Please fill the form!");
        alert.setContentText(errorMessage);
        alert.show();
    }


    public static String getCurrentMonth(){
        String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        String currentMonth = timeStamp.substring(3,5);
        return timeStamp.substring(6)+"-"+month[Integer.parseInt(currentMonth)-1];
    }

    public static String getCurrentDate(){
        String currentDate = new SimpleDateFormat("dd").format(new Date());
        if(currentDate.substring(0,1).contains("0")){
            currentDate = currentDate.substring(1) ;
        }
        return currentDate;
    }

}
