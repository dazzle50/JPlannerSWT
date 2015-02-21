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
import rjc.jplanner.model.DayType;

/*************************************************************************************************/
/************************** Body data provider for day-types NatTable ****************************/
/*************************************************************************************************/

public class DaysBody implements IDataProvider
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // table column count is max number of periods * 2 + 3
    int max = 0;
    for ( int i = 0; i < getRowCount(); i++ )
      if ( JPlanner.plan.day( i ).numPeriods() > max )
        max = JPlanner.plan.day( i ).numPeriods();

    return max * 2 + 3;
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int columnIndex, int rowIndex )
  {
    // return appropriate value for table cell
    DayType day = JPlanner.plan.day( rowIndex );

    if ( columnIndex == 0 )
      return day.name();

    if ( columnIndex == 1 )
      return day.work();

    if ( columnIndex == 2 )
      return day.numPeriods();

    // if column beyond work period starts/ends handle index out of bounds
    try
    {
      if ( columnIndex % 2 == 0 )
        return day.end( columnIndex / 2 - 1 ).toString().substring( 0, 5 );
      else
        return day.start( columnIndex / 2 ).toString().substring( 0, 5 );
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
    // return number of day-types in plan
    return JPlanner.plan.daysCount();
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int columnIndex, int rowIndex, Object newValue )
  {
    // TODO !!!!!!!!!!!!!!

  }

}
