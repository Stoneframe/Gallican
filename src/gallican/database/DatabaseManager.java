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
			else if (applicationInfo.getVersion() > CURRENT_VERSION)
			{
				System.err.println("Database version higher than application version.");
				System.exit(1);
			}
			else if (applicationInfo.getVersion() < CURRENT_VERSION)
			{
				updateDatabase();
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

	private void updateDatabase()
	{

	}
}