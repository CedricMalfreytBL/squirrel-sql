package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetColumnReader
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ResultSetColumnReader.class);

	private final static Long LONG_ZERO = new Long(0);
	private final static Double DOUBLE_ZERO = new Double(0);

	/** The <TT>ResultSet</TT> being read. */
	private final ResultSet _rs;

	/** Describes how to handle "blob" type data. */
	private final LargeResultSetObjectInfo _largeObjInfo;

	/** <TT>true</TT> if an error occured reading a column in th previous row. */
//	private boolean _errorOccured = false;

	/** <TT>true</TT> if the last column read had a value of SQL NULL. */
	private boolean _wasNull;

	/** Metadata for the <TT>ResultSet</TT>. */
	private ResultSetMetaData _rsmd;

	public ResultSetColumnReader(ResultSet rs) throws SQLException
	{
		this(rs, null, null);
	}

	public ResultSetColumnReader(ResultSet rs, LargeResultSetObjectInfo largeObjInfo)
		throws SQLException
	{
		this(rs, largeObjInfo, null);
	}

	public ResultSetColumnReader(ResultSet rs, int[] columnIndices) throws SQLException
	{
		this(rs, null, columnIndices);
	}

	public ResultSetColumnReader(ResultSet rs, LargeResultSetObjectInfo largeObjInfo,
							int[] columnIndices) throws SQLException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("ResultSet == null");
		}

		_rs = rs;
		_rsmd = rs.getMetaData();
		_largeObjInfo = largeObjInfo != null ? largeObjInfo : new LargeResultSetObjectInfo();
	}

	/**
	 * Position <TT>ResultSet</TT> to the next row.
	 * 
	 * @return	<TT>true</TT> if there is a &quot;next row&quot; to read.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public boolean next() throws SQLException
	{
		return _rs.next();
	}

	/**
	 * Retrieve the specifed column as a <TT>Boolean</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	Boolean value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Boolean getBoolean(int columnIdx) throws SQLException
	{
		Object obj = _rs.getObject(columnIdx);
		Boolean results = Boolean.FALSE;
		_wasNull = true;

		if (obj != null)
		{
			final int columnType = _rsmd.getColumnType(columnIdx);
			if (columnType != Types.NULL)
			{
				_wasNull = false;
				switch (columnType)
				{
					// TODO: When JDK1.4 is the earliest JDK supported
					// by Squirrel then remove the hardcoding of the
					// boolean data type.
					case Types.BIT:
					case 16:
//					case Types.BOOLEAN:
						if (obj instanceof Boolean)
						{
							results = (Boolean)obj;
						}
						else
						{
							if (obj instanceof Number)
							{
								if (((Number)obj).intValue() == 0)
								{
									results = Boolean.FALSE;
								}
								else
								{
									results = Boolean.TRUE;
								}
							}
							else
							{
								results = Boolean.valueOf(obj.toString());
							}
						}
						break;
		
					default:
						results = Boolean.valueOf(obj.toString());
						break;
				}
			}
		}

		return results;
	}

	/**
	 * Retrieve the specifed column as a <TT>Date</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	Time value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Date getDate(int columnIdx) throws SQLException
	{
		final Date results = _rs.getDate(columnIdx);
		_wasNull = results == null;
		return results;
	}

	/**
	 * Retrieve the specifed column as a <TT>Double</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	Double value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Double getDouble(int columnIdx) throws SQLException
	{
		Object obj = _rs.getObject(columnIdx);
		Double results = DOUBLE_ZERO;
		_wasNull = true;

		if (obj != null)
		{
			final int columnType = _rsmd.getColumnType(columnIdx);
			if (columnType != Types.NULL)
			{
				_wasNull = false;
				switch (columnType)
				{
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.REAL:
						if (obj instanceof Long)
						{
							results = (Double)obj;
						}
						else if (obj instanceof Number)
						{
							results = new Double(((Number)obj).doubleValue());
						}
						else
						{
							results = new Double(obj.toString());
						}
						break;
		
					default:
						results = new Double(obj.toString());
						break;
				}
			}
		}

		return results;
	}

	/**
	 * Retrieve the specifed column as a <TT>Long</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	long value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Long getLong(int columnIdx) throws SQLException
	{
		Object obj = _rs.getObject(columnIdx);
		Long results = LONG_ZERO;
		_wasNull = true;

		if (obj != null)
		{
			final int columnType = _rsmd.getColumnType(columnIdx);
			if (columnType != Types.NULL)
			{
				_wasNull = false;
				switch (columnType)
				{
					case Types.SMALLINT:
					case Types.TINYINT:
					case Types.INTEGER:
					case Types.BIGINT :
						if (obj instanceof Long)
						{
							results = (Long)obj;
						}
						else if (obj instanceof Number)
						{
							results = new Long(((Number)obj).longValue());
						}
						else
						{
							results = new Long(obj.toString());
						}
						break;
		
					default:
						results = new Long(obj.toString());
						break;
				}
			}
		}

		return results;
	}

	/**
	 * Retrieve the specifed column as an object.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	Object value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Object getObject(int columnIdx) throws SQLException
	{
		final Object results = _rs.getObject(columnIdx);
		_wasNull = results == null;
		return results;
	}

	/**
	 * Retrieve the specifed column as a string.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	String value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public String getString(int columnIdx) throws SQLException
	{
		final String results = _rs.getString(columnIdx);
		_wasNull = results == null;
		return results;
	}

	/**
	 * Retrieve the specifed column as a <TT>Time</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	Time value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Time getTime(int columnIdx) throws SQLException
	{
		final Time results = _rs.getTime(columnIdx);
		_wasNull = results == null;
		return results;
	}

	/**
	 * Retrieve the specifed column as a <TT>TimeStamp</TT>.
	 * 
	 * @param	columnIdx	Column index (starts at 1) of the column to be read.
	 * 
	 * @return	TimeStamp value of the specified column.
	 * 
	 * @throws	SQLException	SQL error occured.
	 */
	public Timestamp getTimeStamp(int columnIdx) throws SQLException
	{
		final Timestamp results = _rs.getTimestamp(columnIdx);
		_wasNull = results == null;
		return results;
	}

	/**
	 * Reports whether the last column read had a value of SQL NULL.
	 *
	 * @return	<TT>true</TT> if last column read was <TT>null</TT>.
	 */
	public boolean wasNull()
	{
		return _wasNull;
	}

	/**
	 * Retrieve whether an error occured reading a column in the previous row.
	 * 
	 * @return	<TT>true</TT> if error occured.
	 */
//	public boolean getColumnErrorInPreviousRow()
//	{
//		return _errorOccured;
//	}

//	private Object[] doRead()
//	{
//		Object[] row = new Object[_columnCount];
//		for (int i = 0; i < _columnCount; ++i)
//		{
//			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
//			try
//			{
//				final int columnType = _rsmd.getColumnType(idx);
//				switch (columnType)
//				{
//
//					case Types.DECIMAL:
//					case Types.NUMERIC:
//						row[i] = _rs.getObject(idx);
//						if (row[i] != null
//							&& !(row[i] instanceof BigDecimal))
//						{
//							if (row[i] instanceof Number)
//							{
//								Number nbr = (Number)row[i];
//								row[i] = new BigDecimal(nbr.doubleValue());
//							}
//							else
//							{
//								row[i] = new BigDecimal(row[i].toString());
//							}
//						}
//						break;
//
//
//					case Types.BINARY:
//						if (_largeObjInfo.getReadBinary())
//						{
//							row[i] = _rs.getString(idx);
//						}
//						else
//						{
//							row[i] = "<Binary>";
//						}
//						break;
//
//					case Types.VARBINARY:
//						if (_largeObjInfo.getReadVarBinary())
//						{
//							row[i] = _rs.getString(idx);
//						}
//						else
//						{
//							row[i] = "<VarBinary>";
//						}
//						break;
//
//					case Types.LONGVARBINARY:
//						if (_largeObjInfo.getReadLongVarBinary())
//						{
//							row[i] = _rs.getString(idx);
//						}
//						else
//						{
//							row[i] = "<LongVarBinary>";
//						}
//						break;
//
//					case Types.BLOB:
//						if (_largeObjInfo.getReadBlobs())
//						{
//							row[i] = null;
//							Blob blob = _rs.getBlob(idx);
//							if (blob != null)
//							{
//								int len = (int)blob.length();
//								if (len > 0)
//								{
//									int bytesToRead = len;
//									if (!_largeObjInfo.getReadCompleteBlobs())
//									{
//										bytesToRead = _largeObjInfo.getReadBlobsSize();
//									}
//									if (bytesToRead > len)
//									{
//										bytesToRead = len;
//									}
//									row[i] = new String(blob.getBytes(1, bytesToRead));
//								}
//							}
//						}
//						else
//						{
//							row[i] = "<Blob>";
//						}
//						break;
//
//					case Types.CLOB:
//						if (_largeObjInfo.getReadClobs())
//						{
//							row[i] = null;
//							Clob clob = _rs.getClob(idx);
//							if (clob != null)
//							{
//								int len = (int)clob.length();
//								if (len > 0)
//								{
//									int charsToRead = len;
//									if (!_largeObjInfo.getReadCompleteClobs())
//									{
//										charsToRead = _largeObjInfo.getReadClobsSize();
//									}
//									if (charsToRead > len)
//									{
//										charsToRead = len;
//									}
//									row[i] = clob.getSubString(1, charsToRead);
//								}
//							}
//						}
//						else
//						{
//							row[i] = "<Clob>";
//						}
//						break;
//
//					case Types.OTHER:
//						row[i] = _rs.getObject(idx);
//						break;
//
//					default :
//						row[i] = "<Unknown>";
//				}
//			}
//			catch (Throwable th)
//			{
//				_errorOccured = true;
//				row[i] = "<Error>";
//				s_log.error("Error reading column data", th);
//			}
//		}
//
//		return row;
//	}
}
