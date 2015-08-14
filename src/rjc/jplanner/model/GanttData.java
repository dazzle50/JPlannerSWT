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

  private static int          size = 7;

  /***************************************** constructor *****************************************/
  public GanttData()
  {
    // nothing to do
  }

  /***************************************** setMilestone ****************************************/
  public void setMilestone( DateTime dt )
  {
    // set gantt data for milestone
    m_start = dt;
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
    m_end = new ArrayList<DateTime>();
    m_end.add( end );
    m_value = new ArrayList<Double>();
    m_value.add( 1.0 );
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
  private void drawTaskBar( GC gc, int y, DateTime start, long msPP )
  {
    // TODO Auto-generated method stub
  }

  /***************************************** drawSummary *****************************************/
  private void drawSummary( GC gc, int y, DateTime start, long msPP )
  {
    // TODO Auto-generated method stub

  }

  /**************************************** drawMilestone ****************************************/
  private void drawMilestone( GC gc, int y, DateTime start, long msPP )
  {
    // draw diamond shaped milestone marker
    int x = x( m_start, start, msPP ) + 100;
    int h = size;

    int[] points = { x, y - h, x + h + 1, y, x, y - h + 1, x - h, y };

    gc.setBackground( JPlanner.gui.COLOR_RED );
    gc.fillPolygon( points );
  }
}
