package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Event;
import gallican.model.Universe;
import gallican.util.Util;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class EventOverviewPane
	extends BorderPane
{
	private final EntityManager entityManager;

	private final ListView<Event> eventListView;

	private final EventPane eventPane;

	private final FlowPane buttonPane;
	private final Button addButton;
	private final Button removeButton;

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	public EventOverviewPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		eventListView = new ListView<>();
		eventListView.setCellFactory(lc -> new NameListCell<>());
		eventListView.itemsProperty().bind(
			Util.createNestedPropertyBinding(universe, Universe::events));

		eventPane = new EventPane(entityManager);
		eventPane.setPadding(new Insets(0, 0, 0, 10));
		eventPane.eventProperty().bind(eventListView.getSelectionModel().selectedItemProperty());
		eventPane.universeProperty().bind(universe);

		addButton = new Button("Add");
		addButton.setMinWidth(70);
		addButton.disableProperty().bind(Bindings.isNull(universe));
		addButton.setOnAction(this::addButtonClicked);

		removeButton = new Button("Remove");
		removeButton.setMinWidth(70);
		removeButton.disableProperty().bind(
			Bindings.or(
				Bindings.isNull(universe),
				Bindings.isNull(eventListView.getSelectionModel().selectedItemProperty())));
		removeButton.setOnAction(this::removeButtonClicked);

		buttonPane = new FlowPane(addButton, removeButton);
		buttonPane.setHgap(5);
		buttonPane.setPadding(new Insets(5, 0, 0, 0));

		setLeft(eventListView);
		setCenter(eventPane);
		setBottom(buttonPane);
	}

	public Universe getUniverse()
	{
		return universe.getValue();
	}

	public void setUniverse(Universe universe)
	{
		this.universe.set(universe);
	}

	public ObjectProperty<Universe> universeProperty()
	{
		return universe;
	}

	private void addButtonClicked(ActionEvent e)
	{
		Util.showTextInputDialog(
			"Create New Event",
			"Name of new event",
			"Name:",
			(name) ->
				{
					executeWithTransaction(() ->
						{
							Event event = new Event();
							event.setName(name);

							getUniverse().addEvent(event);
						});
				});
	}

	private void removeButtonClicked(ActionEvent e)
	{
		executeWithTransaction(() ->
			{
				Event event = eventListView.getSelectionModel().getSelectedItem();

				getUniverse().removeEvent(event);
			});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
