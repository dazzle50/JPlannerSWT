/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlannerSWT                               *
 *                                                                        *
 *  This program is free software: you can redistribute it and/or modify  *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  This program is distributed in the hope that it will be useful,       *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with this program.  If not, see http://www.gnu.org/licenses/    *
 **************************************************************************/

package rjc.jplanner.gui;

import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.resize.event.RowResizeEvent;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.nebula.widgets.nattable.viewport.event.ScrollEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.table.XNatTable;
import rjc.jplanner.model.Calendar;
import rjc.jplanner.model.Date;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.GanttData;
import rjc.jplanner.model.Predecessors;
import rjc.jplanner.model.Predecessors.Predecessor;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/***************** GanttPlot provides a view of the plan tasks and dependencies ******************/
/*************************************************************************************************/

public class GanttPlot extends Composite
{
  private DateTime      m_start;
  private long          m_millisecondsPP;
  private XNatTable     m_table;

  private static int    m_taskHeight = 6;
  private static int    m_arrowSize  = 4;

  public static boolean ganttStretch;

  /**************************************** constructor ******************************************/
  public GanttPlot( Gantt parent )
  {
    // create composite
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );

    // add listener for painting the gantt plot
    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent event )
      {
        shadeNonWorkingDays( event );
        drawTasks( event );
        drawDependencies( event );
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /****************************************** setConfig ******************************************/
  public void setTable( XNatTable table )
  {
    // set gantt-plot associated table
    m_table = table;

    // add listener for table scrolling and row height changes
    m_table.viewport.addLayerListener( new ILayerListener()
    {
      int m_previousY = 0; // used to prevent redraws if y not changed

      @Override
      public void handleLayerEvent( ILayerEvent event )
      {
        // on row resize redraw the plot 
        if ( event instanceof RowResizeEvent )
          redraw();

        // on table scroll redraw the plot (and table to avoid lag)
        if ( event instanceof ScrollEvent )
        {
          ViewportLayer view = m_table.viewport;
          int newY = view.getStartYOfRowPosition( view.getRowPositionByIndex( 0 ) );
          if ( newY != m_previousY )
          {
            redraw();
            m_table.redraw();
            m_previousY = newY;
          }
        }
      }
    } );
  }

  /****************************************** setStart *******************************************/
  public void setStart( DateTime start )
  {
    m_start = start;
  }

  /****************************************** setMsPP ********************************************/
  public void setMsPP( long mspp )
  {
    m_millisecondsPP = mspp;
  }

  /************************************* shadeNonWorkingDays *************************************/
  private void shadeNonWorkingDays( PaintEvent event )
  {
    // shade the non-working days based on the plan default calendar
    int x = event.x;
    int y = event.y;
    int h = event.height;
    int w = event.width;
    GC gc = event.gc;

    // fill in white background
    gc.setBackground( JPlanner.gui.COLOR_GANTT_BACKGROUND );
    gc.fillRectangle( x, y, w, h );

    // calculate start-date and end-date etc
    Calendar calendar = JPlanner.plan.calendar();
    Date date = datetime( x - 1 ).date();
    int endEpoch = datetime( x + w ).date().epochday();
    int startShadeEpoch;

    // for each date check if working and shade accordingly
    gc.setBackground( JPlanner.gui.COLOR_GANTT_NONWORKING );
    do
    {
      // find start of non-working period
      if ( !calendar.isWorking( date ) )
      {
        startShadeEpoch = date.epochday();

        // find end of non-working period
        do
          date.increment();
        while ( date.epochday() <= endEpoch && !calendar.isWorking( date ) );

        // if width at least 1 pixel shade non-working period
        long width = ( date.epochday() - startShadeEpoch ) * DateTime.MILLISECONDS_IN_DAY / m_millisecondsPP;
        if ( width > 0L )
        {
          long xe = x( new DateTime( date.epochday() * DateTime.MILLISECONDS_IN_DAY ) );
          gc.fillRectangle( (int) ( xe - width ), y, (int) width, h );
        }
      }

      date.increment();
    }
    while ( date.epochday() <= endEpoch );
  }

  /****************************************** drawTasks ******************************************/
  private void drawTasks( PaintEvent event )
  {
    // draw tasks on gantt
    int y = event.y;
    int h = event.height;
    GC gc = event.gc;

    // if negative means area to be drawn is below bottom of table, so just return  
    int first = m_table.rowAt( y );
    if ( first < 0 )
      return;

    // loop from first row and draw gantt-data
    int numTasks = JPlanner.plan.tasksCount();
    for ( int row = first; row < numTasks; row++ )
    {
      // get row start-y and height
      int ry = m_table.rowY( row );
      int rh = m_table.rowHeight( row );

      // if task not null, draw task gantt-data
      Task task = JPlanner.plan.task( row );
      if ( task.isNull() )
        continue;
      drawTask( gc, ry + rh / 2, task.ganttData(), "TBD" );

      // if beyond area to be drawn, exit loop
      if ( ry + rh > y + h )
        break;

      // TODO also draw deadline
      /***
      if ( task->deadline() == XDateTime::NULL_DATETIME ) continue;
      int x = task->ganttData()->x( task->deadline(), m_start, m_minsPP );
      p->setPen( pen );
      p->drawLine( x, y-4, x, y+4 );
      p->drawLine( x-4, y, x, y+4 );
      p->drawLine( x+4, y, x, y+4 );
      ***/
    }

  }

  /*************************************** drawDependencies **************************************/
  private void drawDependencies( PaintEvent event )
  {
    // draw dependencies on gantt
    int y = event.y;
    int h = event.height;
    GC gc = event.gc;

    // for each task
    for ( int t = 0; t < JPlanner.plan.tasksCount(); t++ )
    {
      Task task = JPlanner.plan.task( t );
      if ( task.isNull() )
        continue;
      int thisY = m_table.getMiddleY( t );

      // for each predecessor on task
      Predecessors preds = task.predecessors();
      for ( int p = 0; p < preds.count(); p++ )
      {
        Predecessor pred = preds.get( p );
        int otherY = m_table.getMiddleY( pred.task.index() );

        switch ( pred.type )
        {
          case Predecessors.TYPE_START_FINISH:
            drawDependencySF( gc, x( pred.task.start() ), otherY, x( task.end() ), thisY );
            break;
          case Predecessors.TYPE_START_START:
            drawDependencySS( gc, x( pred.task.start() ), otherY, x( task.start() ), thisY );
            break;
          case Predecessors.TYPE_FINISH_FINISH:
            drawDependencyFF( gc, x( pred.task.end() ), otherY, x( task.end() ), thisY );
            break;
          case Predecessors.TYPE_FINISH_START:
            drawDependencyFS( gc, x( pred.task.end() ), otherY, x( task.start() ), thisY );
            break;
          default:
            throw new IllegalArgumentException( "Invalid predecessor type: " + pred.type );
        }
      }
    }

  }

  /*************************************** drawDependencyFS **************************************/
  private void drawDependencyFS( GC gc, int x1, int y1, int x2, int y2 )
  {
    // draw dependency from one task-finish to another task-start
    int sign = y1 > y2 ? -1 : 1;

    // if task-start after or equal task-finish can draw simple arrow
    if ( x2 >= x1 )
    {
      int x = x2 - x1 - 1;
      if ( x < 3 )
        x = 3;

      gc.drawLine( x1 + 1, y1, x1 + x, y1 );
      x++;
      drawArrow( gc, x1 + x, y1 + sign, x1 + x, y2 - sign * ( m_taskHeight + 1 ) );
      return;
    }

    // need to draw arrow double backing from later task-finish to earlier task-start
    gc.drawLine( x1 + 1, y1, x1 + 3, y1 );
    gc.drawLine( x1 + 4, y1 + sign, x1 + 4, y1 + sign * ( m_taskHeight + 3 ) );
    gc.drawLine( x1 + 3, y1 + sign * ( m_taskHeight + 4 ), x2 - 7, y1 + sign * ( m_taskHeight + 4 ) );
    gc.drawLine( x2 - 8, y1 + sign * ( m_taskHeight + 5 ), x2 - 8, y2 - sign );
    drawArrow( gc, x2 - 7, y2, x2 - 1, y2 );
  }

  /*************************************** drawDependencySF **************************************/
  private void drawDependencySF( GC gc, int x1, int y1, int x2, int y2 )
  {
    // draw dependency FINISH_START line on gantt

    gc.setForeground( JPlanner.gui.COLOR_ERROR );
    gc.drawLine( x1, y1, x2, y2 );
    gc.setForeground( JPlanner.gui.COLOR_NO_ERROR );
    JPlanner.trace( "Unhandled dependency x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 );
  }

  /*************************************** drawDependencySS **************************************/
  private void drawDependencySS( GC gc, int x1, int y1, int x2, int y2 )
  {
    // draw dependency FINISH_START line on gantt

    gc.setForeground( JPlanner.gui.COLOR_ERROR );
    gc.drawLine( x1, y1, x2, y2 );
    gc.setForeground( JPlanner.gui.COLOR_NO_ERROR );
    JPlanner.trace( "Unhandled dependency x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 );
  }

  /*************************************** drawDependencyFF **************************************/
  private void drawDependencyFF( GC gc, int x1, int y1, int x2, int y2 )
  {
    // draw dependency FINISH_START line on gantt

    gc.setForeground( JPlanner.gui.COLOR_ERROR );
    gc.drawLine( x1, y1, x2, y2 );
    gc.setForeground( JPlanner.gui.COLOR_NO_ERROR );
    JPlanner.trace( "Unhandled dependency x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 );
  }

  /****************************************** drawArrow ******************************************/
  private void drawArrow( GC gc, int x1, int y1, int x2, int y2 )
  {
    // draw line with arrow at end
    if ( x1 == x2 )
    {
      // vertical line and arrow
      gc.drawLine( x1, y1, x2, y2 );
      int sign = y1 > y2 ? -1 : 1;
      int y = y2 - sign * m_arrowSize;

      for ( int s = 1; s <= m_arrowSize; s++ )
      {
        y2 -= sign;
        gc.drawLine( x2 + s, y, x2 + s, y2 );
        gc.drawLine( x2 - s, y, x2 - s, y2 );
      }
    }
    else
    {
      // horizontal line and arrow
      gc.drawLine( x1, y1, x2, y2 );
      int sign = x1 > x2 ? -1 : 1;
      int x = x2 - sign * m_arrowSize;

      for ( int s = 1; s <= m_arrowSize; s++ )
      {
        x2 -= sign;
        gc.drawLine( x, y2 + s, x2, y2 + s );
        gc.drawLine( x, y2 - s, x2, y2 - s );
      }
    }
  }

  /****************************************** datetime *******************************************/
  private DateTime datetime( int x )
  {
    return m_start.plusMilliseconds( x * m_millisecondsPP );
  }

  /********************************************** x **********************************************/
  private int x( DateTime dt )
  {
    // return x-coordinate for stretched if needed date-time
    dt = JPlanner.plan.stretch( dt, ganttStretch );
    long dtMilliseconds = dt.milliseconds();
    long startMilliseconds = m_start.milliseconds();

    if ( dtMilliseconds > startMilliseconds )
      return (int) ( ( dtMilliseconds - startMilliseconds ) / m_millisecondsPP );
    return (int) ( ( startMilliseconds - dtMilliseconds ) / -m_millisecondsPP );
  }

  /****************************************** drawTask *******************************************/
  public void drawTask( GC gc, int y, GanttData gd, String label )
  {
    // if gantt-data start not valid, don't draw anything
    if ( gd.start == null )
      return;

    // if no gantt-data value, draw milestone, otherwise summary or task bar
    if ( gd.end == null )
      drawMilestone( gc, y, gd );
    else if ( gd.isSummary() )
      drawSummary( gc, y, gd );
    else
      drawTaskBar( gc, y, gd );

    // TODO draw label !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  }

  /***************************************** drawTaskBar *****************************************/
  private void drawTaskBar( GC gc, int ty, GanttData gd )
  {
    // determine scale to draw offset
    double scale = 0.0;
    for ( int period = 0; period < gd.value.size(); period++ )
      if ( gd.value.get( period ) > scale )
        scale = gd.value.get( period );
    scale *= m_taskHeight;

    // set pen and fill colours
    gc.setForeground( JPlanner.gui.COLOR_GANTT_TASK_EDGE );
    gc.setBackground( JPlanner.gui.COLOR_GANTT_TASK_FILL );

    // calc start position of task bar
    int tx = x( gd.start );
    int offset = (int) ( gd.value.get( 0 ) * scale );

    // draw front edge
    gc.drawLine( tx, ty + offset, tx, ty - offset );

    // for each period within task bar draw next section
    int newX, newOffset;
    for ( int period = 1; period < gd.value.size(); period++ )
    {
      newX = x( gd.end.get( period - 1 ) );
      newOffset = (int) ( gd.value.get( period ) * scale );
      if ( offset > 0 && newX > tx )
      {
        gc.fillRectangle( tx + 1, ty - offset + 1, newX - tx, offset + offset - 1 );
        gc.drawLine( tx, ty + offset, newX, ty + offset );
      }
      gc.drawLine( tx, ty - offset, newX, ty - offset );
      gc.drawLine( newX, ty + offset, newX, ty + newOffset );
      gc.drawLine( newX, ty - offset, newX, ty - newOffset );

      tx = newX;
      offset = newOffset;
    }

    // calc end position and draw edges and fill
    newX = x( gd.end.get( gd.end.size() - 1 ) );
    if ( offset > 0 && newX > tx )
    {
      gc.fillRectangle( tx + 1, ty - offset + 1, newX - tx - 1, offset + offset - 1 );
      gc.drawLine( tx, ty + offset, newX, ty + offset );
      gc.drawLine( newX, ty + offset, newX, ty - offset );
    }
    gc.drawLine( tx, ty - offset, newX, ty - offset );
  }

  /***************************************** drawSummary *****************************************/
  private void drawSummary( GC gc, int y, GanttData gd )
  {
    // draw summary
    int xs = x( gd.start );
    int xe = x( gd.end.get( 0 ) );
    int h = m_taskHeight;
    int w = h;
    if ( w > xe - xs )
      w = xe - xs;

    int[] point1 = { xs + w, y, xs, y + h, xs, y - h, xe + 1, y - h, xe + 1, y + h, xe - w, y };
    int[] point2 = { xs + w, y, xs, y + h, xs, y - h };

    gc.setBackground( JPlanner.gui.COLOR_GANTT_SUMMARY );
    gc.fillPolygon( point1 );
    gc.fillPolygon( point2 );
  }

  /**************************************** drawMilestone ****************************************/
  private void drawMilestone( GC gc, int y, GanttData gd )
  {
    // draw diamond shaped milestone marker
    int x = x( gd.start );
    int h = m_taskHeight;

    int[] points = { x, y - h, x + h, y, x, y + h, x - h, y, x };

    gc.setBackground( JPlanner.gui.COLOR_GANTT_MILESTONE );
    gc.fillPolygon( points );
  }

}
