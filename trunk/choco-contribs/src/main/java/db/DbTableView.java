/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package db;

public class DbTableView {

	public final static int INDEX_PK = 0;

	public final static int OFFSET_PK = 1;

	public final static String DEFAULT_TOKEN = "?";

	public final String name;

	protected final String[] attributes;

	protected String[] tokens;



	public DbTableView(String name, String[] attributes, String[] tokens) {
		this(name, attributes);
		this.tokens = tokens;
	}

	public DbTableView(String name, String... attributes) {
		super();
		this.name = name;
		this.attributes = attributes;
		if( name ==null || attributes == null || attributes.length == 0) {
			throw new DatabaseException("invalid table");
		}
	}

	public final String getName() {
		return name;
	}

	public String getAttribute(int i) {
		return attributes[i];
	}

	public String getJdbcToken(int i) {
		return tokens == null ? "?" : ":"+tokens[i];
	}

	public int size() {
		return attributes.length;	
	}



	public final String createCountValueQuery( int attr) {
		final StringBuilder b = new StringBuilder();
		b.append("SELECT COUNT(0) FROM ").append(name);
		b.append(" WHERE ").append(getAttribute(attr));
		b.append("=").append(getJdbcToken(attr));
		return new String(b);
	}

	public final String createCountPKQuery() {
		return createCountValueQuery(INDEX_PK);
	}

	private void appendEquality(StringBuilder b, int i) {
		b.append(getAttribute(i)).append("=").append(getJdbcToken(i));
	}

	public final String createfindPrimaryKeyQuery() {
		final StringBuilder b = new StringBuilder();
		b.append("SELECT ").append(getAttribute(INDEX_PK));
		b.append(" FROM ").append(name);
		b.append(" WHERE ");
		int i;
		for ( i = OFFSET_PK; i < size() - 1; i++) {
			appendEquality(b, i);
			b.append(" AND ");
		}
		appendEquality(b, i);
		return new String(b);
	}	

	public final String createSelectQuery(String selection) {
		final StringBuilder b = new StringBuilder();
		b.append("SELECT ").append(selection);
		b.append(" FROM ").append(name);
		if(attributes != null && attributes.length > 0) {
			b.append(" WHERE ");
			int i;
			for ( i = 0; i < size() - 1; i++) {
				appendEquality(b, i);
				b.append(" AND ");
			}
			appendEquality(b, i);
		}
		return new String(b);
	}

	public final String createInsertQuery(boolean generatedPK) {
		final StringBuilder b = new StringBuilder();
		b.append("INSERT INTO ").append(name);
		b.append(" ( ");
		final int offset = generatedPK ? OFFSET_PK : INDEX_PK;
		int i;
		for ( i = offset; i < size() - 1; i++) {
			b.append(getAttribute(i)).append(", ");
		}
		b.append(getAttribute(i));
		b.append(" ) VALUES ( ");
		for (i = offset; i < size() - 1; i++) {
			b.append(getJdbcToken(i)).append(", ");
		}
		b.append(getJdbcToken(i)).append(" )");
		return new String(b);
	}
}
