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

import java.time.LocalDateTime;

/*************************************************************************************************/
/********************************* Date-time (with no timezone) **********************************/
/*************************************************************************************************/

public class DateTime
{
  private long             m_milliseconds;                        // milliseconds from 00:00:00.000 start of epoch-day

  public static final long MILLISECONDS_IN_DAY = 24 * 3600 * 1000; // milliseconds in day

  public enum Interval
  {
    YEAR, HALFYEAR, QUARTERYEAR, MONTH, WEEK, DAY
  }

  /***************************************** constructor *****************************************/
  public DateTime( long ms )
  {
    // constructor
    m_milliseconds = ms;
  }

  /***************************************** constructor *****************************************/
  public DateTime( Date date, Time time )
  {
    // constructor
    m_milliseconds = date.epochday() * MILLISECONDS_IN_DAY + time.milliseconds();
  }

  /***************************************** constructor *****************************************/
  public DateTime( LocalDateTime dt )
  {
    // constructor
    Date date = Date.fromLocalDate( dt.toLocalDate() );
    Time time = Time.fromLocalTime( dt.toLocalTime() );
    m_milliseconds = date.epochday() * MILLISECONDS_IN_DAY + time.milliseconds();
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string to "YYYY-MM-DD hh:mm:ss.mmm" format
    return date().toString() + " " + time().toString();
  }

  /******************************************** date *********************************************/
  public Date date()
  {
    return new Date( (int) ( m_milliseconds / MILLISECONDS_IN_DAY ) );
  }

  /******************************************** time *********************************************/
  public Time time()
  {
    return Time.fromMilliseconds( (int) ( m_milliseconds % MILLISECONDS_IN_DAY ) );
  }

  /********************************************* now *********************************************/
  public static DateTime now()
  {
    // return a new DateTime from current system clock
    return new DateTime( LocalDateTime.now() );
  }

  /******************************************** year *********************************************/
  public int year()
  {
    return date().year();
  }

  /******************************************** month ********************************************/
  public int month()
  {
    return date().month();
  }

  /********************************************* day *********************************************/
  public int day()
  {
    return date().day();
  }

  /******************************************** hours ********************************************/
  public int hours()
  {
    return time().hours();
  }

  /******************************************* minutes *******************************************/
  public int minutes()
  {
    return time().minutes();
  }

  /******************************************* seconds *******************************************/
  public int seconds()
  {
    return time().seconds();
  }

  /*************************************** addMilliseconds ***************************************/
  public DateTime addMilliseconds( long ms )
  {
    return new DateTime( m_milliseconds + ms );
  }

  /***************************************** milliseconds ****************************************/
  public long milliseconds()
  {
    return m_milliseconds;
  }
}
