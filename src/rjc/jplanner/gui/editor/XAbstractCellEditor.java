/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlannerSWT                               *
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/**************************** Abstract cell editor to JPlanner tables ****************************/
/*************************************************************************************************/

public abstract class XAbstractCellEditor extends AbstractCellEditor
{
  private Control                   m_editor;             // editor for cell

  public static XAbstractCellEditor cellEditorInProgress; // in progress cell editor, or null

  /************************************ createEditorControl **************************************/
  @Override
  public Control createEditorControl( Composite parent )
  {
    // create editor based on column
    cellEditorInProgress = this;
    m_editor = createEditor( getRowIndex(), getColumnIndex() );

    // add key listener for escape & carriage-return
    getEditorPrime().addKeyListener( new KeyAdapter()
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
      }
    } );

    // add key listener for up & down arrow (some editor types only)
    if ( m_editor instanceof SpinEditor == false )
      getEditorPrime().addKeyListener( new KeyAdapter()
      {
        @Override
        public void keyPressed( KeyEvent event )
        {
          // if arrow-up pressed, commit and move up
          if ( event.keyCode == SWT.ARROW_UP )
            commit( MoveDirectionEnum.UP );

          // if arrow-down pressed, commit and move down
          if ( event.keyCode == SWT.ARROW_DOWN )
            commit( MoveDirectionEnum.DOWN );
        }
      } );

    // add listener for losing focus, close editor if in error state
    getEditorPrime().addListener( SWT.FocusOut, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        if ( getEditorPrime().getForeground().equals( JPlanner.gui.COLOR_ERROR ) )
        {
          JPlanner.gui.message( "" );
          close();
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
    // return the control that needs disposing when editor closed etc
    return m_editor;
  }

  /*************************************** getEditorPrime ****************************************/
  public Control getEditorPrime()
  {
    // return control that actually receives the key press events
    if ( m_editor instanceof SpinEditor )
      return ( (SpinEditor) m_editor ).getPrimeEditor();

    if ( m_editor instanceof TextEditor )
      return ( (TextEditor) m_editor ).getPrimeEditor();

    if ( m_editor instanceof XCombo )
      return ( (XCombo) m_editor ).getPrimeEditor();

    // other editors are simple
    return m_editor;
  }

  /************************************** getEditorValue *****************************************/
  @Override
  public Object getEditorValue()
  {
    if ( m_editor instanceof XCombo )
      return ( (XCombo) m_editor ).getPrimeEditor().getText();

    if ( m_editor instanceof TimeEditor )
      return ( (TimeEditor) m_editor ).getPrimeEditor().getText();

    if ( m_editor instanceof TimeSpanEditor )
      return ( (TimeSpanEditor) m_editor ).getPrimeEditor().getText();

    if ( m_editor instanceof SpinEditor )
      return ( (SpinEditor) m_editor ).getText();

    if ( m_editor instanceof TextEditor )
      return JPlanner.clean( ( (TextEditor) m_editor ).getText() );

    // editor class type not handled
    throw new ClassCastException( m_editor.toString() );
  }

  /************************************** setEditorValue *****************************************/
  @Override
  public void setEditorValue( Object value )
  {
    // set editor value
    String str = "";
    if ( value != null )
      str = value.toString();

    // set editor value
    if ( m_editor instanceof TextEditor )
      ( (TextEditor) m_editor ).setText( str );

    if ( m_editor instanceof TimeSpanEditor )
      ( (TimeSpanEditor) m_editor ).setText( str );

    if ( m_editor instanceof SpinEditor )
      ( (SpinEditor) m_editor ).setText( str );

    if ( m_editor instanceof XCombo )
      ( (XCombo) m_editor ).setText( str );
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

  /************************************ validateCanonicalValue ***********************************/
  @Override
  public boolean validateCanonicalValue( Object canonicalValue )
  {
    // simple rule, if editor foreground colour indicates error, don't allow commit
    if ( getEditorPrime().getForeground().equals( JPlanner.gui.COLOR_ERROR ) )
      return false;

    // not red, so allow commit
    return true;
  }

  /******************************************** close ********************************************/
  @Override
  public void close()
  {
    // SpinEditor needs to remove listener from parent before closing
    if ( m_editor instanceof SpinEditor )
      ( (SpinEditor) m_editor ).removeListeners();

    // editor being closed, so clear cell editor in progress
    cellEditorInProgress = null;
    super.close();
  }

  /******************************************* commit ********************************************/
  @Override
  public boolean commit( MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation )
  {
    // SpinEditor needs to remove listener from parent before closing
    if ( m_editor instanceof SpinEditor )
      ( (SpinEditor) m_editor ).removeListeners();

    // editor being committed, so clear cell editor in progress
    cellEditorInProgress = null;
    return super.commit( direction, closeAfterCommit, skipValidation );
  }

  /***************************************** endEditing ******************************************/
  public void endEditing()
  {
    // close or commit editor depending if in error state
    if ( getEditorPrime().getForeground().equals( JPlanner.gui.COLOR_ERROR ) )
      close();
    else
      commit( MoveDirectionEnum.NONE );
  }

}
