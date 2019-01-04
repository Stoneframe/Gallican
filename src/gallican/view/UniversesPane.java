package gallican.view;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import gallican.model.Location;
import gallican.model.Universe;
import gallican.util.Util;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class UniversesPane
	extends FlowPane
{
	private final EntityManager entityManager;

	private final Text title;

	private final ComboBox<Universe> universeComboBox;

	private final Button addButton;
	private final Button editButton;
	private final Button removeButton;

	private final ObservableList<Universe> universes;

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	public UniversesPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		TypedQuery<Universe> query =
				entityManager.createQuery("select u from Universe u", Universe.class);

		universes = FXCollections.observableArrayList(query.getResultList());

		title = new Text("Universe:");
		title.setFont(new Font(20));

		universeComboBox = new ComboBox<>(universes);
		universeComboBox.setPrefWidth(200);
		universeComboBox.setButtonCell(new NameListCell<>());
		universeComboBox.setCellFactory(lc -> new NameListCell<Universe>());
		universeComboBox.getSelectionModel().selectFirst();

		universe.bind(universeComboBox.getSelectionModel().selectedItemProperty());

		addButton = new Button("Add");
		addButton.setMinWidth(70);
		addButton.setOnAction(this::addButtonClicked);

		BooleanBinding universeNullBinding =
				Bindings.createBooleanBinding(() -> universe.get() == null, universe);

		editButton = new Button("Edit");
		editButton.setMinWidth(70);
		editButton.disableProperty().bind(universeNullBinding);
		editButton.setOnAction(this::editButtonClicked);

		removeButton = new Button("Remove");
		removeButton.setMinWidth(70);
		removeButton.disableProperty().bind(universeNullBinding);
		removeButton.setOnAction(this::removeButtonClicked);

		setHgap(5);

		getChildren().addAll(title, universeComboBox, addButton, editButton, removeButton);
	}

	public Universe getUniverse()
	{
		return universe.get();
	}

	public ReadOnlyObjectProperty<Universe> universeProperty()
	{
		return universe;
	}

	private void addButtonClicked(ActionEvent event)
	{
		Util.showTextInputDialog(
			"Create New Universe",
			"Name of new universe",
			"Name:",
			(name) ->
				{
					executeWithTransaction(() ->
						{
							Location location = new Location();
							location.setName(name);

							Universe universe = new Universe();
							universe.setName(name);
							universe.setLocation(location);

							entityManager.persist(universe);
							universes.add(universe);

							universeComboBox.getSelectionModel().selectLast();
						});
				});
	}

	private void editButtonClicked(ActionEvent event)
	{
		Util.showTextInputDialog(
			"Edit Universe",
			"New name of universe",
			"Name:",
			(name) ->
				{
					executeWithTransaction(() ->
						{
							Universe universe = getUniverse();
							universe.setName(name);

							entityManager.persist(universe);
						});
				});
	}

	private void removeButtonClicked(ActionEvent event)
	{
		executeWithTransaction(() ->
			{
				Universe universe = universeComboBox.getSelectionModel().getSelectedItem();

				entityManager.remove(universe);
				universes.remove(universe);

				universeComboBox.getSelectionModel().selectFirst();
			});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
