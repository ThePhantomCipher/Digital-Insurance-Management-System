package com.mycompany.insurancemanagementsystem;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    private boolean isValidId(String id) {
    return id != null && id.matches("\\d{13}");
    }

    private void markInvalid(Control field) {

    field.setStyle(
    "-fx-border-color: red; -fx-border-width: 2px;"
    );
    }

    private void markValid(Control field) {

    field.setStyle("");
    }

    @Override
    public void start(Stage primaryStage) {

        TextField tfID = new TextField();
        TextField tfName = new TextField();
        TextField tfSurname = new TextField();
        TextField tfAge = new TextField();
        TextField tfAddress = new TextField();
        TextField tfCoverage = new TextField();
        TextField tfPremium = new TextField();

        tfPremium.setEditable(false);

        Label idHint =
        new Label("Enter RSA ID only (13 digits)");

        idHint.setStyle(
        "-fx-text-fill: darkblue; -fx-font-size: 11px;"
        );

        ComboBox<String> cbPolicyType =
        new ComboBox<>();

        cbPolicyType.getItems().addAll(
        "Life","Health","Auto"
        );

        TextField tfSearchID = new TextField();
        tfSearchID.setPromptText("Search by ID (13 digits)");

        Button btnSearch = 
        new Button("Search ID");

        Button btnSubmit =
        new Button("Submit");

        Button btnView =
        new Button("View Policies");

        Button btnUpdate = 
        new Button("Update Selected");

        Button btnDelete = 
        new Button("Delete Selected");

        TableView<Policy> table =
        new TableView<>();

        TableColumn<Policy,String> colId =
        new TableColumn<>("ID Number");

        colId.setCellValueFactory(
        new PropertyValueFactory<>("idNum")
        );

        TableColumn<Policy,String> colName =
        new TableColumn<>("Name");

        colName.setCellValueFactory(
        new PropertyValueFactory<>("name")
        );

        TableColumn<Policy,String> colSurname =
        new TableColumn<>("Surname");

        colSurname.setCellValueFactory(
        new PropertyValueFactory<>("surname")
        );

        TableColumn<Policy,String> colAddress =
        new TableColumn<>("Address");

        colAddress.setCellValueFactory(
        new PropertyValueFactory<>("address")
        );

        TableColumn<Policy,Integer> colAge =
        new TableColumn<>("Age");

        colAge.setCellValueFactory(
        new PropertyValueFactory<>("age")
        );

        TableColumn<Policy,String> colType =
        new TableColumn<>("Policy Type");

        colType.setCellValueFactory(
        new PropertyValueFactory<>("policyType")
        );

        TableColumn<Policy,Double> colCoverage =
        new TableColumn<>("Coverage");

        colCoverage.setCellValueFactory(
        new PropertyValueFactory<>("coverageAmount")
        );

        TableColumn<Policy,Double> colPremium =
        new TableColumn<>("Premium");

        colPremium.setCellValueFactory(
        new PropertyValueFactory<>("premiumAmount")
        );

        table.getColumns().addAll(
        colId,
        colName,
        colSurname,
        colAddress,
        colAge,
        colType,
        colCoverage,
        colPremium
        );

        GridPane form = new GridPane();

        form.setVgap(5);
        form.setHgap(10);

        form.add(new Label("ID Number:"),0,0);
        form.add(tfID,1,0);
        form.add(idHint,1,1);

        form.add(new Label("Name:"),0,1);
        form.add(tfName,1,1);

        form.add(new Label("Surname:"),0,2);
        form.add(tfSurname,1,2);

        form.add(new Label("Address:"),0,3);
        form.add(tfAddress,1,3);

        form.add(new Label("Age:"),0,4);
        form.add(tfAge,1,4);

        form.add(new Label("Policy Type:"),0,5);
        form.add(cbPolicyType,1,5);

        form.add(new Label("Coverage:"),0,6);
        form.add(tfCoverage,1,6);

        form.add(new Label("Premium:"),0,7);
        form.add(tfPremium,1,7);

        form.add(new Label("Search ID:"),0,9);
        form.add(tfSearchID,2,10);

        form.add(btnSearch,0,10);
        form.add(btnSubmit,0,8);
        form.add(btnView,1,8);
        form.add(btnUpdate,0,9);
        form.add(btnDelete,1,9);

        Tooltip idTooltip =
        new Tooltip(
        "RSA ID must be exactly 13 digits.\nExample: 9901021234087"
        );

        tfID.setTooltip(idTooltip);

        BorderPane root = new BorderPane();

        VBox topBox = new VBox(10, form);
        topBox.setPadding(new Insets(10));

        root.setTop(topBox);
        root.setCenter(table);

        // Make table grow with window
        VBox.setVgrow(table, Priority.ALWAYS);

        // Column auto-resize
        table.setColumnResizePolicy(
        TableView.CONSTRAINED_RESIZE_POLICY
        );

        root.setPadding(new Insets(15));

        Scene scene =
        new Scene(root,900,600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Insurance Management");
        primaryStage.show();

        table.setOnMouseClicked(e -> {

        Policy selected =
        table.getSelectionModel().getSelectedItem();

        if(selected != null){

        tfID.setText(selected.getIdNum());
        tfName.setText(selected.getName());
        tfSurname.setText(selected.getSurname());
        tfAddress.setText(selected.getAddress());
        tfAge.setText(String.valueOf(selected.getAge()));
        cbPolicyType.setValue(selected.getPolicyType());
        tfCoverage.setText(
        String.valueOf(selected.getCoverageAmount())
        );
        tfPremium.setText(
        String.valueOf(selected.getPremiumAmount())
        );

        }

        });

        //Search button functionailty

        btnSearch.setOnAction(e -> {

        String searchId =
        tfSearchID.getText().trim();

        if(!isValidId(searchId)){

        new Alert(
        Alert.AlertType.WARNING,
        "Enter a valid 13-digit ID."
        ).show();

        return;
        }

        try{

        Connection con =
        DBUtil.connect();

        String query =
        "SELECT c.id_number,\n" + "c.name,\n" + "c.surname,\n" + "c.address,\n" + "c.age,\n" + "p.policy_type,\n" + "p.coverage_amount,\n" + "p.premium_amount\n" + "FROM policies p\n" + "JOIN customers c\n" + "ON p.customer_id=c.customer_id\n" + "WHERE c.id_number=?\n";

        PreparedStatement ps =
        con.prepareStatement(query);

        ps.setString(1, searchId);

        ResultSet rs =
        ps.executeQuery();

        ObservableList<Policy> data =
        FXCollections.observableArrayList();

        while(rs.next()){

        data.add(
        new Policy(
        rs.getString(1),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getInt(5),
        rs.getString(6),
        rs.getDouble(7),
        rs.getDouble(8)
        )
        );

        }

        if(data.isEmpty()){

        new Alert(
        Alert.AlertType.INFORMATION,
        "No policy found for this ID."
        ).show();

        }else{

        table.setItems(data);

        }

        con.close();

        }catch(Exception ex){

        new Alert(
        Alert.AlertType.ERROR,
        "Search failed."
        ).show();

        }

        });

        //Delete button functionaility

        btnDelete.setOnAction(e -> {

        Policy selected =
        table.getSelectionModel().getSelectedItem();

        if(selected == null){

        new Alert(
        Alert.AlertType.WARNING,
        "Select a policy to delete."
        ).show();

        return;
        }

        Alert confirm =
        new Alert(
        Alert.AlertType.CONFIRMATION,
        "Delete this policy?",
        ButtonType.YES,
        ButtonType.NO
        );

        confirm.showAndWait();

        if(confirm.getResult() == ButtonType.YES){

        try{

        DBUtil.deletePolicy(
        selected.getIdNum()
        );

        new Alert(
        Alert.AlertType.INFORMATION,
        "Policy deleted."
        ).show();

        btnView.fire();

        }catch(Exception ex){

        new Alert(
        Alert.AlertType.ERROR,
        "Delete failed."
        ).show();

        }

        }

        });

        //Update button functionality

        btnUpdate.setOnAction(e -> {

            Policy selected =
            table.getSelectionModel().getSelectedItem();

            if(selected == null){

            new Alert(
            Alert.AlertType.WARNING,
            "Select a policy to update."
            ).show();

            return;
            }

            try{

            int age =
            Integer.parseInt(tfAge.getText());

            double coverage =
            Double.parseDouble(tfCoverage.getText());

            String type =
            cbPolicyType.getValue();

            DBUtil.updatePolicy(

            tfID.getText(),
            tfName.getText(),
            tfSurname.getText(),
            tfAddress.getText(),
            age,
            type,
            coverage

        );

        new Alert(
        Alert.AlertType.INFORMATION,
        "Policy updated."
        ).show();

        btnView.fire();

        }catch(Exception ex){

        new Alert(
        Alert.AlertType.ERROR,
        "Update failed."
        ).show();

        }

        });

        // Search field validation

        tfSearchID.textProperty().addListener((obs,o,n)->{

        if(isValidId(n))
        markValid(tfSearchID);
        else
        markInvalid(tfSearchID);

        });

        // Reset view button

        Button btnReset =
        new Button("Show All");

        form.add(btnReset,1,10);

        btnReset.setOnAction(e -> {

        btnView.fire();

        });

        // 🔴 REAL-TIME VALIDATION

        tfID.textProperty().addListener((obs,o,n)->{

        if(isValidId(n)) markValid(tfID);
        else markInvalid(tfID);

        });

        tfAge.textProperty().addListener((obs,o,n)->{

        if(n.matches("\\d*")) markValid(tfAge);
        else markInvalid(tfAge);

        });

        tfCoverage.textProperty().addListener((obs,o,n)->{

        if(n.matches("\\d*(\\.\\d*)?"))
        markValid(tfCoverage);
        else
        markInvalid(tfCoverage);

        });


        // 🔥 SUBMIT

        btnSubmit.setOnAction(e -> {

        try {

        String id =
        tfID.getText().trim();

        if(!isValidId(id)) {

        markInvalid(tfID);

        throw new IllegalArgumentException(
        "ID must be exactly 13 digits."
        );
        }

        if(DBUtil.customerExists(id)) {

        throw new IllegalArgumentException(
        "Duplicate ID detected."
        );
        }

        int age =
        Integer.parseInt(tfAge.getText());

        double coverage =
        Double.parseDouble(tfCoverage.getText());

        String type =
        cbPolicyType.getValue();

        if(type == null)
        throw new IllegalArgumentException(
        "Select policy type."
        );

        double premium =
        DBUtil.calculatePremium(type,coverage);

        tfPremium.setText(
        String.valueOf(premium)
        );

        DBUtil.insertCustomerAndPolicy(
        id,
        tfName.getText(),
        tfSurname.getText(),
        tfAddress.getText(),
        age,
        type,
        coverage
        );

        new Alert(
        Alert.AlertType.INFORMATION,
        "Policy saved!"
        ).show();

        } catch(Exception ex) {

        new Alert(
        Alert.AlertType.WARNING,
        ex.getMessage()
        ).show();

        }

        });


        // VIEW BUTTON

        btnView.setOnAction(e -> {

        try {

        Connection con =
        DBUtil.connect();

        Statement st =
        con.createStatement();

        ResultSet rs =
        st.executeQuery(
        "SELECT c.id_number,c.name,c.surname,c.address,c.age,p.policy_type,p.coverage_amount,p.premium_amount FROM policies p JOIN customers c ON p.customer_id=c.customer_id"
        );

        ObservableList<Policy> data =
        FXCollections.observableArrayList();

        while(rs.next()) {

        data.add(
        new Policy(
        rs.getString(1),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getInt(5),
        rs.getString(6),
        rs.getDouble(7),
        rs.getDouble(8)
        )
        );

        }

        table.setItems(data);

        con.close();

        } catch(Exception ex) {

        new Alert(
        Alert.AlertType.ERROR,
        "Failed to fetch policies."
        ).show();

        }

        });
    }

    public static void main(String[] args) {
    launch(args);
    }
}