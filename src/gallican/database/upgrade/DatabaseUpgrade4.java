package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpgrade4
	implements
		DatabaseUpgrade
{
	@Override
	public int getVersion()
	{
		return 4;
	}

	@Override
	public void upgrade(Connection connection) throws SQLException
	{
		try (Statement statement = connection.createStatement())
		{
			statement.execute(
				"CREATE TABLE SA.EVENTS ("
						+ "	ID BIGINT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
						+ "	DATE DATE, "
						+ "	DESCRIPTION VARCHAR(255),"
						+ "	NAME VARCHAR(255),"
						+ "	LOCATION_ID BIGINT,"
						+ "	UNIVERSE_ID BIGINT"
						+ ")");

			statement.execute(
				"ALTER TABLE SA.EVENTS"
						+ "	ADD FOREIGN KEY (LOCATION_ID)"
						+ "	REFERENCES SA.LOCATIONS (ID)");

			statement.execute(
				"ALTER TABLE SA.EVENTS"
						+ "	ADD FOREIGN KEY (UNIVERSE_ID)"
						+ "	REFERENCES SA.UNIVERSES (ID)");
		}
	}
}
