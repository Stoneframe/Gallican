package gallican.view;

import gallican.model.Dirtyable;
import gallican.model.Displayable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class DisplayValueListCell<T extends Displayable & Dirtyable>
	extends ListCell<T>
{
	@Override
	protected void updateItem(T item, boolean empty)
	{
		super.updateItem(item, empty);

		textProperty().unbind();
		textFillProperty().unbind();

		if (empty || item == null)
		{
			setText("");
		}
		else
		{
			textProperty().bind(item.displayValueProperty());
			textFillProperty().bind(
				Bindings
					.when(item.dirtyProperty())
					.then(Color.RED)
					.otherwise(Color.BLACK));
		}
	}
}
