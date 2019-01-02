package gallican.model;

import javafx.beans.property.StringProperty;

public interface Named
{
	public String getName();

	public void setName(String name);

	public StringProperty nameProperty();
}
