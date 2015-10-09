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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.gui.table.XNatTable;

/*************************************************************************************************/
/*************** Widget showing the Plan/Tasks&Gantt/Resources/Calendars/Days tabs ***************/
/*************************************************************************************************/

public class MainTabWidget extends TabFolder
{
  private PlanProperties m_planProperties;
  private PlanNotes      m_planNotes;
  private TabItem        m_tabTasks;
  private Gantt          m_gantt;
  private XSashForm      m_splitterTasksGantt;
  private XNatTable      m_tasksTable;
  private XNatTable      m_resourcesTable;
  private XNatTable      m_calendarsTable;
  private XNatTable      m_daysTable;

  /**************************************** constructor ******************************************/
  public MainTabWidget( Composite parent, boolean showPlanTab )
  {
    super( parent, SWT.NONE );

    // Plan tab - but only if requested
    if ( showPlanTab )
    {
      TabItem tabPlan = new TabItem( this, SWT.NONE );
      tabPlan.setText( "Plan" );

      XSashForm splitterPlanTab = new XSashForm( this, SWT.SMOOTH );
      tabPlan.setControl( splitterPlanTab );

      ScrolledComposite scrolledProperties = new ScrolledComposite( splitterPlanTab, SWT.H_SCROLL | SWT.V_SCROLL );
      scrolledProperties.setExpandHorizontal( true );
      scrolledProperties.setExpandVertical( true );
      m_planProperties = new PlanProperties( scrolledProperties, SWT.NONE );
      m_planProperties.setBackground( getBackground() );
      m_planProperties.updateFromPlan();
      scrolledProperties.setContent( m_planProperties );
      scrolledProperties.setMinSize( m_planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

      m_planNotes = new PlanNotes( splitterPlanTab, SWT.NONE );
      m_planNotes.setBackground( getBackground() );
      m_planNotes.updateFromPlan();
      splitterPlanTab.preferredLeftChildWidth = m_planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 1;
      splitterPlanTab.monitor( scrolledProperties, m_planNotes );
    }

    // Tasks & Gantt tab
    m_tabTasks = new TabItem( this, SWT.NONE );
    m_tabTasks.setText( "Tasks && Gantt" );

    m_splitterTasksGantt = new XSashForm( this, SWT.SMOOTH );
    m_tabTasks.setControl( m_splitterTasksGantt );

    m_tasksTable = new XNatTable( m_splitterTasksGantt, XNatTable.TableType.TASK );
    m_tasksTable.hideRow( 0 );

    ScrolledComposite ganttView = new ScrolledComposite( m_splitterTasksGantt, SWT.H_SCROLL );
    ganttView.setExpandHorizontal( true );
    ganttView.setExpandVertical( true );
    m_gantt = new Gantt( ganttView, m_tasksTable );
    ganttView.setContent( m_gantt );
    ganttView.setMinSize( m_gantt.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    m_splitterTasksGantt.preferredLeftChildWidth = 650;
    m_splitterTasksGantt.monitor( m_tasksTable, ganttView );

    // Resources tab
    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );
    m_resourcesTable = new XNatTable( this, XNatTable.TableType.RESOURCE );
    m_resourcesTable.hideRow( 0 );
    tabResources.setControl( m_resourcesTable );

    // Calendars tab
    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );
    m_calendarsTable = new XNatTable( this, XNatTable.TableType.CALENDAR );
    tabCalendars.setControl( m_calendarsTable );

    // Days-type tab
    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );
    m_daysTable = new XNatTable( this, XNatTable.TableType.DAY );
    tabDays.setControl( m_daysTable );

    // listener to detect when selected tab changed
    addListener( SWT.Selection, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        // ensure selected tab table has keyboard focus
        if ( getSelection()[0] == m_tabTasks )
          m_tasksTable.forceFocus();
        else
          getSelection()[0].getControl().forceFocus();
      }
    } );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /***************************************** properties ******************************************/
  public PlanProperties properties()
  {
    return m_planProperties;
  }

  /******************************************** notes ********************************************/
  public PlanNotes notes()
  {
    return m_planNotes;
  }

  /****************************************** tasksTab *******************************************/
  public TabItem tasksTab()
  {
    return m_tabTasks;
  }

  /******************************************** gantt ********************************************/
  public Gantt gantt()
  {
    return m_gantt;
  }

  /******************************************** tasks ********************************************/
  public XNatTable tasks()
  {
    return m_tasksTable;
  }

  /****************************************** resources ******************************************/
  public XNatTable resources()
  {
    return m_resourcesTable;
  }

  /****************************************** calendars ******************************************/
  public XNatTable calendars()
  {
    return m_calendarsTable;
  }

  /******************************************** days *********************************************/
  public XNatTable days()
  {
    return m_daysTable;
  }

  /************************************ setTasksGanttSplitter ************************************/
  public void setTasksGanttSplitter( int pos )
  {
    // set tasks-gantt splitter preferred position
    m_splitterTasksGantt.preferredLeftChildWidth = pos;
    m_splitterTasksGantt.setWeights();
  }

  /****************************************** writeXML *******************************************/
  public boolean writeXML( XMLStreamWriter xsw )
  {
    // write display data to XML stream
    try
    {
      // write tasks-gantt display data
      xsw.writeStartElement( XmlLabels.XML_TASKS_GANTT_TAB );
      xsw.writeAttribute( XmlLabels.XML_SPLITTER, Integer.toString( m_splitterTasksGantt.preferredLeftChildWidth ) );
      m_gantt.writeXML( xsw );
      m_tasksTable.writeXML( xsw );
      xsw.writeEndElement(); // XML_TASKS_GANTT_TAB

      // write resources display data
      xsw.writeStartElement( XmlLabels.XML_RESOURCES_TAB );
      m_resourcesTable.writeXML( xsw );
      xsw.writeEndElement(); // XML_RESOURCES_TAB

      // write calendars display data
      xsw.writeStartElement( XmlLabels.XML_CALENDARS_TAB );
      m_calendarsTable.writeXML( xsw );
      xsw.writeEndElement(); // XML_CALENDARS_TAB

      // write day-types display data
      xsw.writeStartElement( XmlLabels.XML_DAYS_TAB );
      m_daysTable.writeXML( xsw );
      xsw.writeEndElement(); // XML_DAYS_TAB
    }
    catch ( XMLStreamException exception )
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }

    return true;
  }

  /************************************** loadXmlTasksGantt **************************************/
  public void loadXmlTasksGantt( XMLStreamReader xsr ) throws XMLStreamException
  {
    // adopt tasks-gantt tab data from XML stream, starting with the attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_SPLITTER:
          setTasksGanttSplitter( Integer.parseInt( xsr.getAttributeValue( i ) ) );
          break;
        default:
          JPlanner.trace( "loadXmlTasksGantt - unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }

    // read tasks-gantt tab XML elements
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of tab data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_TASKS_GANTT_TAB ) )
        return;

      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_GANTT:
            m_gantt.loadXML( xsr );
            break;
          default:
            JPlanner.trace( "MainTabWidget.loadXmlTasksGantt - unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }

  }

  /************************************** loadXmlResources ***************************************/
  public void loadXmlResources( XMLStreamReader xsr )
  {
    // TODO Auto-generated method stub

  }

  /************************************** loadXmlCalendars ***************************************/
  public void loadXmlCalendars( XMLStreamReader xsr )
  {
    // TODO Auto-generated method stub

  }

  /*************************************** loadXmlDayTypes ***************************************/
  public void loadXmlDayTypes( XMLStreamReader xsr )
  {
    // TODO Auto-generated method stub

  }

}
