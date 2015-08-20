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

package rjc.jplanner.model;

import java.util.ArrayList;

import org.eclipse.swt.graphics.GC;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/************************** Data to support the drawing of a gantt task **************************/
/*************************************************************************************************/

public class GanttData
{
  private DateTime            m_start; // start time of gantt task
  private ArrayList<DateTime> m_end;   // list of changes until end
  private ArrayList<Double>   m_value; // list of value change (-ve means summary)

  private static int          size = 6;

  /***************************************** constructor *****************************************/
  public GanttData()
  {
    // nothing to do
  }

  /****************************************** setSummary *****************************************/
  public void setSummary( DateTime start, DateTime end )
  {
    // set gantt data for summary
    m_start = start;
    m_end = new ArrayList<DateTime>();
    m_end.add( end );
    m_value = new ArrayList<Double>();
    m_value.add( -1.0 );
  }

  /******************************************* setTask *******************************************/
  public void setTask( DateTime start, DateTime end )
  {
    // set gantt data for simple task
    m_start = start;

    // check if milestone or task bar needed
    if ( start.milliseconds() < end.milliseconds() )
    {
      m_end = new ArrayList<DateTime>();
      m_end.add( end );
      m_value = new ArrayList<Double>();
      m_value.add( 1.0 );
    }
    else
    {
      m_end = null;
      m_value = null;
    }

  }

  /********************************************** x **********************************************/
  private int x( DateTime dt, DateTime start, long msPP )
  {
    return (int) ( ( dt.milliseconds() - start.milliseconds() ) / msPP );
  }

  /****************************************** drawTask *******************************************/
  public void drawTask( GC gc, int y, DateTime start, long msPP, String label )
  {
    // if gantt-data start not valid, do not draw anything
    if ( m_start == null )
      return;

    // if no gantt-data value, draw milestone, otherwise summary or task bar
    if ( m_end == null )
      drawMilestone( gc, y, start, msPP );
    else if ( m_value.get( 0 ) < 0 )
      drawSummary( gc, y, start, msPP );
    else
      drawTaskBar( gc, y, start, msPP );

    // TODO draw label
  }

  /***************************************** drawTaskBar *****************************************/
  private void drawTaskBar( GC gc, int ty, DateTime start, long msPP )
  {
    // determine scale to draw offset
    double scale = 0.0;
    for ( int period = 0; period < m_value.size(); period++ )
      if ( m_value.get( period ) > scale )
        scale = m_value.get( period );
    scale *= size;

    // set pen and fill colours
    gc.setForeground( JPlanner.gui.COLOR_GANTT_TASK_EDGE );
    gc.setBackground( JPlanner.gui.COLOR_GANTT_TASK_FILL );

    // calc start position of task bar
    int tx = x( m_start, start, msPP );
    int offset = (int) ( m_value.get( 0 ) * scale );

    // draw front edge
    gc.drawLine( tx, ty + offset, tx, ty - offset );

    // for each period within task bar draw next section
    int newX, newOffset;
    for ( int period = 1; period < m_value.size(); period++ )
    {
      newX = x( m_end.get( period - 1 ), start, msPP );
      newOffset = (int) ( m_value.get( period ) * scale );
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
    newX = x( m_end.get( m_end.size() - 1 ), start, msPP );
    if ( offset > 0 && newX > tx )
    {
      gc.fillRectangle( tx + 1, ty - offset + 1, newX - tx - 1, offset + offset - 1 );
      gc.drawLine( tx, ty + offset, newX, ty + offset );
      gc.drawLine( newX, ty + offset, newX, ty - offset );
    }
    gc.drawLine( tx, ty - offset, newX, ty - offset );
  }

  /***************************************** drawSummary *****************************************/
  private void drawSummary( GC gc, int y, DateTime start, long msPP )
  {
    // draw summary
    int xs = x( m_start, start, msPP );
    int xe = x( m_end.get( 0 ), start, msPP );
    int h = size;
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
  private void drawMilestone( GC gc, int y, DateTime start, long msPP )
  {
    // draw diamond shaped milestone marker
    int x = x( m_start, start, msPP );
    int h = size;

    int[] points = { x, y - h, x + h, y, x, y + h, x - h, y, x };

    gc.setBackground( JPlanner.gui.COLOR_GANTT_MILESTONE );
    gc.fillPolygon( points );
  }
}
