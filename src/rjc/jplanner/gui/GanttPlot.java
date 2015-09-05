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

  public static boolean ganttStretch;

  /**************************************** constructor ******************************************/
  public GanttPlot( Composite parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );

    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent event )
      {
        // update the gantt plot for the specified paint-event
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
      @Override
      public void handleLayerEvent( ILayerEvent event )
      {
        // on row resize redraw the plot 
        if ( event instanceof RowResizeEvent )
          redraw();

        // on table scroll redraw the plot (and table to avoid lag)
        if ( event instanceof ScrollEvent )
        {
          redraw();
          table.redraw();
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

  /********************************************** x **********************************************/
  private int x( Date date )
  {
    return (int) ( ( date.epochday() * DateTime.MILLISECONDS_IN_DAY - m_start.milliseconds() ) / m_millisecondsPP );
  }

  /****************************************** datetime *******************************************/
  private DateTime datetime( int x )
  {
    return m_start.plusMilliseconds( x * m_millisecondsPP );
  }

  /************************************* shadeNonWorkingDays *************************************/
  private void shadeNonWorkingDays( PaintEvent event )
  {
    // exit immediately if less than one pixel per 24H
    if ( DateTime.MILLISECONDS_IN_DAY / m_millisecondsPP < 1 )
      return;

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
    Date dateEnd = datetime( x + w ).date();
    int xs = -1;
    int xe = 0;

    // for each date check if working and shade accordingly
    gc.setBackground( JPlanner.gui.COLOR_GANTT_NONWORKING );
    do
    {
      if ( xs < 0 && !calendar.isWorking( date ) )
      {
        xs = x( date ) + 1;
        if ( xs < 0 )
          xs = 0;
      }

      if ( xs >= 0 && calendar.isWorking( date ) )
      {
        xe = x( date );
        gc.fillRectangle( xs, y, xe - xs + 1, h );
        xs = -1;
      }

      date = date.plusDays( 1 );
    }
    while ( date.epochday() <= dateEnd.epochday() );

    // shade any remaining non-working days
    if ( xs >= 0 )
      gc.fillRectangle( xs, y, this.getSize().x, h );
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
    // TODO Auto-generated method stub

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
