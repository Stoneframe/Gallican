package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Location;
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

public class LocationPane
	extends GridPane
{
	private final EntityManager entityManager;

	private final Text title;

	private final TextField nameTextField;
	private final TextArea descriptionTextArea;

	private final Button saveButton;

	private final ObjectProperty<Location> location = new SimpleObjectProperty<>();

	public LocationPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		title = new Text("Location");
		title.setFont(new Font(20));

		nameTextField = new TextField();
		nameTextField.setMinWidth(200);
		nameTextField.disableProperty().bind(Bindings.isNull(location));

		descriptionTextArea = new TextArea();
		descriptionTextArea.setWrapText(true);
		descriptionTextArea.disableProperty().bind(Bindings.isNull(location));

		saveButton = new Button("Save");
		saveButton.setMinWidth(70);
		saveButton.setDisable(true);
		saveButton.setOnAction(this::saveButtonClicked);

		location.addListener(this::locationChanged);

		setVgap(5);
		setHgap(5);

		GridPane.setColumnSpan(descriptionTextArea, 2);

		add(title, 0, 0);

		add(new Text("Name:"), 0, 1);
		add(nameTextField, 1, 1);

		add(new Text("Description:"), 0, 3);
		add(descriptionTextArea, 0, 4);

		add(saveButton, 1, 6);

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

	private void locationChanged(
			ObservableValue<? extends Location> observable,
			Location oldValue,
			Location newValue)
	{
		if (oldValue != null)
		{
			nameTextField.textProperty().unbindBidirectional(oldValue.nameProperty());
			descriptionTextArea.textProperty().unbindBidirectional(oldValue.descriptionProperty());

			saveButton.disableProperty().unbind();
		}

		nameTextField.clear();

		if (newValue != null)
		{
			nameTextField.textProperty().bindBidirectional(newValue.nameProperty());
			descriptionTextArea.textProperty().bindBidirectional(newValue.descriptionProperty());

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
