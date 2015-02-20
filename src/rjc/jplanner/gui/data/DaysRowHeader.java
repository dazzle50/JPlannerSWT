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
/************************ Row header data provider for day-types NatTable ************************/
/*************************************************************************************************/

public class DaysRowHeader implements IDataProvider
{
  private IDataProvider m_body; // data provider for the table body

  /**************************************** constructor ******************************************/
  public DaysRowHeader( IDataProvider body )
  {
    // initialise variable
    m_body = body;
  }

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // must be one
    return 1;
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int columnIndex, int rowIndex )
  {
    // TODO !!!!!!!!!!!!!!
    return "rrr";
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // must be same as body
    return m_body.getRowCount();
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int columnIndex, int rowIndex, Object newValue )
  {
    // setting header data not supported
    throw new UnsupportedOperationException();
  }

}
