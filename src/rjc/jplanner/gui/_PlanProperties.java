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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;

public class _PlanProperties extends Composite
{
  private Text            m_titleText;
  private _DateTimeEditor m_startDT;
  private Text            m_firstText;
  private Text            m_endText;
  private Combo           m_calCombo;
  private Text            m_formatText;
  private Text            m_filenameText;
  private Text            m_filelocText;
  private Text            m_savedbyText;
  private Text            m_savedwhenText;
  private Label           m_tasksNum;
  private Label           m_resourcesNum;
  private Label           m_calendarsNum;
  private Label           m_daysNum;

  /**************************************** constructor ******************************************/
  public _PlanProperties( Composite parent, int style )
  {
    super( parent, style );
    setLayout( new GridLayout( 2, false ) );

    Label titleLabel = new Label( this, SWT.NONE );
    titleLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    titleLabel.setText( "Title" );

    m_titleText = new Text( this, SWT.BORDER );
    m_titleText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label startLabel = new Label( this, SWT.NONE );
    startLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    startLabel.setText( "Start" );

    m_startDT = new _DateTimeEditor( this, SWT.NONE );
    m_startDT.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label earliestLabel = new Label( this, SWT.NONE );
    earliestLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    earliestLabel.setText( "First" );

    m_firstText = new Text( this, SWT.BORDER );
    m_firstText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label endLabel = new Label( this, SWT.NONE );
    endLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    endLabel.setText( "End" );

    m_endText = new Text( this, SWT.BORDER );
    m_endText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label calLabel = new Label( this, SWT.NONE );
    calLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    calLabel.setText( "Default Calendar" );

    m_calCombo = new XComboCalendars( this, SWT.READ_ONLY );
    m_calCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label formatLabel = new Label( this, SWT.NONE );
    formatLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    formatLabel.setText( "Date time format" );

    m_formatText = new Text( this, SWT.BORDER );
    m_formatText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label filenameLabel = new Label( this, SWT.NONE );
    filenameLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    filenameLabel.setText( "File name" );

    m_filenameText = new Text( this, SWT.BORDER );
    m_filenameText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label filelocLabel = new Label( this, SWT.NONE );
    filelocLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    filelocLabel.setText( "File location" );

    m_filelocText = new Text( this, SWT.BORDER );
    m_filelocText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label savedbyLabel = new Label( this, SWT.NONE );
    savedbyLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    savedbyLabel.setText( "Saved by" );

    m_savedbyText = new Text( this, SWT.BORDER );
    m_savedbyText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Label savedwhenLabel = new Label( this, SWT.NONE );
    savedwhenLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
    savedwhenLabel.setText( "Saved when" );

    m_savedwhenText = new Text( this, SWT.BORDER );
    m_savedwhenText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    Group grpNumberOf = new Group( this, SWT.NONE );
    grpNumberOf.setLayout( new GridLayout( 2, false ) );
    grpNumberOf.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
    grpNumberOf.setText( "Number of" );

    Label tasksLabel = new Label( grpNumberOf, SWT.NONE );
    tasksLabel.setText( "Tasks" );

    m_tasksNum = new Label( grpNumberOf, SWT.NONE );
    m_tasksNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_tasksNum.setText( ": 0" );

    Label resourcesLabel = new Label( grpNumberOf, SWT.NONE );
    resourcesLabel.setText( "Resources" );

    m_resourcesNum = new Label( grpNumberOf, SWT.NONE );
    m_resourcesNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_resourcesNum.setText( ": 0" );

    Label calendarsLabel = new Label( grpNumberOf, SWT.NONE );
    calendarsLabel.setText( "Calendars" );

    m_calendarsNum = new Label( grpNumberOf, SWT.NONE );
    m_calendarsNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_calendarsNum.setText( ": 0" );

    Label daysLabel = new Label( grpNumberOf, SWT.NONE );
    daysLabel.setText( "Days" );

    m_daysNum = new Label( grpNumberOf, SWT.NONE );
    m_daysNum.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
    m_daysNum.setText( ": 0" );
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
    m_titleText.setText( JPlanner.plan.title() );
    m_startDT.setDateTime( JPlanner.plan.start() );
    m_calCombo.setText( JPlanner.plan.calendar().name() );
    m_formatText.setText( JPlanner.plan.datetimeFormat() );
    m_filenameText.setText( JPlanner.plan.filename() );
    m_filelocText.setText( JPlanner.plan.fileLocation() );
    m_savedbyText.setText( JPlanner.plan.savedBy() );

    try
    {
      m_savedwhenText.setText( JPlanner.plan.savedWhen().toString() );
    }
    catch (NullPointerException e)
    {
      m_savedwhenText.setText( "" );
    }

    // update the gui "number of" widgets
    m_tasksNum.setText( ": " + JPlanner.plan.tasksCount() );
    m_resourcesNum.setText( ": " + JPlanner.plan.resourcesCount() );
    m_calendarsNum.setText( ": " + JPlanner.plan.calendarsCount() );
    m_daysNum.setText( ": " + JPlanner.plan.daysCount() );
  }
}
