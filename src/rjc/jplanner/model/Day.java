/**************************************************************************
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

package rjc.jplanner.model;

import java.util.ArrayList;

/*************************************************************************************************/
/***************************** Single day type as used by calendars ******************************/
/*************************************************************************************************/

public class Day
{
  private String                   m_name;   // name of day type
  private double                   m_work;   // equivalent days worked (typically 1.0 or 0.0)
  private ArrayList<DayWorkPeriod> m_periods; // list of work periods

  public enum DefaultDayTypes
  {
    NONWORK, STANDARDWORK, SHORT, EVENING, TWENTYFOURHOURS
  }

  final public static int SECTION_NAME    = 0;
  final public static int SECTION_WORK    = 1;
  final public static int SECTION_PERIODS = 2;
  final public static int SECTION_START1  = 3;
  final public static int SECTION_END1    = 4;

  /**************************************** constructor ******************************************/
  public Day()
  {
    // construct empty but usable day type
    m_name = "Null";
    m_work = 0.0;
    m_periods = new ArrayList<DayWorkPeriod>();
  }

  /**************************************** constructor ******************************************/
  public Day( DefaultDayTypes type )
  {
    // construct default day type
    m_periods = new ArrayList<DayWorkPeriod>();

    if ( type == DefaultDayTypes.NONWORK )
    {
      m_name = "Non Working";
      m_work = 0.0;
    }
    else if ( type == DefaultDayTypes.STANDARDWORK )
    {
      m_name = "Standard work day";
      m_work = 1.0;
      m_periods.add( new DayWorkPeriod( 9.0, 13.0 ) );
      m_periods.add( new DayWorkPeriod( 14.0, 18.0 ) );
    }
    else if ( type == DefaultDayTypes.SHORT )
    {
      m_name = "Morning only";
      m_work = 0.5;
      m_periods.add( new DayWorkPeriod( 9.0, 13.0 ) );
    }
    else if ( type == DefaultDayTypes.EVENING )
    {
      m_name = "Evening shift";
      m_work = 0.6;
      m_periods.add( new DayWorkPeriod( 18.0, 22.0 ) );
    }
    else if ( type == DefaultDayTypes.TWENTYFOURHOURS )
    {
      m_name = "24H day";
      m_work = 1.5;
      m_periods.add( new DayWorkPeriod( 0.0, 24.0 ) );
    }
    else
    {
      throw new IllegalArgumentException( "Unhandled DefaultDayType=" + type );
    }
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    return "DayType[" + m_name + ", " + m_work + ", " + m_periods + "]";
  }

  /******************************************* name **********************************************/
  public String name()
  {
    return m_name;
  }

  /******************************************* work **********************************************/
  public double work()
  {
    return m_work;
  }

  /**************************************** numPeriods *******************************************/
  public int numPeriods()
  {
    return m_periods.size();
  }

  /******************************************** end **********************************************/
  public Time end( int num )
  {
    return m_periods.get( num - 1 ).m_end;
  }

  /******************************************* start *********************************************/
  public Time start( int num )
  {
    return m_periods.get( num - 1 ).m_start;
  }

  /****************************************** setData ********************************************/
  public void setData( int section, Object newValue )
  {
    // update day with new value
    if ( section == SECTION_NAME )
      m_name = (String) newValue;
    else if ( section == SECTION_WORK )
      m_work = (double) newValue;
    else if ( section >= SECTION_START1 )
    {
      if ( ( section - SECTION_START1 ) % 2 == 0 )
        m_periods.get( ( section - SECTION_START1 ) / 2 ).m_start = (Time) newValue;
      else
        m_periods.get( ( section - SECTION_END1 ) / 2 ).m_end = (Time) newValue;
    }
    else
      throw new IllegalArgumentException( "Unhandled section=" + section );
  }

}
