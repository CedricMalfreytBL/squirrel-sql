package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;

public interface DndColumn
{
   DndEvent getDndEvent();
   void setDndEvent(DndEvent dndEvent);

   Point getLocationInColumnTextArea();
}
