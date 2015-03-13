/**************************************************************************
 *  ######## WRITTEN USING WindowBuilder Editor ########                  *
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  http://code.google.com/p/jplanner/                                    *
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.model.Date;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.DateTime.Interval;

/*************************************************************************************************/
/************************* GanttScale provides a scale for the gantt plot ************************/
/*************************************************************************************************/

public class _GanttScale extends Composite
{
  private DateTime m_start;
  private long     m_millisecondsPP;
  private Interval m_interval;
  private String   m_format;

  /**************************************** constructor ******************************************/
  public _GanttScale( Composite parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );
    setDragDetect( false );

    m_start = new DateTime( 0 );
    m_millisecondsPP = 3600 * 2000;
    m_interval = Interval.WEEK;
    m_format = "dd MMM uuuu";

    addPaintListener( new PaintListener()
    {
      @Override
      public void paintControl( PaintEvent e )
      {
        // update the gantt plot for the specified paint-event
        drawScale( e );
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

  /****************************************** drawScale ******************************************/
  private void drawScale( PaintEvent event )
  {
    // draw the lines and labels to show the scale
    int x = event.x;
    int y = event.y;
    int h = event.height;
    int w = event.width;
    GC gc = event.gc;

    // fill in background & draw line at bottom
    gc.setBackground( MainWindow.GANTT_NONWORKING );
    gc.fillRectangle( x, y, w, h );
    gc.drawLine( x, this.getSize().y - 1, x + w, this.getSize().y - 1 );

    // calculate the start & end of first internal
    DateTime dts = datetime( x ).trunc( m_interval );
    int xs = x( dts );
    DateTime dte = dts.addInterval( m_interval );
    int xe = x( dte );

    // draw internal line and label
    gc.drawLine( xs, y, xs, y + h );
    drawLabel( gc, dts.toString( m_format ), xs, xe );

    // move through subsequent internals until end of paint area
    while ( xe < x + w )
    {
      dts = dte;
      xs = xe;
      dte = dts.addInterval( m_interval );
      xe = x( dte );

      gc.drawLine( xs, y, xs, y + h );
      drawLabel( gc, dts.toString( m_format ), xs, xe );
    }

  }

  /****************************************** drawLabel ******************************************/
  private void drawLabel( GC gc, String label, int xs, int xe )
  {
    // draw the label between xs & xe scaling smaller if necessary
    Point labelSize = gc.stringExtent( label );
    int xOffset = ( xe - xs - labelSize.x ) / 2;
    int yOffset = ( getSize().y - labelSize.y ) / 2;

    if ( xOffset < 2 )
    {
      // scaling on x-axis needed to fit label
      float scale = ( xe - xs - 3.0f ) / labelSize.x;
      MainWindow.TRANSFORM.scale( scale, 1.0f );
      gc.setTransform( MainWindow.TRANSFORM );

      xOffset = 2;
      gc.drawString( label, (int) ( ( xs + xOffset ) / scale ), yOffset );

      MainWindow.TRANSFORM.identity();
      gc.setTransform( MainWindow.TRANSFORM );
    }
    else
    {
      // no scaling needed to fit label
      gc.drawString( label, xs + xOffset, yOffset );
    }

  }
}
