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

import org.eclipse.swt.widgets.MenuItem;

import rjc.jplanner.gui.MainWindow;

/*************************************************************************************************/
/********************************** Stack of UndoCommand objects *********************************/
/*************************************************************************************************/

public class UndoStack
{
  private ArrayList<IUndoCommand> m_stack; // stack of undo commands
  private int                    m_index; // current command

  /**************************************** constructor ******************************************/
  public UndoStack()
  {
    // initialise private variables
    m_stack = new ArrayList<IUndoCommand>();
    m_index = 0;
  }

  /******************************************** push *********************************************/
  public void push( IUndoCommand command )
  {
    // remove any commands from stack that haven't been actioned (i.e. above index)
    while ( m_stack.size() > m_index )
      m_stack.remove( m_stack.size() - 1 );

    // add new command to stack, do it, and increment stack index
    m_stack.add( command );
    command.redo();
    m_index++;
    updateUndoRedoMenuItems();

    // update undo-stack window if exists
    if ( MainWindow.undoWindow != null )
      MainWindow.undoWindow.updateList( command, m_index );
  }

  /******************************************** undo *********************************************/
  public void undo()
  {
    // decrement index and revert command
    m_index--;
    m_stack.get( m_index ).undo();
    updateUndoRedoMenuItems();
  }

  /******************************************** redo *********************************************/
  public void redo()
  {
    // action command and increment index
    m_stack.get( m_index ).redo();
    m_index++;
    updateUndoRedoMenuItems();
  }

  /*********************************** updateUndoRedoMenuItems ***********************************/
  private void updateUndoRedoMenuItems()
  {
    // update undo menu-item
    MenuItem undo = MainWindow.actionUndo;
    if ( m_index > 0 )
    {
      undo.setText( "Undo " + undoText() + "\tCtrl+Z" );
      undo.setEnabled( true );
    }
    else
    {
      undo.setText( "Undo\tCtrl+Z" );
      undo.setEnabled( false );
    }

    // update redo menu-item
    MenuItem redo = MainWindow.actionRedo;
    if ( m_index < size() )
    {
      redo.setText( "Redo " + redoText() + "\tCtrl+Y" );
      redo.setEnabled( true );
    }
    else
    {
      redo.setText( "Redo\tCtrl+Y" );
      redo.setEnabled( false );
    }

    // also update undo-stack window selected item if exists
    if ( MainWindow.undoWindow != null )
      MainWindow.undoWindow.updateSelection();
  }

  /****************************************** undoText *******************************************/
  public String undoText()
  {
    // return text associated with next potential undo
    return m_stack.get( m_index - 1 ).text();
  }

  /****************************************** redoText *******************************************/
  public String redoText()
  {
    // return text associated with next potential redo
    return m_stack.get( m_index ).text();
  }

  /******************************************** text *********************************************/
  public String text( int index )
  {
    // return text associated with command at index
    return m_stack.get( index ).text();
  }

  /******************************************** clear ********************************************/
  public void clear()
  {
    // clean the stack
    m_stack.clear();
    m_index = 0;
    updateUndoRedoMenuItems();
  }

  /******************************************** size *********************************************/
  public int size()
  {
    // return stack size
    return m_stack.size();
  }

  /******************************************* index *********************************************/
  public int index()
  {
    // return command index
    return m_index;
  }

}
