package gallican.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gallican.database.upgrade.DatabaseUpgrade;
import gallican.database.upgrade.DatabaseUpgrade2;
import gallican.database.upgrade.DatabaseUpgrade3;

public class DatabaseManager
{
	private static final int CURRENT_VERSION = 3;

	private final String javaxPersistenceJdbcUrl;

	private final List<DatabaseUpgrade> upgrades = Arrays.asList(
		new DatabaseUpgrade2(),
		new DatabaseUpgrade3());

	private ApplicationInfo applicationInfo;

	public DatabaseManager(String javaxPersistenceJdbcUrl) throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

		this.javaxPersistenceJdbcUrl = javaxPersistenceJdbcUrl;
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
				updateDatabase(applicationInfo, connection);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private Connection createConnection() throws SQLException
	{
		return DriverManager.getConnection(javaxPersistenceJdbcUrl);
	}

	private void updateDatabase(ApplicationInfo applicationInfo, Connection connection)
			throws SQLException
	{
		int version = applicationInfo.getVersion();

		List<DatabaseUpgrade> upgradesToRun = upgrades
			.stream()
			.filter(u -> u.getVersion() > version)
			.collect(Collectors.toList());

		for (DatabaseUpgrade upgrade : upgradesToRun)
		{
			try
			{
				connection.setAutoCommit(false);

				upgrade.upgrade(connection);

				applicationInfo.setVersion(upgrade.getVersion());

				connection.commit();
			}
			catch (SQLException e)
			{
				connection.rollback();

				throw e;
			}
		}
	}
}
