package gallican.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@Table(name = "Characters")
@Access(AccessType.PROPERTY)
public class Character
	implements
		Named
{
	private final LongProperty id = new SimpleLongProperty();
	private final StringProperty name = new SimpleStringProperty();

	private final ObjectProperty<Universe> universe = new SimpleObjectProperty<>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public long getId()
	{
		return idProperty().get();
	}

	public void setId(long id)
	{
		idProperty().set(id);
	}

	public LongProperty idProperty()
	{
		return id;
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
