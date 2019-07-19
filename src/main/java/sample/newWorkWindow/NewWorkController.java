package sample.newWorkWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sample.PrintException;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class NewWorkController implements Initializable {
    public Button cancelBtn;
    public Button applyBtn;
    public ComboBox<Group> groups;
    public TextField nameField;
    public TextField priceField;
    public TextField unitField;
    public Connection conn;

    private class Group{
        int id;
        String name;
    }
    private ObservableList<Group> groupList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelBtn.setOnAction(event -> cancelBtn.getScene().getWindow().hide());

        for(int i = 1; i<10;i++){
            Group gr = new Group();
            gr.id=i;
            switch (i){
                case 1: gr.name = "Демонтажные работы";break;
                case 2: gr.name = "Отделка потолков";break;
                case 3: gr.name = "Отделка стен";break;
                case 4: gr.name = "Плиточные работы";break;
                case 5: gr.name = "Отделка полов";break;
                case 6: gr.name = "Столярные работы";break;
                case 7: gr.name = "Сантехнические работы";break;
                case 8: gr.name = "Электромонтажные работы";break;
                case 9: gr.name = "Дополнительные работы";break;
            }
            groupList.add(gr);
        }
        groups.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object.name;
            }

            @Override
            public Group fromString(String string) {
                return null;
            }
        });
        groups.setItems(groupList);


        applyBtn.setOnAction(event -> {
            try{
                if(groups.getValue()==null||nameField.getText().isEmpty()||unitField.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Пустые места");
                    alert.setHeaderText("Заполните все пропуски");
                    alert.show();
                }else {
                    ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(N) FROM WORKS WHERE N LIKE '" + groups.getValue().id + "%'");
                    double r = (rs.getInt("COUNT(N)") + 1);
                    r = r / 1000;
                    r = r + groups.getValue().id;
                    conn.createStatement().executeUpdate("INSERT INTO WORKS(N, WORK_NAME, DEF_PRICE, UNIT) VALUES ('" + r + "','" + nameField.getText() + "',0.0,'" + unitField.getText() + "')");
                    applyBtn.getScene().getWindow().hide();
                }

            }catch (Exception ex){PrintException.print(ex);}

        });
    }
}
