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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

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
    updateAll();
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

  /***************************************** updateAll *******************************************/
  public void updateAll()
  {
    // cause whole gantt to be redrawn and size to be re-calculated
    ScrolledComposite sc = (ScrolledComposite) getParent();
    sc.setMinSize( computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    m_plot.redraw();
    m_upperScale.redraw();
    m_lowerScale.redraw();
  }

  /**************************************** updatePlot *******************************************/
  public void updatePlot()
  {
    // cause gantt plot to be redrawn
    m_plot.redraw();
  }

  /*************************************** contextMenu *******************************************/
  public Menu contextMenu()
  {
    // build and return gantt content menu
    Menu contextMenu = new Menu( this );

    MenuItem zoomIn = new MenuItem( contextMenu, SWT.NONE );
    zoomIn.setText( "Zoom in" );
    zoomIn.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        setMsPP( (long) ( m_millisecondsPP / 1.414 ) );
        updateAll();
      }
    } );

    MenuItem zoomOut = new MenuItem( contextMenu, SWT.NONE );
    zoomOut.setText( "Zoom out" );
    zoomOut.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        setMsPP( (long) ( m_millisecondsPP * 1.414 ) );
        updateAll();
      }
    } );

    MenuItem zoomFit = new MenuItem( contextMenu, SWT.NONE );
    zoomFit.setText( "Zoom fit" );
    zoomFit.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        DateTime start = JPlanner.plan.stretch( JPlanner.plan.earliest(), GanttPlot.ganttStretch );
        DateTime end = JPlanner.plan.stretch( JPlanner.plan.end(), GanttPlot.ganttStretch );
        int width = Gantt.this.getParent().getClientArea().width;

        if ( start == null || end == null )
        {
          setEnd( new DateTime( m_start.milliseconds() + m_millisecondsPP * width ) );
          updateAll();
          return;
        }

        long margin = 1L + ( end.milliseconds() - start.milliseconds() ) / 16L;
        setStart( new DateTime( start.milliseconds() - margin ) );
        setEnd( new DateTime( end.milliseconds() + margin ) );
        setMsPP( ( m_end.milliseconds() - m_start.milliseconds() ) / width );
        updateAll();
      }
    } );

    new MenuItem( contextMenu, SWT.SEPARATOR );

    MenuItem upperScale = new MenuItem( contextMenu, SWT.CASCADE );
    upperScale.setText( "Upper scale" );
    upperScale.setMenu( scaleMenu( contextMenu, m_upperScale ) );

    MenuItem lowerScale = new MenuItem( contextMenu, SWT.CASCADE );
    lowerScale.setText( "Lower scale" );
    lowerScale.setMenu( scaleMenu( contextMenu, m_lowerScale ) );

    MenuItem nonWD = new MenuItem( contextMenu, SWT.NONE );
    nonWD.setText( "Non-working days" );

    MenuItem current = new MenuItem( contextMenu, SWT.NONE );
    current.setText( "Current date" );

    MenuItem upperSM = new MenuItem( contextMenu, SWT.NONE );
    upperSM.setText( "Upper scale mark" );

    MenuItem lowerSM = new MenuItem( contextMenu, SWT.NONE );
    lowerSM.setText( "Lower scale mark" );

    return contextMenu;
  }

  /***************************************** scaleMenu *******************************************/
  private Menu scaleMenu( Menu parent, GanttScale scale )
  {
    // build and return scale interval menu
    Menu scaleMenu = new Menu( parent );

    MenuItem year = new MenuItem( scaleMenu, SWT.CHECK );
    year.setText( "Year" );
    year.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        scale.setInterval( Interval.YEAR, "YYYY" );
        scale.redraw();
      }
    } );

    MenuItem halfYear = new MenuItem( scaleMenu, SWT.CHECK );
    halfYear.setText( "Half Year" );
    halfYear.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        scale.setInterval( Interval.HALFYEAR, "YYYY HN" );
        scale.redraw();
      }
    } );

    MenuItem quarterYear = new MenuItem( scaleMenu, SWT.CHECK );
    quarterYear.setText( "Quarter Year" );
    quarterYear.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        scale.setInterval( Interval.QUARTERYEAR, "YYYY QN" );
        scale.redraw();
      }
    } );

    MenuItem month = new MenuItem( scaleMenu, SWT.CHECK );
    month.setText( "Month" );
    month.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        scale.setInterval( Interval.MONTH, "MMM-YYYY" );
        scale.redraw();
      }
    } );

    MenuItem week = new MenuItem( scaleMenu, SWT.CHECK );
    week.setText( "Week" );

    MenuItem day = new MenuItem( scaleMenu, SWT.CHECK );
    day.setText( "Day" );

    new MenuItem( scaleMenu, SWT.SEPARATOR );

    MenuItem format = new MenuItem( scaleMenu, SWT.CHECK );
    format.setText( "Label format" );

    return scaleMenu;
  }

}
