package gallican.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import gallican.util.DBTablePrinter;

public class ApplicationInfo
{
	private static final String APPLICATION_INFO = "ApplicationInfo";

	private final Connection connection;

	public ApplicationInfo(Connection connection)
	{
		this.connection = connection;
	}

	public boolean exists() throws SQLException
	{
		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet result = metaData.getTables(null, null, APPLICATION_INFO.toUpperCase(), null);

		return result.next();
	}

	public void create() throws SQLException
	{
		String sql = String.format(
			"create table %s(Name varchar(20) not null primary key, Value varchar(250))",
			APPLICATION_INFO);

		try (Statement statement = connection.createStatement())
		{
			statement.execute(sql);
		}
	}

	public int getVersion() throws SQLException
	{
		String sql = String.format(
			"select Value from %s where Name = 'Version'",
			APPLICATION_INFO);

		try (Statement statement = connection.createStatement())
		{
			ResultSet result = statement.executeQuery(sql);

			result.next();

			return result.getInt(1);
		}
	}

	public void setVersion(int version) throws SQLException
	{
		String insertSql = String.format(
			"insert into %1$s (Name, Value) values ('Version', '%2$d')",
			APPLICATION_INFO,
			version);

		String updateSql = String.format(
			"update %s set Value = '%s' where Name = 'Version'",
			APPLICATION_INFO,
			version);

		try (Statement statement = connection.createStatement())
		{
			try
			{
				statement.execute(insertSql);
			}
			catch (SQLException e)
			{
				if (e.getSQLState().equals("23505"))
				{
					statement.execute(updateSql);
				}
			}
		}
	}

	public void print()
	{
		DBTablePrinter.printTable(connection, APPLICATION_INFO);
	}
}
