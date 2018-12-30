package gallican;

import gallican.database.DatabaseManager;

public class Gallican
{
	public static void main(String[] args) throws ClassNotFoundException
	{
		DatabaseManager database = new DatabaseManager("gallicanDB");

		database.setup();
	}
}
