package gallican.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

	public Character()
	{
		track(name, description, personality, powers);
	}

	@Column(name = "name")
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

	@Override
	public String toString()
	{
		return String.format("%d - Name: %s", getId(), getName());
	}
}
