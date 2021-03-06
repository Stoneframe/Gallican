package gallican.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "Characters")
public class Character
	extends EntityBase
	implements
		Named
{
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();
	private final StringProperty personality = new SimpleStringProperty();
	private final StringProperty powers = new SimpleStringProperty();

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	private final ObservableList<Event> events = FXCollections.observableArrayList();
	private final SortedList<Event> sortedEvents = new SortedList<>(
			events,
			(a, b) -> a.getDate().compareTo(b.getDate()));
	private final ListProperty<Event> eventsProperty = new SimpleListProperty<>(sortedEvents);

	public Character()
	{
		track(name, description, personality, powers);
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

	@Column(name = "personality")
	public String getPersonality()
	{
		return personality.get();
	}

	public void setPersonality(String personality)
	{
		this.personality.set(personality);
	}

	public StringProperty personalityProperty()
	{
		return personality;
	}

	@Column(name = "powers")
	public String getPowers()
	{
		return powers.get();
	}

	public void setPowers(String powers)
	{
		this.powers.set(powers);
	}

	public StringProperty powersProperty()
	{
		return powers;
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

	public ObservableList<Event> events()
	{
		return sortedEvents;
	}

	@ManyToMany(mappedBy = "Characters")
	public List<Event> getEvents()
	{
		return events;
	}

	protected void setEvents(List<Event> events)
	{
		this.events.setAll(events);
	}

	public ListProperty<Event> eventsProperty()
	{
		return eventsProperty;
	}

	@Override
	public String toString()
	{
		return String.format("%d - Name: %s", getId(), getName());
	}

	@Override
	public void dispose()
	{
		new LinkedList<>(events()).forEach(e -> e.removeCharacter(this));
	}

	@Override
	protected boolean checkIsValid()
	{
		return getName() != null && !getName().isEmpty();
	}
}
