package gallican.view;

import javax.persistence.EntityManager;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

public class GallicanPane
	extends BorderPane
{
	private final UniversesPane universesPane;
	private final CharacterOverviewPane characterOverviewPane;
	private final LocationOverviewPane locationOverviewPane;
	private final EventOverviewPane eventOverviewPane;

	public GallicanPane(EntityManager entityManager)
	{
		universesPane = new UniversesPane(entityManager);
		characterOverviewPane = new CharacterOverviewPane(entityManager);
		locationOverviewPane = new LocationOverviewPane(entityManager);
		eventOverviewPane = new EventOverviewPane(entityManager);

		TabPane tabPane = new TabPane(
				new Tab("Characters", characterOverviewPane),
				new Tab("Locations", locationOverviewPane),
				new Tab("Events", eventOverviewPane));

		tabPane.getStyleClass().add("floating");
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		universesPane.setPadding(new Insets(5));
		characterOverviewPane.setPadding(new Insets(5));
		locationOverviewPane.setPadding(new Insets(5));
		eventOverviewPane.setPadding(new Insets(5));

		characterOverviewPane.universeProperty().bind(universesPane.universeProperty());
		locationOverviewPane.universeProperty().bind(universesPane.universeProperty());
		eventOverviewPane.universeProperty().bind(universesPane.universeProperty());

		setPadding(new Insets(10));

		setTop(universesPane);
		setCenter(tabPane);
	}

	public boolean isDirty()
	{
		return universesPane
			.getAllUniverses()
			.stream()
			.anyMatch(u -> u.isDirty() || u.hasDirtyChildren());
	}
}
