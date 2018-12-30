package gallican.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import gallican.database.Table;

public class DatabaseInfo
	extends Table
{
	public static final String NAME = "DatabaseInfo";

	public DatabaseInfo(Connection connection)
	{
		super(connection, NAME, Column.createInt("Version"));
	}

	@Override
	public void create() throws SQLException
	{
		super.create();

		insert(1);
	}

	public int getVersion() throws SQLException
	{
		String sql = "select Version from " + name;

		try (Statement statement = connection.createStatement())
		{
			ResultSet result = statement.executeQuery(sql);

			result.next();

			return result.getInt(1);
		}
	}
}
