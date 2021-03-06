package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
				"CREATE TABLE SA.LOCATIONS ("
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

			ResultSet result = statement.executeQuery("SELECT NAME FROM SA.UNIVERSES");

			while (result.next())
			{
				updateUniverseLocation(connection, result);
			}
		}
	}

	private void updateUniverseLocation(Connection connection, ResultSet result) throws SQLException
	{
		try (
			PreparedStatement insertStatement = connection.prepareStatement(
				"INSERT INTO SA.LOCATIONS (NAME) VALUES (?)",
				Statement.RETURN_GENERATED_KEYS);
			PreparedStatement updateStatement = connection.prepareStatement(
				"UPDATE SA.UNIVERSES SET LOCATION_ID = ? WHERE NAME = ?"))
		{
			String name = result.getString("name");

			insertStatement.setString(1, name);
			insertStatement.execute();

			ResultSet idResult = insertStatement.getGeneratedKeys();
			idResult.next();
			int id = idResult.getInt(1);

			updateStatement.setInt(1, id);
			updateStatement.setString(2, name);
			updateStatement.execute();
		}
	}
}
