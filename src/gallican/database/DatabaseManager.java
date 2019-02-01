package gallican.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.FileUtils;

import gallican.database.upgrade.DatabaseUpgrade;
import gallican.database.upgrade.DatabaseUpgrade2;
import gallican.database.upgrade.DatabaseUpgrade3;
import gallican.database.upgrade.DatabaseUpgrade4;
import gallican.database.upgrade.DatabaseUpgrade5;
import gallican.database.upgrade.DatabaseUpgrade6;

public class DatabaseManager
{
	private static final String PERSISTENCE_UNIT_NAME = "gallican";
	private static final String DATABASE_NAME = PERSISTENCE_UNIT_NAME + "Db";

	private static final int CURRENT_VERSION = 6;

	private final List<DatabaseUpgrade> upgrades = Arrays.asList(
		new DatabaseUpgrade2(),
		new DatabaseUpgrade3(),
		new DatabaseUpgrade4(),
		new DatabaseUpgrade5(),
		new DatabaseUpgrade6());

	public DatabaseManager() throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	public void backup() throws IOException
	{
		Path databasePath = Paths.get(getDatabasePath());

		if (Files.exists(databasePath))
		{
			Path gallicanPath = databasePath.getParent();
			Path backupDirectoryPath = gallicanPath.resolve("Backup");

			backupDatabase(databasePath, backupDirectoryPath);
			cleanBackup(backupDirectoryPath);
		}
	}

	public void setup()
	{
		try (Connection connection = createConnection())
		{
			ApplicationInfo applicationInfo = new ApplicationInfo(connection);

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

	public EntityManager createEntityManager()
	{
		Map<String, String> properties = new HashMap<>();

		properties.put("javax.persistence.jdbc.url", getJavaxPersistenceJdbcUrl());

		EntityManagerFactory factory =
				Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

		return factory.createEntityManager();
	}

	private Connection createConnection() throws SQLException
	{
		return DriverManager.getConnection(getJavaxPersistenceJdbcUrl());
	}

	private String getJavaxPersistenceJdbcUrl()
	{
		return "jdbc:derby:" + getDatabasePath() + ";create=true";
	}

	private String getDatabasePath()
	{
		return Paths
			.get(
				System.getProperty("user.home"),
				"AppData/Local/Gallican",
				DATABASE_NAME)
			.toString()
			.replace("\\", "/");
	}

	private void backupDatabase(Path databasePath, Path backupDirectoryPath) throws IOException
	{
		if (Files.notExists(backupDirectoryPath))
		{
			Files.createDirectory(backupDirectoryPath);
		}

		String backupName = String.format(
			"%s %s",
			DATABASE_NAME,
			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")));

		Path backupPath = backupDirectoryPath.resolve(backupName);

		FileUtils.copyDirectory(databasePath.toFile(), backupPath.toFile());
	}

	private void cleanBackup(Path backupDirectoryPath) throws IOException
	{
		Files
			.list(backupDirectoryPath)
			.sorted((a, b) -> b.getFileName().compareTo(a.getFileName()))
			.skip(5)
			.forEach(f ->
				{
					try
					{
						FileUtils.deleteDirectory(f.toFile());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				});
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
