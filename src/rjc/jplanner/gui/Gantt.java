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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
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
import rjc.jplanner.gui.editor.XAbstractCellEditor;
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
  public Gantt( ScrolledComposite parent, XNatTable table )
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

    m_lowerScale = new GanttScale( this );
    m_lowerScale.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    m_plot = new GanttPlot( this );
    m_plot.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
    m_plot.setTable( table );

    // set context menu
    Menu menu = contextMenu();
    m_upperScale.setMenu( menu );
    m_lowerScale.setMenu( menu );
    m_plot.setMenu( menu );

    // set default gantt parameters
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
    xsw.writeStartElement( XmlLabels.XML_GANTT );
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

    // close gantt element
    xsw.writeEndElement(); // XML_GANTT
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr ) throws XMLStreamException
  {
    // adopt gantt display data from XML stream, starting with the attributes
    setDefault();
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_START:
          setStart( new DateTime( xsr.getAttributeValue( i ) ) );
          break;
        case XmlLabels.XML_END:
          setEnd( new DateTime( xsr.getAttributeValue( i ) ) );
          break;
        case XmlLabels.XML_MSPP:
          setMsPP( Long.parseLong( xsr.getAttributeValue( i ) ) );
          break;
        default:
          JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }

    // read XML gantt data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of gantt data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_GANTT ) )
        return;

      // 
      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_UPPER_SCALE:
            m_upperScale.loadXML( xsr );
            break;
          case XmlLabels.XML_LOWER_SCALE:
            m_lowerScale.loadXML( xsr );
            break;
          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }

  }

  /***************************************** setDefault ******************************************/
  public void setDefault()
  {
    // set gantt to default parameters and trigger redraw
    setStart( new DateTime( JPlanner.plan.start().milliseconds() - 300000000L ) );
    setEnd( m_start.plusDays( 100 ) );
    setMsPP( 3600 * 6000 );
    m_upperScale.setInterval( Interval.MONTH, "MMM-YYYY" );
    m_lowerScale.setInterval( Interval.WEEK, "dd" );
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

  /******************************************** zoom *********************************************/
  private void zoom( double factor, int mid )
  {
    // calculate offset from origin to zoom mid-point
    ScrolledComposite sc = (ScrolledComposite) getParent();
    int offset = mid - sc.getOrigin().x;

    // zoom
    setMsPP( (long) ( m_millisecondsPP * factor ) );
    updateAll();

    // update origin to keep zoom mid-point stationary
    sc.setOrigin( (int) ( mid / factor - offset ), sc.getOrigin().y );
  }

  /*************************************** contextMenu *******************************************/
  public Menu contextMenu()
  {
    // build and return gantt content menu
    Menu contextMenu = new Menu( this );
    contextMenu.addMenuListener( new MenuAdapter()
    {
      @Override
      public void menuShown( MenuEvent event )
      {
        // if any table cell editing in progress, end it
        if ( XAbstractCellEditor.cellEditorInProgress != null )
          XAbstractCellEditor.cellEditorInProgress.endEditing();
      }
    } );

    // zoom in -------------------------------
    MenuItem zoomIn = new MenuItem( contextMenu, SWT.NONE );
    zoomIn.setText( "Zoom in" );
    zoomIn.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        // zoom around x-position of mouse pointer
        int x = Gantt.this.toControl( getDisplay().getCursorLocation() ).x;
        zoom( 1.0 / 1.414, x );
      }
    } );

    // zoom out -------------------------------
    MenuItem zoomOut = new MenuItem( contextMenu, SWT.NONE );
    zoomOut.setText( "Zoom out" );
    zoomOut.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        // zoom around x-position of mouse pointer
        int x = Gantt.this.toControl( getDisplay().getCursorLocation() ).x;
        zoom( 1.414, x );
      }
    } );

    // zoom fit -------------------------------
    MenuItem zoomFit = new MenuItem( contextMenu, SWT.NONE );
    zoomFit.setText( "Zoom fit" );
    zoomFit.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        // zoom to ensure whole plan from start to end is visible within gantt width
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

    // upper scale -------------------------------
    MenuItem upperScale = new MenuItem( contextMenu, SWT.CASCADE );
    upperScale.setText( "Upper scale" );
    upperScale.setMenu( scaleMenu( contextMenu, m_upperScale ) );

    // lower scale -------------------------------
    MenuItem lowerScale = new MenuItem( contextMenu, SWT.CASCADE );
    lowerScale.setText( "Lower scale" );
    lowerScale.setMenu( scaleMenu( contextMenu, m_lowerScale ) );

    // non working days -------------------------------
    MenuItem nonWD = new MenuItem( contextMenu, SWT.CHECK );
    nonWD.setText( "Non-working days" );
    nonWD.setEnabled( false );

    // current date -------------------------------
    MenuItem current = new MenuItem( contextMenu, SWT.CHECK );
    current.setText( "Current date" );
    current.setEnabled( false );

    // upper scale mark -------------------------------
    MenuItem upperSM = new MenuItem( contextMenu, SWT.CHECK );
    upperSM.setText( "Upper scale mark" );
    upperSM.setEnabled( false );

    // lower scale mark -------------------------------
    MenuItem lowerSM = new MenuItem( contextMenu, SWT.CHECK );
    lowerSM.setText( "Lower scale mark" );
    lowerSM.setEnabled( false );

    return contextMenu;
  }

  /***************************************** scaleMenu *******************************************/
  private Menu scaleMenu( Menu parent, GanttScale scale )
  {
    // build and return scale interval menu
    Menu scaleMenu = new Menu( parent );

    // year -------------------------------
    MenuItem year = new MenuItem( scaleMenu, SWT.CASCADE );
    year.setText( "Year" );
    year.setMenu( yearMenu( scaleMenu, scale ) );

    // half year -------------------------------
    MenuItem halfYear = new MenuItem( scaleMenu, SWT.CASCADE );
    halfYear.setText( "Half Year" );
    halfYear.setMenu( halfYearMenu( scaleMenu, scale ) );

    // quarter year -------------------------------
    MenuItem quarterYear = new MenuItem( scaleMenu, SWT.CASCADE );
    quarterYear.setText( "Quarter Year" );
    quarterYear.setMenu( quarterYearMenu( scaleMenu, scale ) );

    // month -------------------------------
    MenuItem month = new MenuItem( scaleMenu, SWT.CASCADE );
    month.setText( "Month" );
    month.setMenu( monthMenu( scaleMenu, scale ) );

    // week -------------------------------
    MenuItem week = new MenuItem( scaleMenu, SWT.CASCADE );
    week.setText( "Week" );
    week.setMenu( weekMenu( scaleMenu, scale ) );

    // day -------------------------------
    MenuItem day = new MenuItem( scaleMenu, SWT.CASCADE );
    day.setText( "Day" );
    day.setMenu( dayMenu( scaleMenu, scale ) );

    return scaleMenu;
  }

  /***************************************** menuItem ********************************************/
  private void menuItem( Menu menu, GanttScale scale, Interval interval, String format )
  {
    // build menu item for given interval and format
    MenuItem item = new MenuItem( menu, SWT.RADIO );
    item.setText( DateTime.now().toString( format ) + "\t(" + format + ")" );
    item.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        scale.setInterval( interval, format );
        scale.redraw();
      }
    } );
  }

  /************************************** menuItemFormat *****************************************/
  private void menuItemFormat( Menu menu, GanttScale scale )
  {
    // build menu item for user defined format
    new MenuItem( menu, SWT.SEPARATOR );
    MenuItem format = new MenuItem( menu, SWT.RADIO );
    format.setText( "Format..." );
    format.setEnabled( false );
  }

  /***************************************** yearMenu ********************************************/
  private Menu yearMenu( Menu parent, GanttScale scale )
  {
    // build and return year menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.YEAR, "yy" );
    menuItem( menu, scale, Interval.YEAR, "yyyy" );
    menuItemFormat( menu, scale );

    return menu;
  }

  /*************************************** halfYearMenu ******************************************/
  private Menu halfYearMenu( Menu parent, GanttScale scale )
  {
    // build and return year-half menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.HALFYEAR, "BB" );
    menuItem( menu, scale, Interval.HALFYEAR, "yyyy BB" );
    menuItemFormat( menu, scale );

    return menu;
  }

  /************************************* quarterYearMenu *****************************************/
  private Menu quarterYearMenu( Menu parent, GanttScale scale )
  {
    // build and return year-quarter menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.QUARTERYEAR, "QQQ" );
    menuItem( menu, scale, Interval.QUARTERYEAR, "yyyy QQQ" );
    menuItemFormat( menu, scale );

    return menu;
  }

  /**************************************** monthMenu ********************************************/
  private Menu monthMenu( Menu parent, GanttScale scale )
  {
    // build and return month menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.MONTH, "MM" );
    menuItem( menu, scale, Interval.MONTH, "MMM" );
    menuItem( menu, scale, Interval.MONTH, "MMMM" );
    menuItem( menu, scale, Interval.MONTH, "MMMMM" );
    menuItem( menu, scale, Interval.MONTH, "MMM-yy" );
    menuItem( menu, scale, Interval.MONTH, "MMM-yyyy" );
    menuItemFormat( menu, scale );

    return menu;
  }

  /***************************************** weekMenu ********************************************/
  private Menu weekMenu( Menu parent, GanttScale scale )
  {
    // build and return week menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.WEEK, "'W'w" );
    menuItem( menu, scale, Interval.WEEK, "dd" );
    menuItem( menu, scale, Interval.WEEK, "dd-MMM" );
    menuItemFormat( menu, scale );

    return menu;
  }

  /***************************************** dayMenu *********************************************/
  private Menu dayMenu( Menu parent, GanttScale scale )
  {
    // build and return day menu
    Menu menu = new Menu( parent );
    menuItem( menu, scale, Interval.DAY, "eee" );
    menuItem( menu, scale, Interval.DAY, "eeee" );
    menuItem( menu, scale, Interval.DAY, "eeeee" );
    menuItem( menu, scale, Interval.DAY, "dd" );
    menuItem( menu, scale, Interval.DAY, "dd-MMM" );
    menuItem( menu, scale, Interval.DAY, "dd/MM/yy" );
    menuItemFormat( menu, scale );

    return menu;
  }

}
