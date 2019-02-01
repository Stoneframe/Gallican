package gallican;

import gallican.database.DatabaseManager;
import gallican.util.Util;
import gallican.view.GallicanPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Gallican
	extends Application
{
	@Override
	public void start(Stage stage)
	{
		try
		{
			DatabaseManager databaseManager = new DatabaseManager();

			databaseManager.backup();
			databaseManager.setup();

			GallicanPane gallicanPane = new GallicanPane(databaseManager.createEntityManager());

			Scene scene = new Scene(gallicanPane);

			stage.setOnCloseRequest((event) ->
				{
					if (gallicanPane.isDirty())
					{
						Util.showConfirmationDialog(
							"Unsaved entities!",
							"You are about to close with unsaved entities.",
							"Do you want to close?",
							buttonType ->
								{
									if (buttonType != ButtonType.OK)
									{
										event.consume();
									}
								});
					}
				});

			stage.setTitle("Gallican");
			stage.setScene(scene);
			stage.sizeToScene();
			stage.setResizable(false);
			stage.show();
		}
		catch (Exception e)
		{
			Util.showErrorDialog("Crash!", "Gallican has crashed!", e.toString());
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
