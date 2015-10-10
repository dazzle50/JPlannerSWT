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

package rjc.jplanner.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringEscapeUtils;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.command.UndoStack;

/*************************************************************************************************/
/************************** Holds the complete data model for the plan ***************************/
/*************************************************************************************************/

public class Plan
{
  private String    m_title;          // plan title as set in properties
  private DateTime  m_start;          // plan start date-time as set in properties
  private Calendar  m_calendar;       // plan's default calendar
  private String    m_datetimeFormat; // format to display date-times
  private String    m_dateFormat;     // format to display dates
  private String    m_filename;       // filename when saved or loaded
  private String    m_fileLocation;   // file location
  private String    m_savedBy;        // who saved last
  private DateTime  m_savedWhen;      // when was last saved
  private String    m_notes;          // plan notes as on plan tab

  private UndoStack m_undostack;      // undo stack for plan editing

  public Tasks      tasks;            // list of plan tasks
  public Resources  resources;        // list of plan resources
  public Calendars  calendars;        // list of plan calendars
  public Days       daytypes;         // list of plan day types

  /**************************************** constructor ******************************************/
  public Plan()
  {
    // construct empty but usable Plan
    tasks = new Tasks();
    resources = new Resources();
    calendars = new Calendars();
    daytypes = new Days();

    m_undostack = new UndoStack();
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // convert to string
    String hash = super.toString();
    String id = hash.substring( hash.lastIndexOf( '.' ) + 1 );
    return id + "[" + m_title + ", " + m_start + ", " + tasks.size() + " Tasks, " + resources.size() + " Resources, "
        + calendars.size() + " Calendars, " + daytypes.size() + " DayTypes]";
  }

  /**************************************** initialise *******************************************/
  public void initialise()
  {
    // initialise plan with default settings and contents
    daytypes.initialise();
    calendars.initialise();
    resources.initialise();
    tasks.initialise();

    m_title = "";
    m_calendar = calendar( 0 );
    m_start = m_calendar.workUp( new DateTime( Date.now(), Time.fromMilliseconds( 0 ) ) );
    m_datetimeFormat = "EEE dd/MM/yyyy HH:mm";
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
    for ( int i = 0; i < tasks.size(); i++ )
      if ( !tasks.get( i ).isNull() )
        count++;

    return count;
  }

  /************************************ resourcesNotNullCount ************************************/
  public int resourcesNotNullCount()
  {
    // return number of not-null resources in plan
    int count = 0;
    for ( int i = 0; i < resources.size(); i++ )
      if ( !resources.get( i ).isNull() )
        count++;

    return count;
  }

  /**************************************** tasksCount *******************************************/
  public int tasksCount()
  {
    // return number of tasks in plan (including null tasks)
    return tasks.size();
  }

  /************************************** resourcesCount *****************************************/
  public int resourcesCount()
  {
    // return number of resources in plan (including null resources)
    return resources.size();
  }

  /************************************** calendarsCount *****************************************/
  public int calendarsCount()
  {
    // return number of calendars in plan
    return calendars.size();
  }

  /***************************************** daysCount *******************************************/
  public int daysCount()
  {
    // return number of day-types in plan
    return daytypes.size();
  }

  /******************************************** task *********************************************/
  public Task task( int index )
  {
    // return task corresponding to index
    return tasks.get( index );
  }

  public int index( Task task )
  {
    return tasks.indexOf( task );
  }

  /****************************************** resource *******************************************/
  public Resource resource( int index )
  {
    // return resource corresponding to index
    return resources.get( index );
  }

  public int index( Resource res )
  {
    return resources.indexOf( res );
  }

  /****************************************** calendar *******************************************/
  public Calendar calendar( int index )
  {
    // return calendar corresponding to index
    return calendars.get( index );
  }

  public int index( Calendar cal )
  {
    return calendars.indexOf( cal );
  }

  /********************************************* day *********************************************/
  public Day day( int index )
  {
    // return day-type corresponding to index
    return daytypes.get( index );
  }

  public int index( Day day )
  {
    return daytypes.indexOf( day );
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
    // return start date-time of earliest starting plan task
    DateTime earliest = null;
    DateTime start = null;

    for ( Task task : tasks )
    {
      // if task is null, skip
      if ( task.isNull() )
        continue;

      // if task is summary, skip
      if ( task.isSummary() )
        continue;

      // if earliest is null, this must be earliest so far
      if ( earliest == null )
      {
        earliest = task.start();
        continue;
      }

      // check is task start is earlier
      start = task.start();
      if ( start.isLessThan( earliest ) )
        earliest = start;
    }

    return earliest;
  }

  /********************************************* end *********************************************/
  public DateTime end()
  {
    // return end date-time of latest ending plan task
    DateTime latest = null;
    DateTime end = null;

    for ( Task task : tasks )
    {
      // if task is null, skip
      if ( task.isNull() )
        continue;

      // if task is summary, skip
      if ( task.isSummary() )
        continue;

      // if latest is null, this must be latest so far
      if ( latest == null )
      {
        latest = task.end();
        continue;
      }

      // check is task end is later
      end = task.end();
      if ( latest.isLessThan( end ) )
        latest = end;
    }

    return latest;
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

  /*************************************** setFileDetails ****************************************/
  public void setFileDetails( String name, String loc, String user, DateTime when )
  {
    // set details related to file 
    m_filename = name;
    m_fileLocation = loc;
    m_savedBy = user;
    m_savedWhen = when;
  }

  /****************************************** savePlan *******************************************/
  public boolean savePlan( XMLStreamWriter xsw, FileOutputStream fos )
  {
    // save plan to specified XML stream
    try
    {
      // write plan data to XML stream
      xsw.writeStartElement( XmlLabels.XML_PLAN_DATA );
      xsw.writeAttribute( XmlLabels.XML_TITLE, m_title );
      xsw.writeAttribute( XmlLabels.XML_START, m_start.toString() );
      xsw.writeAttribute( XmlLabels.XML_CALENDAR, Integer.toString( index( m_calendar ) ) );
      xsw.writeAttribute( XmlLabels.XML_DT_FORMAT, m_datetimeFormat );
      xsw.writeAttribute( XmlLabels.XML_D_FORMAT, m_dateFormat );

      // because XMLStreamWriter doesn't encode new-lines correctly 
      // write notes attribute directly instead of xsw.writeAttribute( XML_NOTES, m_notes );
      String notes = StringEscapeUtils.escapeXml( m_notes ).replaceAll( "\\n", "&#10;" );
      notes = notes.replaceAll( "\\r", "" );
      notes = " " + XmlLabels.XML_NOTES + "=\"" + notes + "\"";
      fos.write( notes.getBytes( Charset.forName( XmlLabels.ENCODING ) ) );

      // write day, calendar, resource, and task data to XML stream
      daytypes.writeXML( xsw );
      calendars.writeXML( xsw );
      resources.writeXML( xsw );
      tasks.writeXML( xsw );

      xsw.writeEndElement(); // XML_PLAN_DATA
      return true;
    }
    catch ( XMLStreamException | IOException exception )
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr, String filename, String fileloc ) throws XMLStreamException
  {
    // as id of plan-calendar read before the calendars, need temporary store
    int calendarId = -1;

    // load plan from XML stream
    while ( xsr.hasNext() )
    {
      // if reached end of plan data, exit loop
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_PLAN_DATA ) )
        break;

      // if start element read data
      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_JPLANNER:
            loadXmlJPlanner( xsr );
            break;
          case XmlLabels.XML_PLAN_DATA:
            calendarId = loadXmlPlan( xsr );
            break;
          case XmlLabels.XML_DAY_DATA:
            daytypes.loadXML( xsr );
            break;
          case XmlLabels.XML_CAL_DATA:
            calendars.loadXML( xsr );
            break;
          case XmlLabels.XML_RES_DATA:
            resources.loadXML( xsr );
            break;
          case XmlLabels.XML_TASK_DATA:
            tasks.loadXML( xsr );
            break;
          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }

      xsr.next();
    }

    // if calendar-id still negative, default to first calendar
    if ( calendarId < 0 )
      m_calendar = calendar( 0 );
    else
      m_calendar = calendar( calendarId );

    m_filename = filename;
    m_fileLocation = fileloc;

    // setup special task 0
    Task task = JPlanner.plan.task( 0 );
    task.setData( Task.SECTION_TITLE, "PROJECT" );
    task.setIndent( -1 );
  }

  /***************************************** loadXmlPlan *****************************************/
  private int loadXmlPlan( XMLStreamReader xsr ) throws XMLStreamException
  {
    // as calendars not yet loaded just keep calendar-id
    int calendarId = -1;

    // read XML plan attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_TITLE:
          m_title = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_START:
          m_start = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_DT_FORMAT:
          m_datetimeFormat = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_D_FORMAT:
          m_dateFormat = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_CALENDAR:
          calendarId = Integer.parseInt( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_NOTES:
          m_notes = xsr.getAttributeValue( i );
          break;
        default:
          JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }

    // return calendar-id to be set as default calendar
    return calendarId;
  }

  /*************************************** loadXmlJPlanner ***************************************/
  private void loadXmlJPlanner( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML JPlanner attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_SAVEUSER:
          m_savedBy = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_SAVEWHEN:
          m_savedWhen = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_FORMAT:
        case XmlLabels.XML_SAVENAME:
        case XmlLabels.XML_SAVEWHERE:
          break;
        default:
          JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }
  }

  /******************************************* errors ********************************************/
  public String errors()
  {
    // check what errors plan may have
    if ( daysCount() == 0 )
      return "No day-types";

    if ( calendarsCount() == 0 )
      return "No calendars";

    if ( index( calendar() ) == -1 )
      return "Invalid default calendar";

    return null;
  }

  /******************************************* stretch *******************************************/
  public DateTime stretch( DateTime dt, boolean stretchTasks )
  {
    // if input date-time is null return null
    if ( dt == null )
      return dt;

    // return date-time stretched across full 24 hrs if plan stretchTasks flag is true
    if ( stretchTasks )
    {
      Time time = m_calendar.day( dt.date() ).stretch( dt.time() );
      return new DateTime( dt.date(), time );
    }

    // plan stretchTasks flag not true, so return original date-time
    return dt;
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // schedule the plan!
    JPlanner.trace( "============================== SCHEDULE started ==============================" );
    tasks.schedule();
    JPlanner.trace( "============================== SCHEDULE finished ==============================" );
  }

}