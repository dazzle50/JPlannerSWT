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
    // initialise private variables
    m_stack = new ArrayList<UndoCommand>();
    m_index = 0;
  }

  /******************************************** push *********************************************/
  public void push( UndoCommand command )
  {
    // remove any commands from stack that haven't been actioned (i.e. above index)
    while ( m_stack.size() > m_index )
      m_stack.remove( m_stack.size() - 1 );

    // add new command to stack, do it, and increment stack index
    m_stack.add( command );
    command.redo();
    m_index++;
  }

  /******************************************** undo *********************************************/
  void undo()
  {
    // decrement index and revert command
    m_index--;
    m_stack.get( m_index ).undo();
  }

  /******************************************** redo *********************************************/
  void redo()
  {
    // action command and increment index
    m_stack.get( m_index ).redo();
    m_index++;
  }

  /****************************************** undoText *******************************************/
  String undoText()
  {
    // return text associated with next potential undo
    return m_stack.get( m_index - 1 ).text();
  }

  /****************************************** redoText *******************************************/
  String redoText()
  {
    // return text associated with next potential redo
    return m_stack.get( m_index ).text();
  }

  /******************************************** clear ********************************************/
  void clear()
  {
    // clean the stack
    m_stack.clear();
    m_index = 0;
  }

  /******************************************** size *********************************************/
  int size()
  {
    // return stack size
    return m_stack.size();
  }

  /******************************************* index *********************************************/
  int index()
  {
    // return command index
    return m_index;
  }
}
