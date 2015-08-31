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

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/************************* UndoCommand for updating calendar exceptions **************************/
/*************************************************************************************************/

public class CommandSetCalendarExceptions implements IUndoCommand
{

  /**************************************** constructor ******************************************/
  public CommandSetCalendarExceptions( int calID, Object newValue, Object oldValue )
  {
    // TODO Auto-generated constructor stub
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // TODO Auto-generated method stub

  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // TODO Auto-generated method stub

  }

  /****************************************** update *********************************************/
  @Override
  public void update()
  {
    // update plan properties on gui
    JPlanner.gui.calendarTables().refresh();

    // update schedule
    JPlanner.gui.schedule();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // TODO Auto-generated method stub
    return "Exceptions TBD";
  }

}
