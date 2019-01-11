package gallican.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "Universes")
public class Universe
	extends EntityBase
	implements
		Named
{
	private final StringProperty name = new SimpleStringProperty();

	private final ListProperty<Character> characters = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	private final ObservableList<Event> events = FXCollections.observableArrayList();
	private final SortedList<Event> sortedEvents = new SortedList<>(
			events,
			(a, b) -> a.getDate().compareTo(b.getDate()));

	private final ObjectProperty<Location> location = new SimpleObjectProperty<>();

	public Universe()
	{
		track(name);
	}

	@Column(name = "name")
	public String getName()
	{
		return nameProperty().get();
	}

	public void setName(String name)
	{
		nameProperty().set(name);
	}

	public StringProperty nameProperty()
	{
		return name;
	}

	public ObservableList<Character> characters()
	{
		return FXCollections.unmodifiableObservableList(charactersProperty().get());
	}

	public void addCharacter(Character character)
	{
		getCharacters().add(character);
		character.setUniverse(this);
	}

	public void removeCharacter(Character character)
	{
		getCharacters().remove(character);
		character.setUniverse(null);
	}

	@OneToMany(mappedBy = "Universe", orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "UniverseId")
	public List<Character> getCharacters()
	{
		return charactersProperty().get();
	}

	protected void setCharacters(List<Character> characters)
	{
		charactersProperty().set(FXCollections.observableArrayList(characters));
	}

	public ListProperty<Character> charactersProperty()
	{
		return characters;
	}

	public ObservableList<Event> events()
	{
		return sortedEvents;
	}

	public void addEvent(Event event)
	{
		events.add(event);
		event.setUniverse(this);
	}

	public void removeEvent(Event event)
	{
		events.remove(event);
		event.setUniverse(null);
	}

	@OneToMany(mappedBy = "Universe", orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "UniverseId")
	List<Event> getEvents()
	{
		return events;
	}

	void setEvents(List<Event> events)
	{
		this.events.setAll(events);
	}

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
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

	public boolean hasDirtyChildren()
	{
		return anyDirtyCharacters() || anyDirtyLocations();
	}

	@Override
	public String toString()
	{
		return getId() + " - " + getName();
	}

	private boolean anyDirtyCharacters()
	{
		return getCharacters().stream().anyMatch(c -> c.isDirty());
	}

	private boolean anyDirtyLocations()
	{
		return isLocationDirty(getLocation());
	}

	private boolean isLocationDirty(Location location)
	{
		return location.isDirty()
				|| location.getLocations().stream().anyMatch(l -> isLocationDirty(l));
	}
}
