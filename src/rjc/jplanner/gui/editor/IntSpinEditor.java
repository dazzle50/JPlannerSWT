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

package rjc.jplanner.gui.editor;

import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/*************************************************************************************************/
/*********************** Specialised editor for integers using SWT Spinner ***********************/
/*************************************************************************************************/

public class IntSpinEditor extends AbstractCellEditor
{
  private Spinner spinner; // SWT spinner

  /************************************ createEditorControl **************************************/
  @Override
  public Control createEditorControl( Composite parent )
  {
    // create editor
    spinner = new Spinner( parent, SWT.CENTER );
    spinner.setMaximum( 10 );
    spinner.setPageIncrement( 1 );

    // add a modify listener to prevent spinner text going above max
    spinner.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( ModifyEvent e )
      {
        int num = Integer.parseInt( spinner.getText() );
        if ( num > spinner.getMaximum() )
          spinner.setSelection( spinner.getMaximum() );
      }
    } );

    // add a key listener to commit or close the editor for special key strokes
    spinner.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR )
        {
          boolean commit = ( event.stateMask == SWT.ALT ) ? false : true;
          MoveDirectionEnum move = MoveDirectionEnum.NONE;
          if ( event.stateMask == 0 )
            move = MoveDirectionEnum.DOWN;
          else if ( event.stateMask == SWT.SHIFT )
            move = MoveDirectionEnum.UP;

          if ( commit )
            commit( move );
        }
        else if ( event.keyCode == SWT.ESC && event.stateMask == 0 )
          close();
      }
    } );

    return spinner;
  }

  /************************************* getEditorControl ****************************************/
  @Override
  public Control getEditorControl()
  {
    // return the editor
    return spinner;
  }

  /************************************** getEditorValue *****************************************/
  @Override
  public Object getEditorValue()
  {
    // return editor value (as a String)
    return String.format( "%d", spinner.getSelection() );
  }

  /************************************** setEditorValue *****************************************/
  @Override
  public void setEditorValue( Object value )
  {
    // set editor value
    spinner.setSelection( Integer.parseInt( value.toString() ) );
  }

  /*************************************** activateCell ******************************************/
  @Override
  protected Control activateCell( Composite parent, Object value )
  {
    // create editor, set value and focus
    createEditorControl( parent );
    setEditorValue( value );
    spinner.setFocus();
    return spinner;
  }

}
