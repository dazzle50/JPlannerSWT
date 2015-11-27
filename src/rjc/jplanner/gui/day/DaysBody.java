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

package rjc.jplanner.gui.day;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import rjc.jplanner.JPlanner;
import rjc.jplanner.command.CommandDaySetNumPeriods;
import rjc.jplanner.command.CommandDaySetValue;
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/************************** Body data provider for day-types NatTable ****************************/
/*************************************************************************************************/

public class DaysBody implements IDataProvider
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // table column count is max number of periods * 2 + SECTION_START1
    int max = 0;
    for ( int i = 0; i < getRowCount(); i++ )
      if ( JPlanner.plan.day( i ).numPeriods() > max )
        max = JPlanner.plan.day( i ).numPeriods();

    return max * 2 + Day.SECTION_START1;
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // return number of day-types in plan
    return JPlanner.plan.daysCount();
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int col, int row )
  {
    // return appropriate display value for table cell
    return JPlanner.plan.day( row ).toString( col );
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int col, int row, Object newValue )
  {
    // if new value equals old value, exit with no command
    Object oldValue = getDataValue( col, row );
    if ( newValue.equals( oldValue ) )
      return;

    // special command for setting number of work periods, otherwise generic
    if ( col == Day.SECTION_PERIODS )
      JPlanner.plan.undostack().push( new CommandDaySetNumPeriods( row, newValue, oldValue ) );
    else
      JPlanner.plan.undostack().push( new CommandDaySetValue( row, col, newValue, oldValue ) );
  }

}
