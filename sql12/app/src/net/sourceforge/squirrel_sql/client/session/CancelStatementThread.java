package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.Statement;
import java.sql.SQLException;

public class CancelStatementThread extends Thread
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(CancelStatementThread.class);

   private static final ILogger s_log = LoggerController.createLogger(CancelStatementThread.class);


   private Statement _stmt;
   private IMessageHandler _messageHandler;
   private boolean _threadFinished;
   private boolean _cancelSucceeded;
   private boolean _joinReturned;

   public CancelStatementThread(Statement stmt, IMessageHandler messageHandler)
   {
      _stmt = stmt;
      _messageHandler = messageHandler;
   }

   public boolean tryCancel()
   {
      try
      {
         start();
         join(1000);
         _joinReturned = true;


         if(false == _threadFinished)
         {
            // i18n[CancelStatementThread.cancelTimedOut=Failed to cancel statement within one second. Possibly your driver/database does not support canceling statements. If cancelling succeeds later you'll get a further message.]
            String msg = s_stringMgr.getString("CancelStatementThread.cancelTimedOut");
            _messageHandler.showErrorMessage(msg);
            s_log.error(msg);
            return false;
         }

         if(_cancelSucceeded)
         {
            // i18n[CancelStatementThread.cancelSucceeded=The database has been asked to cancel the statment]
            String msg = s_stringMgr.getString("CancelStatementThread.cancelSucceeded");
            _messageHandler.showMessage(msg);
            return true;
         }
         else
         {
            return false;
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void run()
   {
      try
      {
         // Code to simulate hanging calls to _stmt.cancel()
         //synchronized(this)
         //{
         //   try
         //   {
         //      this.wait(5000);
         //   }
         //   catch (InterruptedException e)
         //   {
         //      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         //   }
         //}

         _stmt.cancel();

         if(_joinReturned)
         {
            // i18n[CancelStatementThread.cancelSucceededLate=Canceling statement succeeded now. But took longer than one second.]
            String msg = s_stringMgr.getString("CancelStatementThread.cancelSucceededLate");
            _messageHandler.showMessage(msg);
         }
         else
         {
            _cancelSucceeded = true;
         }
      }
      catch (SQLException e)
      {
         // i18n[CancelStatementThread.cancelFailed=Failed to cancel statement. Propably the driver/RDDBMS does not support canceling statements. See logs for further details ({0})]
         String msg = s_stringMgr.getString("CancelStatementThread.cancelFailed", e);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, e);
      }
      finally
      {
         _threadFinished = true;
      }
   }
}
