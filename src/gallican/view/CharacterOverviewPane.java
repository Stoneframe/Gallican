package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Character;
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

public class CharacterOverviewPane
	extends BorderPane
{
	private final EntityManager entityManager;

	private final ListView<Character> characterListView;

	private final CharacterPane characterPane;

	private final FlowPane buttonPane;
	private final Button addButton;
	private final Button removeButton;

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	public CharacterOverviewPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		characterListView = new ListView<>();
		characterListView.setCellFactory(lc -> new NameListCell<>());
		characterListView.itemsProperty().bind(
			Util.createNestedPropertyBinding(universe, Universe::characters));

		characterPane = new CharacterPane(entityManager);
		characterPane.setPadding(new Insets(0, 0, 0, 10));
		characterPane.characterProperty().bind(
			characterListView.getSelectionModel().selectedItemProperty());

		addButton = new Button("Add");
		addButton.setMinWidth(70);
		addButton.disableProperty().bind(Bindings.isNull(universe));
		addButton.setOnAction(this::addButtonClicked);

		removeButton = new Button("Remove");
		removeButton.setMinWidth(70);
		removeButton.disableProperty().bind(
			Bindings.or(
				Bindings.isNull(universe),
				Bindings.isNull(characterListView.getSelectionModel().selectedItemProperty())));
		removeButton.setOnAction(this::removeButtonClicked);

		buttonPane = new FlowPane(addButton, removeButton);
		buttonPane.setHgap(5);
		buttonPane.setPadding(new Insets(5, 0, 0, 0));

		setLeft(characterListView);
		setCenter(characterPane);
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

	private void addButtonClicked(ActionEvent event)
	{
		Util.showTextInputDialog(
			"Create New Character",
			"Name of new character",
			"Name:",
			(name) ->
				{
					executeWithTransaction(() ->
						{
							Character character = new Character();
							character.setName(name);

							getUniverse().addCharacter(character);

							characterListView.getSelectionModel().select(character);
						});
				});
	}

	private void removeButtonClicked(ActionEvent event)
	{
		executeWithTransaction(() ->
			{
				Character character = characterListView.getSelectionModel().getSelectedItem();

				getUniverse().removeCharacter(character);
			});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
