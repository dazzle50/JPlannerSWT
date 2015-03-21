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

package rjc.jplanner.command;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/****************************** UndoCommand for updating plan notes ******************************/
/*************************************************************************************************/

public class CommandSetPlanNotes implements UndoCommand
{
  private String m_oldNotes;
  private String m_newNotes;

  /**************************************** constructor ******************************************/
  public CommandSetPlanNotes( String newNotes )
  {
    // initialise private variables
    m_oldNotes = JPlanner.plan.notes();
    m_newNotes = newNotes;
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

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // TODO Auto-generated method stub
    return null;
  }

}