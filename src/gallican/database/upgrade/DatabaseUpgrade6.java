package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUpgrade6
	implements
		DatabaseUpgrade
{
	@Override
	public int getVersion()
	{
		return 6;
	}

	@Override
	public void upgrade(Connection connection) throws SQLException
	{
		Map<Integer, Date> idToDateMap = new HashMap<>();

		try (Statement statement = connection.createStatement())
		{
			ResultSet result = statement.executeQuery("SELECT ID, DATE FROM SA.EVENTS");

			while (result.next())
			{
				int id = result.getInt("ID");
				Date date = result.getDate("DATE");

				idToDateMap.put(id, date);
			}

			statement.execute("ALTER TABLE SA.EVENTS DROP COLUMN DATE");
			statement.execute("ALTER TABLE SA.EVENTS ADD DATE BIGINT");

			for (int id : idToDateMap.keySet())
			{
				Date date = idToDateMap.get(id);

				try (
					PreparedStatement updateStatement = connection.prepareStatement(
						"update SA.EVENTS set DATE = ? WHERE ID = ?"))
				{
					long epochDay = LocalDate.parse(date.toString()).toEpochDay();

					updateStatement.setLong(1, epochDay);
					updateStatement.setInt(2, id);

					updateStatement.execute();
				}
			}

			statement.execute("ALTER TABLE SA.EVENTS ALTER DATE NOT NULL");
		}
	}
}
