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

package rjc.jplanner.model;

import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.command.UndoStack;
import rjc.jplanner.model.Calendar.DefaultCalendarTypes;
import rjc.jplanner.model.Day.DefaultDayTypes;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

/*************************************************************************************************/
/************************** Holds the complete data model for the plan ***************************/
/*************************************************************************************************/

public class Plan
{
  private String              m_title;                              // plan title as set in properties
  private DateTime            m_start;                              // plan start date-time as set in properties
  private Calendar            m_calendar;                           // plan's default calendar
  private String              m_datetimeFormat;                     // format to display date-times
  private String              m_dateFormat;                         // format to display dates
  private String              m_filename;                           // filename when saved or loaded
  private String              m_fileLocation;                       // file location
  private String              m_savedBy;                            // who saved last
  private DateTime            m_savedWhen;                          // when was last saved
  private String              m_notes;                              // plan notes as on plan tab

  private UndoStack           m_undostack;                          // undo stack for plan editing

  private ArrayList<Task>     m_tasks;                              // list of plan tasks
  private ArrayList<Resource> m_resources;                          // list of plan resources
  private ArrayList<Calendar> m_calendars;                          // list of plan calendars
  private ArrayList<Day>      m_daytypes;                           // list of plan day types

  public static final String  XML_JPLANNER      = "JPlanner";
  public static final String  XML_VERSION       = "version";
  public static final String  XML_USER          = "user";
  public static final String  XML_WHEN          = "when";
  public static final String  XML_DAY_DATA      = "days-data";
  public static final String  XML_CAL_DATA      = "calendars-data";
  public static final String  XML_RES_DATA      = "resources-data";
  public static final String  XML_TASK_DATA     = "task-data";
  public static final String  XML_PLAN_DATA     = "plan-data";
  public static final String  XML_PLAN_TITLE    = "title";
  public static final String  XML_PLAN_START    = "start";
  public static final String  XML_PLAN_CALENDAR = "calendar";
  public static final String  XML_PLAN_DTF      = "datetime-format";
  public static final String  XML_PLAN_DF       = "date-format";
  public static final String  XML_PLAN_NOTES    = "notes";
  public static final String  XML_ID            = "id";
  public static final String  XML_DAY           = "day";
  public static final String  XML_CALENDAR      = "calendar";
  public static final String  XML_RESOURCE      = "resource";
  public static final String  XML_TASK          = "task";
  public static final String  XML_DAY_NAME      = "name";
  public static final String  XML_DAY_WORK      = "work";
  public static final String  XML_DAY_PERIOD    = "period";
  public static final String  XML_PERIOD_START  = "start";
  public static final String  XML_PERIOD_END    = "end";

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
    m_calendar = calendar( 0 );
    m_start = m_calendar.workUp( new DateTime( Date.now(), Time.fromMilliseconds( 0 ) ) );
    m_datetimeFormat = "EEE dd/MM/yyyy hh:mm";
    m_dateFormat = "dd/MM/yyyy";
    m_filename = "";
    m_fileLocation = "";
    m_savedBy = "";
    m_notes = "";
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
    // return number of tasks in plan (including null tasks)
    return m_tasks.size();
  }

  /************************************** resourcesCount *****************************************/
  public int resourcesCount()
  {
    // return number of resources in plan (including null resources)
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

  /******************************************** task *********************************************/
  public Task task( int index )
  {
    // return task corresponding to index
    return m_tasks.get( index );
  }

  /****************************************** resource *******************************************/
  public Resource resource( int index )
  {
    // return resource corresponding to index
    return m_resources.get( index );
  }

  /****************************************** calendar *******************************************/
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

  /******************************************** index ********************************************/
  public int index( Day day )
  {
    return m_daytypes.indexOf( day );
  }

  public int index( Calendar cal )
  {
    return m_calendars.indexOf( cal );
  }

  public int index( Resource res )
  {
    return m_resources.indexOf( res );
  }

  public int index( Task task )
  {
    return m_tasks.indexOf( task );
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

  /****************************************** earliest *******************************************/
  public DateTime earliest()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    return null;
  }

  /********************************************* end *********************************************/
  public DateTime end()
  {
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    return null;
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

  /****************************************** dateFormat *****************************************/
  public String dateFormat()
  {
    return m_dateFormat;
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

  /******************************************* setNotes ******************************************/
  public void setNotes( String notes )
  {
    m_notes = notes;
  }

  /******************************************* setTitle ******************************************/
  public void setTitle( String title )
  {
    m_title = title;
  }

  /******************************************* setStart ******************************************/
  public void setStart( DateTime start )
  {
    m_start = start;
  }

  /****************************************** setCalendar ****************************************/
  public void setCalendar( Calendar cal )
  {
    m_calendar = cal;
  }

  /*************************************** setDatetimeFormat *************************************/
  public void setDatetimeFormat( String DTformat )
  {
    m_datetimeFormat = DTformat;
  }

  /**************************************** setDateFormat ****************************************/
  public void setDateFormat( String Dformat )
  {
    m_dateFormat = Dformat;
  }

  /****************************************** savePlan *******************************************/
  public boolean savePlan( String fileName )
  {
    // if no file-name passed in, exit immediately returning false
    if ( fileName == null )
      return false;

    // attempt to save plan as XML to specified file
    try
    {
      // create XML stream writer
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      FileOutputStream file = new FileOutputStream( fileName );
      XMLStreamWriter xsw = new IndentingXMLStreamWriter( xof.createXMLStreamWriter( file, "UTF-8" ) );

      // start XML document
      xsw.writeStartDocument( "UTF-8", "1.0" );
      xsw.writeStartElement( XML_JPLANNER );
      xsw.writeAttribute( XML_VERSION, "2015-04" );
      xsw.writeAttribute( XML_USER, System.getProperty( "user.name" ) );
      xsw.writeAttribute( XML_WHEN, DateTime.now().toString() );

      // write day-types data to XML stream
      xsw.writeStartElement( XML_DAY_DATA );
      for ( Day day : m_daytypes )
        day.saveToXML( xsw );
      xsw.writeEndElement(); // XML_DAY_DATA

      // write calendars data to XML stream
      xsw.writeStartElement( XML_CAL_DATA );
      for ( Calendar cal : m_calendars )
        cal.saveToXML( xsw );
      xsw.writeEndElement(); // XML_CAL_DATA

      // write resources data to XML stream
      xsw.writeStartElement( XML_RES_DATA );
      for ( Resource res : m_resources )
        res.saveToXML( xsw );
      xsw.writeEndElement(); // XML_RES_DATA

      // write tasks data to XML stream
      xsw.writeStartElement( XML_TASK_DATA );
      for ( Task task : m_tasks )
        task.saveToXML( xsw );
      xsw.writeEndElement(); // XML_TASK_DATA

      // write plan data to XML stream
      xsw.writeEmptyElement( XML_PLAN_DATA );
      xsw.writeAttribute( XML_PLAN_TITLE, m_title );
      xsw.writeAttribute( XML_PLAN_START, m_start.toString() );
      xsw.writeAttribute( XML_PLAN_CALENDAR, Integer.toString( index( m_calendar ) ) );
      xsw.writeAttribute( XML_PLAN_DTF, m_datetimeFormat );
      xsw.writeAttribute( XML_PLAN_DF, m_dateFormat );
      xsw.writeAttribute( XML_PLAN_NOTES, m_notes );

      // close XML document
      xsw.writeEndElement(); // XML_JPLANNER
      xsw.writeEndDocument();
      xsw.flush();
      xsw.close();
      file.close();
      return true;
    }
    catch (Exception exception)
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }
  }

}
