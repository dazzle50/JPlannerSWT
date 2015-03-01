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

package rjc.jplanner.gui.table;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import rjc.jplanner.model.Resource;

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
  public Object getDataValue( int col, int row )
  {
    // return column title
    if ( col == Resource.SECTION_ID )
      return "Id";

    if ( col == Resource.SECTION_NAME )
      return "Name";

    if ( col == Resource.SECTION_ORG )
      return "Organisation";

    if ( col == Resource.SECTION_GROUP )
      return "Group";

    if ( col == Resource.SECTION_ROLE )
      return "Role";

    if ( col == Resource.SECTION_ALIAS )
      return "Alias";

    if ( col == Resource.SECTION_START )
      return "Start";

    if ( col == Resource.SECTION_END )
      return "End";

    if ( col == Resource.SECTION_AVAIL )
      return "Available";

    if ( col == Resource.SECTION_COST )
      return "Cost";

    if ( col == Resource.SECTION_CALENDAR )
      return "Calendar";

    if ( col == Resource.SECTION_COMMENT )
      return "Comment";

    throw new IllegalArgumentException( "Column=" + col );
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
  public void setDataValue( int col, int row, Object newValue )
  {
    // setting header data not supported
    throw new UnsupportedOperationException();
  }

}
