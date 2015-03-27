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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/******************************** Editor for day-type table cells ********************************/
/*************************************************************************************************/

public class DayCellEditor extends AbstractCellEditor
{
  private Control m_editor; // editor for cell

  /************************************ createEditorControl **************************************/
  @Override
  public Control createEditorControl( Composite parent )
  {
    // create editor based on column
    m_editor = null;
    int row = getRowIndex();
    int col = getColumnIndex();
    JPlanner.trace( "DayCellEditor - createEditorControl    col=" + col + "   row=" + row );

    if ( col == Day.SECTION_NAME )
    {
      m_editor = new Text( parent, SWT.SINGLE );
    }

    if ( col == Day.SECTION_WORK )
    {
      Spinner spin = new Spinner( parent, SWT.NONE );
      spin.setDigits( 2 ); // 2 decimal places
      spin.setMinimum( 0 ); // min 0.00
      spin.setMaximum( 1000 ); // max 10.00
      spin.setIncrement( 10 ); // step 0.10
      spin.setPageIncrement( 100 ); // page 1.00
      m_editor = spin;
    }

    if ( col == Day.SECTION_PERIODS )
    {
      Spinner spin = new Spinner( parent, SWT.NONE );
      spin.setMinimum( 0 ); // min 0
      spin.setMaximum( 9 ); // max 9
      m_editor = spin;
    }

    if ( col >= Day.SECTION_START1 )
    {
      // TODO - use Text editor until find/write something better
      m_editor = new Text( parent, SWT.SINGLE );
    }

    m_editor.addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        // if escape pressed, close without committing
        if ( event.keyCode == SWT.ESC )
          close();

        // if carriage-return pressed, commit
        if ( event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR )
          commit( MoveDirectionEnum.NONE );

        // if arrow-up pressed, commit and move up
        if ( event.keyCode == SWT.ARROW_UP && m_editor instanceof Spinner == false )
          commit( MoveDirectionEnum.UP );

        // if arrow-down pressed, commit and move down
        if ( event.keyCode == SWT.ARROW_DOWN && m_editor instanceof Spinner == false )
          commit( MoveDirectionEnum.DOWN );

        // if tab pressed, commit and move
        if ( event.keyCode == SWT.TAB )
        {
          if ( event.stateMask == SWT.SHIFT )
            commit( MoveDirectionEnum.LEFT );
          else
            commit( MoveDirectionEnum.RIGHT );
        }

      }
    } );

    return m_editor;
  }

  /************************************* getEditorControl ****************************************/
  @Override
  public Control getEditorControl()
  {
    // return the editor
    return m_editor;
  }

  /************************************** getEditorValue *****************************************/
  @Override
  public Object getEditorValue()
  {
    // return editor value (as a String)
    int col = getColumnIndex();

    if ( col == Day.SECTION_NAME )
      return ( (Text) m_editor ).getText();

    if ( col == Day.SECTION_WORK || col == Day.SECTION_PERIODS )
      return ( (Spinner) m_editor ).getText();

    // must be a work period start or end
    return ( (Text) m_editor ).getText();
  }

  /************************************** setEditorValue *****************************************/
  @Override
  public void setEditorValue( Object value )
  {
    // set editor value
    int col = getColumnIndex();
    String str = value.toString();
    JPlanner.trace( "setEditorValue '" + value + "'" );

    // if day name, set text
    if ( col == Day.SECTION_NAME )
    {
      ( (Text) m_editor ).setText( str );
      ( (Text) m_editor ).selectAll();
    }

    // if day work, set spinner catching if not valid number
    if ( col == Day.SECTION_WORK )
    {
      if ( str.equals( "." ) )
        str = "0.";
      try
      {
        ( (Spinner) m_editor ).setSelection( (int) ( 100 * Float.parseFloat( str ) ) );
      }
      catch (NumberFormatException e)
      {
        Double work = JPlanner.plan.day( getRowIndex() ).work();
        ( (Spinner) m_editor ).setSelection( (int) ( 100 * work ) );
      }
    }

    // if day number of work periods, set spinner catching if not valid number
    if ( col == Day.SECTION_PERIODS )
    {
      try
      {
        ( (Spinner) m_editor ).setSelection( Integer.parseInt( str ) );
      }
      catch (NumberFormatException e)
      {
        int periods = JPlanner.plan.day( getRowIndex() ).numPeriods();
        ( (Spinner) m_editor ).setSelection( periods );
      }
    }

    // otherwise its a work period start or end
    if ( col >= Day.SECTION_START1 )
    {
      ( (Text) m_editor ).setText( str );
      ( (Text) m_editor ).selectAll();
    }

  }

  /*************************************** activateCell ******************************************/
  @Override
  protected Control activateCell( Composite parent, Object value )
  {
    // create editor, set value and focus
    createEditorControl( parent );
    setEditorValue( value );
    m_editor.setFocus();
    return m_editor;
  }

}
