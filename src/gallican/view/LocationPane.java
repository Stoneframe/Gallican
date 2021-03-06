package gallican.view;

import javax.persistence.EntityManager;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.fxmisc.easybind.monadic.PropertyBinding;

import gallican.model.Event;
import gallican.model.Location;
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

public class LocationPane
	extends GridPane
{
	private final EntityManager entityManager;

	private final PropertyBinding<String> nameBinding;
	private final PropertyBinding<String> descriptionBinding;
	private final MonadicBinding<Boolean> dirtyBinding;
	private final MonadicBinding<Boolean> validBinding;
	private final MonadicBinding<ObservableList<Event>> eventsBinding;

	private final BooleanBinding saveBinding;

	private final Text title;

	private final TextField nameTextField;
	private final TextArea descriptionTextArea;

	private final Button saveButton;

	private final ListView<Event> eventListView;

	private final ObjectProperty<Location> location = new SimpleObjectProperty<>();

	public LocationPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		nameBinding = EasyBind
			.monadic(location)
			.selectProperty(Location::nameProperty);
		descriptionBinding = EasyBind
			.monadic(location)
			.selectProperty(Location::descriptionProperty);
		validBinding = EasyBind
			.select(location)
			.selectObject(c -> c.validProperty())
			.orElse(false);
		dirtyBinding = EasyBind
			.select(location)
			.selectObject(e -> e.dirtyProperty())
			.orElse(false);
		eventsBinding = EasyBind
			.select(location)
			.selectObject(Location::eventsProperty);

		saveBinding = Bindings
			.createBooleanBinding(
				() -> validBinding.get() && dirtyBinding.get(),
				validBinding,
				dirtyBinding)
			.not();

		title = new Text("Location");
		title.setFont(new Font(20));

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(Bindings.isNull(location));
		nameTextField.textProperty().bindBidirectional(nameBinding);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setMinHeight(724);
		descriptionTextArea.setWrapText(true);
		descriptionTextArea.disableProperty().bind(Bindings.isNull(location));
		descriptionTextArea.textProperty().bindBidirectional(descriptionBinding);

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setOnAction(this::saveButtonClicked);
		saveButton.disableProperty().bind(saveBinding);

		eventListView = new ListView<>();
		eventListView.setMinWidth(400);
		eventListView.setCellFactory(lc -> new DisplayValueListCell<>());
		eventListView.disableProperty().bind(Bindings.isNull(location));
		eventListView.itemsProperty().bind(eventsBinding);

		setVgap(5);
		setHgap(5);

		GridPane.setColumnSpan(descriptionTextArea, 2);
		GridPane.setRowSpan(eventListView, 6);

		add(title, 0, 0);

		add(new Text("Name:"), 0, 1);
		add(nameTextField, 1, 1);

		add(new Text("Description:"), 0, 3);
		add(descriptionTextArea, 0, 4);

		add(saveButton, 1, 6);

		Text historyText = new Text("History:");
		historyText.setFont(new Font(20));

		add(historyText, 3, 0);
		add(eventListView, 3, 1);

		setHalignment(saveButton, HPos.RIGHT);
	}

	public Location getLocation()
	{
		return location.get();
	}

	public void setLocation(Location location)
	{
		this.location.set(location);
	}

	public ObjectProperty<Location> locationProperty()
	{
		return location;
	}

	private void saveButtonClicked(ActionEvent event)
	{
		executeWithTransaction(() ->
			{
				entityManager.persist(location.get());
			});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
