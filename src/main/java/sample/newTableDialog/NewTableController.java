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
import sample.mainTable.Controller;
import sample.welcomeWindow.WelcomeController;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private WelcomeController owner;
    Preferences user = Preferences.userRoot();
    private ObservableList<Contractor> contractorsList = FXCollections.observableArrayList();
    private Connection conn;
    public  int contrID;

    private class Contractor{
        int ID;
        String name;
    }


    public void setOwner(WelcomeController owner) {
        this.owner = owner;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Class.forName("org.sqlite.JDBC");
            String path = user.get("pathToDB", "");
            if (path.isEmpty()) {
                owner.loadFileBase(applyBtn.getScene().getWindow());
            } else {
                String URL = "jdbc:sqlite:" + path;
                conn = DriverManager.getConnection(URL);
            }
            init();
        }catch(Exception ex){PrintException.print(ex);}




        cancelBtn.setOnAction(e->cancelBtn.getScene().getWindow().hide());
        cancelBtn.setCancelButton(true);
        applyBtn.setDefaultButton(true);
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
                        contractors.setValue(contractorsList.filtered(t->t.ID==contrID).get(0));
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
        try {
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
            PrintException.print(ex);}
    }
}
