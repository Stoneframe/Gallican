package gallican.view;

import java.time.LocalDate;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class AddEventDialog
	extends Dialog<Pair<LocalDate, String>>
{
	public AddEventDialog()
	{
		setTitle("Create New Event");
		setHeaderText("Set event date and name");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		DatePicker date = new DatePicker();
		TextField name = new TextField();

		grid.add(new Label("Date:"), 0, 0);
		grid.add(date, 1, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(name, 1, 1);

		getDialogPane().setContent(grid);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		BooleanBinding disableBinding = Bindings.or(
			Bindings.createBooleanBinding(() -> date.getValue() == null, date.valueProperty()),
			Bindings.createBooleanBinding(() -> name.getText().equals(""), name.textProperty()));

		Button okButton = (Button)getDialogPane().lookupButton(ButtonType.OK);
		okButton.disableProperty().bind(disableBinding);

		setResultConverter(dialogButton ->
			{
				if (dialogButton == ButtonType.OK)
				{
					return new Pair<>(date.getValue(), name.getText());
				}

				return null;
			});

		name.requestFocus();
	}
}
