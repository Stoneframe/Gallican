package gallican.view;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.util.StringConverter;

public final class SimpleStringConverter
	extends StringConverter<LocalDate>
{
	@Override
	public String toString(LocalDate object)
	{
		if (object == null)
		{
			return "";
		}
		else
		{
			return object.toString();
		}

	}

	@Override
	public LocalDate fromString(String string)
	{
		try
		{
			return LocalDate.parse(string);
		}
		catch (DateTimeParseException e)
		{
			return null;
		}
	}
}
