package gallican.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		String sql = String.format(
			"create table %s(%s)",
			name,
			String.join(
				", ",
				Arrays
					.stream(columns)
					.map(c -> c.toString())
					.collect(Collectors.toList())));

		try (Statement statement = connection.createStatement())
		{
			statement.execute(sql);
		}
	}

	public void insert(Object... values) throws SQLException
	{
		String sql = String.format(
			"insert into %s(%s) values (%s)",
			name,
			String.join(
				", ",
				Arrays.stream(columns).map(c -> c.getName()).collect(Collectors.toList())),
			String.join(
				", ",
				Arrays.stream(columns).map(c -> "?").collect(Collectors.toList())));

		try (PreparedStatement statement = connection.prepareStatement(sql))
		{
			for (int i = 0; i < columns.length; i++)
			{
				Column column = columns[i];

				switch (column.getType())
				{
					case Column.INT:
						statement.setInt(i + 1, (int)values[i]);
						break;

					case Column.STRING:
						statement.setString(i + 1, (String)values[i]);
						break;
				}
			}

			statement.execute();
		}
	}

	protected static class Column
	{
		public static final int INT = 0;
		public static final int STRING = 1;

		private final String name;
		private final int type;
		private final int length;

		public static Column createInt(String name)
		{
			return new Column(name, INT, 0);
		}

		public static Column createString(String name, int length)
		{
			return new Column(name, STRING, length);
		}

		private Column(String name, int type, int length)
		{
			this.name = name;
			this.type = type;
			this.length = length;
		}

		public String getName()
		{
			return name;
		}

		public int getType()
		{
			return type;
		}

		public int getLength()
		{
			return length;
		}

		@Override
		public String toString()
		{
			return getName() + " " + getTypeString();
		}

		private String getTypeString()
		{
			switch (getType())
			{
				case INT:
					return "int";
				case STRING:
					return "varchar(" + length + ")";
				default:
					throw new RuntimeException("Type not supported");
			}
		}
	}
}
