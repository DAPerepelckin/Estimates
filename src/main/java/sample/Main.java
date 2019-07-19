package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.welcomeWindow.WelcomeController;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
       //new  LoadWorksBase().load();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Estimates");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(480);
        primaryStage.setMinWidth(570);
        WelcomeController controller = loader.getController();
        primaryStage.show();
        controller.load(scene.getWindow());

    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
