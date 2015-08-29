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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*************************************************************************************************/
/************************************ Date (with no timezone) ************************************/
/*************************************************************************************************/

public class Date
{
  private int              m_epochday;                               // simple count of days where day 0 is 01-Jan-1970

  // min int=-2^31 gives minimum date of approx 5,800,000 BC
  public static final Date MIN_VALUE = new Date( Integer.MIN_VALUE );

  // max int=2^31-1 gives maximum date of approx 5,800,000 AD
  public static final Date MAX_VALUE = new Date( Integer.MAX_VALUE );

  /**************************************** constructor ******************************************/
  public Date( int epochday )
  {
    // constructor from epoch-day
    m_epochday = epochday;
  }

  /**************************************** constructor ******************************************/
  public Date( int year, int month, int day )
  {
    // constructor from specified year, month, day
    m_epochday = (int) LocalDate.of( year, month, day ).toEpochDay();
  }

  /**************************************** constructor ******************************************/
  public Date( LocalDate localDate )
  {
    // return a new Date from LocalDate
    m_epochday = (int) localDate.toEpochDay();
  }

  /****************************************** epochday *******************************************/
  public int epochday()
  {
    // return int count of days from day 0 is 01-Jan-1970
    return m_epochday;
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    // convert to string in ISO-8601 format "uuuu-MM-dd"
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.toString();
  }

  /****************************************** toString *******************************************/
  public String toString( String format )
  {
    // convert to string in specified format
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.format( DateTimeFormatter.ofPattern( format ) );
  }

  /********************************************* now *********************************************/
  public static Date now()
  {
    // return a new Date from current system clock
    return new Date( (int) LocalDate.now().toEpochDay() );
  }

  /***************************************** fromString ******************************************/
  public static Date fromString( String str )
  {
    // if string of type YYYY-MM-DD or YYYY/MM/DD
    if ( str.matches( "\\d\\d\\d\\d-\\d\\d-\\d\\d" ) || str.matches( "\\d\\d\\d\\d/\\d\\d/\\d\\d" ) )
    {
      int year = Integer.parseInt( str.substring( 0, 4 ) );
      int mon = Integer.parseInt( str.substring( 5, 7 ) );
      int day = Integer.parseInt( str.substring( 8, 10 ) );
      return new Date( year, mon, day );
    }

    throw new IllegalArgumentException( "String=" + str );
  }

  /******************************************** year *********************************************/
  public int year()
  {
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.getYear();
  }

  /******************************************** month ********************************************/
  public int month()
  {
    // return month of year as number 1 to 12
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.getMonthValue();
  }

  /***************************************** dayOfMonth ******************************************/
  public int dayOfMonth()
  {
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.getDayOfMonth();
  }

  /****************************************** plusDays *******************************************/
  public Date plusDays( int days )
  {
    return new Date( m_epochday + days );
  }

  /***************************************** plusWeeks *******************************************/
  public Date plusWeeks( int weeks )
  {
    return new Date( m_epochday + 7 * weeks );
  }

  /***************************************** plusMonths ******************************************/
  public Date plusMonths( int months )
  {
    return new Date( LocalDate.ofEpochDay( m_epochday ).plusMonths( months ) );
  }

  /***************************************** plusYears *******************************************/
  public Date plusYears( int years )
  {
    return new Date( LocalDate.ofEpochDay( m_epochday ).plusYears( years ) );
  }

  /******************************************* equals ********************************************/
  public boolean equals( Date other )
  {
    return m_epochday == other.m_epochday;
  }

}