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

/*************************************************************************************************/
/************************** Body data provider for day-types NatTable ****************************/
/*************************************************************************************************/

public class DaysBody implements IDataProvider
{

  /**************************************** constructor ******************************************/
  public DaysBody()
  {
    // TODO !!!!!!!!!!!!!!
  }

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // TODO !!!!!!!!!!!!!!
    return 6;
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int columnIndex, int rowIndex )
  {
    // TODO !!!!!!!!!!!!!!
    return "day...";
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // TODO !!!!!!!!!!!!!!
    return 6;
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int columnIndex, int rowIndex, Object newValue )
  {
    // TODO !!!!!!!!!!!!!!

  }

}
