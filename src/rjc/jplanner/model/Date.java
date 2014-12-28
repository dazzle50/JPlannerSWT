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

import java.time.LocalDate;

/*************************************************************************************************/
/************************************ Date (with no timezone) ************************************/
/*************************************************************************************************/

public class Date
{
  private int m_epochday; // simple count of days where day 0 is 01-Jan-1970

  // min int=-2^31 gives minimum date of approx 5,800,000 BC
  // max int=2^31-1 gives maximum date of approx 5,800,000 AD

  /* ======================================= constructor ======================================= */
  private Date( int epochday )
  {
    // constructor (from pre-validated epochday)
    m_epochday = epochday;
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
    // convert to string in "YYYY-MM-DD" format
    LocalDate ld = LocalDate.ofEpochDay( m_epochday );
    return ld.toString();
  }

  /********************************************* now *********************************************/
  public static Date now()
  {
    // return a new Date from current system clock
    return new Date( (int) LocalDate.now().toEpochDay() );
  }

  /**************************************** fromLocalDate ****************************************/
  public static Date fromLocalDate( LocalDate localDate )
  {
    // return a new Date from LocalDate
    return new Date( (int) localDate.toEpochDay() );
  }
}