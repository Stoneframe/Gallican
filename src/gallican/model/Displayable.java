package gallican.model;

import javafx.beans.property.ReadOnlyStringProperty;

public interface Displayable
{
	public String getDisplayValue();

	public ReadOnlyStringProperty displayValueProperty();
}
