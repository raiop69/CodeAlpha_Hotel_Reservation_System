 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
         Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
//        scene.getStylesheets().add(getClass().getResource("/hotel/style.css").toExternalForm());

        primaryStage.setTitle("LuxeStay Hotel Reservation System");
        primaryStage.setScene(scene);
//        primaryStage.setMinWidth(1280); 
  //      primaryStage.setMinHeight(720);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
