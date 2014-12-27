/**************************************************************************
 *  Copyright (C) 2014 by Richard Crook                                   *
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

package rjc.jplanner;

import rjc.jplanner.plan.DateTime;
import rjc.jplanner.plan.DayWorkPeriod;

/*************************************************************************************************/
// JPlanner by Richard Crook
// Aims to be a project planner similar to M$Project with table entry of tasks & Gantt chart
// Also aims to have automatic resource levelling and scheduling based on task priority
// Also aims to have resource levels variable within single task
// Also aims to have Gantt chart task bar thickness showing this variable resource usage
// Based on work I started as early as 2005
/*************************************************************************************************/

public class jplanner
{

  /******************************************** main *********************************************/
  public static void main( String[] args )
  {
    // main entry point for application startup
    trace( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ JPlanner started ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );

    DayWorkPeriod p1 = new DayWorkPeriod( 1.3, 12.5646786 );
    trace( "" + p1 );
  }

  /******************************************* debug *********************************************/
  public static void trace( String txt )
  {
    // prints txt prefixed by date-time
    System.out.println( DateTime.now() + " " + txt );
  }

}
