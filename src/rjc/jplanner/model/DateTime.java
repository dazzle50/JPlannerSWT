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

package rjc.jplanner.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/*************************************************************************************************/
/********************************* Date-time (with no timezone) **********************************/
/*************************************************************************************************/

public class DateTime
{
  private long                 m_milliseconds;                                                      // milliseconds from 00:00:00.000 start of epoch-day

  public static final long     MILLISECONDS_IN_DAY = Time.MILLISECONDS_IN_DAY;                      // milliseconds in day
  public static final DateTime MIN_VALUE           = new DateTime( Date.MIN_VALUE, Time.MIN_VALUE );
  public static final DateTime MAX_VALUE           = new DateTime( Date.MAX_VALUE, Time.MAX_VALUE );

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
  public DateTime( String str )
  {
    // constructor, date must be split from time by a space
    int split = str.indexOf( 'T' );
    Date date = Date.fromString( str.substring( 0, split ) );
    Time time = Time.fromString( str.substring( split + 1, str.length() ) );
    m_milliseconds = date.epochday() * MILLISECONDS_IN_DAY + time.milliseconds();
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
    Date date = new Date( dt.toLocalDate() );
    Time time = new Time( dt.toLocalTime() );
    m_milliseconds = date.epochday() * MILLISECONDS_IN_DAY + time.milliseconds();
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string to "YYYY-MM-DDThh:mm:ss.mmm" format
    return date().toString() + "T" + time().toString();
  }

  /****************************************** toString *******************************************/
  public String toString( String format )
  {
    // convert to string in specified format
    LocalDateTime ldt = LocalDateTime.ofEpochSecond( m_milliseconds / 1000L, (int) ( m_milliseconds % 1000 * 1000000 ),
        ZoneOffset.UTC );

    return ldt.format( DateTimeFormatter.ofPattern( format ) );
  }

  /******************************************** date *********************************************/
  public Date date()
  {
    if ( m_milliseconds < 0 )
      return new Date( (int) ( m_milliseconds / MILLISECONDS_IN_DAY ) - 1 );

    return new Date( (int) ( m_milliseconds / MILLISECONDS_IN_DAY ) );
  }

  /******************************************** time *********************************************/
  public Time time()
  {
    int ms = (int) ( m_milliseconds % MILLISECONDS_IN_DAY );
    if ( ms < 0 )
      ms += MILLISECONDS_IN_DAY;

    return Time.fromMilliseconds( ms );
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

  /***************************************** dayOfMonth ******************************************/
  public int dayOfMonth()
  {
    return date().dayOfMonth();
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
  public DateTime plusMilliseconds( long ms )
  {
    return new DateTime( m_milliseconds + ms );
  }

  /***************************************** milliseconds ****************************************/
  public long milliseconds()
  {
    return m_milliseconds;
  }

  /******************************************** trunc ********************************************/
  public DateTime trunc( Interval interval )
  {
    // return new date-time truncated down to specified interval
    if ( interval == Interval.YEAR )
    {
      Date date = new Date( date().year(), 1, 1 );
      return new DateTime( date.epochday() * MILLISECONDS_IN_DAY );
    }

    if ( interval == Interval.HALFYEAR )
    {
      Date date = date();
      int month = ( ( date.month() - 1 ) / 6 ) * 6 + 1;

      Date hy = new Date( date.year(), month, 1 );
      return new DateTime( hy.epochday() * MILLISECONDS_IN_DAY );
    }

    if ( interval == Interval.QUARTERYEAR )
    {
      Date date = date();
      int month = ( ( date.month() - 1 ) / 3 ) * 3 + 1;

      Date qy = new Date( date.year(), month, 1 );
      return new DateTime( qy.epochday() * MILLISECONDS_IN_DAY );
    }

    if ( interval == Interval.MONTH )
    {
      Date date = date();
      Date md = new Date( date.year(), date.month(), 1 );
      return new DateTime( md.epochday() * MILLISECONDS_IN_DAY );
    }

    if ( interval == Interval.WEEK )
    {
      int day = (int) ( m_milliseconds / MILLISECONDS_IN_DAY );
      int dayOfWeek = ( day + 3 ) % 7;
      return new DateTime( ( day - dayOfWeek ) * MILLISECONDS_IN_DAY );
    }

    if ( interval == Interval.DAY )
    {
      long ms = ( m_milliseconds / MILLISECONDS_IN_DAY ) * MILLISECONDS_IN_DAY;
      return new DateTime( ms );
    }

    throw new IllegalArgumentException( "interval=" + interval );
  }

  /****************************************** plusDays *******************************************/
  public DateTime plusDays( int days )
  {
    // return new date-time specified days added or subtracted
    return new DateTime( m_milliseconds + days * MILLISECONDS_IN_DAY );
  }

  /***************************************** plusMonths ******************************************/
  public DateTime plusMonths( int months )
  {
    // return new date-time specified months added or subtracted
    return new DateTime( date().plusMonths( months ), time() );
  }

  /***************************************** plusYears *******************************************/
  public DateTime plusYears( int years )
  {
    // return new date-time specified months added or subtracted
    return new DateTime( date().plusYears( years ), time() );
  }

  /**************************************** plusInterval *****************************************/
  public DateTime plusInterval( Interval interval )
  {
    // add one specified interval to date-time
    if ( interval == Interval.YEAR )
      return plusYears( 1 );

    if ( interval == Interval.HALFYEAR )
      return plusMonths( 6 );

    if ( interval == Interval.QUARTERYEAR )
      return plusMonths( 3 );

    if ( interval == Interval.MONTH )
      return plusMonths( 1 );

    if ( interval == Interval.WEEK )
      return plusDays( 7 );

    if ( interval == Interval.DAY )
      return plusDays( 1 );

    throw new IllegalArgumentException( "interval=" + interval );
  }

  /****************************************** isLessThan *****************************************/
  public boolean isLessThan( DateTime other )
  {
    return m_milliseconds < other.m_milliseconds;
  }

  /******************************************* equals ********************************************/
  public boolean equals( DateTime other )
  {
    return m_milliseconds == other.m_milliseconds;
  }

}