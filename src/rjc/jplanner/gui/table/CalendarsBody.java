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

package rjc.jplanner.gui.table;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import rjc.jplanner.JPlanner;
import rjc.jplanner.command.CommandCalendarSetCycleLength;
import rjc.jplanner.command.CommandCalendarSetExceptions;
import rjc.jplanner.command.CommandCalendarSetValue;
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
  public Object getDataValue( int col, int row )
  {
    // return appropriate value for table cell
    return JPlanner.plan.calendar( col ).toString( row );
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // table row count is max number of normals + SECTION_NORMAL1
    int max = 0;
    for ( int i = 0; i < getColumnCount(); i++ )
      if ( JPlanner.plan.calendar( i ).numNormals() > max )
        max = JPlanner.plan.calendar( i ).numNormals();

    return max + Calendar.SECTION_NORMAL1;
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int col, int row, Object newValue )
  {
    // if new value equals old value, exit with no command
    Object oldValue = getDataValue( col, row );
    if ( newValue.equals( oldValue ) )
      return;

    // special command for setting exceptions & cycle-length, otherwise generic
    if ( row == Calendar.SECTION_EXCEPTIONS )
      JPlanner.plan.undostack().push( new CommandCalendarSetExceptions( col, newValue, oldValue ) );
    else if ( row == Calendar.SECTION_CYCLE )
      JPlanner.plan.undostack().push( new CommandCalendarSetCycleLength( col, newValue, oldValue ) );
    else
      JPlanner.plan.undostack().push( new CommandCalendarSetValue( col, row, newValue, oldValue ) );
  }

}
