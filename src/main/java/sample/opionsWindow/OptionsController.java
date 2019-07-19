package sample.opionsWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import sample.PrintException;

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
    private Preferences user = Preferences.userRoot();

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

        openBtn.setOnAction(e -> user.putBoolean("open",askBtn.isSelected()));

        applyBnt.setOnAction(e -> applyBnt.getScene().getWindow().hide());

        addBtnDB.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(addBtnDB.getScene().getWindow());
            if (file != null) {
                user.put("pathToDB", file.getAbsolutePath());
                pathToDBField.setText(file.getAbsolutePath());
            }
        });

        addBtnSave.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(addBtnSave.getScene().getWindow());
            if (file != null) {
                user.put("pathToSave", file.getAbsolutePath());
                pathToSaveField.setText(file.getAbsolutePath());
            }

        });

        try{
            Class.forName("org.sqlite.JDBC");
            String path = user.get("pathToDB","");
            String URL = "jdbc:sqlite:"+ path;
            Connection conn = DriverManager.getConnection(URL);
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
            conn.close();
        }catch (Exception ex){
            PrintException.print(ex);}


        profiles.setOnAction(e->user.putInt("profile_id",profiles.getValue().ID));

    }
}
