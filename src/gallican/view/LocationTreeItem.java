package gallican.view;

import java.util.stream.Collectors;

import gallican.model.Location;
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
	}
}
