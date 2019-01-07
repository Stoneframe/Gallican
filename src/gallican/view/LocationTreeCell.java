package gallican.view;

import gallican.model.Location;
import javafx.collections.ListChangeListener;

public class LocationTreeCell
	extends NamedTreeCell<Location>
	implements
		ListChangeListener<Location>
{
	private Location location;

	@Override
	protected void updateItem(Location item, boolean empty)
	{
		super.updateItem(item, empty);

		if (this.location != null)
		{
			this.location.locationsProperty().removeListener(this);
		}

		this.location = item;

		if (this.location != null)
		{
			this.location.locationsProperty().addListener(this);
		}
	}

	@Override
	public void onChanged(Change<? extends Location> c)
	{
		while (c.next())
		{
			if (c.wasAdded())
			{
				getTreeItem()
					.getChildren()
					.add(new LocationTreeItem(c.getAddedSubList().get(0)));
			}
			else if (c.wasRemoved())
			{
				getTreeItem()
					.getChildren()
					.removeIf(ti -> ti.getValue().equals(c.getRemoved().get(0)));
			}
		}
	}
}
