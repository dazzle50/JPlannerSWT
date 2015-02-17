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
import org.eclipse.nebula.widgets.nattable.style.theme.ThemeConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class _MainTabWidget extends TabFolder
{
  private Text     tempPlanProperties;
  private Text     tempPlanNotes;
  private NatTable tableDays;
  private NatTable tableCalendars;
  private NatTable tableResources;
  private NatTable tableTasks;

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public _MainTabWidget( Composite parent, ThemeConfiguration theme )
  {
    super( parent, SWT.NONE );

    TabItem tabPlan = new TabItem( this, SWT.NONE );
    tabPlan.setText( "Plan" );

    SashForm splitterPlanTab = new SashForm( this, SWT.SMOOTH );
    tabPlan.setControl( splitterPlanTab );

    tempPlanProperties = new Text( splitterPlanTab, SWT.BORDER );

    tempPlanNotes = new Text( splitterPlanTab, SWT.BORDER );
    splitterPlanTab.setWeights( new int[] { 1, 1 } );

    TabItem tabTasksGantt = new TabItem( this, SWT.NONE );
    tabTasksGantt.setText( "Tasks && Gantt" );

    SashForm splitterTasksGantt = new SashForm( this, SWT.SMOOTH );
    tabTasksGantt.setControl( splitterTasksGantt );

    tableTasks = new TasksNatTable( splitterTasksGantt, theme );

    ScrolledComposite scrolledComposite = new ScrolledComposite( splitterTasksGantt, SWT.BORDER | SWT.H_SCROLL
        | SWT.V_SCROLL );
    scrolledComposite.setExpandHorizontal( true );
    scrolledComposite.setExpandVertical( true );
    splitterTasksGantt.setWeights( new int[] { 1, 1 } );

    // Resources tab
    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );
    tableResources = new ResourcesNatTable( this, theme );
    tabResources.setControl( tableResources );

    // Calendars tab
    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );
    tableCalendars = new CalendarsNatTable( this, theme );
    tabCalendars.setControl( tableCalendars );

    // Days-type tab
    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );
    tableDays = new DaysNatTable( this, theme );
    tabDays.setControl( tableDays );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
