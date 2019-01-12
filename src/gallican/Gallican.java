package gallican;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
	private static final String PERSISTENCE_UNIT_NAME = "gallican";

	@Override
	public void start(Stage stage) throws Exception
	{
		DatabaseManager database = new DatabaseManager(getJavaxPersistenceJdbcUrl());
		database.setup();

		GallicanPane gallicanPane = new GallicanPane(createEntityManager());

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

	private EntityManager createEntityManager()
	{
		Map<String, String> properties = new HashMap<>();

		properties.put("javax.persistence.jdbc.url", getJavaxPersistenceJdbcUrl());

		EntityManagerFactory factory =
				Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

		return factory.createEntityManager();
	}

	private String getJavaxPersistenceJdbcUrl()
	{
		return "jdbc:derby:" + getDatabasePath() + ";create=true";
	}

	private String getDatabasePath()
	{
		return Paths
			.get(
				System.getProperty("user.home"),
				"AppData/Local/Gallican/gallicanDb")
			.toString()
			.replace("\\", "/");
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
