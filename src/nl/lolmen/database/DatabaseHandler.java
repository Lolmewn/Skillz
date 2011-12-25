package nl.lolmen.database;

import java.net.MalformedURLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Logger;

public abstract class DatabaseHandler {
	protected Logger log;
	protected final String PREFIX;
	protected final String DATABASE_PREFIX;
	protected Connection connection;
	protected enum Statements {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, 
		CREATE, ALTER, DROP, TRUNCATE, RENAME  
	}
	
	public DatabaseHandler(Logger log, String prefix, String dp) {
		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp;
		this.connection = null;
	}
	
	protected void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + this.DATABASE_PREFIX + toWrite);
		}
	}

	protected void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			} else {
				this.log.warning(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		}
	}
	
	abstract boolean initialize();

	abstract Connection open()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract void close();

	abstract Connection getConnection()
		throws MalformedURLException, InstantiationException, IllegalAccessException;
	
	abstract boolean checkConnection();

	abstract ResultSet query(String query)
		throws MalformedURLException, InstantiationException, IllegalAccessException;

	protected Statements getStatement(String query) {
		String trimmedQuery = query.trim();
		if (trimmedQuery.substring(0,6).equals("SELECT"))
			return Statements.SELECT;
		else if (trimmedQuery.substring(0,6).equals("INSERT"))
			return Statements.INSERT;
		else if (trimmedQuery.substring(0,6).equals("UPDATE"))
			return Statements.UPDATE;
		else if (trimmedQuery.substring(0,6).equals("DELETE"))
			return Statements.DELETE;
		else if (trimmedQuery.substring(0,6).equals("CREATE"))
			return Statements.CREATE;
		else if (trimmedQuery.substring(0,5).equals("ALTER"))
			return Statements.ALTER;
		else if (trimmedQuery.substring(0,4).equals("DROP"))
			return Statements.DROP;
		else if (trimmedQuery.substring(0,8).equals("TRUNCATE"))
			return Statements.TRUNCATE;
		else if (trimmedQuery.substring(0,6).equals("RENAME"))
			return Statements.RENAME;
		else if (trimmedQuery.substring(0,2).equals("DO"))
			return Statements.DO;
		else if (trimmedQuery.substring(0,7).equals("REPLACE"))
			return Statements.REPLACE;
		else if (trimmedQuery.substring(0,4).equals("LOAD"))
			return Statements.LOAD;
		else if (trimmedQuery.substring(0,7).equals("HANDLER"))
			return Statements.HANDLER;
		else if (trimmedQuery.substring(0,4).equals("CALL"))
			return Statements.CALL;
		else
			return Statements.SELECT;
	}

	abstract boolean createTable(String query);
	
	abstract boolean checkTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;

	abstract boolean wipeTable(String table)
		throws MalformedURLException, InstantiationException, IllegalAccessException;
}