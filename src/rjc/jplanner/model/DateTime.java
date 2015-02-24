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
  private Date m_date;
  private Time m_time;

  /***************************************** constructor *****************************************/
  public DateTime( Date date, Time time )
  {
    // constructor
    m_date = date;
    m_time = time;
  }

  /***************************************** constructor *****************************************/
  public DateTime( LocalDateTime dt )
  {
    // constructor
    m_date = Date.fromLocalDate( dt.toLocalDate() );
    m_time = Time.fromLocalTime( dt.toLocalTime() );
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string to "YYYY-MM-DD hh:mm:ss.mmm" format
    return m_date.toString() + " " + m_time.toString();
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
    return m_date.year();
  }

  /******************************************** month ********************************************/
  public int month()
  {
    return m_date.month();
  }

  /********************************************* day *********************************************/
  public int day()
  {
    return m_date.day();
  }

  /******************************************** hours ********************************************/
  public int hours()
  {
    return m_time.hours();
  }

  /******************************************* minutes *******************************************/
  public int minutes()
  {
    return m_time.minutes();
  }

  /******************************************* seconds *******************************************/
  public int seconds()
  {
    return m_time.seconds();
  }
}
