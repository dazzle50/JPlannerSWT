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
/********************** Column header data provider for resources NatTable ***********************/
/*************************************************************************************************/

public class ResourcesColumnHeader implements IDataProvider
{
  private IDataProvider m_body; // data provider for the table body

  /**************************************** constructor ******************************************/
  public ResourcesColumnHeader( IDataProvider body )
  {
    // initialise variable
    m_body = body;
  }

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // must be same as body
    return m_body.getColumnCount();
  }

  /*************************************** getDataValue ******************************************/
  @Override
  public Object getDataValue( int columnIndex, int rowIndex )
  {
    // return column title
    if ( columnIndex == 0 )
      return "Initials";

    if ( columnIndex == 1 )
      return "Name";

    if ( columnIndex == 2 )
      return "Organisation";

    if ( columnIndex == 3 )
      return "Group";

    if ( columnIndex == 4 )
      return "Role";

    if ( columnIndex == 5 )
      return "Alias";

    if ( columnIndex == 6 )
      return "Start";

    if ( columnIndex == 7 )
      return "End";

    if ( columnIndex == 8 )
      return "Available";

    if ( columnIndex == 9 )
      return "Cost";

    if ( columnIndex == 10 )
      return "Calendar";

    if ( columnIndex == 11 )
      return "Comment";

    return "???";
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // must be one
    return 1;
  }

  /*************************************** setDataValue ******************************************/
  @Override
  public void setDataValue( int columnIndex, int rowIndex, Object newValue )
  {
    // setting header data not supported
    throw new UnsupportedOperationException();
  }

}
