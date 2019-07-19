package sample.forWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sample.PrintException;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ForWindowController implements Initializable {
    public Button cancelBtn;
    public Button okBtn;
    public ComboBox<Contractor> contractors;
    public TextField addressField;
    public TextField fioField;
    public TextField orgProfileField;
    public Connection conn;
    public int tableID;

    private class Contractor{
        int ID;
        String orgName;
        String address;
    }

    private ObservableList<Contractor> contractorsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            ResultSet rs = conn.createStatement().executeQuery("SELECT CONTRACTOR_ID,ORGANIZATION_NAME,ADDRESS FROM CONTRACTORS");
            while(rs.next()){
                Contractor contractor = new Contractor();
                contractor.address = rs.getString("ADDRESS");
                contractor.orgName = rs.getString("ORGANIZATION_NAME");
                contractor.ID = rs.getInt("CONTRACTOR_ID");
                contractorsList.add(contractor);
            }
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT CONTRACTORS_ID FROM TABLES WHERE TABLE_ID = "+tableID);
            int contrID = rs1.getInt("CONTRACTORS_ID");

            contractors.setConverter(new StringConverter<Contractor>() {
                @Override
                public String toString(Contractor object) {
                    return object.orgName;
                }

                @Override
                public Contractor fromString(String string) {
                    return null;
                }
            });

            contractors.setItems(contractorsList);
            contractors.setValue(contractorsList.filtered(f -> f.ID==contrID).get(0));



        }catch (Exception ex){PrintException.print(ex);}


    }
}
