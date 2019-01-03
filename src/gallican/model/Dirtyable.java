package gallican.model;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface Dirtyable
{
	public boolean isDirty();

	public ReadOnlyBooleanProperty dirtyProperty();
}
