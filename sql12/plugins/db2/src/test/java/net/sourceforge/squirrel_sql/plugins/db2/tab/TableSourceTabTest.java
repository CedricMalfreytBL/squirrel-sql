/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.db2.tab;


import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.junit.Before;

public class TableSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new TableSourceTab(HINT, STMT_SEP, true);
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		super.mockDatabaseObjectInfo = mockTableInfo;
   	expect(mockDatabaseObjectInfo.getDatabaseObjectType()).andStubReturn(DatabaseObjectType.TABLE);
   	expect(mockTableInfo.getType()).andStubReturn("MATERIALIZED");
   	
	}

}
