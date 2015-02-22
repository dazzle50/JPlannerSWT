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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

public class _PlanProperties extends Composite
{
  private Text            titleText;
  private _DateTimeEditor startDT;
  private Text            firstText;
  private Text            endText;
  private Text            calText;
  private Text            formatText;
  private Text            filenameText;
  private Text            filelocText;
  private Text            savedbyText;
  private Text            savedwhenText;
  private Label           tasksNum;
  private Label           resourcesNum;
  private Label           calendarsNum;
  private Label           daysNum;

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public _PlanProperties( Composite parent, int style )
  {
    super( parent, style );
    setLayout( new GridLayout( 2, false ) );

    Label titleLabel = new Label( this, SWT.NONE );
    titleLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    titleLabel.setText( "Title" );

    titleText = new Text( this, SWT.BORDER );
    titleText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label startLabel = new Label( this, SWT.NONE );
    startLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    startLabel.setText( "Start" );

    startDT = new _DateTimeEditor( this, SWT.NONE );
    startDT.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label earliestLabel = new Label( this, SWT.NONE );
    earliestLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    earliestLabel.setText( "First" );

    firstText = new Text( this, SWT.BORDER );
    firstText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label endLabel = new Label( this, SWT.NONE );
    endLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    endLabel.setText( "End" );

    endText = new Text( this, SWT.BORDER );
    endText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label calLabel = new Label( this, SWT.NONE );
    calLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    calLabel.setText( "Default Calendar" );

    calText = new Text( this, SWT.BORDER );
    calText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label formatLabel = new Label( this, SWT.NONE );
    formatLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    formatLabel.setText( "Date time format" );

    formatText = new Text( this, SWT.BORDER );
    formatText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label filenameLabel = new Label( this, SWT.NONE );
    filenameLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    filenameLabel.setText( "File name" );

    filenameText = new Text( this, SWT.BORDER );
    filenameText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label filelocLabel = new Label( this, SWT.NONE );
    filelocLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    filelocLabel.setText( "File location" );

    filelocText = new Text( this, SWT.BORDER );
    filelocText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label savedbyLabel = new Label( this, SWT.NONE );
    savedbyLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    savedbyLabel.setText( "Saved by" );

    savedbyText = new Text( this, SWT.BORDER );
    savedbyText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label savedwhenLabel = new Label( this, SWT.NONE );
    savedwhenLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    savedwhenLabel.setText( "Saved when" );

    savedwhenText = new Text( this, SWT.BORDER );
    savedwhenText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Group grpNumberOf = new Group( this, SWT.NONE );
    grpNumberOf.setLayout( new GridLayout( 2, false ) );
    grpNumberOf.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
    grpNumberOf.setText( "Number of" );

    Label tasksLabel = new Label( grpNumberOf, SWT.NONE );
    tasksLabel.setText( "Tasks" );

    tasksNum = new Label( grpNumberOf, SWT.NONE );
    tasksNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    tasksNum.setText( ": 0" );

    Label resourcesLabel = new Label( grpNumberOf, SWT.NONE );
    resourcesLabel.setText( "Resources" );

    resourcesNum = new Label( grpNumberOf, SWT.NONE );
    resourcesNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    resourcesNum.setText( ": 0" );

    Label calendarsLabel = new Label( grpNumberOf, SWT.NONE );
    calendarsLabel.setText( "Calendars" );

    calendarsNum = new Label( grpNumberOf, SWT.NONE );
    calendarsNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    calendarsNum.setText( ": 0" );

    Label daysLabel = new Label( grpNumberOf, SWT.NONE );
    daysLabel.setText( "Days" );

    daysNum = new Label( grpNumberOf, SWT.NONE );
    daysNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    daysNum.setText( ": 0" );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /*************************************** updateFromPlan ****************************************/
  public void updateFromPlan()
  {
    // update the gui property widgets with values from plan
    titleText.setText( JPlanner.plan.title() );
    startDT.setDateTime( JPlanner.plan.start() );
    calText.setText( JPlanner.plan.calendar().name() );
    formatText.setText( JPlanner.plan.datetimeFormat() );
    filenameText.setText( JPlanner.plan.filename() );
    filelocText.setText( JPlanner.plan.fileLocation() );
    savedbyText.setText( JPlanner.plan.savedBy() );

    try
    {
      savedwhenText.setText( JPlanner.plan.savedWhen().toString() );
    }
    catch (NullPointerException e)
    {
      savedwhenText.setText( "" );
    }

    // update the gui "number of" widgets
    tasksNum.setText( ": " + JPlanner.plan.tasksCount() );
    resourcesNum.setText( ": " + JPlanner.plan.resourcesCount() );
    calendarsNum.setText( ": " + JPlanner.plan.calendarsCount() );
    daysNum.setText( ": " + JPlanner.plan.daysCount() );
  }
}
