package gallican.database.upgrade;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseUpgrade
{
	int getVersion();

	void upgrade(Connection connection) throws SQLException;
}