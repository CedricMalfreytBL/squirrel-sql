package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

/**
 * This is a <TT>IDataSetViewerDestination</TT> that doesn't
 * actually display the data, it simply stores it for future use.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataSetListModel implements IDataSetModel {
	/** Event types. */
	private interface IEventTypes {
		int ALL_ROWS_ADDED = 1;
		int MOVE_TO_TOP = 2;
	}

	/**
	 * Data. Each element is an array of objects. Each object is
	 * the data for a column.
	 */
	private List _data = new ArrayList();

	/** Column headings. */
	private ColumnDisplayDefinition[] _hdgs = new ColumnDisplayDefinition[0];

	/** If <TT>true</TT> column headings should be shown. */
	private boolean _showHeadings;

	/** Listeners for this object. */
	private EventListenerList _listenerList = new EventListenerList();

	/**
	 * Clear the data.
	 */
	public void clear() {
		_data.clear();
	}

	/**
	 * Specify the column headings to use.
	 * 
	 * @param	hdgs	Column headings to use.
	 */
	public void setColumnDefinitions(ColumnDisplayDefinition[] hdgs) {
		_hdgs = hdgs != null ? hdgs : new ColumnDisplayDefinition[0];
	}

	/**
	 * Get the column definitions.
	 * 
	 * @return	Column definitions.
	 */
	public ColumnDisplayDefinition[] getColumnDefinitions() {
		return _hdgs;
	}

	/**
	 * Specify whether to show the column headings.
	 * 
	 * @param	show	<TT>true</TT> if headings to be shown else <TT>false</TT>.
	 */
	public void showHeadings(boolean show) {
		_showHeadings = show;
	}

	/**
	 * Add a row.
	 * 
	 * @param	row		Array of objects specifying the row data.
	 */
	public void addRow(Object[] row) {
		_data.add(row);
	}

	/**
	 * Called once all rows have been added..
	 */
	public void allRowsAdded() {
		fireEvent(IEventTypes.ALL_ROWS_ADDED);
	}

	/**
	 * Indicates that the output display should scroll to the top.
	 */
	public void moveToTop() {
		fireEvent(IEventTypes.MOVE_TO_TOP);
	}

	/**
	 * Return number of rows in model.
	 * 
	 * @return	Number of rows.
	 */
	public int getRowCount() {
		return _data.size();
	}

	/**
	 * Return the data value for the specified cell.
	 * 
	 * @param	rowIndex	The row whose value is being retrieved.
	 * @param	columnIndex	The column whose value is being retrieved.
	 *
	 * @return	the data value for the specified cell.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return ((Object[])_data.get(rowIndex))[columnIndex];
	}

	/**
	 * Set the data value for the specified cell.
	 * 
	 * @param	value		The new value for the cell.
	 * @param	rowIndex	The row whose value is being set.
	 * @param	columnIndex	The column whose value is being set.
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		((Object[])_data.get(rowIndex))[columnIndex] = value;
	}

	/**
	 * Adds a listener for events in this model.
	 *
	 * @param	lis		<TT>DataSetModelListener</TT> that will be
	 *					notified when events occur in this model.
	 */
	public synchronized void addListener(DataSetModelListener lis) {
		_listenerList.add(DataSetModelListener.class, lis);
	}

	/**
	 * Removes an event listener fromthis model.
	 *
	 * @param	lis		<TT>DataSetModelListener</TT> to be removed.
	 */
	public synchronized void removeListener(DataSetModelListener lis) {
		_listenerList.remove(DataSetModelListener.class, lis);
	}

	/**
	 * Fire a <TT>DataSetModel</TT> event.
	 * 
	 * @param	eventType	Specifies the event type. @see IEventType.
	 */
	protected void fireEvent(int eventType) {
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		DataSetModelEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 ) {
			if (listeners[i] == DataSetModelListener.class) {
				// Lazily create the event:
				if (evt == null) {
					evt = new DataSetModelEvent(this);
				}
				DataSetModelListener lis = (DataSetModelListener)listeners[i + 1];
				switch (eventType) {
					case IEventTypes.ALL_ROWS_ADDED: {
						lis.allRowsAdded(evt);
						break;
					}
					case IEventTypes.MOVE_TO_TOP: {
						lis.moveToTop(evt);
						break;
					}
					default: {
						throw new IllegalArgumentException("Invalid eventTypes passed: " + eventType);
					}
				}
			}
		}
	}

}

