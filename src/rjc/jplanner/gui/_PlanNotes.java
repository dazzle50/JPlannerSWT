/**************************************************************************
 *  ######## WRITTEN USING WindowBuilder Editor ########                  *
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class _PlanNotes extends Composite
{

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public _PlanNotes( Composite parent, int style )
  {
    super( parent, style );
    setLayout( new GridLayout( 1, false ) );

    Label notesLabel = new Label( this, SWT.NONE );
    notesLabel.setText( "Notes" );

    StyledText notesText = new StyledText( this, SWT.BORDER | SWT.V_SCROLL );
    notesText.setAlwaysShowScrollBars( false );
    notesText.setWordWrap( true );
    notesText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
