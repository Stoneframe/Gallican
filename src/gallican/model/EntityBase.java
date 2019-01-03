package gallican.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class EntityBase
{
	private final LongProperty id = new SimpleLongProperty();

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
}
