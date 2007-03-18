package net.sourceforge.squirrel_sql.plugins.postgres;
/*
 * Copyright (C) 2007 Rob Manning
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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

/**
 * Some types that are created by the Informix plugin.
 * @author manningr
 *
 */
public interface IObjectTypes {

    DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Trigger");
    DatabaseObjectType VIEW_PARENT = DatabaseObjectType.createNewDatabaseObjectType("View");
    DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Indices");
    DatabaseObjectType SEQUENCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Sequences");    
}