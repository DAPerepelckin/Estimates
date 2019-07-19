package sample.workBaseWindow;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.PrintException;
import sample.addWindow.AddWindowController;
import sample.newGroupWindow.NewGroupController;
import sample.newWorkWindow.NewWorkController;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


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
    private WorkCollection list = new WorkCollection();
    private Preferences user = Preferences.userRoot();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        workList.setItems(list.getRowList());

        apply.setDefaultButton(true);

        workList.setOnKeyReleased(event -> {
            switch (event.getCode()){
               // case ENTER: owner.setNameField(workList.getSelectionModel().getSelectedItem().getName(),workList.getSelectionModel().getSelectedIndex()+1);
               //     workList.getScene().getWindow().hide();break;
                case ESCAPE: workList.getScene().getWindow().hide();break;
            }
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


        workList.setRowFactory(e->{
           TableRow<WorkRow> row = new TableRow<>();
           row.setOnMouseClicked(mouseEvent -> {
               if(mouseEvent.getClickCount() ==2&&(!row.isEmpty())){
                   owner.setNameField(row.getItem().getName(),row.getItem().getId());
                   workList.getScene().getWindow().hide();
               }
           });
           return row;
       });


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
            workList.getSelectionModel().select(0);
        } catch (Exception ex) {
            PrintException.print(ex);}
    }
}
