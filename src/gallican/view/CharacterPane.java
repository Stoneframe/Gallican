package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Character;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
	private final TextArea descriptionTextArea;
	private final TextArea personalityTextArea;
	private final TextArea powersTextArea;

	private final Button saveButton;

	private final ObjectProperty<Character> character = new SimpleObjectProperty<>();

	public CharacterPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		title = new Text("Character");
		title.setFont(new Font(20));

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(Bindings.isNull(character));

		descriptionTextArea = new TextArea();
		descriptionTextArea.setWrapText(true);
		descriptionTextArea.disableProperty().bind(Bindings.isNull(character));

		personalityTextArea = new TextArea();
		personalityTextArea.setWrapText(true);
		personalityTextArea.disableProperty().bind(Bindings.isNull(character));

		powersTextArea = new TextArea();
		powersTextArea.setWrapText(true);
		powersTextArea.disableProperty().bind(Bindings.isNull(character));

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setDisable(true);
		saveButton.setOnAction(this::saveButtonClicked);

		character.addListener(this::characterChanged);

		setVgap(5);
		setHgap(5);

		GridPane.setColumnSpan(descriptionTextArea, 2);
		GridPane.setColumnSpan(personalityTextArea, 2);
		GridPane.setColumnSpan(powersTextArea, 2);

		add(title, 0, 0);

		add(new Text("Name:"), 0, 1);
		add(nameTextField, 1, 1);

		add(new Text("Description:"), 0, 3);
		add(descriptionTextArea, 0, 4);

		add(new Text("Personality:"), 0, 6);
		add(personalityTextArea, 0, 7);

		add(new Text("Powers:"), 0, 9);
		add(powersTextArea, 0, 10);

		add(saveButton, 1, 12);

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
			descriptionTextArea.textProperty().unbindBidirectional(oldValue.descriptionProperty());
			personalityTextArea.textProperty().unbindBidirectional(oldValue.personalityProperty());
			powersTextArea.textProperty().unbindBidirectional(oldValue.powersProperty());

			saveButton.disableProperty().unbind();
		}

		nameTextField.clear();

		if (newValue != null)
		{
			nameTextField.textProperty().bindBidirectional(newValue.nameProperty());
			descriptionTextArea.textProperty().bindBidirectional(newValue.descriptionProperty());
			personalityTextArea.textProperty().bindBidirectional(newValue.personalityProperty());
			powersTextArea.textProperty().bindBidirectional(newValue.powersProperty());

			saveButton.disableProperty().bind(newValue.dirtyProperty().not());
		}
		else
		{
			saveButton.setDisable(true);
		}
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
