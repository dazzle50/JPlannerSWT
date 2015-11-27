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

package rjc.jplanner.command;

import rjc.jplanner.gui.table.XNatTable;

/*************************************************************************************************/
/********************** UndoCommand for expanding summary task on gui table **********************/
/*************************************************************************************************/

public class CommandSummaryExpand implements IUndoCommand
{
  private XNatTable m_table;
  private int       m_row;

  /**************************************** constructor ******************************************/
  public CommandSummaryExpand( XNatTable table, int row )
  {
    // initialise private variables
    m_table = table;
    m_row = row;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command - expanding
    m_table.expandSummary( m_row );
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command - collapsing
    m_table.collapseSummary( m_row );
  }

  /****************************************** update *********************************************/
  @Override
  public void update()
  {
    // redraw gantt plot & table (table would be redrawn anyway but this avoids any visual delay) 
    m_table.gantt.updatePlot();
    m_table.redraw();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    return "Expanded task " + m_row;
  }

}
