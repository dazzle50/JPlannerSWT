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
import rjc.jplanner.command.CommandResourceSetValue;
import rjc.jplanner.model.Resource;

/*************************************************************************************************/
/************************** Body data provider for resources NatTable ****************************/
/*************************************************************************************************/

public class ResourcesBody implements IDataProvider
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // table row count is constant
    return Resource.SECTION_MAX + 1;
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int col, int row )
  {
    // return appropriate value for table cell
    return JPlanner.plan.resource( row ).toString( col );
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // return number of resources in plan
    return JPlanner.plan.resourcesCount();
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int col, int row, Object newValue )
  {
    // if new value equals old value, exit with no command
    Object oldValue = getDataValue( col, row );
    if ( newValue.equals( oldValue ) )
      return;

    JPlanner.plan.undostack().push( new CommandResourceSetValue( row, col, newValue, oldValue ) );
  }

}
