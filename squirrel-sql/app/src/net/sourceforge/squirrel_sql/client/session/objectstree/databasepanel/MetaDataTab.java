package net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel;
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
///import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
//import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
//import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
//import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.BaseProcedurePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.ResultSetPanel;

/**
 * This tab shows the meta data in the database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MetaDataTab extends BaseDatabasePanelTab {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Metadata";
		String HINT = "Show database metadata";
	}

	/** Component to be displayed. */
	private MyComponent _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle() {
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint() {
		return i18n.HINT;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent() {
		if (_comp == null) {
			_comp = new MyComponent();
		}
		return _comp;
	}

	/**
	 * Refresh the component displaying the data types.
	 */
	public synchronized void refreshComponent() throws IllegalStateException {
		ISession session = getSession();
		if (session == null) {
			throw new IllegalStateException("Null ISession");
		}
		((MyComponent)getComponent()).load(session);
	}

	/**
	 * Component for this tab.
	 */
	private class MyComponent extends JPanel {
		private boolean _fullyCreated = false;
		private IDataSet _ds;
		private DataSetViewer _viewer;
//		private IDataSetViewerDestination _dest;

		MyComponent() {
			super(new BorderLayout());
		}

		void load(ISession session) {
			try {
				// Lazily create the user interface.
				if (!_fullyCreated) {
					createUserInterface();
					_fullyCreated = true;
				}

				_viewer.show(_ds);

			} catch (Exception ex) {
				Logger log = session.getApplication().getLogger();
				log.showMessage(Logger.ILogTypes.ERROR, ex);
			}
		}

		private void createUserInterface() throws DataSetException {
			ISession session = getSession();

			String destClassName = session.getProperties().getMetaDataOutputClassName();
			//_dest = createDestination(destClassName);
			_viewer = new DataSetViewer();
			//_viewer.setDestination(_dest);
			_viewer.setDestination(destClassName);
			try {
				_ds = session.getSQLConnection().createMetaDataDataSet(session.getMessageHandler());
			} catch (Throwable th) {
				Logger log = session.getApplication().getLogger();
				log.showMessage(Logger.ILogTypes.ERROR, th);
			}
			add(new JScrollPane(_viewer.getDestinationComponent()), BorderLayout.CENTER);
		}
	}
}
