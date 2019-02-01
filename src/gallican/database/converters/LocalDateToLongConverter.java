package gallican.database.converters;

import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateToLongConverter
	implements
		AttributeConverter<LocalDate, Long>
{
	@Override
	public Long convertToDatabaseColumn(LocalDate entityAttribute)
	{
		return entityAttribute.toEpochDay();
	}

	@Override
	public LocalDate convertToEntityAttribute(Long databaseColumn)
	{
		return LocalDate.ofEpochDay(databaseColumn);
	}
}
