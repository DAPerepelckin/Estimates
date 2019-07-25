package sample.addWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.PrintException;
import sample.mainTable.Controller;
import sample.newGroupWindow.NewGroupController;
import sample.workBaseWindow.WorkBaseController;


public class AddWindowController implements Initializable {
    public Button cancelBtn;
    public Button applyBtn;
    public TextArea commentArea;
    public TextField countField;
    public TextField nameField;
    public TextField priceField;
    public int tableID;
    public Controller owner;
    public ComboBox<String> groups;
    public AnchorPane addWindowPane;
    private int workID;
    public Connection conn;
    private ObservableList[] groupList = new ObservableList[2];
    public boolean gr = true;


    public void initialize(URL url, ResourceBundle resourceBundle) {


        cancelBtn.setOnAction(e ->cancelBtn.getScene().getWindow().hide());

        applyBtn.setOnAction(e -> {
            if(groups.getValue()==null||nameField.getText().isEmpty()||priceField.getText().isEmpty()||countField.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Пустые места");
                alert.setHeaderText("Заполните все пропуски");
                alert.show();
            }else{
                add();
            }
        });

        nameField.setOnMouseClicked(e -> initWorkList());

        addWindowPane.setOnKeyReleased(e->{
            if(e.getCode()== KeyCode.ESCAPE){cancelBtn.getScene().getWindow().hide();}
        });

    }


    public void initWorkList(){
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/workBaseWindow.fxml"));
            AnchorPane addLayout = loader.load();
            Scene secondScene = new Scene(addLayout);
            Stage newWindow = new Stage();
            newWindow.setScene(secondScene);
            newWindow.setTitle("Выбор работы");
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(this.nameField.getScene().getWindow());
            newWindow.centerOnScreen();
            WorkBaseController controller = loader.getController();
            controller.conn = conn;
            controller.owner = this;
            controller.deleteBtn.setVisible(false);
            controller.searchLine.getProperties().put("pane-left-anchor",135.0);
            newWindow.show();
            controller.update("");
        } catch (IOException ex) {PrintException.print(ex);}
    }

    public void setNameField(String name, int num) {
        nameField.setText(name);
        workID = num;
    }

    private void add(){
        try {
            gr = true;
            if(groups.getSelectionModel().getSelectedItem().equalsIgnoreCase("Новая группа")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/newGroupWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Новая группа");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(groups.getScene().getWindow());
                NewGroupController controller = loader.getController();
                controller.conn = conn;
                controller.owner1 = this;
                controller.ID = tableID;
                newWindow.centerOnScreen();
                gr = false;
                newWindow.showAndWait();
                ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(GROUP_ID) FROM GROUPS WHERE TABLE_ID = "+tableID);
                ResultSet groupName = conn.createStatement().executeQuery("SELECT GROUP_NAME FROM GROUPS WHERE GROUP_ID = "+rs.getInt("MAX(GROUP_ID)")+" AND TABLE_ID = "+ tableID);
                groups.setValue(groupName.getString("GROUP_NAME"));
            }
            if(gr) {
                Statement st = conn.createStatement();
                ResultSet rs1 = st.executeQuery("SELECT MAX(N) FROM MAIN_TABLE where TABLE_ID = " + tableID);
                int max = rs1.getInt("MAX(N)") + 1;
                Statement statement = conn.createStatement();
                int index = groups.getSelectionModel().getSelectedIndex();
                statement.executeUpdate("insert into MAIN_TABLE(TABLE_ID, WORK_ID, N, COUNT, PRICE,GROUP_ID) VALUES ( " + tableID + ", " + workID + ", " + max + ", " + countField.getText().replaceAll(",", ".") + ", " + priceField.getText().replaceAll(",", ".") + ", " + groupList[1].get(index) + " )");
                owner.update();
                applyBtn.getScene().getWindow().hide();
            }
        } catch (Exception ex) {PrintException.print(ex);}

    }

    public void load() {

        try {
            ResultSet rs = this.conn.createStatement().executeQuery("SELECT GROUP_NAME, GROUP_ID FROM GROUPS where TABLE_ID = " + this.tableID);
            this.groupList[0] = FXCollections.observableArrayList();
            this.groupList[1] = FXCollections.observableArrayList();

            groupList[0].add("Новая группа");
            groupList[1].add("");

            while(rs.next()) {
                this.groupList[0].add(rs.getString("GROUP_NAME"));
                this.groupList[1].add(rs.getInt("GROUP_ID"));
            }

            this.groups.setItems(this.groupList[0]);
        } catch (Exception ex) {PrintException.print(ex);}

    }
}
