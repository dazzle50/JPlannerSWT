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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Calendar;
import rjc.jplanner.model.Date;
import rjc.jplanner.model.DateTime;

/*************************************************************************************************/
/***************** GanttPlot provides a view of the plan tasks and dependencies ******************/
/*************************************************************************************************/

public class GanttPlot extends Composite
{
  private DateTime m_start;
  private long     m_millisecondsPP;

  /**************************************** constructor ******************************************/
  public GanttPlot( Composite parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );

    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent e )
      {
        // update the gantt plot for the specified paint-event
        shadeNonWorkingDays( e );
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /********************************************** x **********************************************/
  private int x( Date date )
  {
    return (int) ( ( date.epochday() * DateTime.MILLISECONDS_IN_DAY - m_start.milliseconds() ) / m_millisecondsPP );
  }

  /********************************************** x **********************************************/
  private int x( DateTime dt )
  {
    return (int) ( ( dt.milliseconds() - m_start.milliseconds() ) / m_millisecondsPP );
  }

  /****************************************** datetime *******************************************/
  private DateTime datetime( int x )
  {
    return m_start.addMilliseconds( x * m_millisecondsPP );
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

      date = date.addDays( 1 );
    }
    while ( date.epochday() <= dateEnd.epochday() );

    // shade any remaining non-working days
    if ( xs >= 0 )
      gc.fillRectangle( xs, y, this.getSize().x, h );
  }

  /****************************************** setConfig ******************************************/
  public void setConfig( DateTime start, long msPP )
  {
    // set gantt-plot configuration
    m_start = start;
    m_millisecondsPP = msPP;
  }
}
