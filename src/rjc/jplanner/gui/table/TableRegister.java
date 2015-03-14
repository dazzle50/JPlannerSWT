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

import java.util.ArrayList;

/*************************************************************************************************/
/****************** Distributes table refresh requests to all registered tables ******************/
/*************************************************************************************************/

public class TableRegister
{
  private ArrayList<XNatTable> m_tables = new ArrayList<XNatTable>();

  /***************************************** addListener *****************************************/
  public void register( XNatTable toAdd )
  {
    m_tables.add( toAdd );
  }

  /******************************************* refresh *******************************************/
  public void refresh()
  {
    for ( XNatTable table : m_tables )
      table.refresh();
  }

}
