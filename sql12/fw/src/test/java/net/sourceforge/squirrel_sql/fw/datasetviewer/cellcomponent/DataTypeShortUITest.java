/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * @author Stefan Willinger
 * 
 */
public class DataTypeShortUITest extends AbstractNumericDataTypeUITest {

	/**
	 * The main method is not used at all in the test - it is just here to allow
	 * for user interaction testing with the graphical component, which doesn't
	 * require launching SQuirreL.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		 JFrame frame = new DataTypeShortUITest().constructTestFrameInEDT();
		 frame.setVisible(true);
		 frame.pack();
	}
	

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractNumericDataTypeUITest#initClassUnderTests(net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition)
	 */
	@Override
	protected IDataTypeComponent initClassUnderTest(ColumnDisplayDefinition columnDisplayDefinition) {
		return new DataTypeShort(null, columnDisplayDefinition);
				
	}

}
