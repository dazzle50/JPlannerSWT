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

import rjc.jplanner.model.Task;

/*************************************************************************************************/
/************************ Column header data provider for tasks NatTable *************************/
/*************************************************************************************************/

public class TasksColumnHeader implements IDataProvider
{
  private IDataProvider m_body; // data provider for the table body

  /**************************************** constructor ******************************************/
  public TasksColumnHeader( IDataProvider body )
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
    if ( col == Task.SECTION_TITLE )
      return "Title";

    if ( col == Task.SECTION_DURATION )
      return "Duration";

    if ( col == Task.SECTION_START )
      return "Start";

    if ( col == Task.SECTION_END )
      return "End";

    if ( col == Task.SECTION_WORK )
      return "Work";

    if ( col == Task.SECTION_PRED )
      return "Predecessors";

    if ( col == Task.SECTION_RES )
      return "Resources";

    if ( col == Task.SECTION_TYPE )
      return "Type";

    if ( col == Task.SECTION_PRIORITY )
      return "Priority";

    if ( col == Task.SECTION_DEADLINE )
      return "Deadline";

    if ( col == Task.SECTION_COST )
      return "Cost";

    if ( col == Task.SECTION_COMMENT )
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
