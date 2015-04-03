/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlanner                                  *
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.DateTime.Interval;

/*************************************************************************************************/
/*************** Gantt shows tasks in a gantt plot with upper & lower gantt scales ***************/
/*************************************************************************************************/

public class Gantt extends Composite
{
  private DateTime   m_start;
  private DateTime   m_end;
  private long       m_millisecondsPP;

  private GanttScale m_upperScale;
  private GanttScale m_lowerScale;
  private GanttPlot  m_chart;

  /**************************************** constructor ******************************************/
  public Gantt( Composite parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );
    setBackground( getBackground() ); // needed for some strange reason for no_redraw_resize to work!

    m_start = new DateTime( JPlanner.plan.start().milliseconds() - 300000000L );
    m_end = m_start.addDays( 100 );
    m_millisecondsPP = 3600 * 6000;

    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );

    m_upperScale = new GanttScale( this );
    m_upperScale.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_upperScale.setConfig( m_start, m_millisecondsPP, Interval.MONTH, "MMM-YYYY" );

    m_lowerScale = new GanttScale( this );
    m_lowerScale.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_lowerScale.setConfig( m_start, m_millisecondsPP, Interval.WEEK, "dd" );

    m_chart = new GanttPlot( this );
    m_chart.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
    m_chart.setConfig( m_start, m_millisecondsPP );
  }

  @Override
  public Point computeSize( int wHint, int hHint, boolean changed )
  {
    // only horizontal size is important, as vertically it stretches
    return new Point( (int) ( ( m_end.milliseconds() - m_start.milliseconds() ) / m_millisecondsPP ), 1 );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }
}
