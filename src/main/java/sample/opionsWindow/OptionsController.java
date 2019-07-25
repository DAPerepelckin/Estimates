package sample.opionsWindow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import sample.PrintException;
import sample.workBaseWindow.WorkBaseController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class OptionsController implements Initializable {
    public TextField pathToDBField;
    public Button applyBnt;
    public Button addBtnDB;
    public CheckBox logsBtn;
    public Button addBtnSave;
    public TextField pathToSaveField;
    public CheckBox askBtn;
    public ComboBox<Profile> profiles;
    public CheckBox openBtn;
    public Button refactorBtn;
    private Preferences user = Preferences.userRoot();
    public Connection conn;

    private class Profile{
        int ID;
        String name;

    }



    private ObservableList<Profile> profileList =  FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        pathToDBField.setText(user.get("pathToDB", ""));
        pathToSaveField.setText(user.get("pathToSave", ""));
        logsBtn.setSelected(user.getBoolean("logs", true));
        askBtn.setSelected(user.getBoolean("ask", true));
        openBtn.setSelected(user.getBoolean("open",true));

        logsBtn.setOnAction(e -> user.putBoolean("logs", logsBtn.isSelected()));

        askBtn.setOnAction(e -> user.putBoolean("ask", askBtn.isSelected()));

        openBtn.setOnAction(e -> user.putBoolean("open",openBtn.isSelected()));

        applyBnt.setOnAction(e -> applyBnt.getScene().getWindow().hide());

        addBtnDB.setOnAction(e -> {
            try {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(addBtnDB.getScene().getWindow());
                if (file != null) {
                    if (conn.createStatement().executeQuery("SELECT VERSION FROM VERSION").getString("VERSION").equalsIgnoreCase("25.07.11.20")) {
                        user.put("pathToDB", file.getAbsolutePath());
                        pathToDBField.setText(file.getAbsolutePath());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("База данных устарела");
                        alert.setTitle("База данных устарела");
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.millis(1000),
                                        event -> alert.close()
                                )
                        );
                        alert.show();
                        timeline.play();
                    }
                }
            }catch (Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("База данных устарела");
                alert.setTitle("База данных устарела");
                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.millis(2000),
                                event -> alert.close()
                        )
                );
                alert.show();
                timeline.play();
            }
        });

        refactorBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/workBaseWindow.fxml"));
                AnchorPane addLayout = loader.load();
                Scene secondScene = new Scene(addLayout);
                Stage newWindow = new Stage();
                newWindow.setScene(secondScene);
                newWindow.setTitle("Редактирование списка работ");
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(refactorBtn.getScene().getWindow());
                newWindow.centerOnScreen();
                WorkBaseController controller = loader.getController();
                controller.conn = conn;
                controller.apply.setText("ОК");
                controller.apply.setOnAction(event1 ->controller.apply.getScene().getWindow().hide());
                controller.deleteBtn.setVisible(true);
                controller.searchLine.getProperties().put("pane-left-anchor",255.0);
                controller.append.setText("Добавить");

                controller.update("");
                newWindow.showAndWait();
                if(!conn.isClosed()) {
                    if (!conn.getAutoCommit()) conn.commit();
                }
            }catch (Exception ex){PrintException.print(ex);}
        });

        addBtnSave.setOnAction(e -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(addBtnSave.getScene().getWindow());
                if (file != null) {
                    user.put("pathToSave", file.getAbsolutePath());
                        pathToSaveField.setText(file.getAbsolutePath());
                }
        });




        profiles.setOnAction(e->user.putInt("profile_id",profiles.getValue().ID));

    }
    public void init(){
        try{
            ResultSet rs = conn.createStatement().executeQuery("SELECT ORGANIZATION_NAME, PROFILE_ID FROM PROFILES");

            while (rs.next()){
                Profile profile = new Profile();
                profile.ID = rs.getInt("PROFILE_ID");
                profile.name = rs.getString("ORGANIZATION_NAME");
                profileList.add(profile);
            }
            profiles.setConverter(new StringConverter<Profile>() {
                @Override
                public String toString(Profile object) {
                    return object.name;
                }

                @Override
                public Profile fromString(String string) {
                    return null;
                }
            });
            profiles.setItems(profileList);
            profiles.setValue(profileList.filtered(t->t.ID==user.getInt("profile_id",-1)).get(0));
        }catch (Exception ex){
            PrintException.print(ex);}
    }
}
