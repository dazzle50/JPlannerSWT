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

package rjc.jplanner;

import java.io.File;

import org.eclipse.swt.widgets.Display;

import rjc.jplanner.gui.MainWindow;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.Plan;

/*************************************************************************************************/
// JPlanner by Richard Crook
// Aims to be a project planner similar to M$Project with table entry of tasks & Gantt chart
// Also aims to have automatic resource levelling and scheduling based on task priority
// Also aims to have resource levels variable within single task
// Also aims to have Gantt chart task bar thickness showing this variable resource usage
// Based on work I started as early as 2005
/*************************************************************************************************/

public class JPlanner
{
  public static Plan       plan; // globally accessible plan
  public static MainWindow gui; // globally accessible main-window

  /******************************************** main *********************************************/
  public static void main( String[] args )
  {
    // main entry point for application startup
    trace( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ JPlanner started ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
    trace( "" + plan );
    plan = new Plan();
    trace( "" + plan );
    plan.initialise();
    trace( "" + plan );

    // create main application display
    Display display = new Display();
    gui = new MainWindow( display );
    gui.initialise();
    gui.loadPlan( new File( "C:\\Users\\Richard\\EclipseWS\\JPlannerSWT\\tests\\!defaultload.xml" ) );
    JPlanner.plan.schedule();
    gui.open();

    // run the event loop as long as the window is open
    while ( !gui.isDisposed() )
    {
      // read the next OS event queue and transfer it to a SWT event
      if ( !display.readAndDispatch() )
      {
        // if there are currently no other OS event to process, sleep until the next OS event is available
        display.sleep();
      }
    }

    // disposes all associated windows and their components
    display.dispose();

    trace( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ JPlanner ended ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
  }

  /******************************************* trace *********************************************/
  public static void trace( String txt )
  {
    // prints txt prefixed by date-time
    System.out.println( DateTime.now() + " " + txt );
  }

  /******************************************* clean *********************************************/
  public static String clean( String txt )
  {
    // returns a clean string
    return txt.trim().replaceAll( "\\s", " " ).replaceAll( "(\\s{2,})", " " );
  }

}
