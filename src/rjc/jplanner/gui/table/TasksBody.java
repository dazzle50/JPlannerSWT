/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlanner                                  *
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
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/**************************** Body data provider for tasks NatTable ******************************/
/*************************************************************************************************/

public class TasksBody implements IDataProvider
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // table row count is constant
    return Task.SECTION_MAX + 1;
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int col, int row )
  {
    // return appropriate value for table cell
    return JPlanner.plan.task( row ).toString( col );
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // return number of tasks in plan
    return JPlanner.plan.tasksCount();
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int col, int row, Object newValue )
  {
    // TODO !!!!!!!!!!!!!!

  }

}
