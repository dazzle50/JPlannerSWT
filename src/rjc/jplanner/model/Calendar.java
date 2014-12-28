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

package rjc.jplanner.model;

import java.util.ArrayList;
import java.util.HashMap;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.DayType.DefaultDayTypes;

/*************************************************************************************************/
/********************************* Single calendar for planning **********************************/
/*************************************************************************************************/

public class Calendar
{
  private String                 m_name;       // name of calendar
  private Date                   m_cycleAnchor; // anchor date of calendar cycle
  private ArrayList<DayType>     m_normal;     // normal basic cycle days
  private HashMap<Date, DayType> m_exceptions; // exceptions override normal days

  enum DefaultCalendarTypes
  {
    STANDARD, FULLTIME, FANCY
  };

  /**************************************** constructor ******************************************/
  public Calendar()
  {
    // construct empty but usable calendar
    m_name = "Null";
    m_cycleAnchor = new Date( 2000, 1, 1 );
    m_normal = new ArrayList<DayType>();
    m_exceptions = new HashMap<Date, DayType>();
  }

  /**************************************** constructor ******************************************/
  public Calendar( DefaultCalendarTypes type )
  {
    // construct default calendar
    DayType working = JPlanner.plan.day( DefaultDayTypes.STANDARDWORK.ordinal() );
    DayType nonWorking = JPlanner.plan.day( DefaultDayTypes.NONWORK.ordinal() );
    DayType fullTime = JPlanner.plan.day( DefaultDayTypes.TWENTYFOURHOURS.ordinal() );
    DayType evening = JPlanner.plan.day( DefaultDayTypes.EVENING.ordinal() );
    DayType shortDay = JPlanner.plan.day( DefaultDayTypes.SHORT.ordinal() );

    m_normal = new ArrayList<DayType>();
    m_exceptions = new HashMap<Date, DayType>();

    if ( type == DefaultCalendarTypes.STANDARD )
    {
      m_name = "Standard";
      m_cycleAnchor = new Date( 2000, 1, 1 );

      for ( int n = 0; n < 7; n++ )
      {
        if ( n < 2 ) // Sat + Sun
          m_normal.add( nonWorking );
        else
          // Mon to Fri
          m_normal.add( working );
      }

      m_exceptions.put( new Date( 2014, 12, 25 ), nonWorking );
      m_exceptions.put( new Date( 2014, 12, 26 ), nonWorking );
      m_exceptions.put( new Date( 2015, 1, 1 ), nonWorking );
      m_exceptions.put( new Date( 2015, 4, 3 ), nonWorking );
      m_exceptions.put( new Date( 2015, 4, 6 ), nonWorking );
      m_exceptions.put( new Date( 2015, 5, 4 ), nonWorking );
      m_exceptions.put( new Date( 2015, 5, 25 ), nonWorking );
      m_exceptions.put( new Date( 2015, 8, 31 ), nonWorking );
      m_exceptions.put( new Date( 2015, 12, 25 ), nonWorking );
      m_exceptions.put( new Date( 2015, 12, 28 ), nonWorking );
    }
    else if ( type == DefaultCalendarTypes.FULLTIME )
    {
      m_name = "Full time";
      m_cycleAnchor = new Date( 2015, 1, 1 );

      m_normal.add( fullTime );
    }
    else if ( type == DefaultCalendarTypes.FANCY )
    {
      m_name = "Fancy";
      m_cycleAnchor = new Date( 2015, 1, 1 );

      m_normal.add( nonWorking );
      m_normal.add( nonWorking );
      m_normal.add( nonWorking );
      m_normal.add( shortDay );
      m_normal.add( shortDay );
      m_normal.add( evening );
      m_normal.add( evening );
      m_normal.add( fullTime );
      m_normal.add( nonWorking );
      m_normal.add( fullTime );

      m_exceptions.put( new Date( 2015, 12, 25 ), nonWorking );
      m_exceptions.put( new Date( 2015, 12, 28 ), nonWorking );
    }
    else
    {
      throw new IllegalArgumentException( "Unhandled DefaultCalendarTypes=" + type );
    }

  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    return "Calendar[" + m_name + ", " + m_cycleAnchor + ", " + m_normal + "]";
  }
}