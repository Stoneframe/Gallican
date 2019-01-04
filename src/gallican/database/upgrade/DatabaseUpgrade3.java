package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpgrade3
	implements
		DatabaseUpgrade
{
	@Override
	public int getVersion()
	{
		return 3;
	}

	@Override
	public void upgrade(Connection connection) throws SQLException
	{
		try (Statement statement = connection.createStatement())
		{
			statement.execute(
				"create table SA.LOCATIONS ("
						+ "	ID BIGINT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
						+ "	NAME VARCHAR(255),"
						+ " LOCATION_ID BIGINT,"
						+ "	DESCRIPTION VARCHAR(1000))");

			statement.execute(
				"ALTER TABLE SA.LOCATIONS"
						+ "	ADD FOREIGN KEY (LOCATION_ID)"
						+ "	REFERENCES SA.LOCATIONS (ID)");

			statement.execute(
				"ALTER TABLE SA.UNIVERSES"
						+ " ADD COLUMN LOCATION_ID BIGINT");

			statement.execute(
				"ALTER TABLE SA.UNIVERSES"
						+ "	ADD FOREIGN KEY (LOCATION_ID)"
						+ "	REFERENCES SA.LOCATIONS (ID)");
		}
	}
}
