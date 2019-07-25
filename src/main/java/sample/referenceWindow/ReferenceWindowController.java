package sample.referenceWindow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ReferenceWindowController implements Initializable {
    public Hyperlink mailLink;
    public Label copyLbl;
    public TextField addWork;
    public TextField addGroup;
    public TextField deleteWork;
    public TextField deleteGroup;
    public TextField clear;
    public TextField save;
    public AnchorPane refWindow;
    public TextField forField;
    private Preferences user = Preferences.userRoot();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        copyLbl.setVisible(false);
        save.setText(user.get("saveKey","S"));
        clear.setText(user.get("clearKey","P"));
        addGroup.setText(user.get("addGroupKey","G"));
        addWork.setText(user.get("addWorkKey","W"));
        forField.setText(user.get("forKey","F"));

        save.setOnKeyReleased(event -> {
            save.setText(event.getCode().getName());
            user.put("saveKey",event.getCode().getName());
        });

        clear.setOnKeyReleased(event -> {
            clear.setText(event.getCode().getName());
            user.put("clearKey",event.getCode().getName());
        });
        addWork.setOnKeyReleased(event -> {
            addWork.setText(event.getCode().getName());
            user.put("addWorkKey",event.getCode().getName());
        });
        addGroup.setOnKeyReleased(event -> {
            addGroup.setText(event.getCode().getName());
            user.put("addWorkKey",event.getCode().getName());
        });

        forField.setOnKeyReleased(event -> {
            forField.setText(event.getCode().getName());
            user.get("forKey",event.getCode().getName());
        });


        mailLink.setOnAction(event -> {
            StringSelection selection = new StringSelection(mailLink.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection,null);
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            e->copyLbl.setVisible(false)
                    )
            );
            copyLbl.setVisible(true);
            timeline.play();
        });
    }
}
