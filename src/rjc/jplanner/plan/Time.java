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

package rjc.jplanner.plan;

import java.time.LocalTime;

/*************************************************************************************************/
/******************** Time of day from 00:00:00.000 to 24:00:00.000 inclusive ********************/
/*************************************************************************************************/

public class Time
{
  private int      m_milliseconds;        // milliseconds from 00:00:00.000 start of day

  static final int MAX = 24 * 3600 * 1000; // milliseconds in day

  // anything between Zero and MAX inclusive is valid, anything else invalid

  /* ======================================= constructor ======================================= */
  private Time( int milliseconds )
  {
    // constructor (from pre-validated milliseconds)
    m_milliseconds = milliseconds;
  }

  /**************************************** milliseconds *****************************************/
  public int milliseconds()
  {
    // return int number of milliseconds from start of day
    return m_milliseconds;
  }

  /****************************************** fromHours ******************************************/
  public static Time fromHours( double hours )
  {
    // return a Time from double hours
    if ( hours < 0.0 || hours > 24.0 )
      throw new IllegalArgumentException( "hours=" + hours );

    return new Time( (int) ( hours * 3600_000.0 ) );
  }

  /************************************** fromMilliseconds ***************************************/
  public static Time fromMilliseconds( int milliseconds )
  {
    // return a Time from int milliseconds
    if ( milliseconds < 0 || milliseconds > MAX )
      throw new IllegalArgumentException( "milliseconds=" + milliseconds );

    return new Time( milliseconds );
  }

  /**************************************** fromLocalTime ****************************************/
  public static Time fromLocalTime( LocalTime localTime )
  {
    // return a new Time from a java.time.LocalTime
    return new Time( (int) ( localTime.toNanoOfDay() / 1_000_000L ) );
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string to "hh:mm:ss.mmm" format
    StringBuilder buf = new StringBuilder( 16 );
    int hour = m_milliseconds / 3600_000;
    int minute = m_milliseconds / 60_000 % 60;
    int second = m_milliseconds / 1000 % 60;
    int milli = m_milliseconds % 1000;

    buf.append( hour < 10 ? "0" : "" ).append( hour );
    buf.append( minute < 10 ? ":0" : ":" ).append( minute );
    buf.append( second < 10 ? ":0" : ":" ).append( second );
    buf.append( milli < 10 ? ":0" : ":" ).append( milli < 100 ? "0" : "" ).append( milli );

    return buf.toString();
  }

  /********************************************* now *********************************************/
  public static Time now()
  {
    // return a new Time from current system clock
    return new Time( (int) ( LocalTime.now().toNanoOfDay() / 1_000_000L ) );
  }
}
