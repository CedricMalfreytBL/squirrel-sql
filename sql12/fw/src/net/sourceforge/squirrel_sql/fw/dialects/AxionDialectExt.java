/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * A dialect delegate for the Axion database.
 */

public class AxionDialectExt extends CommonHibernateDialect implements HibernateDialect
{
	private class AxionDialectHelper extends Dialect {
		public AxionDialectHelper() {
			// Do not use Axion's bigint data type.
			// I get the following exception in my test:
			// org.axiondb.AxionException:
			// Invalid value "3074478078827346" for column
			// (BIGINT_TYPE_TABLE).BIGINT_COLUMN, expected numeric(20,10) :
			// data exception: numeric value out of range
			// Can someone please tell me why Axion expects big integers to be limited
			// to 20 precision and 10 scale?(Integers should have scale == 0, right?)
			// So an Axion bigint is limited to just 20 digits to the left of the
			// decimal point.
			// TODO: consider filing a bug report against Axion build M3.
			// 38 is the maximum precision for Axion's numeric type.
			registerColumnType(Types.BIGINT, "numeric($p,0)");
			registerColumnType(Types.BINARY, "binary($l)");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.BOOLEAN, "bit");
			registerColumnType(Types.CHAR, "char($l)");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "numeric($p,$s)");
			registerColumnType(Types.DOUBLE, "numeric($p,$s)");
			registerColumnType(Types.FLOAT, "numeric($p,$s)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "longvarbinary");
			registerColumnType(Types.LONGVARCHAR, "longvarchar");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			// Don't use "real" type. Axion sets the column size to 12 by default,
			// yet it can handle more precision. So data being copied from the real
			// column can potentially be larger than what the column claims to support.
			// This will be a problem for other databases that pay attention to the
			// column size.
			// TODO: Perhaps re-introduce the REAL type, but use the new
			// getPrecisionDigits to max out the precision.
			registerColumnType(Types.REAL, "numeric($p,$s)");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "varbinary($l)");
			registerColumnType(Types.VARCHAR, "varchar($l)");
		}
	}
	
	/** extended hibernate dialect used in this wrapper */
	private AxionDialectHelper _dialect = new AxionDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSchemasInTableDefinition()
	 */
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	public int getMaxPrecision(int dataType)
	{
		return 38;
	}

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect is design to work with.
	 */
	public String getDisplayName()
	{
		return "Axion";
	}

	/**
	 * Returns boolean value indicating whether or not this dialect supports the specified database
	 * product/version.
	 * 
	 * @param databaseProductName
	 *           the name of the database as reported by DatabaseMetaData.getDatabaseProductName()
	 * @param databaseProductVersion
	 *           the version of the database as reported by DatabaseMetaData.getDatabaseProductVersion()
	 * @return true if this dialect can be used for the specified product name and version; false otherwise.
	 */
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().startsWith("Axion"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports dropping columns from
	 * tables.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsDropColumn()
	{
		return true;
	}

	/**
	 * Returns the SQL that forms the command to drop the specified colum in the specified table.
	 * 
	 * @param tableName
	 *           the name of the table that has the column
	 * @param columnName
	 *           the name of the column to drop.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support dropping columns.
	 */
	public String getColumnDropSQL(String tableName, String columnName)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName);
	}

	/**
	 * Returns the SQL that forms the command to drop the specified table. If cascade contraints is supported
	 * by the dialect and cascadeConstraints is true, then a drop statement with cascade constraints clause
	 * will be formed.
	 * 
	 * @param iTableInfo
	 *           the table to drop
	 * @param cascadeConstraints
	 *           whether or not to drop any FKs that may reference the specified table.
	 * @return the drop SQL command.
	 */
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView)
	{
		String cascadeClause = DialectUtils.CASCADE_CLAUSE;
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, cascadeClause, false);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names. alter table tableName add constraint constraintName primary key (column [,column])
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti)
	{
		// Axion doesn't allow column alterations of the nullable attribute.
		// Fortunately, it doesn't require this to add a primary key.
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false) };
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports adding comments to columns.
	 * 
	 * @return true if column comments are supported; false otherwise.
	 */
	public boolean supportsColumnComment()
	{
		return false;
	}

	/**
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param info
	 *           information about the column such as type, name, etc.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column from
	 * null to not-null and vice versa.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	/**
	 * Returns the SQL used to alter the specified column to not allow null values
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports renaming columns.
	 * 
	 * @return true if the database supports changing the name of columns; false otherwise.
	 */
	public boolean supportsRenameColumn()
	{
		return true;
	}

	/**
	 * Returns the SQL that is used to change the column name. alter table tableName alter column oldColumnName
	 * rename to newColumnName
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause);
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports modifying a columns type.
	 * 
	 * @return true if supported; false otherwise
	 */
	public boolean supportsAlterColumnType()
	{
		return false;
	}

	/**
	 * Returns the SQL that is used to change the column type.
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support modifying column types.
	 */
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_TYPE_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column's
	 * default value.
	 * 
	 * @return true if the database supports modifying column defaults; false otherwise
	 */
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	/**
	 * Returns the SQL command to change the specified column's default value alter table test alter column
	 * PKCOL2 set default 0
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause);
	}

	/**
	 * Returns the SQL command to drop the specified table's primary key. alter table tableName drop primary
	 * key
	 * 
	 * @param pkName
	 *           the name of the primary key that should be dropped
	 * @param tableName
	 *           the name of the table whose primary key should be dropped
	 * @return
	 */
	public String getDropPrimaryKeySQL(String pkName, String tableName)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
	}

	/**
	 * Returns the SQL command to drop the specified table's foreign key constraint.
	 * 
	 * @param fkName
	 *           the name of the foreign key that should be dropped
	 * @param tableName
	 *           the name of the table whose foreign key should be dropped
	 * @return
	 */
	public String getDropForeignKeySQL(String fkName, String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
	}

	/**
	 * Returns the SQL command to create the specified table.
	 * 
	 * @param tables
	 *           the tables to get create statements for
	 * @param md
	 *           the metadata from the ISession
	 * @param prefs
	 *           preferences about how the resultant SQL commands should be formed.
	 * @param isJdbcOdbc
	 *           whether or not the connection is via JDBC-ODBC bridge.
	 * @return the SQL that is used to create the specified table
	 */
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	public DialectType getDialectType()
	{
		return DialectType.AXION;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		// TODO Auto-generated method stub		
		return null;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      DatabaseObjectQualifier, net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, Boolean, Boolean, Boolean, boolean, java.lang.String,
	 *      java.util.Collection, java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, String, java.lang.String[], boolean, java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateTableSQL(java.lang.String,
	 *      java.util.List, java.util.List,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier)
	 */
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(String,
	 *      java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getInsertIntoSQL(java.lang.String,
	 *      java.util.List, java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getUpdateSQL(java.lang.String,
	 *      java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[],
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences)
	 */
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAccessMethods()
	 */
	public boolean supportsAccessMethods()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddForeignKeyConstraint()
	 */
	public boolean supportsAddForeignKeyConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddUniqueConstraint()
	 */
	public boolean supportsAddUniqueConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAlterSequence()
	 */
	public boolean supportsAlterSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAutoIncrement()
	 */
	public boolean supportsAutoIncrement()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCheckOptionsForViews()
	 */
	public boolean supportsCheckOptionsForViews()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateIndex()
	 */
	public boolean supportsCreateIndex()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateSequence()
	 */
	public boolean supportsCreateSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateTable()
	 */
	public boolean supportsCreateTable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateView()
	 */
	public boolean supportsCreateView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropConstraint()
	 */
	public boolean supportsDropConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropIndex()
	 */
	public boolean supportsDropIndex()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropSequence()
	 */
	public boolean supportsDropSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsEmptyTables()
	 */
	public boolean supportsEmptyTables()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsIndexes()
	 */
	public boolean supportsIndexes()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsInsertInto()
	 */
	public boolean supportsInsertInto()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsMultipleRowInserts()
	 */
	public boolean supportsMultipleRowInserts()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameTable()
	 */
	public boolean supportsRenameTable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameView()
	 */
	public boolean supportsRenameView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequence()
	 */
	public boolean supportsSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequenceInformation()
	 */
	public boolean supportsSequenceInformation()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsTablespace()
	 */
	public boolean supportsTablespace()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsUpdate()
	 */
	public boolean supportsUpdate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		// TODO verify this is correct
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition() {
		// TODO verify this is correct
		return false;
	}
		
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getQualifiedIdentifier(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}