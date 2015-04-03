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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.model.Task;

/*************************************************************************************************/
/******************************** Editor for calendar table cells ********************************/
/*************************************************************************************************/

public class CalendarCellEditor extends AbstractCellEditor
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
    JPlanner.trace( "CalendarCellEditor - createEditorControl    col=" + col + "   row=" + row );

    if ( col == Task.SECTION_TITLE )
    {
      m_editor = new Text( parent, SWT.SINGLE );
    }

    // TODO

    m_editor.addKeyListener( new XKeyAdapter( this, m_editor ) );
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

    // TODO
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

    // TODO
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
