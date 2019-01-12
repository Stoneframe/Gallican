package gallican.model;

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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleLongProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class EntityBase
	implements
		Dirtyable
{
	private final LongProperty id = new SimpleLongProperty();

	private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(false);
	private final ReadOnlyBooleanWrapper dirty = new ReadOnlyBooleanWrapper(false);

	private Property<?>[] properties;
	private int prevHashStatus;

	private BooleanBinding validBinding;
	private BooleanBinding dirtyBinding;

	protected void track(Property<?>... properties)
	{
		this.properties = properties;

		validBinding = Bindings.createBooleanBinding(this::checkIsValid, properties);
		dirtyBinding = Bindings.createBooleanBinding(this::checkIsDirty, properties);

		valid.bind(validBinding);
		dirty.bind(dirtyBinding);
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
	public boolean isValid()
	{
		return valid.get();
	}

	public ReadOnlyBooleanProperty validProperty()
	{
		return valid.getReadOnlyProperty();
	}

	@Transient
	public boolean isDirty()
	{
		return dirty.get();
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
		prevHashStatus = calculateHashStatus();

		if (validBinding != null) validBinding.invalidate();
		if (dirtyBinding != null) dirtyBinding.invalidate();
	}
	
	public abstract void dispose();

	protected abstract boolean checkIsValid();

	private boolean checkIsDirty()
	{
		return prevHashStatus != calculateHashStatus();
	}

	private int calculateHashStatus()
	{
		int hashCode = 0;

		for (Property<?> property : properties)
		{
			hashCode ^= property.getValue() != null
					? property.getValue().hashCode()
					: 0;
		}

		return hashCode;
	}
}
