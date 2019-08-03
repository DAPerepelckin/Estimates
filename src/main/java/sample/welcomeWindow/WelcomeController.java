package sample.welcomeWindow;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import sample.PrintException;
import sample.contractorsWindow.ContractorsController;
import sample.forWindow.ForWindowController;
import sample.mainTable.Controller;
import sample.newTableDialog.NewTableController;
import sample.opionsWindow.OptionsController;


import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import java.util.prefs.Preferences;

public class WelcomeController implements Initializable {
    public TableView<WelcomeRow> archiveTable;
    public TableColumn<WelcomeRow,String> dateCol;
    public TableColumn<WelcomeRow,String> nameCol;
    public TableColumn<WelcomeRow,String> sumCol;
    public TableColumn<WelcomeRow,Integer> idCol;
    public Button newTableBtn;
    public Button options;
    public Button deleteBtn;
    public Button forBtn;
    public  Button openBtn;
    public Button contractorsBtn;
    public AnchorPane welcomePane;
    public Button refBtn;
    private Preferences user = Preferences.userRoot();
    public Connection conn;

    private WelcomeCollection collection = new WelcomeCollection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));
        archiveTable.setItems(collection.getRowList());

        refBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/referenceWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Справка");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(contractorsBtn.getScene().getWindow());
                newWindow.centerOnScreen();
                newWindow.show();
            }catch (Exception ex){PrintException.print(ex);}
        });

        contractorsBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/contractorsWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Заказчики");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(contractorsBtn.getScene().getWindow());
                newWindow.centerOnScreen();
                ContractorsController controller = loader.getController();
                controller.conn = conn;
                controller.apply.setText("ОК");
                controller.apply.setOnAction(event1 -> controller.apply.getScene().getWindow().hide());
                controller.deleteBtn.setVisible(true);
                controller.searchLine.getProperties().put("pane-left-anchor", 255.0);
                controller.append.setText("Добавить");
                //controller.owner = this;
                controller.update("");
                newWindow.showAndWait();
            }catch (Exception ex){PrintException.print(ex);}
        });

        archiveTable.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case DELETE: deleteEstimate(archiveTable.getScene().getWindow()); break;
                case ENTER: openTable(archiveTable.getSelectionModel().getSelectedItem());
            }
        });

        openBtn.setOnAction(event -> {
            if(archiveTable.getSelectionModel().getSelectedItems().size()>0) {
                openTable(archiveTable.getSelectionModel().getSelectedItem());
            }
        });


        forBtn.setOnAction(event -> {
            try {
                if(archiveTable.getSelectionModel().getSelectedItems().size()>0) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/forWindow.fxml"));
                    AnchorPane addLayout = loader.load();
                    Scene secondScene = new Scene(addLayout);
                    Stage newWindow = new Stage();
                    newWindow.setScene(secondScene);
                    newWindow.setTitle("О смете");
                    newWindow.initModality(Modality.WINDOW_MODAL);
                    newWindow.initOwner(forBtn.getScene().getWindow());
                    newWindow.centerOnScreen();
                    ForWindowController controller = loader.getController();
                    controller.tableID = archiveTable.getSelectionModel().getSelectedItem().getId();
                    controller.conn = conn;
                    controller.load();
                    newWindow.showAndWait();
                    load(forBtn.getScene().getWindow());
                }
            }catch (Exception ex){PrintException.print(ex);}
        });


        options.setOnAction(e->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/optionsWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Настройки");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(options.getScene().getWindow());
                OptionsController controller = loader.getController();
                controller.conn = conn;
                controller.init();
                //OptionsController controller = loader.getController();
                newWindow.centerOnScreen();
                newWindow.showAndWait();
                load(options.getScene().getWindow());
            }catch (Exception ex){PrintException.print(ex);}
        });


        archiveTable.setRowFactory(e->{
            TableRow<WelcomeRow> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() ==2&&(!row.isEmpty())){
                    openTable(row.getItem());
                }
            });


            return row;
        });

        newTableBtn.setOnAction(e->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/newTableDialog.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Новая смета");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(newTableBtn.getScene().getWindow());
                NewTableController controller = loader.getController();
                controller.owner = this;
                controller.conn = conn;
                newWindow.centerOnScreen();
                newWindow.show();
            }catch (Exception ex){PrintException.print(ex);}
        });

        deleteBtn.setOnAction(e->deleteEstimate(deleteBtn.getScene().getWindow()));
    }

    public void close(){
        try {
            newTableBtn.getScene().getWindow().hide();
        }catch (Exception ex){PrintException.print(ex);}
    }

    public void load(Window owner){
        try {
            collection.clear();
            Class.forName("org.sqlite.JDBC");
            String path = user.get("pathToDB","");
            File f = new File(path);
            if(path.isEmpty()||!f.exists()||f.isDirectory()){loadFileBase(owner);}else {
                String URL = "jdbc:sqlite:" + path;
                conn = DriverManager.getConnection(URL);
                ResultSet old = conn.createStatement().executeQuery("SELECT VERSION FROM VERSION");
                String ver = old.getString("VERSION");
                old.close();
                if(!ver.equalsIgnoreCase("25.07.11.20")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("База данных устарела");
                    alert.setTitle("База данных устарела");
                    Timeline timeline = new Timeline(
                            new KeyFrame(
                                    Duration.millis(1500),
                                    event -> alert.close()
                            )
                    );
                    timeline.play();
                    alert.showAndWait();
                }
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT TABLE_ID,TABLE_NAME, TABLE_DATE,TRANSPORT,POGRUZ FROM TABLES");
                while (rs.next()) {
                    int ID = rs.getInt("TABLE_ID");
                    double transport = rs.getDouble("TRANSPORT");
                    double pogruz = rs.getDouble("POGRUZ");
                    String name = rs.getString("TABLE_NAME");
                    String date = rs.getString("TABLE_DATE");
                    Statement st = conn.createStatement();
                    ResultSet rs1 = st.executeQuery("SELECT PRICE,COUNT FROM MAIN_TABLE WHERE TABLE_ID =" + ID);
                    double sum = 0.0;
                    while (rs1.next()) {
                        sum += rs1.getDouble("PRICE") * rs1.getDouble("COUNT");
                    }
                    rs1.close();
                    sum = sum * (100 + transport + pogruz) / 100;
                    collection.add(new WelcomeRow(ID, date, name, new DecimalFormat("#0.00").format(sum)));
                }
                rs.close();
            }
            archiveTable.getSelectionModel().select(0);
        }catch (Exception ex){
            if(ex.getMessage().equalsIgnoreCase("[SQLITE_ERROR] SQL error or missing database (no such table: VERSION)")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("База данных устарела");
                alert.setTitle("База данных устарела");
                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.millis(1500),
                                event -> alert.close()
                        )
                );
                timeline.play();
                alert.showAndWait();
            }else {
                PrintException.print(ex);
                loadFileBase(owner);
            }

        }
    }
    private void loadFileBase(Window owner){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("База данных не найдена");
        alert.setHeaderText("Укажите путь до файла базы данных");
        Optional <ButtonType> opt = alert.showAndWait();
        if(opt.toString().equalsIgnoreCase("Optional.empty")){owner.hide();}else {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(options.getScene().getWindow());
            if (file != null) {
                user.put("pathToDB", file.getAbsolutePath());
            }
            load(owner);

        }
    }
    private void deleteEstimate(Window owner){
        if(archiveTable.getSelectionModel().getSelectedItems().size()>0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление строк");
            alert.setHeaderText("Вы уверены что хотите удалить смету?");
            alert.setContentText("Все содержимое удалится");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                int index = archiveTable.getSelectionModel().getSelectedIndex();
                int id = archiveTable.getItems().get(index).getId();
                try {
                    conn.createStatement().executeUpdate("DELETE FROM TABLES WHERE TABLE_ID = " + id);
                    conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE TABLE_ID = " + id);
                    conn.createStatement().executeUpdate("DELETE FROM GROUPS WHERE TABLE_ID = " + id);
                    conn.close();
                    load(owner);
                } catch (Exception ex) {
                    PrintException.print(ex);
                }
            }
        }
    }
    private void openTable(WelcomeRow row){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainTableWindow.fxml"));
            VBox addLayout = loader.load();
            Scene secondScene = new Scene(addLayout);
            Stage newWindow = new Stage();
            newWindow.setScene(secondScene);
            newWindow.setTitle(row.getName());
            newWindow.centerOnScreen();
            Controller controller = loader.getController();
            controller.ID = row.getId();
            controller.update();
            newWindow.setOnCloseRequest(e->controller.closeWindow(newWindow));
            newWindow.show();
            archiveTable.getScene().getWindow().hide();

        }catch (Exception ex){PrintException.print(ex);}
    }

}
