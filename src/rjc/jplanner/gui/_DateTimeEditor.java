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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class _DateTimeEditor extends Composite
{

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public _DateTimeEditor( Composite parent, int style )
  {
    super( parent, style );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );

    DateTime date = new DateTime( this, SWT.DROP_DOWN );

    Menu menu = new Menu( date );
    date.setMenu( menu );

    MenuItem workForward = new MenuItem( menu, SWT.NONE );
    workForward.setText( "Forward to next work period start" );

    MenuItem workBack = new MenuItem( menu, SWT.NONE );
    workBack.setText( "Back to previous work period end" );

    DateTime time = new DateTime( this, SWT.BORDER | SWT.TIME );

    Menu popup = new Menu( time );
    MenuItem workStart = new MenuItem( popup, SWT.CASCADE );
    workStart.setText( "Start of work day" );
    time.setMenu( popup );

    MenuItem workEnd = new MenuItem( popup, SWT.NONE );
    workEnd.setText( "End of work day" );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /***************************************** setDateTime *****************************************/
  public void setDateTime( rjc.jplanner.model.DateTime start )
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  }
}
