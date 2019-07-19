package sample.mainTable;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.PrintException;
import sample.addWindow.AddWindowController;
import sample.deleteGroupWindow.deleteGroupController;
import sample.newGroupWindow.NewGroupController;
import sample.saveExcel.SaveExcel;
import sample.welcomeWindow.WelcomeController;


import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


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
    public Label sumWork;
    public Label sumAll;
    public int ID;
    public VBox mainWindow;
    public Button forBtn;
    private Preferences user = Preferences.userRoot();
    private Savepoint[] saves = new Savepoint[50];


    private Connection conn;

    private MainCollection rowMainCollection = new MainCollection();
    private MainCollection costs = new MainCollection();
    //private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");



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

        columnCount.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPrice.setCellFactory(TextFieldTableCell.forTableColumn());
        columnComment.setCellFactory(TextFieldTableCell.forTableColumn());

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

        forBtn.setOnAction(event -> {
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
                newWindow.show();
            }catch (Exception ex){PrintException.print(ex);}
        });



        mainWindow.setOnKeyReleased(event->{
            switch (event.getCode()){
                case F7:
                    closeWindow(mainWindow.getScene().getWindow()); break;
                case DELETE:if(mainTable.getSelectionModel().getSelectedItem().getUnit().isEmpty()){
                    deleteGroupAction();}else {
                    deleteWorksAction();}break;
                case F1: addAction();break;
                case F2: newGroupAction();break;
                case F3: deleteWorksAction();break;
                case F4: deleteGroupAction();break;
                case F5: clearAction();break;
                case F6: saveAction();break;
            }
        });
        mainWindow.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.Z&&event.isControlDown()){
                try {
                    back(conn,saves);
                    update();
                } catch (Exception ex) {PrintException.print(ex);}
            }
        });



        mainTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        /*
        mainTable.setRowFactory(tv->{
            TableRow<MainRow> row = new TableRow<>();

            row.setOnDragDetected(e->{
                if(!row.isEmpty()&&mainTable.getSelectionModel().getSelectedCells().size()==1){
                    int index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null,null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    e.consume();
                }else{
                    mainTable.getSelectionModel().clearSelection();
                }
            });

            row.setOnDragOver(e->{
                Dragboard db = e.getDragboard();
                if(db.hasContent(SERIALIZED_MIME_TYPE)){
                    if(row.getIndex()!= (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        e.consume();
                    }
                }
            });

            row.setOnDragDropped(e->{
                Dragboard db = e.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    try {
                        int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);

                        int dropIndex;
                        if (row.isEmpty()) {
                            dropIndex = mainTable.getItems().size();
                        } else {
                            dropIndex = row.getIndex();
                        }
                        if (!mainTable.getItems().get(draggedIndex).getUnit().isEmpty() && !mainTable.getItems().get(dropIndex).getUnit().isEmpty()) {
                            int draggedN = mainTable.getItems().get(draggedIndex).getNum();
                            int dropN = mainTable.getItems().get(dropIndex).getNum();

                            ResultSet rs = conn.createStatement().executeQuery("SELECT GROUP_ID FROM MAIN_TABLE WHERE N = " + draggedN + " AND TABLE_ID = " + ID);
                            ResultSet rs1 = conn.createStatement().executeQuery("SELECT GROUP_ID FROM MAIN_TABLE WHERE N = " + dropN + " AND TABLE_ID = " + ID);
                            int dragGroup = rs.getInt("GROUP_ID");
                            int dropGroup = rs1.getInt("GROUP_ID");

                            if (dragGroup == dropGroup) {
                                conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET N = -1 WHERE N = " + draggedN + " AND TABLE_ID = " + ID);
                                conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET N = " + draggedN + " WHERE N = " + dropN + " AND TABLE_ID = " + ID);
                                conn.createStatement().executeUpdate("UPDATE MAIN_TABLE SET N = " + dropN + " WHERE N = -1 AND TABLE_ID = " + ID);

                                MainRow draggedPerson = mainTable.getItems().remove(draggedIndex);
                                mainTable.getItems().add(dropIndex, draggedPerson);

                                e.setDropCompleted(true);
                                mainTable.getSelectionModel().clearSelection();
                                e.consume();
                            }
                        }
                        }catch(Exception ex){ex.printStackTrace();
                    }
                }
            });
           return row;
        });
        */

        createBtn.setOnAction(e->newGroupAction());
        clearBtn.setOnAction(e->clearAction());
        deleteGroupBtn.setOnAction(e->deleteGroupAction());
        deleteBtn.setOnAction(e->deleteWorksAction());
        closeBtn.setOnAction(e->closeWindow(closeBtn.getScene().getWindow()));
        saveBtn.setOnAction(e->saveAction());
        addBtn.setOnAction(e->addAction());


    }

    private void saveAction(){
        try {
            new SaveExcel().saveEstimateExcel(conn, ID, saveBtn.getScene().getWindow());
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
            if(!conn.getAutoCommit())conn.commit();
            conn.close();
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
                rowMainCollection.add(new MainRow(rsGroup.getInt("GROUP_ID"),rsGroup.getString("GROUP_NAME"),"","","","",""));
                ResultSet rs = conn.createStatement().executeQuery("SELECT WORK_ID, N, COUNT, PRICE FROM MAIN_TABLE WHERE GROUP_ID = "+rsGroup.getInt("GROUP_ID")+" ORDER BY N");
                while (rs.next()){
                    ResultSet rs1 = conn.createStatement().executeQuery("SELECT WORK_NAME, UNIT FROM WORKS WHERE WORK_ID = "+rs.getInt("WORK_ID"));
                    int num = rs.getInt("N");
                    String name = rs1.getString("WORK_NAME");
                    String unit = rs1.getString("UNIT");
                    double count = rs.getDouble("COUNT");
                    double price = rs.getDouble("PRICE");
                    double sum = count*price;
                    String count1 = new DecimalFormat("#0.00").format(count);
                    String price1 = new DecimalFormat("#0.00").format(price);
                    String sum1 = new DecimalFormat("#0.00").format(sum);
                    s+=sum;
                    rowMainCollection.add(new MainRow(num,name,unit,count1,price1,sum1,""));
                }
                String groupSum = new DecimalFormat("#0.00").format(s);
                rowMainCollection.add(new MainRow(null,"","","","",groupSum,""));
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
            costs.add(new MainRow(null,s1,"%",new DecimalFormat("#0.00").format(count1),new DecimalFormat("#0.00").format(price1),new DecimalFormat("#0.00").format(count1*price1),""));
            costs.add(new MainRow(null,s2,"%",new DecimalFormat("#0.00").format(count2),new DecimalFormat("#0.00").format(price1),new DecimalFormat("#0.00").format(count2*price1),""));

            String s3 = "Итого по работам:  ";
            sumWork.setText(s3+ new DecimalFormat("#0.00").format(sum1Work));
            double all = sum1Work+costs.getSum();
            String s4 = "Итого по смете:  ";
            sumAll.setText(s4+ new DecimalFormat("#0.00").format(all));
            mainTable.getSelectionModel().select(0);
        } catch (Exception ex) {PrintException.print(ex);}

    }



    public void onEditChanged2(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        if(mainRowStringCellEditEvent.getNewValue().matches("-?\\d+(\\.\\d+)?")) {
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
        update();
    }

    public void onEditChanged(TableColumn.CellEditEvent<MainRow, String> mainRowStringCellEditEvent) {
        if(mainRowStringCellEditEvent.getNewValue().matches("-?\\d+(\\.\\d+)?")) {
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
        update();

    }

    private void save(Connection conn,Savepoint[] saves) throws Exception {
        if (saves.length - 1 >= 0) System.arraycopy(saves, 0, saves, 1, saves.length - 1);
        saves[0] = conn.setSavepoint();
    }
    private void back(Connection conn, Savepoint[] saves)throws Exception {
        if(saves[0]!=null){
            conn.rollback(saves[0]);
            System.arraycopy(saves, 1, saves, 0, saves.length - 1);
        }
    }
}
