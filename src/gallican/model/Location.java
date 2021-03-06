package gallican.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

@Entity
@Table(name = "Locations")
public class Location
	extends EntityBase
	implements
		Named
{
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();
	private final ObjectProperty<Location> location = new SimpleObjectProperty<>();
	private final ListProperty<Location> locations = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	private final ObservableList<Event> events = FXCollections.observableArrayList();
	private final SortedList<Event> sortedEvents = new SortedList<>(
			events,
			(a, b) -> a.getDate().compareTo(b.getDate()));
	private final ListProperty<Event> eventsProperty = new SimpleListProperty<>(sortedEvents);

	public Location(String name)
	{
		this();

		setName(name);
	}

	private Location()
	{
		track(name, description);
	}

	public static List<Location> toList(Location location)
	{
		List<Location> list = new LinkedList<>();

		list.add(location);

		for (Location child : location.getLocations())
		{
			list.addAll(toList(child));
		}

		return list;
	}

	@Column(name = "name", nullable = false)
	@Override
	public String getName()
	{
		return name.get();
	}

	@Override
	public void setName(String name)
	{
		this.name.set(name);
	}

	@Override
	public StringProperty nameProperty()
	{
		return name;
	}

	@Column(name = "description")
	public String getDescription()
	{
		return description.get();
	}

	public void setDescription(String description)
	{
		this.description.set(description);
	}

	public StringProperty descriptionProperty()
	{
		return description;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public Universe getUniverse()
	{
		return universeProperty().get();
	}

	public void setUniverse(Universe universe)
	{
		universeProperty().set(universe);
	}

	public ObjectProperty<Universe> universeProperty()
	{
		return universe;
	}

	@ManyToOne
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

	public ObservableList<Location> locations()
	{
		return locations.get();
	}

	public void addLocation(Location location)
	{
		getLocations().add(location);
		location.setLocation(this);
	}

	public void removeLocation(Location location)
	{
		getLocations().remove(location);
		location.setLocation(null);
	}

	@OneToMany(mappedBy = "Location", cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	public List<Location> getLocations()
	{
		return locations.get();
	}

	void setLocations(List<Location> locations)
	{
		this.locations.set(FXCollections.observableArrayList(locations));
	}

	public ListProperty<Location> locationsProperty()
	{
		return locations;
	}

	public ObservableList<Event> events()
	{
		return sortedEvents;
	}

	public void addEvent(Event event)
	{
		event.setLocation(this);
		events.add(event);
	}

	public void removeEvent(Event event)
	{
		events.remove(event);
	}

	@OneToMany(mappedBy = "Location", cascade = CascadeType.ALL)
	List<Event> getEvents()
	{
		return events;
	}

	void setEvents(List<Event> events)
	{
		this.events.setAll(events);
	}

	public boolean hasEvents()
	{
		List<Location> locations = toList(this);

		return locations.stream().anyMatch(l -> !l.events().isEmpty());
	}

	public ListProperty<Event> eventsProperty()
	{
		return eventsProperty;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public void dispose()
	{

	}

	@Override
	protected boolean checkIsValid()
	{
		return getName() != null && !getName().isEmpty();
	}
}
