package sample.newGroupWindow;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import sample.PrintException;
import sample.addWindow.AddWindowController;
import sample.mainTable.Controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class NewGroupController implements Initializable {
    public Button cancelBtn;
    public Button applyBtn;
    public TextField nameField;
    public Connection conn;
    public Controller owner;
    public AddWindowController owner1;

    public int ID;
    public ObservableList<TablePosition> range;
    public AnchorPane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelBtn.setOnAction(e->cancelBtn.getScene().getWindow().hide());
        applyBtn.setDefaultButton(true);
        window.setOnKeyReleased(event -> {
            if(event.getCode()== KeyCode.ESCAPE){
                window.getScene().getWindow().hide();
            }
        });

        nameField.setOnKeyReleased(event -> {
            if(event.getCode()== KeyCode.ENTER)applyAction();
        });

        applyBtn.setOnAction(e->applyAction());
    }

    private void applyAction(){
        try {
            conn.createStatement().executeUpdate("INSERT INTO GROUPS(GROUP_NAME, TABLE_ID) VALUES ( '"+nameField.getText()+"', "+ID+" )");
            if(owner!=null){owner.update();}else{owner1.gr=true; owner1.load();}
            applyBtn.getScene().getWindow().hide();
        }catch (Exception ex){PrintException.print(ex);}
    }
}
