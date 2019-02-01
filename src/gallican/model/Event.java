package gallican.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gallican.database.converters.LocalDateToLongConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Entity
@Table(name = "Events")
public class Event
	extends EntityBase
	implements
		Named,
		Displayable
{
	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	private final ObjectProperty<Location> location = new SimpleObjectProperty<>();

	private final ListProperty<Character> characters = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	private final ReadOnlyStringWrapper displayValue = new ReadOnlyStringWrapper();

	public Event(LocalDate date, String name, Location location)
	{
		this();

		setDate(date);
		setName(name);
		assignToLocation(location);
	}

	private Event()
	{
		track(date, name, description, location, characters);

		displayValue.bind(
			Bindings.createStringBinding(() -> getDate() + " - " + getName(), date, name));
	}

	@Column(name = "Date", nullable = false)
	@Convert(converter = LocalDateToLongConverter.class)
	public LocalDate getDate()
	{
		return date.get();
	}

	public void setDate(LocalDate date)
	{
		this.date.set(date);
	}

	public ObjectProperty<LocalDate> dateProperty()
	{
		return date;
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

	@Override
	public String getDisplayValue()
	{
		return displayValue.get();
	}

	@Override
	public ReadOnlyStringProperty displayValueProperty()
	{
		return displayValue.getReadOnlyProperty();
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

	@ManyToOne
	@JoinColumn(name = "Universe_Id", nullable = false)
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

	public ObservableList<Character> characters()
	{
		return charactersProperty().get();
	}

	public void addCharacter(Character character)
	{
		getCharacters().add(character);
		character.getEvents().add(this);
	}

	public void removeCharacter(Character character)
	{
		getCharacters().remove(character);
		character.getEvents().remove(this);
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "EventCharacterMappings",
			joinColumns = @JoinColumn(name = "EventId"),
			inverseJoinColumns = @JoinColumn(name = "CharacterId"))
	List<Character> getCharacters()
	{
		return characters.get();
	}

	void setCharacters(List<Character> characters)
	{
		this.characters.set(FXCollections.observableArrayList(characters));
	}

	public ListProperty<Character> charactersProperty()
	{
		return characters;
	}

	@ManyToOne
	@JoinColumn(name = "Location_Id", nullable = false)
	public Location getLocation()
	{
		return location.get();
	}

	void setLocation(Location location)
	{
		this.location.set(location);
	}

	public void assignToLocation(Location location)
	{
		if (this.getLocation() != null)
		{
			this.getLocation().removeEvent(this);
		}

		location.addEvent(this);
	}

	public ObjectProperty<Location> locationProperty()
	{
		return location;
	}

	@Override
	public void dispose()
	{
		getLocation().removeEvent(this);
		new LinkedList<>(getCharacters()).forEach(c -> removeCharacter(c));
	}

	@Override
	protected boolean checkIsValid()
	{
		return (getDate() != null)
				&&
				(getName() != null && !getName().isEmpty())
				&&
				(getLocation() != null);
	}
}
