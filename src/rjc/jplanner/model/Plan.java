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

package rjc.jplanner.model;

import java.util.ArrayList;

import rjc.jplanner.command.UndoCommand;
import rjc.jplanner.command.UndoStack;
import rjc.jplanner.model.Calendar.DefaultCalendarTypes;
import rjc.jplanner.model.Day.DefaultDayTypes;

/*************************************************************************************************/
/************************** Holds the complete data model for the plan ***************************/
/*************************************************************************************************/

public class Plan
{
  private String              m_title;         // plan title as set in properties
  private DateTime            m_start;         // plan start date-time as set in properties
  private Calendar            m_calendar;      // plan's default calendar
  private String              m_datetimeFormat; // format to display date-times
  private String              m_filename;      // filename when saved or loaded
  private String              m_fileLocation;  // file location
  private String              m_savedBy;       // who saved last
  private DateTime            m_savedWhen;     // when was last saved
  private String              m_notes;         // plan notes as on plan tab

  private UndoStack           m_undostack;     // undo stack for plan editing

  private ArrayList<Task>     m_tasks;         // list of plan tasks
  private ArrayList<Resource> m_resources;     // list of plan resources
  private ArrayList<Calendar> m_calendars;     // list of plan calendars
  private ArrayList<Day>      m_daytypes;      // list of plan day types

  /**************************************** constructor ******************************************/
  public Plan()
  {
    // construct empty but usable Plan
    m_tasks = new ArrayList<Task>();
    m_resources = new ArrayList<Resource>();
    m_calendars = new ArrayList<Calendar>();
    m_daytypes = new ArrayList<Day>();
    m_undostack = new UndoStack();
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // convert to string
    String hash = super.toString();
    String id = hash.substring( hash.lastIndexOf( '.' ) + 1 );
    return id + "[" + m_title + ", " + m_start + ", " + m_tasks.size() + " Tasks, " + m_resources.size()
        + " Resources, " + m_calendars.size() + " Calendars, " + m_daytypes.size() + " DayTypes]";
  }

  /**************************************** initialise *******************************************/
  public void initialise()
  {
    // initialise plan with default settings and contents
    m_daytypes.clear();
    for ( DefaultDayTypes type : DefaultDayTypes.values() )
      m_daytypes.add( new Day( type ) );

    m_calendars.clear();
    for ( DefaultCalendarTypes type : DefaultCalendarTypes.values() )
      m_calendars.add( new Calendar( type ) );

    m_resources.clear();
    for ( int count = 0; count < 5; count++ )
      m_resources.add( new Resource() );

    m_tasks.clear();
    for ( int count = 0; count < 10; count++ )
      m_tasks.add( new Task() );

    m_title = "";
    m_start = DateTime.now();
    m_calendar = calendar( 0 );
    m_datetimeFormat = "ddd dd/MM/yyyy hh:mm";
    m_filename = "";
    m_fileLocation = "";
    m_savedBy = "";
  }

  /************************************** tasksNotNullCount **************************************/
  public int tasksNotNullCount()
  {
    // return number of not-null tasks in plan
    int count = 0;
    for ( int i = 0; i < m_tasks.size(); i++ )
      if ( !m_tasks.get( i ).isNull() )
        count++;

    return count;
  }

  /************************************ resourcesNotNullCount ************************************/
  public int resourcesNotNullCount()
  {
    // return number of not-null resources in plan
    int count = 0;
    for ( int i = 0; i < m_resources.size(); i++ )
      if ( !m_resources.get( i ).isNull() )
        count++;

    return count;
  }

  /**************************************** tasksCount *******************************************/
  public int tasksCount()
  {
    // return number of tasks in plan
    return m_tasks.size();
  }

  /************************************** resourcesCount *****************************************/
  public int resourcesCount()
  {
    // return number of resources in plan
    return m_resources.size();
  }

  /************************************** calendarsCount *****************************************/
  public int calendarsCount()
  {
    // return number of calendars in plan
    return m_calendars.size();
  }

  /***************************************** daysCount *******************************************/
  public int daysCount()
  {
    // return number of day-types in plan
    return m_daytypes.size();
  }

  /******************************************* task **********************************************/
  public Task task( int index )
  {
    // return task corresponding to index
    return m_tasks.get( index );
  }

  /***************************************** resource ********************************************/
  public Resource resource( int index )
  {
    // return resource corresponding to index
    return m_resources.get( index );
  }

  /***************************************** calendar ********************************************/
  public Calendar calendar( int index )
  {
    // return calendar corresponding to index
    return m_calendars.get( index );
  }

  /********************************************* day *********************************************/
  public Day day( int index )
  {
    // return day-type corresponding to index
    return m_daytypes.get( index );
  }

  /******************************************** title ********************************************/
  public String title()
  {
    return m_title;
  }

  /******************************************** notes ********************************************/
  public String notes()
  {
    return m_notes;
  }

  /******************************************** start ********************************************/
  public DateTime start()
  {
    return m_start;
  }

  /******************************************* calendar ******************************************/
  public Calendar calendar()
  {
    return m_calendar;
  }

  /**************************************** datetimeFormat ***************************************/
  public String datetimeFormat()
  {
    return m_datetimeFormat;
  }

  /******************************************* filename ******************************************/
  public String filename()
  {
    return m_filename;
  }

  /***************************************** fileLocation ****************************************/
  public String fileLocation()
  {
    return m_fileLocation;
  }

  /******************************************* savedBy *******************************************/
  public String savedBy()
  {
    return m_savedBy;
  }

  /****************************************** savedWhen ******************************************/
  public DateTime savedWhen()
  {
    return m_savedWhen;
  }

  /****************************************** undostack ******************************************/
  public UndoStack undostack()
  {
    return m_undostack;
  }

  /******************************************** push *********************************************/
  public void push( UndoCommand command )
  {
    // push new command onto undo stack
    m_undostack.push( command );
  }

}
