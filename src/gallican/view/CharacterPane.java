package gallican.view;

import javax.persistence.EntityManager;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.fxmisc.easybind.monadic.PropertyBinding;

import gallican.model.Character;
import gallican.model.Event;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CharacterPane
	extends GridPane
{
	private final EntityManager entityManager;

	private final PropertyBinding<String> nameBinding;
	private final PropertyBinding<String> descriptionBinding;
	private final PropertyBinding<String> personalityBinding;
	private final PropertyBinding<String> powersBinding;
	private final MonadicBinding<Boolean> validBinding;
	private final MonadicBinding<Boolean> dirtyBinding;
	private final MonadicBinding<ObservableList<Event>> eventsBinding;

	private final BooleanBinding saveBinding;

	private final Text title;

	private final TextField nameTextField;
	private final TextArea descriptionTextArea;
	private final TextArea personalityTextArea;
	private final TextArea powersTextArea;

	private final Button saveButton;

	private final ListView<Event> eventListView;

	private final ObjectProperty<Character> character = new SimpleObjectProperty<>();

	public CharacterPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		nameBinding = EasyBind
			.monadic(character)
			.selectProperty(Character::nameProperty);
		descriptionBinding = EasyBind
			.monadic(character)
			.selectProperty(Character::descriptionProperty);
		personalityBinding = EasyBind
			.monadic(character)
			.selectProperty(Character::personalityProperty);
		powersBinding = EasyBind
			.monadic(character)
			.selectProperty(Character::powersProperty);
		validBinding = EasyBind
			.select(character)
			.selectObject(c -> c.validProperty())
			.orElse(false);
		dirtyBinding = EasyBind
			.select(character)
			.selectObject(c -> c.dirtyProperty())
			.orElse(false);
		eventsBinding = EasyBind
			.select(character)
			.selectObject(Character::eventsProperty);

		saveBinding = Bindings
			.createBooleanBinding(
				() -> validBinding.get() && dirtyBinding.get(),
				validBinding,
				dirtyBinding)
			.not();

		title = new Text("Character");
		title.setFont(new Font(20));

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(Bindings.isNull(character));
		nameTextField.textProperty().bindBidirectional(nameBinding);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setMinHeight(300);
		descriptionTextArea.setWrapText(true);
		descriptionTextArea.disableProperty().bind(Bindings.isNull(character));
		descriptionTextArea.textProperty().bindBidirectional(descriptionBinding);

		personalityTextArea = new TextArea();
		personalityTextArea.setWrapText(true);
		personalityTextArea.disableProperty().bind(Bindings.isNull(character));
		personalityTextArea.textProperty().bindBidirectional(personalityBinding);

		powersTextArea = new TextArea();
		powersTextArea.setWrapText(true);
		powersTextArea.disableProperty().bind(Bindings.isNull(character));
		powersTextArea.textProperty().bindBidirectional(powersBinding);

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setOnAction(this::saveButtonClicked);
		saveButton.disableProperty().bind(saveBinding);

		eventListView = new ListView<>();
		eventListView.setCellFactory(lc -> new DisplayValueListCell<>());
		eventListView.disableProperty().bind(Bindings.isNull(character));
		eventListView.itemsProperty().bind(eventsBinding);

		setVgap(5);
		setHgap(5);

		GridPane.setColumnSpan(descriptionTextArea, 2);
		GridPane.setColumnSpan(personalityTextArea, 2);
		GridPane.setColumnSpan(powersTextArea, 2);
		GridPane.setRowSpan(eventListView, 7);

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

		Text historyText = new Text("History:");
		historyText.setFont(new Font(20));

		add(historyText, 3, 0);
		add(eventListView, 3, 1);

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

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
