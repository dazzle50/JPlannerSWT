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

package rjc.jplanner.gui.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Calendar;

/*************************************************************************************************/
/************************** Body data provider for calendars NatTable ****************************/
/*************************************************************************************************/

public class CalendarsBody implements IDataProvider
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // return number of calendars in plan
    return JPlanner.plan.calendarsCount();
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int columnIndex, int rowIndex )
  {
    // return appropriate value for table cell
    Calendar cal = JPlanner.plan.calendar( columnIndex );

    if ( rowIndex == 0 )
      return cal.name();

    if ( rowIndex == 1 )
      return cal.anchor();

    if ( rowIndex == 2 )
      return cal.numExceptions();

    if ( rowIndex == 3 )
      return cal.numNormals();

    // if row beyond normals handle index out of bounds
    try
    {
      return cal.normal( rowIndex - 4 ).name();
    }
    catch (IndexOutOfBoundsException e)
    {
      return "";
    }
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // table row count is max number of normals + 4
    int max = 0;
    for ( int i = 0; i < getColumnCount(); i++ )
      if ( JPlanner.plan.calendar( i ).numNormals() > max )
        max = JPlanner.plan.calendar( i ).numNormals();

    return max + 4;
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int columnIndex, int rowIndex, Object newValue )
  {
    // TODO !!!!!!!!!!!!!!

  }

}
