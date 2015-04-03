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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/*************************************************************************************************/
/******************************* KeyAdapter for table cell editors *******************************/
/*************************************************************************************************/

public class XKeyAdapter extends KeyAdapter
{
  private AbstractCellEditor m_cellEditor; // NatTable cell editor
  private Control            m_editor;    // SWT editor

  /**************************************** constructor ******************************************/
  public XKeyAdapter( AbstractCellEditor cellEditor, Control editor )
  {
    // call super constructor and initialise private variables
    super();
    m_cellEditor = cellEditor;
    m_editor = editor;
  }

  /***************************************** keyPressed ******************************************/
  @Override
  public void keyPressed( KeyEvent event )
  {
    // if escape pressed, close without committing
    if ( event.keyCode == SWT.ESC )
      m_cellEditor.close();

    // if carriage-return pressed, commit
    if ( event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR )
      m_cellEditor.commit( MoveDirectionEnum.NONE );

    // if arrow-up pressed, commit and move up
    if ( event.keyCode == SWT.ARROW_UP && m_editor instanceof Spinner == false )
      m_cellEditor.commit( MoveDirectionEnum.UP );

    // if arrow-down pressed, commit and move down
    if ( event.keyCode == SWT.ARROW_DOWN && m_editor instanceof Spinner == false )
      m_cellEditor.commit( MoveDirectionEnum.DOWN );

    // if tab pressed, commit and move left or right
    if ( event.keyCode == SWT.TAB )
    {
      if ( event.stateMask == SWT.SHIFT )
        m_cellEditor.commit( MoveDirectionEnum.LEFT );
      else
        m_cellEditor.commit( MoveDirectionEnum.RIGHT );
    }

  }

}
