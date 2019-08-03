package sample.deleteGroupWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import sample.PrintException;
import sample.mainTable.Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class deleteGroupController implements Initializable {
    public ComboBox<Group> groups;
    public Button cancelBtn;
    public Button applyBtn;
    public Connection conn;
    public int ID;
    public Controller owner;
    private ObservableList<Group> groupList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelBtn.setOnAction(e->cancelBtn.getScene().getWindow().hide());

        applyBtn.setOnAction(e->{
            try {
                conn.createStatement().executeUpdate("DELETE FROM GROUPS WHERE GROUP_ID = " + groups.getValue().groupID);
                conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE GROUP_ID = "+ groups.getValue().groupID);
                owner.update();
                applyBtn.getScene().getWindow().hide();
            }catch (Exception ex){
                PrintException.print(ex);}
        });
    }
    private class Group{
        int groupID;
        String groupName;

        Group(int groupID, String groupName) {
            this.groupID = groupID;
            this.groupName = groupName;
        }
    }
    public void load() {
        try {
            ResultSet rs = this.conn.createStatement().executeQuery("SELECT GROUP_NAME, GROUP_ID FROM GROUPS where TABLE_ID = " + ID);
            while(rs.next()) {
                groupList.add(new Group(rs.getInt("GROUP_ID"),rs.getString("GROUP_NAME")));
            }
            rs.close();
            groups.setItems(groupList);
            groups.setConverter(new StringConverter<Group>() {
                @Override
                public String toString(Group object) {
                    return object.groupName;
                }

                @Override
                public Group fromString(String string) {
                    return null;
                }
            });
        } catch (Exception ex) {PrintException.print(ex);}

    }


}
