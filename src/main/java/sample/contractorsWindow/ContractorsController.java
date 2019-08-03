package sample.contractorsWindow;

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
import sample.addContractors.AddContractorsController;
import sample.forWindow.ForWindowController;
import sample.newTableDialog.NewTableController;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;


public class ContractorsController implements Initializable {
    public TableView<ContractorRow> contractors;
    public TableColumn<ContractorRow, Integer> idCol;
    public TableColumn<ContractorRow, String> posCol;
    public TableColumn<ContractorRow, String> fioCol;
    public TableColumn<ContractorRow, String> orgNameCol;
    public TableColumn<ContractorRow, String> addressCol;
    public TableColumn<ContractorRow, Integer> okpoCol;
    public TextField searchLine;
    public Button deleteBtn;
    public Button apply;
    public Button append;
    private ContractorCollection contractorList = new ContractorCollection();
    public Connection conn;
    public NewTableController owner;
    public ForWindowController owner1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        fioCol.setCellValueFactory(new PropertyValueFactory<>("fio"));
        orgNameCol.setCellValueFactory(new PropertyValueFactory<>("orgName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        okpoCol.setCellValueFactory(new PropertyValueFactory<>("okpo"));
        contractors.setItems(contractorList.getRowList());

        searchLine.textProperty().addListener((observable, oldValue, newValue) -> update(newValue));

        contractors.setOnKeyReleased(event -> {
            if(event.getCode()== KeyCode.ESCAPE)contractors.getScene().getWindow().hide();
        });

        append.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/addContractorsWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Новый заказчик");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(append.getScene().getWindow());
                AddContractorsController controller = loader.getController();
                controller.conn = conn;
                newWindow.centerOnScreen();
                newWindow.showAndWait();
                update("");
            }catch (Exception ex){PrintException.print(ex);}
        });

        deleteBtn.setOnAction(event -> deleteAction());

        apply.setOnAction(event -> {
            if(contractors.getSelectionModel().getSelectedItems().size()>0){
                if(owner1==null) {
                    owner.setContractor(contractors.getSelectionModel().getSelectedItem().getId(), contractors.getSelectionModel().getSelectedItem().getOrgName());
                }else {
                    owner1.setContractor(contractors.getSelectionModel().getSelectedItem().getId(),contractors.getSelectionModel().getSelectedItem().getFio(), contractors.getSelectionModel().getSelectedItem().getOrgName(), contractors.getSelectionModel().getSelectedItem().getAddress());
                }
                contractors.getScene().getWindow().hide();
            }
        });



        contractors.setRowFactory(e->{
            TableRow<ContractorRow> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() ==2&&(!row.isEmpty())){
                    if(!deleteBtn.isVisible()) {
                        if(owner1==null) {
                            owner.setContractor(contractors.getSelectionModel().getSelectedItem().getId(), contractors.getSelectionModel().getSelectedItem().getOrgName());
                        }else {
                            owner1.setContractor(contractors.getSelectionModel().getSelectedItem().getId(),contractors.getSelectionModel().getSelectedItem().getFio(), contractors.getSelectionModel().getSelectedItem().getOrgName(), contractors.getSelectionModel().getSelectedItem().getAddress());
                        }
                        contractors.getScene().getWindow().hide();
                    }else{
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addContractorsWindow.fxml"));
                            AnchorPane addLayout = loader.load();
                            Scene secondScene = new Scene(addLayout);
                            Stage newWindow = new Stage();
                            newWindow.setScene(secondScene);
                            newWindow.setTitle(contractors.getSelectionModel().getSelectedItem().getOrgName());
                            newWindow.initModality(Modality.WINDOW_MODAL);
                            newWindow.initOwner(append.getScene().getWindow());
                            AddContractorsController controller = loader.getController();
                            controller.conn = conn;
                            controller.applyBtn.setText("OK");
                            int okpo = contractors.getSelectionModel().getSelectedItem().getOkpo();
                            if(okpo == 0){controller.OKPOField.setText("");}else {controller.OKPOField.setText(String.valueOf(okpo));}
                            controller.addressField.setText(contractors.getSelectionModel().getSelectedItem().getAddress());
                            controller.nameField.setText(contractors.getSelectionModel().getSelectedItem().getOrgName());
                            controller.fioField.setText(contractors.getSelectionModel().getSelectedItem().getFio());
                            controller.positionField.setText(contractors.getSelectionModel().getSelectedItem().getPosition());
                            controller.contrID = contractors.getSelectionModel().getSelectedItem().getId();
                            newWindow.centerOnScreen();
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
        try{
            if(deleteBtn.isVisible()){
                if (contractors.getSelectionModel().getSelectedItems().size() > 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Удаление заказчика");
                    alert.setHeaderText("Вы уверены что хотите удалить заказчика?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.get() == ButtonType.OK) {
                        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(CONTRACTORS_ID) FROM TABLES WHERE CONTRACTORS_ID = "+contractors.getSelectionModel().getSelectedItem().getId());
                        int count = rs.getInt("COUNT(CONTRACTORS_ID)");
                        rs.close();
                        if(count==0) {
                            conn.createStatement().executeUpdate("DELETE FROM CONTRACTORS WHERE CONTRACTOR_ID = " + contractors.getSelectionModel().getSelectedItem().getId());
                            update("");
                        }else{
                            ResultSet rs2 = conn.createStatement().executeQuery("SELECT TABLE_NAME FROM TABLES WHERE CONTRACTORS_ID = "+contractors.getSelectionModel().getSelectedItem().getId());
                            StringBuilder s = new StringBuilder();
                            while (rs2.next()){
                                s.append(rs2.getString("TABLE_NAME")).append(", ");
                            }
                            rs2.close();
                            s = new StringBuilder(s.substring(0, s.length() - 2));
                            alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Заказчик используется в сметах");
                            alert.setHeaderText("Заказчик, который вы хотите удалить, используется в сметах:");
                            alert.setContentText(s.toString());
                            alert.show();
                        }
                    }
                }
            }
        }catch (Exception ex){PrintException.print(ex);}
    }

    public void update(String search){
        try {
            contractorList.clear();
            ResultSet rs = conn.createStatement().executeQuery("SELECT CONTRACTOR_ID,POSITION,FIO,ORGANIZATION_NAME,ADDRESS,OKPO FROM CONTRACTORS");
            while (rs.next()){
                int id = rs.getInt("CONTRACTOR_ID");
                String position = rs.getString("POSITION");
                String fio = rs.getString("FIO");
                String orgName = rs.getString("ORGANIZATION_NAME");
                String address = rs.getString("ADDRESS");
                int okpo = rs.getInt("OKPO");
                if(address.toLowerCase().contains(search.toLowerCase())||orgName.toLowerCase().contains(search.toLowerCase()))contractorList.add(new ContractorRow(id,position,fio,orgName,address,okpo));
            }
            rs.close();
            contractors.getSelectionModel().select(0);

        }catch (Exception ex) {PrintException.print(ex);}
    }
}
