package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Character;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CharacterPane
	extends GridPane
{
	private final EntityManager entityManager;

	private final Text title;

	private final TextField nameTextField;

	private final Button saveButton;

	private final ObjectProperty<Character> character = new SimpleObjectProperty<>();

	public CharacterPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		title = new Text("Character");
		title.setFont(new Font(20));

		BooleanBinding characterNullBinding =
				Bindings.createBooleanBinding(() -> character.get() == null, character);

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(characterNullBinding);

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setOnAction(this::saveButtonClicked);
		saveButton.disableProperty().bind(characterNullBinding);

		character.addListener(this::characterChanged);

		setVgap(5);
		setHgap(5);

		add(title, 0, 0);
		add(new Text("Name:"), 0, 1);
		add(nameTextField, 1, 1);
		add(saveButton, 1, 3);

		setHalignment(saveButton, HPos.RIGHT);
	}

	public Character getCharacter()
	{
		return character.get();
	}

	public void setCharacter(Character character)
	{
		this.character.set(character);
	}

	public ObjectProperty<Character> characterProperty()
	{
		return character;
	}

	private void saveButtonClicked(ActionEvent event)
	{
		executeWithTransaction(() ->
			{
				entityManager.persist(character.get());
			});
	}

	private void characterChanged(
			ObservableValue<? extends Character> observable,
			Character oldValue,
			Character newValue)
	{
		if (oldValue != null)
		{
			nameTextField.textProperty().unbindBidirectional(oldValue.nameProperty());
		}

		nameTextField.clear();

		if (newValue != null)
		{
			nameTextField.textProperty().bindBidirectional(newValue.nameProperty());
		}
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
