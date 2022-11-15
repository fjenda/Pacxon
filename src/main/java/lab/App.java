package lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lab.controllers.ControllerHandler;
import lab.controllers.GameController;
import lab.controllers.MenuController;
import lab.enums.GameState;

/**
 *  Class <b>App</b> - extends class Application and it is an entry point of the program
 * @author     Java I
 */
public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private ControllerHandler controllerHandler;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			controllerHandler = new ControllerHandler(this, primaryStage);
			controllerHandler.changeScene(GameState.MENU);
			//Construct a main window with a canvas.
			primaryStage.resizableProperty().set(false);
			primaryStage.setTitle("Java 1 - Pacxon project");
			primaryStage.show();

			//Exit program when main window is closed
			primaryStage.setOnCloseRequest(this::exitProgram);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exitProgram(WindowEvent evt) {
		System.exit(0);
	}
}