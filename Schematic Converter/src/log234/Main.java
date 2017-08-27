package log234;

	
import javafx.application.Application;
import javafx.stage.Stage;
import lightbulb.LightbulbTerminal;


public class Main extends Application {
    public static LightbulbTerminal terminal = new LightbulbTerminal();
    
	@Override
	public void start(Stage primaryStage) {
	    Thread converter = new Thread(new SchematicConverter());
	    terminal.setThread(converter);
	    converter.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
