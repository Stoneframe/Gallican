package gallican.view;

import gallican.model.Dirtyable;
import gallican.model.Named;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class NameListCell<T extends Named & Dirtyable>
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
			textProperty().bind(item.nameProperty());
			textFillProperty().bind(
				Bindings
					.when(item.dirtyProperty())
					.then(Color.RED)
					.otherwise(Color.BLACK));
		}
	}
}
