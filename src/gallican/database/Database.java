package gallican.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import gallican.database.tables.DatabaseInfo;

public class Database
{
	private final String name;

	private DatabaseInfo databaseInfo;

	public Database(String name) throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

		this.name = name;
	}

	public void setup()
	{
		try
		{
			Connection connection = createConnection();

			databaseInfo = new DatabaseInfo(connection);

			if (!databaseInfo.exists())
			{
				databaseInfo.create();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private Connection createConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
	}
}
