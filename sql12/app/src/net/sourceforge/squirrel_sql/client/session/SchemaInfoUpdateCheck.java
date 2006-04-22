package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.DatabaseMetaData;


/**
 * This class tries to update SchemaInfo after standard CREATE/ALTER statements.
 * This way Syntax higlightning and code completion are available just after
 * CREATE/ALTER statements were send to the DB.
 */
public class SchemaInfoUpdateCheck
{
   private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("CREATE\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_TABLE = Pattern.compile("ALTER\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_INSERT_INTO = Pattern.compile("INSERT\\s+INTO\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_VIEW = Pattern.compile("CREATE\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_VIEW = Pattern.compile("ALTER\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_PROCEDURE = Pattern.compile("CREATE\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_PROCEDURE = Pattern.compile("ALTER\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_CREATE_FUNCTION = Pattern.compile("CREATE\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_ALTER_FUNCTION = Pattern.compile("ALTER\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");


   private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("DROP\\s+TABLE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_DROP_VIEW = Pattern.compile("DROP\\s+VIEW\\s+([A-Z0-9_\\.\"]+)");

   private static final Pattern PATTERN_DROP_PROCEDURE = Pattern.compile("DROP\\s+PROCEDURE\\s+([A-Z0-9_\\.\"]+)");
   private static final Pattern PATTERN_DROP_FUNCTION = Pattern.compile("DROP\\s+FUNCTION\\s+([A-Z0-9_\\.\"]+)");


   private HashMap _updateDatabaseObjectInfos = new HashMap();
   private HashMap _dropTableSimpleNames = new HashMap();
   private HashMap _dropProcedureSimpleNames = new HashMap();


   private ISession _session;
   private SQLDatabaseMetaData _dmd;


   SchemaInfoUpdateCheck(ISession session)
   {
      _session = session;
      _dmd = _session.getSQLConnection().getSQLMetaData();
   }

   void addExecutionInfo(SQLExecutionInfo exInfo)
   {
      if(null == exInfo || null == exInfo.getSQL())
      {
         return;
      }

      TableInfo[] tis = getTableInfos(exInfo.getSQL());
      for (int i = 0; i < tis.length; i++)
      {
         _updateDatabaseObjectInfos.put(tis[i], tis[i]);
      }


      ProcedureInfo[] pi = getProcedureInfos(exInfo.getSQL());
      for (int i = 0; i < pi.length; i++)
      {
         _updateDatabaseObjectInfos.put(pi[i], pi[i]);
      }

      String dtsn = getDropTableSimpleName(exInfo.getSQL());
      if(null != dtsn)
      {
         _dropTableSimpleNames.put(dtsn, dtsn);
      }

      String dpsn = getDropProcedureSimpleName(exInfo.getSQL());
      if(null != dpsn)
      {
         _dropProcedureSimpleNames.put(dpsn, dpsn);
      }
   }

   private String getDropProcedureSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_DROP_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true)[0].getSimpleName();
      }

      matcher = PATTERN_DROP_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true)[0].getSimpleName();
      }

      return null;
   }

   private String getDropTableSimpleName(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_DROP_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", true)[0].getSimpleName();
      }

      matcher = PATTERN_DROP_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", true)[0].getSimpleName();
      }

      return null;

   }

   void flush()
   {
      if(30 < _updateDatabaseObjectInfos.size() + _dropTableSimpleNames.size() + _dropProcedureSimpleNames.size())
      {
         // reload complete SchemaInfo
         SQLDatabaseMetaData dmd = _session.getSQLConnection().getSQLMetaData();
         DatabaseObjectInfo sessionOI = new DatabaseObjectInfo(null, null, "SessionDummy", DatabaseObjectType.SESSION, dmd);
         _session.getSchemaInfo().reloadCache(sessionOI);

      }
      else
      {
         for(Iterator i = _updateDatabaseObjectInfos.keySet().iterator(); i.hasNext();)
         {
            IDatabaseObjectInfo doi = (IDatabaseObjectInfo) i.next();
            _session.getSchemaInfo().reloadCache(doi);
         }

         for(Iterator i = _dropTableSimpleNames.keySet().iterator(); i.hasNext();)
         {
            String simpleTableName = (String) i.next();
            _session.getSchemaInfo().cleanAndReloadCacheForSimpleTableName(simpleTableName);
         }

         for(Iterator i = _dropProcedureSimpleNames.keySet().iterator(); i.hasNext();)
         {
            String simpleProcName = (String) i.next();
            _session.getSchemaInfo().cleanAndReloadCacheForSimpleProcedureName(simpleProcName);
         }

      }

      if(0 < _updateDatabaseObjectInfos.size()  + _dropTableSimpleNames.size() + _dropProcedureSimpleNames.size())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               repaintSqlEditor();
            }
         });
      }

   }

   private void repaintSqlEditor()
   {
      BaseSessionInternalFrame activeSessionWindow = _session.getActiveSessionWindow();

      if (activeSessionWindow instanceof SQLInternalFrame)
      {
         ((SQLInternalFrame) activeSessionWindow).getSQLPanelAPI().getSQLEntryPanel().getTextComponent().repaint();
      }

      if (activeSessionWindow instanceof SessionInternalFrame)
      {
         ((SessionInternalFrame) activeSessionWindow).getSQLPanelAPI().getSQLEntryPanel().getTextComponent().repaint();
      }
   }


   private ProcedureInfo[] getProcedureInfos(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_ALTER_PROCEDURE.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true);
      }

      matcher = PATTERN_CREATE_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, false);
      }

      matcher = PATTERN_ALTER_FUNCTION.matcher(upperSql);
      if(matcher.find())
      {
         return createProcdureInfos(matcher, sql, true);
      }

      return new ProcedureInfo[0];


   }

   private ProcedureInfo[] createProcdureInfos(Matcher matcher, String sql, boolean isAlterOrDrop)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String proc = sql.substring(endIx - len, endIx);
      String[] splits = proc.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);

      if(isAlterOrDrop)
      {
         String buf = _session.getSchemaInfo().getCaseSensitiveProcedureName(simpleName);
         if(null != buf)
         {
            simpleName = buf;
         }
         return new ProcedureInfo[]{new ProcedureInfo(null, null, simpleName, null, DatabaseMetaData.procedureResultUnknown, _dmd)};
      }
      else
      {
         // For example DB2 stores all table names in upper case.
         // That's why we may not find table as it was written in the create statement.
         // So we try out the upper case name to.
         return new ProcedureInfo[]
            {
               new ProcedureInfo(null, null, simpleName, null, DatabaseMetaData.procedureResultUnknown, _dmd),
               new ProcedureInfo(null, null, simpleName.toUpperCase(), null, DatabaseMetaData.procedureResultUnknown, _dmd)
            };
      }
   }

   private String removeQuotes(String simpleName)
   {
      if(simpleName.startsWith("\""))
      {
         simpleName = simpleName.substring(1);
      }

      if(simpleName.endsWith("\""))
      {
         simpleName = simpleName.substring(0, simpleName.length() - 1);
      }

      return simpleName;
   }


   private TableInfo[] getTableInfos(String sql)
   {
      sql = sql.trim();
      String upperSql = sql.toUpperCase();

      Matcher matcher;

      matcher = PATTERN_CREATE_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", false);
      }

      matcher = PATTERN_ALTER_TABLE.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", true);
      }

      matcher = PATTERN_INSERT_INTO.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "TABLE", false);
      }

      matcher = PATTERN_CREATE_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", false);
      }

      matcher = PATTERN_ALTER_VIEW.matcher(upperSql);
      if(matcher.find())
      {
         return createTableInfos(matcher, sql, "VIEW", true);
      }

      return new TableInfo[0];


   }

   private TableInfo[] createTableInfos(Matcher matcher, String sql, String type, boolean isAlterOrDrop)
   {
      int endIx = matcher.end(1);
      int len = matcher.group(1).length();
      String table = sql.substring(endIx - len, endIx);
      String[] splits = table.split("\\.");
      String simpleName = splits[splits.length - 1];
      simpleName = removeQuotes(simpleName);

      if(isAlterOrDrop)
      {
         String buf = _session.getSchemaInfo().getCaseSensitiveTableName(simpleName);
         if(null != buf)
         {
            simpleName = buf;
         }
         return new TableInfo[]{new TableInfo(null, null, simpleName, type, null, _dmd)};
      }
      else
      {
         // For example DB2 stores all table names in upper case.
         // That's why we may not find table as it was written in the create statement.
         // So we try out the upper case name to.
         return new TableInfo[]
            {
               new TableInfo(null, null, simpleName, type, null, _dmd),
               new TableInfo(null, null, simpleName.toUpperCase(), type, null, _dmd)
            };
      }
   }

}
