package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpgrade5
	implements
		DatabaseUpgrade
{
	@Override
	public int getVersion()
	{
		return 5;
	}

	@Override
	public void upgrade(Connection connection) throws SQLException
	{
		try (Statement statement = connection.createStatement())
		{
			statement.execute("ALTER TABLE SA.UNIVERSES ALTER NAME NOT NULL");
			statement.execute("ALTER TABLE SA.UNIVERSES ALTER LOCATION_ID NOT NULL");

			statement.execute("ALTER TABLE SA.CHARACTERS ALTER NAME NOT NULL");
			statement.execute("ALTER TABLE SA.CHARACTERS ALTER UNIVERSE_ID NOT NULL");

			statement.execute("ALTER TABLE SA.LOCATIONS ALTER NAME NOT NULL");

			statement.execute("ALTER TABLE SA.EVENTS ALTER DATE NOT NULL");
			statement.execute("ALTER TABLE SA.EVENTS ALTER NAME NOT NULL");
			statement.execute("ALTER TABLE SA.EVENTS ALTER LOCATION_ID NOT NULL");
			statement.execute("ALTER TABLE SA.EVENTS ALTER UNIVERSE_ID NOT NULL");
		}
	}
}
