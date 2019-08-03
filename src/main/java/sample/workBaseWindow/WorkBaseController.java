package sample.workBaseWindow;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.PrintException;
import sample.addWindow.AddWindowController;
import sample.newWorkWindow.NewWorkController;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;


public class WorkBaseController implements Initializable {
    public Button append;
    public Button apply;
    public TextField searchLine;
    public TableView<WorkRow> workList;
    public TableColumn<WorkRow, Integer> idCol;
    public TableColumn<WorkRow,String> numCol;
    public TableColumn<WorkRow,String> nameCol;
    public TableColumn<WorkRow,String> priceCol;
    public TableColumn<WorkRow,String> unitCol;
    public AddWindowController owner;
    public Connection conn;
    public Button deleteBtn;
    private WorkCollection list = new WorkCollection();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        workList.setItems(list.getRowList());



        workList.setOnKeyReleased(event -> {
                if(event.getCode()==KeyCode.ESCAPE)workList.getScene().getWindow().hide();
        });

        searchLine.textProperty().addListener((observable, oldValue, newValue) -> update(newValue));


        apply.setOnAction(e->{
            if(workList.getSelectionModel().getSelectedItems().size()>0){
                owner.setNameField(workList.getSelectionModel().getSelectedItem().getName(),workList.getSelectionModel().getSelectedItem().getId());
                workList.getScene().getWindow().hide();
            }
        });

        append.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/newWorkWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Добавить работу");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(append.getScene().getWindow());
                newWindow.centerOnScreen();
                NewWorkController controller = loader.getController();
                controller.conn = conn;
                newWindow.showAndWait();
                update("");
            }catch (Exception ex){PrintException.print(ex);}

        });

        deleteBtn.setOnAction(event -> deleteAction());

        workList.setOnKeyReleased(event -> {
            if(event.getCode()== KeyCode.DELETE)deleteAction();
        });

        workList.setRowFactory(e->{
           TableRow<WorkRow> row = new TableRow<>();
           row.setOnMouseClicked(mouseEvent -> {
               if(mouseEvent.getClickCount() ==2&&(!row.isEmpty())){
                   if(!deleteBtn.isVisible()) {
                       owner.setNameField(row.getItem().getName(), row.getItem().getId());
                       workList.getScene().getWindow().hide();
                   }else{
                       try {
                           FXMLLoader loader = new FXMLLoader(getClass().getResource("/newWorkWindow.fxml"));
                           AnchorPane addLayout = loader.load();
                           Scene secondScene = new Scene(addLayout);
                           Stage newWindow = new Stage();
                           newWindow.setScene(secondScene);
                           newWindow.setTitle(workList.getSelectionModel().getSelectedItem().getName());
                           newWindow.initModality(Modality.WINDOW_MODAL);
                           newWindow.initOwner(append.getScene().getWindow());
                           newWindow.centerOnScreen();
                           NewWorkController controller = loader.getController();
                           controller.conn = conn;
                           String num =workList.getSelectionModel().getSelectedItem().getNum();
                           num = num.substring(0,num.indexOf("."));
                           int index = Integer.parseInt(num)-1;
                           controller.groups.setValue(controller.groupList.get(index));
                           controller.unitField.setText(workList.getSelectionModel().getSelectedItem().getUnit());
                           controller.nameField.setText(workList.getSelectionModel().getSelectedItem().getName());
                           controller.priceField.setText(workList.getSelectionModel().getSelectedItem().getPrice());
                           controller.workID = workList.getSelectionModel().getSelectedItem().getId();
                           newWindow.showAndWait();
                           update("");
                       }catch (Exception ex){PrintException.print(ex);}
                   }
               }
           });
           return row;
       });


    }
    private void deleteAction(){
        try {
            if (deleteBtn.isVisible()) {
                if (workList.getSelectionModel().getSelectedItems().size() > 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Удаление работы");
                    alert.setHeaderText("Вы уверены что хотите удалить работу?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.get() == ButtonType.OK) {
                        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(WORK_ID) FROM MAIN_TABLE WHERE WORK_ID = "+workList.getSelectionModel().getSelectedItem().getId());
                        int count = rs.getInt("COUNT(WORK_ID)");
                        rs.close();
                        if(count==0) {
                            conn.createStatement().executeUpdate("DELETE FROM WORKS WHERE WORK_ID = " + workList.getSelectionModel().getSelectedItem().getId());
                            update("");
                        }else{
                            ResultSet rs2 = conn.createStatement().executeQuery("SELECT TABLE_ID FROM MAIN_TABLE WHERE WORK_ID = "+workList.getSelectionModel().getSelectedItem().getId());
                            StringBuilder s = new StringBuilder();
                            while(rs2.next()){
                                ResultSet rs3 = conn.createStatement().executeQuery("SELECT TABLE_NAME FROM TABLES WHERE TABLE_ID = "+rs2.getInt("TABLE_ID"));
                                s.append(rs3.getString("TABLE_NAME")).append(", ");
                                rs3.close();
                            }
                            rs2.close();
                            s = new StringBuilder(s.substring(0, s.length() - 2));
                            alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Работа используется в сметах");
                            alert.setHeaderText("Работа, которую вы хотите удалить, используется в сметах:");
                            alert.setContentText(s.toString());
                            alert.show();
                        }
                    }
                }
            }
        }catch (Exception ex){PrintException.print(ex);}
    }

    public void update(String search){
        list.clear();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT WORK_ID,N,WORK_NAME,DEF_PRICE,UNIT FROM WORKS ORDER BY N");
            while(rs.next()){
                int id = rs.getInt("WORK_ID");
                String num = rs.getString("N");
                String name = rs.getString("WORK_NAME");
                String price = rs.getString("DEF_PRICE");
                String unit = rs.getString("UNIT");
                if(num.toLowerCase().contains(search.toLowerCase())||name.toLowerCase().contains(search.toLowerCase()))list.add(new WorkRow(id,num, name, price, unit));
            }
            rs.close();
            workList.getSelectionModel().select(0);
        } catch (Exception ex) {
            PrintException.print(ex);}
    }

}
