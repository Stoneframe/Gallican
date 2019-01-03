package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpgrade2
	implements
		DatabaseUpgrade
{
	@Override
	public int getVersion()
	{
		return 2;
	}

	@Override
	public void upgrade(Connection connection) throws SQLException
	{
		try (Statement statement = connection.createStatement())
		{
			statement.execute("alter table SA.CHARACTERS add DESCRIPTION varchar(1000)");
			statement.execute("alter table SA.CHARACTERS add PERSONALITY varchar(1000)");
			statement.execute("alter table SA.CHARACTERS add POWERS varchar(1000)");
		}
	}
}
