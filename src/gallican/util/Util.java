package gallican.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class Util
{
	public static <P, C> C nullConditional(P object, Function<P, C> getValue)
	{
		return object != null ? getValue.apply(object) : null;
	}

	public static Optional<String> showTextInputDialog(String title, String header, String content)
	{
		TextInputDialog inputDialog = new TextInputDialog();

		inputDialog.setTitle(title);
		inputDialog.setHeaderText(header);
		inputDialog.setContentText(content);

		TextField inputField = inputDialog.getEditor();
		Button okButton = (Button)inputDialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.disableProperty().bind(
			Bindings.createBooleanBinding(
				() -> inputField.getText().equals(""),
				inputField.textProperty()));

		return inputDialog.showAndWait();
	}

	public static void showTextInputDialog(
			String title,
			String header,
			String content,
			Consumer<String> ifPresent)
	{
		Optional<String> result = showTextInputDialog(title, header, content);

		result.ifPresent(ifPresent);
	}

	public static <T, V> ObjectBinding<V> createNestedPropertyBinding(
			ReadOnlyObjectProperty<T> property,
			Function<T, V> getNestedValue)
	{
		return Bindings.createObjectBinding(
			() -> Util.nullConditional(property.get(), getNestedValue),
			property);
	}
}
