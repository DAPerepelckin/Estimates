package sample.newTableDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.PrintException;
import sample.addContractors.AddContractorsController;
import sample.contractorsWindow.ContractorsController;
import sample.mainTable.Controller;
import sample.welcomeWindow.WelcomeController;



import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class NewTableController implements Initializable {
    public Button cancelBtn;
    public Button applyBtn;
    public TextField nameField;
    public ComboBox<Contractor> contractors;
    public WelcomeController owner;
    Preferences user = Preferences.userRoot();
    private ObservableList<Contractor> contractorsList = FXCollections.observableArrayList();
    public Connection conn;
    public  int contrID;

    private class Contractor{
        int ID;
        String name;
    }

    public void setContractor(int id, String name){
        Contractor contractor = new Contractor();
        contractor.ID = id;
        contractor.name = name;

        contractors.setConverter(new StringConverter<Contractor>() {
            @Override
            public String toString(Contractor object) {
                return object.name;
            }

            @Override
            public Contractor fromString(String string) {
                return null;
            }
        });

        contractors.setValue(contractor);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                controller.owner = this;
                controller.update("");
                newWindow.showAndWait();
            }catch (Exception ex){PrintException.print(ex);}
        });


        cancelBtn.setOnAction(e->cancelBtn.getScene().getWindow().hide());


        applyBtn.setOnAction(e->{

            try {
                if (nameField.getText().isEmpty()||contractors.getValue()==null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Пустые места");
                    alert.setHeaderText("Заполните все пропуски");
                    alert.show();
                }else {
                    if(contractors.getValue().ID == 0){
                        contrID = -1;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addContractorsWindow.fxml"));
                        AnchorPane addLayout = loader.load();
                        Scene secondScene = new Scene(addLayout);
                        Stage newWindow = new Stage();
                        newWindow.setScene(secondScene);
                        newWindow.setTitle("Новый заказчик");
                        newWindow.initModality(Modality.WINDOW_MODAL);
                        newWindow.initOwner(applyBtn.getScene().getWindow());
                        AddContractorsController controller = loader.getController();
                        controller.owner = this;
                        controller.conn = conn;
                        newWindow.centerOnScreen();
                        newWindow.showAndWait();
                    }else{contrID = contractors.getValue().ID;}
                    if(contrID!=-1){
                        Statement statement = conn.createStatement();
                        Date date = new Date();
                        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
                        statement.executeUpdate("INSERT INTO TABLES(TABLE_NAME, TABLE_DATE,TRANSPORT,POGRUZ,CONTRACTORS_ID ) VALUES ( '" + nameField.getText() + "','" + formatForDateNow.format(date) + "',0.0,0.0, " + contractors.getValue().ID + ")");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainTableWindow.fxml"));
                        VBox addLayout = loader.load();
                        Controller controller = loader.getController();
                        Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery("SELECT seq FROM sqlite_sequence WHERE name = 'TABLES'");
                        controller.ID = rs.getInt("seq");
                        Scene secondScene = new Scene(addLayout);
                        Stage newWindow = new Stage();
                        newWindow.setScene(secondScene);
                        newWindow.setTitle(nameField.getText());
                        newWindow.centerOnScreen();
                        newWindow.show();
                        applyBtn.getScene().getWindow().hide();
                        owner.close();
                    }
                    conn.close();
                }
            }catch (Exception ex){PrintException.print(ex);}

        });
    }
    public void init(){
       /* try {
            ResultSet contractorRS = conn.createStatement().executeQuery("SELECT CONTRACTOR_ID, ORGANIZATION_NAME FROM CONTRACTORS");
            while (contractorRS.next()) {
                Contractor contractor = new Contractor();
                contractor.ID = contractorRS.getInt("CONTRACTOR_ID");
                contractor.name = contractorRS.getString("ORGANIZATION_NAME");
                contractorsList.add(contractor);
            }
            Contractor contractor = new Contractor();
            contractor.ID = 0;
            contractor.name = "Добавить заказчика";
            contractorsList.add(contractor);


            contractors.setConverter(new StringConverter<Contractor>() {
                @Override
                public String toString(Contractor object) {
                    return object.name;
                }

                @Override
                public Contractor fromString(String string) {
                    return null;
                }
            });
            contractors.setItems(contractorsList);
        }catch (Exception ex){
            PrintException.print(ex);}*/
    }
}
