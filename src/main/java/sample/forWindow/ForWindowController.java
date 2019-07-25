package sample.forWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.PrintException;
import sample.contractorsWindow.ContractorsController;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ForWindowController implements Initializable {
    public Button cancelBtn;
    public Button okBtn;
    public ComboBox<Contractor> contractors;
    public TextField addressField;
    public TextField fioField;
    public TextField orgProfileField;
    public Connection conn;
    public int tableID;
    public TextField nameField;
    private Preferences user = Preferences.userRoot();

    private class Contractor{
        int ID;
        String orgName;
        String address;
        String fio;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {



        contractors.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/contractorsWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Заказчики");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(contractors.getScene().getWindow());
                newWindow.centerOnScreen();
                ContractorsController controller = loader.getController();
                controller.conn = conn;
                controller.deleteBtn.setVisible(false);
                controller.searchLine.getProperties().put("pane-left-anchor", 135.0);
                controller.owner1 = this;
                controller.update("");
                newWindow.showAndWait();
                conn.createStatement().executeUpdate("UPDATE TABLES SET CONTRACTORS_ID = " + contractors.getValue().ID);
                load();
            }catch (Exception ex){PrintException.print(ex);}
        });

        cancelBtn.setOnAction(event -> cancelBtn.getScene().getWindow().hide());

        okBtn.setOnAction(event -> {
            try {
                conn.createStatement().executeUpdate("UPDATE TABLES SET TABLE_NAME = '"+nameField.getText()+"' WHERE TABLE_ID = " +tableID);
                conn.createStatement().executeUpdate("UPDATE TABLES SET CONTRACTORS_ID = " + contractors.getValue().ID+" WHERE TABLE_ID = " +tableID);
                load();
                okBtn.getScene().getWindow().hide();
            }catch (Exception ex){PrintException.print(ex);}
        });


    }

    public void setContractor(int id, String fio, String orgName, String address){
        Contractor contractor = new Contractor();
        contractor.ID = id;
        contractor.fio = fio;
        contractor.orgName = orgName;
        contractor.address = address;
        contractors.setValue(contractor);
    }


    public void load(){
        try{
            ResultSet rs = conn.createStatement().executeQuery("SELECT CONTRACTORS_ID FROM TABLES WHERE TABLE_ID = "+tableID);
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT CONTRACTOR_ID, POSITION, FIO, ORGANIZATION_NAME, ADDRESS FROM CONTRACTORS WHERE CONTRACTOR_ID = "+rs.getInt("CONTRACTORS_ID"));

            Contractor contractor = new Contractor();
            contractor.ID = rs1.getInt("CONTRACTOR_ID");
            contractor.fio = rs1.getString("FIO");
            contractor.orgName = rs1.getString("ORGANIZATION_NAME");
            contractor.address = rs1.getString("ADDRESS");

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

            contractors.setValue(contractor);

            ResultSet rs2 = conn.createStatement().executeQuery("SELECT TABLE_NAME FROM TABLES WHERE TABLE_ID = "+tableID);
            nameField.setText(rs2.getString("TABLE_NAME"));
            addressField.setText(contractors.getValue().address);
            fioField.setText(contractors.getValue().fio);
            ResultSet rs12 = conn.createStatement().executeQuery("SELECT ORGANIZATION_NAME FROM PROFILES WHERE PROFILE_ID = "+user.getInt("PROFILE_ID",1));
            orgProfileField.setText(rs12.getString("ORGANIZATION_NAME"));
        }catch (Exception ex){PrintException.print(ex);}
    }
}
