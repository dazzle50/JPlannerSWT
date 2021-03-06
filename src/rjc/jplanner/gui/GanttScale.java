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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.DateTime.Interval;

/*************************************************************************************************/
/************************* GanttScale provides a scale for the gantt plot ************************/
/*************************************************************************************************/

public class GanttScale extends Canvas implements PaintListener
{
  private DateTime        m_start;
  private long            m_millisecondsPP;
  private Interval        m_interval;
  private String          m_format;

  final public static int GANTTSCALE_HEIGHT = 15;

  /**************************************** constructor ******************************************/
  public GanttScale( Gantt parent )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED );
    addPaintListener( this );
  }

  /**************************************** computeSize ******************************************/
  @Override
  public Point computeSize( int wHint, int hHint, boolean changed )
  {
    // only vertical size is important, as horizontally it stretches
    return new Point( 1, GANTTSCALE_HEIGHT );
  }

  /*************************************** paintControl ******************************************/
  @Override
  public void paintControl( PaintEvent event )
  {
    // paint gantt scale
    drawScale( event, true );
  }

  /********************************************** x **********************************************/
  private int x( DateTime dt )
  {
    return (int) ( ( dt.milliseconds() - m_start.milliseconds() ) / m_millisecondsPP );
  }

  /****************************************** datetime *******************************************/
  private DateTime datetime( int x )
  {
    return m_start.plusMilliseconds( x * m_millisecondsPP );
  }

  /****************************************** drawScale ******************************************/
  private void drawScale( PaintEvent event, boolean drawLabels )
  {
    // draw the lines and labels to show the scale
    int x = event.x;
    int y = event.y;
    int h = event.height;
    int w = event.width;
    GC gc = event.gc;

    // fill in background & draw line at bottom
    gc.setBackground( JPlanner.gui.COLOR_GANTT_NONWORKING );
    gc.fillRectangle( x, y, w, h );
    gc.setForeground( JPlanner.gui.COLOR_GANTT_DIVIDER );
    gc.drawLine( x, this.getSize().y - 1, x + w, this.getSize().y - 1 );

    // intervals so small don't draw interval lines or labels
    DateTime dt = new DateTime( 0 ).trunc( m_interval );
    if ( ( x( dt.plusInterval( m_interval ) ) - x( dt ) ) < 2 )
      return;

    // calculate the start & end of first internal
    DateTime dts = datetime( x ).trunc( m_interval );
    int xs = x( dts );
    DateTime dte = dts.plusInterval( m_interval );
    int xe = x( dte );

    // draw internal line and label
    gc.drawLine( xs, y, xs, y + h );
    gc.setForeground( JPlanner.gui.COLOR_BLACK );
    if ( drawLabels )
      drawLabel( gc, dts.toString( m_format ), xs, xe );

    // move through subsequent internals until end of paint area
    while ( xe < x + w )
    {
      dts = dte;
      xs = xe;
      dte = dts.plusInterval( m_interval );
      xe = x( dte );

      gc.setForeground( JPlanner.gui.COLOR_GANTT_DIVIDER );
      gc.drawLine( xs, y, xs, y + h );
      gc.setForeground( JPlanner.gui.COLOR_BLACK );
      if ( drawLabels && !drawLabel( gc, dts.toString( m_format ), xs, xe ) )
      {
        // scaling means label not legible so redraw whole gantt-scale without labels
        drawScale( event, false );
        return;
      }
    }

  }

  /****************************************** drawLabel ******************************************/
  private boolean drawLabel( GC gc, String label, int xs, int xe )
  {
    // draw the label between xs & xe scaling smaller if necessary
    Point labelSize = gc.stringExtent( label );
    int xOffset = ( xe - xs - labelSize.x ) / 2;
    int yOffset = ( getSize().y - labelSize.y ) / 2 - 1;

    if ( xOffset < 1 )
    {
      // scaling on x-axis needed to fit label
      float scale = ( xe - xs - 2.0f ) / labelSize.x;

      // if scaling too small means text won't be legible
      if ( scale < 0.35 )
        return false;

      // draw label with required scaling
      JPlanner.gui.TRANSFORM.scale( scale, 1.0f );
      gc.setTransform( JPlanner.gui.TRANSFORM );
      gc.drawString( label, (int) ( ( xs + 2 ) / scale ), yOffset, true );

      // reset scaling ready for next interval
      JPlanner.gui.TRANSFORM.identity();
      gc.setTransform( JPlanner.gui.TRANSFORM );
    }
    else
    {
      // no scaling needed to fit label
      gc.drawString( label, xs + xOffset, yOffset, true );
    }

    // return true to indicate text is legible
    return true;
  }

  /****************************************** setConfig ******************************************/
  public void setInterval( Interval interval, String format )
  {
    // set gantt-scale configuration
    m_interval = interval;
    m_format = format;
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

  /****************************************** writeXML *******************************************/
  public void writeXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write gantt-scale display data to XML stream
    xsw.writeAttribute( XmlLabels.XML_INTERVAL, m_interval.toString() );
    xsw.writeAttribute( XmlLabels.XML_FORMAT, m_format );
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr )
  {
    // adopt gantt-scale display data from XML stream
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_INTERVAL:
          m_interval = Interval.valueOf( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_FORMAT:
          m_format = xsr.getAttributeValue( i );
          break;
        default:
          JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }
  }

}
