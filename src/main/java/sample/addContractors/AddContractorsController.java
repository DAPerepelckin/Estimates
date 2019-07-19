package sample.addContractors;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.PrintException;
import sample.newTableDialog.NewTableController;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class AddContractorsController implements Initializable {
    public TextField nameField;
    public TextField fioField;
    public TextField addressField;
    public TextField positionField;
    public TextField OKPOField;
    public Button applyBtn;
    public NewTableController owner;
    public Connection conn;
    public Button cancelBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applyBtn.setOnAction(e->{
            try {
                if(!nameField.getText().isEmpty()&&!fioField.getText().isEmpty()&&!positionField.getText().isEmpty()&&!addressField.getText().isEmpty()) {
                    if(OKPOField.getText().isEmpty()){
                        conn.createStatement().executeUpdate("INSERT INTO CONTRACTORS(POSITION, FIO, ORGANIZATION_NAME, ADDRESS, OKPO) VALUES ('"+positionField.getText()+"','"+fioField.getText()+"','"+nameField.getText()+"','"+addressField.getText()+"',null)");
                    }else {
                        conn.createStatement().executeUpdate("INSERT INTO CONTRACTORS(POSITION, FIO, ORGANIZATION_NAME, ADDRESS, OKPO) VALUES ('" + positionField.getText() + "','" + fioField.getText() + "','" + nameField.getText() + "','" + addressField.getText() + "'," + OKPOField.getText() + ")");
                    }
                    owner.init();
                    owner.contrID = conn.createStatement().executeQuery("SELECT seq FROM sqlite_sequence WHERE name = 'CONTRACTORS'").getInt("seq");
                    applyBtn.getScene().getWindow().hide();
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Пустые места");
                    alert.setHeaderText("Заполните все пропуски");
                    alert.show();
                }
            }catch (Exception ex){PrintException.print(ex);}
        });

        cancelBtn.setOnAction(e->applyBtn.getScene().getWindow().hide());


    }
}
