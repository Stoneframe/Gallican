package database;

public class Database
{
	public Database() throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}
	
	
}
