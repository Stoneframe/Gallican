package gallican;

import gallican.database.Database;

public class Gallican
{
	public static void main(String[] args) throws ClassNotFoundException
	{
		Database database = new Database("testdb");
		
		database.setup();
	}
}
