package gallican.view;

import java.util.stream.Collectors;

import gallican.model.Location;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

public class LocationTreeItem
	extends TreeItem<Location>
{
	public LocationTreeItem(Location location)
	{
		super(location);

		getChildren().addAll(
			location
				.getLocations()
				.stream()
				.map(l -> new LocationTreeItem(l))
				.collect(Collectors.toList()));

		location.locations().addListener(new ListChangeListener<Location>()
		{
			@Override
			public void onChanged(Change<? extends Location> c)
			{
				while (c.next())
				{
					if (c.wasAdded())
					{
						getChildren().add(new LocationTreeItem(c.getAddedSubList().get(0)));
					}
					else if (c.wasRemoved())
					{
						getChildren().removeIf(ti -> ti.getValue().equals(c.getRemoved().get(0)));
					}
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof LocationTreeItem)) return false;

		LocationTreeItem other = (LocationTreeItem)obj;

		return this.getValue().equals(other.getValue());
	}
}
