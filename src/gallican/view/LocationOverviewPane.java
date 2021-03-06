package gallican.view;

import javax.persistence.EntityManager;

import gallican.model.Location;
import gallican.model.Universe;
import gallican.util.Util;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class LocationOverviewPane
	extends BorderPane
{
	private final EntityManager entityManager;

	private final TreeView<Location> locationTreeView;

	private final LocationPane locationPane;

	private final FlowPane buttonPane;
	private final Button addButton;
	private final Button removeButton;

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	public LocationOverviewPane(EntityManager entityManager)
	{
		this.entityManager = entityManager;

		locationTreeView = new TreeView<>();
		locationTreeView.setCellFactory(tc -> new LocationTreeCell());

		universe.addListener(new ChangeListener<Universe>()
		{
			@Override
			public void changed(
					ObservableValue<? extends Universe> observable,
					Universe oldValue,
					Universe newValue)
			{
				if (newValue != null && newValue.getLocation() != null)
				{
					locationTreeView.setRoot(new LocationTreeItem(newValue.getLocation()));
				}
				else
				{
					locationTreeView.setRoot(null);
				}
			}
		});

		locationPane = new LocationPane(entityManager);
		locationPane.setPadding(new Insets(0, 0, 0, 10));
		locationPane.locationProperty().bind(
			Util.<TreeItem<Location>, Location> createNestedPropertyBinding(
				locationTreeView.getSelectionModel().selectedItemProperty(),
				ti -> ti.getValue()));

		addButton = new Button("Add");
		addButton.setMinWidth(70);
		addButton.disableProperty().bind(
			Bindings.or(
				Bindings.isNull(universe),
				Bindings.isNull(locationTreeView.getSelectionModel().selectedItemProperty())));
		addButton.setOnAction(this::addButtonClicked);

		removeButton = new Button("Remove");
		removeButton.setMinWidth(70);
		removeButton.disableProperty().bind(
			Bindings.or(
				Bindings.isNull(universe),
				Bindings.isNull(locationTreeView.getSelectionModel().selectedItemProperty())));
		removeButton.setOnAction(this::removeButtonClicked);

		buttonPane = new FlowPane(addButton, removeButton);
		buttonPane.setHgap(5);
		buttonPane.setPadding(new Insets(5, 0, 0, 0));

		setLeft(locationTreeView);
		setCenter(locationPane);
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
							Location location = new Location(name);

							TreeItem<Location> selectedItem = locationTreeView
								.getSelectionModel()
								.getSelectedItem();

							selectedItem.getValue().addLocation(location);

							TreeItem<Location> newItem = selectedItem
								.getChildren()
								.stream()
								.filter(i -> i.getValue().equals(location))
								.findFirst()
								.get();

							locationTreeView.getSelectionModel().select(newItem);
						});
				});
	}

	private void removeButtonClicked(ActionEvent event)
	{
		executeWithTransaction(() ->
			{
				Location location = locationTreeView
					.getSelectionModel()
					.getSelectedItem()
					.getValue();

				Location parent = location.getLocation();

				if (parent == null)
				{
					Util.showErrorDialog(
						"Error!",
						"Removal of Root Location",
						"You cannot remove the Root Location.");
				}
				else if (location.hasEvents())
				{
					Util.showErrorDialog(
						"Error!",
						"Removal of Location with Events",
						"You cannot remove this Location whilst it has Events associated with it."
								+ " The Events are either directly associated to this Location, or one of its descendants."
								+ " First either remove the events, or move them to another location.");
				}
				else
				{
					parent.removeLocation(location);

					locationTreeView.getSelectionModel().clearSelection();
				}
			});
	}

	private void executeWithTransaction(Runnable action)
	{
		entityManager.getTransaction().begin();

		action.run();

		entityManager.getTransaction().commit();
	}
}
