package sample.deleteGroupWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import sample.PrintException;
import sample.mainTable.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class deleteGroupController implements Initializable {
    public ComboBox groups;
    public Button cancelBtn;
    public Button applyBtn;
    public Connection conn;
    public int ID;
    public Controller owner;
    private ObservableList[] groupList = new ObservableList[2];


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelBtn.setOnAction(e->cancelBtn.getScene().getWindow().hide());

        applyBtn.setOnAction(e->{
            try {
                conn.createStatement().executeUpdate("DELETE FROM GROUPS WHERE GROUP_ID = " + groupList[1].get(groups.getSelectionModel().getSelectedIndex()));
                conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE GROUP_ID = "+groupList[1].get(groups.getSelectionModel().getSelectedIndex()));
                owner.update();
                applyBtn.getScene().getWindow().hide();
            }catch (Exception ex){
                PrintException.print(ex);}
        });
    }
    public void load() {
        try {
            ResultSet rs = this.conn.createStatement().executeQuery("SELECT GROUP_NAME, GROUP_ID FROM GROUPS where TABLE_ID = " + ID);
            this.groupList[0] = FXCollections.observableArrayList();
            this.groupList[1] = FXCollections.observableArrayList();

            while(rs.next()) {
                this.groupList[0].add(rs.getString("GROUP_NAME"));
                this.groupList[1].add(rs.getInt("GROUP_ID"));
            }

            this.groups.setItems(this.groupList[0]);
        } catch (Exception ex) {PrintException.print(ex);}

    }


}
