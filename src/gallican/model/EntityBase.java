package gallican.model;

import java.util.Arrays;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;

import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleLongProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class EntityBase
{
	private final LongProperty id = new SimpleLongProperty();

	private final ReadOnlyBooleanWrapper dirty = new ReadOnlyBooleanWrapper(false);

	protected void track(Property<?>... properties)
	{
		Arrays.stream(properties).forEach(p -> p.addListener(o -> setDirty(true)));
	}

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

	@Transient
	public boolean isDirty()
	{
		return dirtyProperty().get();
	}

	public ReadOnlyBooleanProperty dirtyProperty()
	{
		return dirty.getReadOnlyProperty();
	}

	@PostLoad
	@PostUpdate
	@PostPersist
	public void reset()
	{
		setDirty(false);
	}

	private void setDirty(boolean dirty)
	{
		this.dirty.set(dirty);
	}
}
