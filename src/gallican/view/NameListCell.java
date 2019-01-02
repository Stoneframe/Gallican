package gallican.view;

import gallican.model.Named;
import javafx.scene.control.ListCell;

public class NameListCell<T extends Named>
	extends ListCell<T>
{
	@Override
	protected void updateItem(T item, boolean empty)
	{
		super.updateItem(item, empty);

		textProperty().unbind();

		if (empty || item == null)
		{
			setText("");
		}
		else
		{
			textProperty().bind(item.nameProperty());
		}
	}
}
