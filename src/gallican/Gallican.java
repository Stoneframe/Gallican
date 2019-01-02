package gallican;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import gallican.database.DatabaseManager;
import gallican.view.GallicanPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gallican
	extends Application
{
	private static final String PERSISTENCE_UNIT_NAME = "gallican";
	private static EntityManagerFactory factory;

	@Override
	public void start(Stage stage) throws Exception
	{
		DatabaseManager database = new DatabaseManager("gallicanDB");
		database.setup();

		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = factory.createEntityManager();

		GallicanPane gallicanPane = new GallicanPane(entityManager);

		Scene scene = new Scene(gallicanPane);

		stage.setTitle("Gallican");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
