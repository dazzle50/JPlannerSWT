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

import java.util.Set;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Tasks.PredecessorsList;

/*************************************************************************************************/
/******************************* UndoCommand for outdenting tasks ********************************/
/*************************************************************************************************/

public class CommandTaskOutdent implements IUndoCommand
{
  private Set<Integer>     m_rows;         // rows to be outdented
  private PredecessorsList m_predecessors; // predecessors before cleaning

  /**************************************** constructor ******************************************/
  public CommandTaskOutdent( Set<Integer> rows )
  {
    // initialise private variables
    m_rows = rows;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    JPlanner.plan.tasks.outdent( m_rows );
    m_predecessors = JPlanner.plan.tasks.cleanPredecessors();
    JPlanner.gui.message( m_predecessors.toString( "Cleaned" ) );
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command
    JPlanner.plan.tasks.indent( m_rows );
    JPlanner.plan.tasks.restorePredecessors( m_predecessors );
    JPlanner.gui.message( m_predecessors.toString( "Restored" ) );
  }

  /****************************************** update *********************************************/
  @Override
  public void update()
  {
    // re-schedule plan (which in turn will update gui)
    JPlanner.gui.schedule();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // determine lowest & highest row number
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for ( int row : m_rows )
    {
      if ( row < min )
        min = row;
      if ( row > max )
        max = row;
    }

    // command description
    if ( min == max )
      return "Outdented task " + min;
    else
      return "Outdented tasks " + min + " to " + max;
  }

}
