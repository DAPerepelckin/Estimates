package sample.mainTable;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.Main;
import sample.PrintException;
import sample.addWindow.AddWindowController;
import sample.deleteGroupWindow.deleteGroupController;
import sample.forWindow.ForWindowController;
import sample.newGroupWindow.NewGroupController;
import sample.saveExcel.SaveExcel;
import sample.welcomeWindow.WelcomeController;


import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static javafx.scene.input.KeyCode.*;


public class Controller implements Initializable {
    public Button createBtn;
    public Button clearBtn;
    public Button saveBtn;
    public Button deleteBtn;
    public Button addBtn;
    public Button closeBtn;
    public Button deleteGroupBtn;
    public TableView<MainRow> mainTable;
    public TableColumn<MainRow,Integer> columnNum;
    public TableColumn<MainRow,String> columnName;
    public TableColumn<MainRow,String> columnUnit;
    public TableColumn<MainRow,String> columnCount;
    public TableColumn<MainRow,String> columnPrice;
    public TableColumn<MainRow,String> columnSum;
    public TableColumn<MainRow,String> columnComment;


    public TableView<MainRow> rashodTable;
    public TableColumn<MainRow,Integer> colNum;
    public TableColumn<MainRow,String> colName;
    public TableColumn<MainRow,String> colUnit;
    public TableColumn<MainRow,String> colCount;
    public TableColumn<MainRow,String> colPrice;
    public TableColumn<MainRow,String> colSum;
    public TableColumn<MainRow,String> colCom;
    public TableColumn<MainRow,Integer> columnID;
    public Label sumWork;
    public Label sumAll;
    public int ID;
    public VBox mainWindow;
    public Button forBtn;

    private Preferences user = Preferences.userRoot();
    private Savepoint[] saves = new Savepoint[50];

    private boolean edit = false;
    private Connection conn;

    private MainCollection rowMainCollection = new MainCollection();
    private MainCollection costs = new MainCollection();
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private ArrayList<MainRow> selections = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Class.forName("org.sqlite.JDBC");
            String path = user.get("pathToDB","");
            String URL = "jdbc:sqlite:"+ path;
            conn = DriverManager.getConnection(URL);
        }catch (Exception ex){PrintException.print(ex);}

        columnNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        columnCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnSum.setCellValueFactory(new PropertyValueFactory<>("sum"));
        columnComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        columnCount.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPrice.setCellFactory(TextFieldTableCell.forTableColumn());
        columnComment.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());

        mainTable.setItems(rowMainCollection.getRowList());
        mainTable.requestFocus();

        colNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSum.setCellValueFactory(new PropertyValueFactory<>("sum"));
        colCom.setCellValueFactory(new PropertyValueFactory<>("comment"));

        colCount.setCellFactory(TextFieldTableCell.forTableColumn());
        rashodTable.setItems(costs.getRowList());

        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mainTable.setRowFactory(tv -> {
            TableRow<MainRow> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    save(conn,saves);
                    Integer index = row.getIndex();

                    selections.clear();

                    ObservableList<MainRow> items = mainTable.getSelectionModel().getSelectedItems();

                    selections.addAll(items);


                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();

                if (db.hasContent(SERIALIZED_MIME_TYPE)) {

                    int dropIndex;MainRow dI=null;

                    if (row.isEmpty()) {
                        dropIndex = mainTable.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                        dI = mainTable.getItems().get(dropIndex);
                    }
                    int delta=0;
                    if(dI!=null)
                        while(selections.contains(dI)) {
                            delta=1;
                            --dropIndex;
                            if(dropIndex<0) {
                                dI=null;dropIndex=0;
                                break;
                            }
                            dI = mainTable.getItems().get(dropIndex);
                        }

                    for(MainRow sI:selections) {
                        mainTable.getItems().remove(sI);
                    }

                    if(dI!=null)
                        dropIndex=mainTable.getItems().indexOf(dI)+delta;
                    else if(dropIndex!=0)
                        dropIndex=mainTable.getItems().size();



                    mainTable.getSelectionModel().clearSelection();

                    for(MainRow sI:selections) {
                        mainTable.getItems().add(dropIndex, sI);
                        mainTable.getSelectionModel().select(dropIndex);
                        dropIndex++;

                    }

                    event.setDropCompleted(true);
                    selections.clear();
                    event.consume();
                }
            });
            row.setOnDragDone(event ->updateNum());

            return row ;
        });



        mainWindow.setOnKeyReleased(event->{
            if(!edit) {
                switch (event.getCode()) {
                    case ESCAPE:
                        closeWindow(mainWindow.getScene().getWindow());
                        break;
                    case DELETE:
                        if (mainTable.getSelectionModel().getSelectedItem().getUnit().isEmpty()) {
                            deleteGroupAction();
                        } else {
                            deleteWorksAction();
                        }
                        break;
                }
            }
        });

        mainWindow.setOnKeyPressed(event -> {
            if(!edit) {
                if (event.isControlDown()) {
                    if (event.getCode().getName().equalsIgnoreCase(user.get("addWorkKey", "W"))) {
                        addAction();
                    }
                    if (event.getCode().getName().equalsIgnoreCase(user.get("addGroupKey", "G"))) {
                        newGroupAction();
                    }
                    if (event.getCode().getName().equalsIgnoreCase(user.get("clearKey", "P"))) {
                        clearAction();
                    }
                    if (event.getCode().getName().equalsIgnoreCase(user.get("saveKey", "S"))) {
                        saveAction();
                    }
                    if (event.getCode().getName().equalsIgnoreCase("Z")) {
                        back(conn, saves);
                        update();
                    }
                    if (event.getCode().getName().equalsIgnoreCase(user.get("forKey", "F"))) {
                        forBtnAction();
                    }
                }
            }
            if(!edit) {
                if (event.getCode() == Z && event.isControlDown()) {
                    try {
                        back(conn, saves);
                        update();
                    } catch (Exception ex) {
                        PrintException.print(ex);
                    }
                }
            }
        });







        createBtn.setOnAction(e->newGroupAction());
        clearBtn.setOnAction(e->clearAction());
        deleteGroupBtn.setOnAction(e->deleteGroupAction());
        deleteBtn.setOnAction(e->deleteWorksAction());
        closeBtn.setOnAction(e->closeWindow(closeBtn.getScene().getWindow()));
        saveBtn.setOnAction(e->saveAction());
        addBtn.setOnAction(e->addAction());
        forBtn.setOnAction(e->forBtnAction());

        createBtn.setTooltip(new Tooltip("Ctrl+"+user.get("addGroupKey","G")));
        clearBtn.setTooltip(new Tooltip("Ctrl+"+user.get("clearKey","P")));
        deleteGroupBtn.setTooltip(new Tooltip("Delete"));
        deleteBtn.setTooltip(new Tooltip("Delete"));
        closeBtn.setTooltip(new Tooltip("Escape"));
        saveBtn.setTooltip(new Tooltip("Ctrl+"+user.get("saveKey","S")));
        addBtn.setTooltip(new Tooltip("Ctrl+"+user.get("addWorkKey","W")));
        forBtn.setTooltip(new Tooltip("Ctrl+"+user.get("forKey","F")));

    }

    private void saveAction(){
        try {
            new SaveExcel().saveEstimateExcel(conn, ID, saveBtn.getScene().getWindow());
        }catch (Exception ex){PrintException.print(ex);}
    }

    private void forBtnAction(){
        try {
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
            controller.tableID = ID;
            controller.conn = conn;
            newWindow.show();
            controller.load();
        }catch (Exception ex){PrintException.print(ex);}
    }

    private void deleteGroupAction(){
        if(mainTable.getSelectionModel().getSelectedCells().size()==1) {
            if(mainTable.getItems().get(mainTable.getSelectionModel().getSelectedCells().get(0).getRow()).getNum()!=null
                    && mainTable.getItems().get(mainTable.getSelectionModel().getSelectedCells().get(0).getRow()).getUnit().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление строк");
                alert.setHeaderText("Вы уверены что хотите удалить группу");
                alert.setContentText("Все содержимое удалится");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    try {
                        save(conn,saves);
                        conn.createStatement().executeUpdate("DELETE FROM GROUPS WHERE GROUP_ID = " + mainTable.getItems().get(mainTable.getSelectionModel().getSelectedCells().get(0).getRow()).getNum());
                        conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE GROUP_ID = " + mainTable.getItems().get(mainTable.getSelectionModel().getSelectedCells().get(0).getRow()).getNum());
                        update();
                    } catch (Exception ex) {PrintException.print(ex);}
                }
            }else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление строк");
                alert.setHeaderText("Вы уверены что хотите удалить группу");
                alert.setContentText("Все содержимое удалится");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/deleteGroupWindow.fxml"));
                        AnchorPane addLayout = loader.load();
                        Scene secondScene = new Scene(addLayout);
                        Stage newWindow = new Stage();
                        newWindow.setScene(secondScene);
                        newWindow.setTitle("delete group");
                        newWindow.initModality(Modality.WINDOW_MODAL);
                        newWindow.initOwner(deleteGroupBtn.getScene().getWindow());
                        deleteGroupController controller = loader.getController();
                        controller.conn = conn;
                        controller.ID = ID;
                        controller.owner = this;
                        controller.load();
                        newWindow.centerOnScreen();
                        newWindow.show();
                    } catch (Exception ex) {PrintException.print(ex);}
                }
            }
        }else{mainTable.getSelectionModel().clearSelection();}
    }

    private void deleteWorksAction(){
        if(mainTable.getSelectionModel().getSelectedItems().size()>0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление строк");
            alert.setHeaderText("Вы уверены что хотите удалить строки?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                try {
                    save(conn,saves);
                    int index = mainTable.getSelectionModel().getSelectedItem().getNum();
                    conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE TABLE_ID = " + ID + " AND N = " + index);
                    conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET N = N-1 WHERE N > " + index + " AND TABLE_ID = " + ID);
                    update();
                } catch (Exception ex) {PrintException.print(ex);}
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Удаление строк");
            alert.setHeaderText("Нет выделенных строк");
            alert.show();
        }
    }

    private void clearAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Очистить");
        alert.setHeaderText("Вы уверены что хотите очистить смету?");
        Optional<ButtonType> option = alert.showAndWait();
        if(option.get()==ButtonType.OK) {
            try {
                save(conn,saves);
                conn.createStatement().executeUpdate("DELETE FROM MAIN_TABLE WHERE TABLE_ID = " + ID);
                conn.createStatement().executeUpdate("DELETE FROM GROUPS WHERE TABLE_ID = "+ID);
                rowMainCollection.clear();
            } catch (Exception ex) {PrintException.print(ex);}
        }
    }

    private void newGroupAction(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/newGroupWindow.fxml"));
            AnchorPane addLayout = loader.load();
            Scene secondScene = new Scene(addLayout);
            Stage newWindow = new Stage();
            newWindow.setScene(secondScene);
            newWindow.setTitle("Новая группа");
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(createBtn.getScene().getWindow());
            NewGroupController controller = loader.getController();
            controller.conn = conn;
            controller.range = mainTable.getSelectionModel().getSelectedCells();
            controller.ID = ID;
            controller.owner = this;
            newWindow.centerOnScreen();
            save(conn,saves);
            newWindow.show();
        }catch (Exception ex){PrintException.print(ex);}

    }

    private void addAction(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWindow.fxml"));
            AnchorPane addLayout = loader.load();
            Scene secondScene = new Scene(addLayout);
            Stage newWindow = new Stage();
            newWindow.setScene(secondScene);
            newWindow.setTitle("Добавить работу");
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(addBtn.getScene().getWindow());
            AddWindowController controller = loader.getController();
            controller.tableID = ID;
            controller.owner = this;
            controller.conn = conn;
            controller.load();
            newWindow.centerOnScreen();
            save(conn,saves);
            newWindow.show();
            controller.initWorkList();
        } catch (Exception ex) {PrintException.print(ex);}
    }

    public void closeWindow(Window owner){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeWindow.fxml"));
            AnchorPane addLayout = loader.load();
            Scene secondScene = new Scene(addLayout);
            Stage newWindow = new Stage();
            newWindow.setScene(secondScene);
            newWindow.setTitle("Estimates");
            newWindow.centerOnScreen();
            if(!conn.isClosed()){
            if(!conn.getAutoCommit())conn.commit();
            conn.close();}
            WelcomeController controller = loader.getController();
            newWindow.show();
            closeBtn.getScene().getWindow().hide();
            controller.load(owner);


        }catch (Exception ex){ex.printStackTrace();}
    }

    public void update() {

        try {
            rowMainCollection.clear();

            ResultSet rsGroup = conn.createStatement().executeQuery("SELECT GROUP_ID,GROUP_NAME FROM GROUPS WHERE TABLE_ID = "+ID);

            while (rsGroup.next()){
                double s = 0.0;
                rowMainCollection.add(new MainRow(rsGroup.getInt("GROUP_ID"),rsGroup.getString("GROUP_NAME"),"","","","","",0));
                ResultSet rs = conn.createStatement().executeQuery("SELECT WORK_ID, N, COUNT, PRICE, WORK_NOTE, STRING_ID FROM MAIN_TABLE WHERE GROUP_ID = "+rsGroup.getInt("GROUP_ID")+" ORDER BY N");
                while (rs.next()){
                    ResultSet rs1 = conn.createStatement().executeQuery("SELECT WORK_NAME, UNIT FROM WORKS WHERE WORK_ID = "+rs.getInt("WORK_ID"));
                    int num = rs.getInt("N");
                    String name = "";
                    if(rs.getString("WORK_NOTE")==null) {
                        name = rs1.getString("WORK_NAME");
                    }else{
                        name = rs.getString("WORK_NOTE");
                    }
                    String unit = rs1.getString("UNIT");
                    double count = rs.getDouble("COUNT");
                    double price = rs.getDouble("PRICE");
                    double sum = count*price;
                    int ID = rs.getInt("STRING_ID");
                    String count1 = new DecimalFormat("#0.00").format(count);
                    String price1 = new DecimalFormat("#0.00").format(price);
                    String sum1 = new DecimalFormat("#0.00").format(sum);
                    s+=sum;
                    rowMainCollection.add(new MainRow(num,name,unit,count1,price1,sum1,"",ID));
                }
                String groupSum = new DecimalFormat("#0.00").format(s);
                rowMainCollection.add(new MainRow(null,"","","","",groupSum,"",0));
            }
            ResultSet sum1 = conn.createStatement().executeQuery("SELECT COUNT,PRICE FROM MAIN_TABLE WHERE TABLE_ID = "+ID);
            double sum1Work=0.0;
            while(sum1.next()){sum1Work+=sum1.getDouble("PRICE")*sum1.getDouble("COUNT");}

            ResultSet rs2 = conn.createStatement().executeQuery("SELECT TRANSPORT, POGRUZ FROM TABLES WHERE TABLE_ID = "+ID);
            costs.clear();
            double count1;
            double count2;
            if(rs2.isClosed()){count1 = 0.0;count2 = 0.0;}else {
                count1 = rs2.getDouble("TRANSPORT");
                count2 = rs2.getDouble("POGRUZ");
            }
            double price1 =sum1Work/100;
            String s1 = "Транспортные расходы";
            String s2 = "Погрузочно-разгрузочные расходы";
            costs.add(new MainRow(null,s1,"%",new DecimalFormat("#0.00").format(count1),new DecimalFormat("#0.00").format(price1),new DecimalFormat("#0.00").format(count1*price1),"", 0));
            costs.add(new MainRow(null,s2,"%",new DecimalFormat("#0.00").format(count2),new DecimalFormat("#0.00").format(price1),new DecimalFormat("#0.00").format(count2*price1),"",0));

            String s3 = "Итого по работам:  ";
            sumWork.setText(s3+ new DecimalFormat("#0.00").format(sum1Work));
            double all = sum1Work+costs.getSum();
            String s4 = "Итого по смете:  ";
            sumAll.setText(s4+ new DecimalFormat("#0.00").format(all));
            mainTable.getSelectionModel().select(0);
        } catch (Exception ex) {PrintException.print(ex);}

    }



    public void onEditChanged2(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        if(mainRowStringCellEditEvent.getNewValue().replaceAll(",",".").matches("-?\\d+(\\.\\d+)?")) {
            try {
                save(conn,saves);
                if (mainRowStringCellEditEvent.getTableColumn().getText().equalsIgnoreCase("Кол-во")) {
                    if(mainRowStringCellEditEvent.getRowValue().getName().equalsIgnoreCase("Транспортные расходы")){
                        conn.createStatement().executeUpdate("UPDATE TABLES SET TRANSPORT = " + mainRowStringCellEditEvent.getNewValue().replaceAll(",", ".") + " WHERE TABLE_ID = " + ID);
                    }else {
                        conn.createStatement().executeUpdate("UPDATE TABLES SET POGRUZ = " + mainRowStringCellEditEvent.getNewValue().replaceAll(",", ".") + " WHERE TABLE_ID = " + ID);
                    }

                }
            } catch (Exception ex) {PrintException.print(ex);}
        }
        edit = false;
        update();
    }

    public void onEditChanged(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        try {
            if (mainRowStringCellEditEvent.getTableColumn().getText().equalsIgnoreCase("Наименование")) {
                conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET WORK_NOTE = '" + mainRowStringCellEditEvent.getNewValue() + "' WHERE TABLE_ID = " + ID + " AND N = " + mainRowStringCellEditEvent.getRowValue().getNum());
            }
        }catch (Exception ex){PrintException.print(ex);}

        if(mainRowStringCellEditEvent.getNewValue().replaceAll(",",".").matches("-?\\d+(\\.\\d+)?")) {
            try {
                save(conn,saves);
                if (mainRowStringCellEditEvent.getTableColumn().getText().equalsIgnoreCase("Цена")) {
                    conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET PRICE = " + mainRowStringCellEditEvent.getNewValue().replaceAll(",", ".") + " WHERE TABLE_ID = " + ID + " AND N = " + mainRowStringCellEditEvent.getRowValue().getNum());
                }
                if (mainRowStringCellEditEvent.getTableColumn().getText().equalsIgnoreCase("Кол-во")) {
                    conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET COUNT = " + mainRowStringCellEditEvent.getNewValue().replaceAll(",", ".") + " WHERE TABLE_ID = " + ID + " AND N = " + mainRowStringCellEditEvent.getRowValue().getNum());
                }

            } catch (Exception ex) {PrintException.print(ex);}
        }
        edit = false;
        update();

    }

    private void save(Connection conn,Savepoint[] saves) {
        try {
            if (saves.length - 1 >= 0) System.arraycopy(saves, 0, saves, 1, saves.length - 1);
            saves[0] = conn.setSavepoint();
        }catch (Exception ex){PrintException.print(ex);}
    }
    private void back(Connection conn, Savepoint[] saves) {
        try {
            if (saves[0] != null) {
                conn.rollback(saves[0]);
                System.arraycopy(saves, 1, saves, 0, saves.length - 1);
            }
        }catch (Exception ex){PrintException.print(ex);}
    }
    private void updateNum(){
        try {
            conn.createStatement();
            for(int i=0;i<mainTable.getItems().size();i++){
                if(!mainTable.getItems().get(i).getUnit().isEmpty()){
                    conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET N = "+i+ " WHERE STRING_ID = "+mainTable.getItems().get(i).getID());
                }
            }

            update();


        }catch (Exception ex){PrintException.print(ex);}
    }

    public void onEditCancel(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        edit = false;
    }

    public void onEditStart(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        edit = true;
    }
}
