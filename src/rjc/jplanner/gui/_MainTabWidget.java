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

public class _MainTabWidget extends TabFolder
{
  private _PlanProperties planProperties;
  private _PlanNotes      planNotes;
  private NatTable        tableDays;
  private NatTable        tableCalendars;
  private NatTable        tableResources;
  private NatTable        tableTasks;

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public _MainTabWidget( Composite parent )
  {
    super( parent, SWT.NONE );

    TabItem tabPlan = new TabItem( this, SWT.NONE );
    tabPlan.setText( "Plan" );

    XSashForm splitterPlanTab = new XSashForm( this, SWT.SMOOTH );
    tabPlan.setControl( splitterPlanTab );

    ScrolledComposite scrolledProperties = new ScrolledComposite( splitterPlanTab, SWT.H_SCROLL | SWT.V_SCROLL );
    scrolledProperties.setExpandHorizontal( true );
    scrolledProperties.setExpandVertical( true );
    planProperties = new _PlanProperties( scrolledProperties, SWT.NONE );
    planProperties.setBackground( getBackground() );
    scrolledProperties.setContent( planProperties );
    scrolledProperties.setMinSize( planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    planNotes = new _PlanNotes( splitterPlanTab, SWT.NONE );
    planNotes.setBackground( getBackground() );
    splitterPlanTab.preferredLeftChildWidth = planProperties.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 1;
    splitterPlanTab.monitor( scrolledProperties, planNotes );
    planProperties.updateFromPlan();

    TabItem tabTasksGantt = new TabItem( this, SWT.NONE );
    tabTasksGantt.setText( "Tasks && Gantt" );

    XSashForm splitterTasksGantt = new XSashForm( this, SWT.SMOOTH );
    tabTasksGantt.setControl( splitterTasksGantt );

    tableTasks = new XNatTable( splitterTasksGantt, XNatTable.TableType.TASK );

    ScrolledComposite gantt = new ScrolledComposite( splitterTasksGantt, SWT.H_SCROLL | SWT.V_SCROLL );
    gantt.setExpandHorizontal( true );
    gantt.setExpandVertical( true );
    splitterTasksGantt.monitor( tableTasks, gantt );

    // Resources tab
    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );
    tableResources = new XNatTable( this, XNatTable.TableType.RESOURCE );
    tabResources.setControl( tableResources );

    // Calendars tab
    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );
    tableCalendars = new XNatTable( this, XNatTable.TableType.CALENDAR );
    tabCalendars.setControl( tableCalendars );

    // Days-type tab
    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );
    tableDays = new XNatTable( this, XNatTable.TableType.DAY );
    tabDays.setControl( tableDays );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
