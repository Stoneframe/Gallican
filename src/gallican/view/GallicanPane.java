package gallican.view;

import javax.persistence.EntityManager;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class GallicanPane
	extends BorderPane
{
	private final UniversesPane universesPane;
	private final CharacterOverviewPane charactersPane;

	public GallicanPane(EntityManager entityManager)
	{
		universesPane = new UniversesPane(entityManager);
		charactersPane = new CharacterOverviewPane(entityManager);

		BorderPane.setMargin(universesPane, new Insets(5));
		BorderPane.setMargin(charactersPane, new Insets(5));

		charactersPane.universeProperty().bind(universesPane.universeProperty());

		setPadding(new Insets(10));

		setTop(universesPane);
		setCenter(charactersPane);
	}
}
