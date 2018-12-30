package gallican.database.tables;

import java.sql.Connection;

import gallican.database.Table;

public class DatabaseInfo
	extends Table
{
	public static final String NAME = "DatabaseInfo";

	public DatabaseInfo(Connection connection)
	{
		super(connection, NAME, new Column("Version", "int"));
	}
}
