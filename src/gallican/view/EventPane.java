package gallican.view;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.fxmisc.easybind.monadic.PropertyBinding;

import gallican.model.Character;
import gallican.model.Event;
import gallican.model.Location;
import gallican.model.Universe;
import gallican.util.Util;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class EventPane
	extends GridPane
{
	private final EntityManager entityManager;

	private final PropertyBinding<LocalDate> dateBinding;
	private final PropertyBinding<String> nameBinding;
	private final PropertyBinding<String> descriptionBinding;
	private final MonadicBinding<String> locationBinding;
	private final MonadicBinding<ObservableList<Character>> charactersBinding;
	private final MonadicBinding<Boolean> dirtyBinding;

	private final Text title;

	private final DatePicker datePicker;
	private final TextField nameTextField;
	private final TextArea descriptionTextArea;

	private final TextField locationTextField;

	private final ListView<Character> characterListView;
	private final Button addCharacterButton;
	private final Button removeCharacterButton;

	private final Button saveButton;

	private final ObjectProperty<Event> event = new SimpleObjectProperty<>();

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	public EventPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		dateBinding = EasyBind
			.monadic(event)
			.selectProperty(Event::dateProperty);
		nameBinding = EasyBind
			.monadic(event)
			.selectProperty(Event::nameProperty);
		descriptionBinding = EasyBind
			.monadic(event)
			.selectProperty(Event::descriptionProperty);
		locationBinding = EasyBind
			.select(event)
			.select(Event::locationProperty)
			.selectObject(Location::nameProperty);
		charactersBinding = EasyBind
			.select(event)
			.selectObject(Event::charactersProperty);
		dirtyBinding = EasyBind
			.select(event)
			.selectObject(e -> e.dirtyProperty().not())
			.orElse(true);

		title = new Text("Event");
		title.setFont(new Font(20));

		datePicker = new DatePicker();
		datePicker.setMinWidth(200);
		datePicker.disableProperty().bind(Bindings.isNull(event));
		datePicker.valueProperty().bindBidirectional(dateBinding);

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(Bindings.isNull(event));
		nameTextField.textProperty().bindBidirectional(nameBinding);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setWrapText(true);
		descriptionTextArea.disableProperty().bind(Bindings.isNull(event));
		descriptionTextArea.textProperty().bindBidirectional(descriptionBinding);

		locationTextField = new TextField();
		locationTextField.disableProperty().bind(Bindings.isNull(event));
		locationTextField.setOnMouseClicked(this::locationTextFieldClicked);
		locationTextField.textProperty().bind(locationBinding);

		characterListView = new ListView<>();
		characterListView.setCellFactory(lc -> new NameListCell<>());
		characterListView.disableProperty().bind(Bindings.isNull(event));
		characterListView.itemsProperty().bind(charactersBinding);

		addCharacterButton = new Button("Add");
		addCharacterButton.setMinWidth(50);
		addCharacterButton.disableProperty().bind(Bindings.isNull(event));

		removeCharacterButton = new Button("Remove");
		removeCharacterButton.setMinWidth(50);
		removeCharacterButton.disableProperty().bind(
			Bindings.or(
				Bindings.isNull(event),
				Bindings.isNull(characterListView.getSelectionModel().selectedItemProperty())));

		FlowPane characterButtonPane = new FlowPane(
				Orientation.HORIZONTAL,
				5,
				5,
				addCharacterButton,
				removeCharacterButton);

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setOnAction(this::saveButtonClicked);
		saveButton.disableProperty().bind(dirtyBinding);

		setVgap(5);
		setHgap(5);

		GridPane.setColumnSpan(descriptionTextArea, 2);
		GridPane.setColumnSpan(characterListView, 2);
		GridPane.setColumnSpan(characterButtonPane, 2);

		add(title, 0, 0);

		add(new Text("Date:"), 0, 1);
		add(datePicker, 1, 1);

		add(new Text("Name:"), 0, 3);
		add(nameTextField, 1, 3);

		add(new Text("Description:"), 0, 5);
		add(descriptionTextArea, 0, 6);

		add(new Text("Location:"), 0, 8);
		add(locationTextField, 1, 8);

		add(new Text("Characters:"), 0, 10);
		add(characterListView, 0, 11);
		add(characterButtonPane, 0, 12);

		add(saveButton, 1, 14);

		setHalignment(saveButton, HPos.RIGHT);
	}

	public Event getEvent()
	{
		return event.get();
	}

	public void setEvent(Event event)
	{
		this.event.set(event);
	}

	public ObjectProperty<Event> eventProperty()
	{
		return event;
	}

	public Universe getUniverse()
	{
		return universe.get();
	}

	public void setUniverse(Universe universe)
	{
		this.universe.set(universe);
	}

	public ObjectProperty<Universe> universeProperty()
	{
		return universe;
	}

	private void saveButtonClicked(ActionEvent e)
	{
		executeWithTransaction(() ->
			{
				entityManager.persist(event.get());
			});
	}

	private void locationTextFieldClicked(MouseEvent event)
	{
		if (locationTextField.isDisabled()) return;

		List<Location> locations = Location.toList(getUniverse().getLocation());

		Util.showChoiceDialog(
			"Location",
			"Choose location for event.",
			"Select location:",
			locations,
			location ->
				{
					getEvent().setLocation(location);
				});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
