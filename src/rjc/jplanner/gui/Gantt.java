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
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.gui.table.XNatTable;
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
  private GanttPlot  m_plot;

  /**************************************** constructor ******************************************/
  public Gantt( Composite parent, XNatTable table )
  {
    super( parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE );

    // needed for some strange reason for no_redraw_resize to work!
    setBackground( getBackground() );

    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );

    m_upperScale = new GanttScale( this );
    m_upperScale.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_upperScale.setInterval( Interval.MONTH, "MMM-YYYY" );

    m_lowerScale = new GanttScale( this );
    m_lowerScale.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_lowerScale.setInterval( Interval.WEEK, "dd" );

    m_plot = new GanttPlot( this );
    m_plot.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
    m_plot.setTable( table );

    // set default gantt start, end, and milliseconds-per-pixel
    setDefault();
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

  /****************************************** writeXML *******************************************/
  public void writeXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write gantt display data to XML stream
    xsw.writeAttribute( XmlLabels.XML_START, m_start.toString() );
    xsw.writeAttribute( XmlLabels.XML_END, m_end.toString() );
    xsw.writeAttribute( XmlLabels.XML_MSPP, Long.toString( m_millisecondsPP ) );
    xsw.writeAttribute( XmlLabels.XML_NONWORKING, "???" );
    xsw.writeAttribute( XmlLabels.XML_CURRENT, "???" );
    xsw.writeAttribute( XmlLabels.XML_STRETCH, Boolean.toString( GanttPlot.ganttStretch ) );

    // write upper-scale display data
    xsw.writeStartElement( XmlLabels.XML_UPPER_SCALE );
    m_upperScale.writeXML( xsw );
    xsw.writeEndElement(); // XML_UPPER_SCALE

    // write lower-scale display data
    xsw.writeStartElement( XmlLabels.XML_LOWER_SCALE );
    m_lowerScale.writeXML( xsw );
    xsw.writeEndElement(); // XML_LOWER_SCALE
  }

  /***************************************** setDefault ******************************************/
  public void setDefault()
  {
    // set gantt to default start/end/milliseconds-per-pixel and trigger redraw
    setStart( new DateTime( JPlanner.plan.start().milliseconds() - 300000000L ) );
    setEnd( m_start.plusDays( 100 ) );
    setMsPP( 3600 * 6000 );
    updateGantt();
  }

  /****************************************** setStart *******************************************/
  public void setStart( DateTime start )
  {
    // set start for gantt and scale/plot components
    m_start = start;
    m_upperScale.setStart( start );
    m_lowerScale.setStart( start );
    m_plot.setStart( start );
  }

  /******************************************* setEnd ********************************************/
  public void setEnd( DateTime end )
  {
    // set end for gantt, scale/plot components don't have end
    m_end = end;
  }

  /******************************************* setMsPP *******************************************/
  public void setMsPP( long mspp )
  {
    // set milliseconds-per-pixel for gantt and scale/plot components
    m_millisecondsPP = mspp;
    m_upperScale.setMsPP( mspp );
    m_lowerScale.setMsPP( mspp );
    m_plot.setMsPP( mspp );
  }

  /***************************************** setConfig *******************************************/
  public void setConfig( Gantt other )
  {
    // set this gantt to same config as other gantt
    setStart( other.m_start );
    setEnd( other.m_end );
    setMsPP( other.m_millisecondsPP );
  }

  /**************************************** updateGantt ******************************************/
  public void updateGantt()
  {
    // to force redraw/update of scrolling gantt, reset the parent scrolled-composite content
    ScrolledComposite sc = (ScrolledComposite) getParent();
    sc.setContent( this );
    sc.setMinSize( computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

}
