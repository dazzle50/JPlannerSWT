/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlanner                                  *
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/*************************************************************************************************/
/**************************** Abstract cell editor to JPlanner tables ****************************/
/*************************************************************************************************/

public abstract class XAbstractCellEditor extends AbstractCellEditor
{
  private Control m_editor;            // editor for cell
  private boolean m_upDownArrowsCommit;

  /************************************ createEditorControl **************************************/
  @Override
  public Control createEditorControl( Composite parent )
  {
    // create editor based on column
    m_editor = createEditor( getRowIndex(), getColumnIndex() );

    // identify control that receives key presses & whether arrow-up & down should commit
    Control editor = m_editor;
    m_upDownArrowsCommit = true;
    if ( m_editor instanceof TimeSpanEditor )
    {
      editor = ( (TimeSpanEditor) m_editor ).getEditor();
      m_upDownArrowsCommit = false;
    }
    if ( m_editor instanceof Spinner )
      m_upDownArrowsCommit = false;

    // add key listener for closing & committing
    editor.addKeyListener( new KeyAdapter()
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

        if ( m_upDownArrowsCommit )
        {
          // if arrow-up pressed, commit and move up
          if ( event.keyCode == SWT.ARROW_UP )
            commit( MoveDirectionEnum.UP );

          // if arrow-down pressed, commit and move down
          if ( event.keyCode == SWT.ARROW_DOWN )
            commit( MoveDirectionEnum.DOWN );
        }
      }
    } );

    return m_editor;
  }

  protected abstract Control createEditor( int row, int col );

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
    if ( m_editor instanceof Spinner )
      return ( (Spinner) m_editor ).getText();

    if ( m_editor instanceof Combo )
      return ( (Combo) m_editor ).getText();

    if ( m_editor instanceof TimeSpanEditor )
      return ( (TimeSpanEditor) m_editor ).getText();

    // none of above, therefore must be a Text editor
    return ( (Text) m_editor ).getText();
  }

  /************************************** setEditorValue *****************************************/
  @Override
  public void setEditorValue( Object value )
  {
    // set editor value
    if ( value == null )
      value = "";
    setEditor( m_editor, value.toString(), getRowIndex(), getColumnIndex() );
  }

  protected abstract void setEditor( Control editor, String value, int row, int col );

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
