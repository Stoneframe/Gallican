package gallican.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager
{
	private static final int CURRENT_VERSION = 1;

	private final String name;

	private ApplicationInfo applicationInfo;

	public DatabaseManager(String name) throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

		this.name = name;
	}

	public void setup()
	{
		try (Connection connection = createConnection())
		{
			applicationInfo = new ApplicationInfo(connection);

			if (!applicationInfo.exists())
			{
				applicationInfo.create();
				applicationInfo.setVersion(CURRENT_VERSION);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private Connection createConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:derby:database/" + name + ";create=true");
	}
}
