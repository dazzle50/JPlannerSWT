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

package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rjc.jplanner.JPlanner;
import rjc.jplanner.command.CommandPlanSetNotes;

/*************************************************************************************************/
/**************************** Widget display & editing of plan notes *****************************/
/*************************************************************************************************/

public class PlanNotes extends Composite
{
  StyledText m_notesText;

  /**************************************** constructor ******************************************/
  public PlanNotes( Composite parent, int style )
  {
    super( parent, style );
    setLayout( new GridLayout( 1, false ) );

    Label notesLabel = new Label( this, SWT.NONE );
    notesLabel.setText( "Notes" );

    m_notesText = new StyledText( this, SWT.BORDER | SWT.V_SCROLL );
    m_notesText.setAlwaysShowScrollBars( false );
    m_notesText.setWordWrap( true );
    m_notesText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
  }

  /*************************************** updateFromPlan ****************************************/
  public void updateFromPlan()
  {
    // update the gui noteswith text from plan
    m_notesText.setText( JPlanner.plan.notes() );
  }

  /************************************** updatePlanNotes ****************************************/
  public void updatePlan()
  {
    // if notes not changed, return doing nothing, otherwise update via undostack command
    if ( JPlanner.plan.notes().equals( m_notesText.getText() ) )
      return;

    JPlanner.plan.undostack().push( new CommandPlanSetNotes( m_notesText.getText() ) );
  }
}
