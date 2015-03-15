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

package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import rjc.jplanner.JPlanner;
import rjc.jplanner.command.UndoCommand;

/*************************************************************************************************/
/**************************** Window for plan undo-stack command list ****************************/
/*************************************************************************************************/

public class UndoStackWindow extends Shell
{
  private List m_list; // list showing undo stack commands

  /**************************************** constructor ******************************************/
  public UndoStackWindow( Display display )
  {
    super( display, SWT.SHELL_TRIM );
    setSize( 248, 200 );
    setText( "Undo stack" );
    setLayout( new FillLayout( SWT.FILL ) );

    // inform the main window if this window is disposed (i.e. closed by user pressing X in corner)
    addDisposeListener( new DisposeListener()
    {
      @Override
      public void widgetDisposed( DisposeEvent e )
      {
        MainWindow.undoWindow = null;

        if ( !MainWindow.actionUndoStackView.isDisposed() )
          MainWindow.actionUndoStackView.setSelection( false );
      }
    } );

    // create list to show commands
    m_list = new List( this, SWT.V_SCROLL );
    setList();
    m_list.showSelection();

    // if the selected item is above or below current undostack index then redo or undo
    m_list.addListener( SWT.Selection, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        int index = m_list.getSelectionIndex();

        if ( index > JPlanner.plan.undostack().index() )
        {
          int num = index - JPlanner.plan.undostack().index();
          for ( int i = 1; i <= num; i++ )
            JPlanner.plan.undostack().redo();

          return;
        }

        if ( index < JPlanner.plan.undostack().index() )
        {
          int num = JPlanner.plan.undostack().index() - index;
          for ( int i = 1; i <= num; i++ )
            JPlanner.plan.undostack().undo();

          return;
        }

      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /******************************************* setList *******************************************/
  public void setList()
  {
    // initialise list with current plan undo-stack list of commands
    m_list.removeAll();
    m_list.add( "<empty>" );

    int size = JPlanner.plan.undostack().size();
    for ( int i = 0; i < size; i++ )
      m_list.add( JPlanner.plan.undostack().text( i ) );

    m_list.setSelection( JPlanner.plan.undostack().index() );
  }

  /***************************************** updateList ******************************************/
  public void updateList( UndoCommand command, int index )
  {
    // update list with command at position index, first delete any items below new index
    for ( int i = m_list.getItemCount() - 1; i >= index; i-- )
      m_list.remove( i );

    // add new command and set selection
    m_list.add( command.text() );
    m_list.setSelection( index );
  }

  /*************************************** updateSelection ***************************************/
  public void updateSelection()
  {
    // ensure list selected item matches undo-stack index
    m_list.setSelection( JPlanner.plan.undostack().index() );
  }

}
