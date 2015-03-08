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

public class _MainTabWidget extends TabFolder
{
  private _PlanProperties m_planProperties;
  private _PlanNotes      m_planNotes;
  private NatTable        m_tableDays;
  private NatTable        m_tableCalendars;
  private NatTable        m_tableResources;
  private NatTable        m_tableTasks;

  /**************************************** constructor ******************************************/
  public _MainTabWidget( Composite parent )
  {
    super( parent, SWT.NONE );

    // Plan tab
    TabItem tabPlan = new TabItem( this, SWT.NONE );
    tabPlan.setText( "Plan" );

    XSashForm splitterPlanTab = new XSashForm( this, SWT.SMOOTH );
    tabPlan.setControl( splitterPlanTab );

    ScrolledComposite scrolledProperties = new ScrolledComposite( splitterPlanTab, SWT.H_SCROLL | SWT.V_SCROLL );
    scrolledProperties.setExpandHorizontal( true );
    scrolledProperties.setExpandVertical( true );
    m_planProperties = new _PlanProperties( scrolledProperties, SWT.NONE );
    m_planProperties.setBackground( getBackground() );
    scrolledProperties.setContent( m_planProperties );
    scrolledProperties.setMinSize( m_planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    m_planNotes = new _PlanNotes( splitterPlanTab, SWT.NONE );
    m_planNotes.setBackground( getBackground() );
    splitterPlanTab.preferredLeftChildWidth = m_planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 1;
    splitterPlanTab.monitor( scrolledProperties, m_planNotes );
    m_planProperties.updateFromPlan();

    // Tasks & Gantt tab
    TabItem tabTasksGantt = new TabItem( this, SWT.NONE );
    tabTasksGantt.setText( "Tasks && Gantt" );

    XSashForm splitterTasksGantt = new XSashForm( this, SWT.SMOOTH );
    tabTasksGantt.setControl( splitterTasksGantt );

    m_tableTasks = new XNatTable( splitterTasksGantt, XNatTable.TableType.TASK );

    ScrolledComposite ganttView = new ScrolledComposite( splitterTasksGantt, SWT.H_SCROLL );
    ganttView.setExpandHorizontal( true );
    ganttView.setExpandVertical( true );
    _Gantt gantt = new _Gantt( ganttView );
    ganttView.setContent( gantt );
    ganttView.setMinSize( gantt.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    splitterTasksGantt.monitor( m_tableTasks, ganttView );

    // Resources tab
    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );
    m_tableResources = new XNatTable( this, XNatTable.TableType.RESOURCE );
    tabResources.setControl( m_tableResources );

    // Calendars tab
    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );
    m_tableCalendars = new XNatTable( this, XNatTable.TableType.CALENDAR );
    tabCalendars.setControl( m_tableCalendars );

    // Days-type tab
    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );
    m_tableDays = new XNatTable( this, XNatTable.TableType.DAY );
    tabDays.setControl( m_tableDays );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
