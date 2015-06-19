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

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import rjc.jplanner.gui.table.XNatTable;

/*************************************************************************************************/
/*************** Widget showing the Plan/Tasks&Gantt/Resources/Calendars/Days tabs ***************/
/*************************************************************************************************/

public class MainTabWidget extends TabFolder
{
  private PlanProperties m_planProperties;
  private PlanNotes      m_planNotes;
  private TabItem        m_tabTasks;

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

    XSashForm splitterTasksGantt = new XSashForm( this, SWT.SMOOTH );
    m_tabTasks.setControl( splitterTasksGantt );

    NatTable tableTasks = new XNatTable( splitterTasksGantt, XNatTable.TableType.TASK );

    ScrolledComposite ganttView = new ScrolledComposite( splitterTasksGantt, SWT.H_SCROLL );
    ganttView.setExpandHorizontal( true );
    ganttView.setExpandVertical( true );
    Gantt gantt = new Gantt( ganttView );
    ganttView.setContent( gantt );
    ganttView.setMinSize( gantt.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    splitterTasksGantt.preferredLeftChildWidth = 650;
    splitterTasksGantt.monitor( tableTasks, ganttView );

    // Resources tab
    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );
    NatTable tableResources = new XNatTable( this, XNatTable.TableType.RESOURCE );
    tabResources.setControl( tableResources );

    // Calendars tab
    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );
    NatTable tableCalendars = new XNatTable( this, XNatTable.TableType.CALENDAR );
    tabCalendars.setControl( tableCalendars );

    // Days-type tab
    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );
    NatTable tableDays = new XNatTable( this, XNatTable.TableType.DAY );
    tabDays.setControl( tableDays );
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

}
