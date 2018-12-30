package gallican.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Table
{
	protected final String name;
	protected final Column[] columns;

	protected final Connection connection;

	protected Table(Connection connection, String name, Column... columns)
	{
		this.name = name;
		this.connection = connection;
		this.columns = columns;
	}

	public boolean exists() throws SQLException
	{
		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet result = metaData.getTables(null, null, name.toUpperCase(), null);

		return result.next();
	}

	public void create() throws SQLException
	{
		connection.createStatement().execute(
			String.format(
				"create table %s(%s)",
				name,
				String.join(
					", ",
					Arrays.stream(columns).map(c -> c.toString()).collect(Collectors.toList()))));
	}

	protected static class Column
	{
		private final String name;
		private final String type;

		public Column(String name, String type)
		{
			this.name = name;
			this.type = type;
		}

		public String getName()
		{
			return name;
		}

		public String getType()
		{
			return type;
		}
		
		@Override
		public String toString()
		{
			return getName() + " " + getType();
		}
	}
}
