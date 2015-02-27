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

import java.util.ArrayList;

/*************************************************************************************************/
/********************************** Stack of UndoCommand objects *********************************/
/*************************************************************************************************/

public class UndoStack
{
  private ArrayList<UndoCommand> m_stack; // stack of undo commands
  private int                    m_index; // current command

  /**************************************** constructor ******************************************/
  public UndoStack()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!

  }

  /******************************************** push *********************************************/
  void push( UndoCommand undo )
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!

  }

  /******************************************** undo *********************************************/
  void undo()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!

  }

  /******************************************** redo *********************************************/
  void redo()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!

  }

  /******************************************** clean ********************************************/
  void clean()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!

  }

  /******************************************** count ********************************************/
  int count()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!
    return 0;
  }
}
