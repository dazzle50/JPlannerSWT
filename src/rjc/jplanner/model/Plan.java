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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
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
  private String    m_title;         // plan title as set in properties
  private DateTime  m_start;         // plan start date-time as set in properties
  private Calendar  m_calendar;      // plan's default calendar
  private String    m_datetimeFormat; // format to display date-times
  private String    m_dateFormat;    // format to display dates
  private String    m_filename;      // filename when saved or loaded
  private String    m_fileLocation;  // file location
  private String    m_savedBy;       // who saved last
  private DateTime  m_savedWhen;     // when was last saved
  private String    m_notes;         // plan notes as on plan tab

  private UndoStack m_undostack;     // undo stack for plan editing

  private Tasks     m_tasks;         // list of plan tasks
  private Resources m_resources;     // list of plan resources
  private Calendars m_calendars;     // list of plan calendars
  private Days      m_daytypes;      // list of plan day types

  /**************************************** constructor ******************************************/
  public Plan()
  {
    // construct empty but usable Plan
    m_tasks = new Tasks();
    m_resources = new Resources();
    m_calendars = new Calendars();
    m_daytypes = new Days();

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
    m_daytypes.initialise();
    m_calendars.initialise();
    m_resources.initialise();
    m_tasks.initialise();

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

  public int index( Task task )
  {
    return m_tasks.indexOf( task );
  }

  /****************************************** resource *******************************************/
  public Resource resource( int index )
  {
    // return resource corresponding to index
    return m_resources.get( index );
  }

  public int index( Resource res )
  {
    return m_resources.indexOf( res );
  }

  /****************************************** calendar *******************************************/
  public Calendar calendar( int index )
  {
    // return calendar corresponding to index
    return m_calendars.get( index );
  }

  public int index( Calendar cal )
  {
    return m_calendars.indexOf( cal );
  }

  /********************************************* day *********************************************/
  public Day day( int index )
  {
    // return day-type corresponding to index
    return m_daytypes.get( index );
  }

  public int index( Day day )
  {
    return m_daytypes.indexOf( day );
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
      // write day, calendar, resource, and task data to XML stream
      m_daytypes.writeXML( xsw );
      m_calendars.writeXML( xsw );
      m_resources.writeXML( xsw );
      m_tasks.writeXML( xsw );

      // write plan data to XML stream
      xsw.writeEmptyElement( XmlLabels.XML_PLAN_DATA );
      xsw.writeAttribute( XmlLabels.XML_TITLE, m_title );
      xsw.writeAttribute( XmlLabels.XML_START, m_start.toString() );
      xsw.writeAttribute( XmlLabels.XML_CALENDAR, Integer.toString( index( m_calendar ) ) );
      xsw.writeAttribute( XmlLabels.XML_DT_FORMAT, m_datetimeFormat );
      xsw.writeAttribute( XmlLabels.XML_D_FORMAT, m_dateFormat );

      // because XMLStreamWriter doesn't encode new-lines correctly 
      // write notes attribute directly instead of xsw.writeAttribute( XML_NOTES, m_notes );
      String notes = StringEscapeUtils.escapeXml( m_notes ).replaceAll( "\\n", "&#10;" );
      notes = " " + XmlLabels.XML_NOTES + "=\"" + notes + "\"";
      fos.write( notes.getBytes( Charset.forName( XmlLabels.ENCODING ) ) );

      return true;
    }
    catch ( XMLStreamException | IOException exception )
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }
  }

  /****************************************** loadPlan *******************************************/
  public boolean loadPlan( File file )
  {
    // attempt to save plan as XML to specified file
    JPlanner.trace( "######## Loading '" + file + "' ########" );
    try
    {
      // create XML stream reader
      XMLInputFactory xif = XMLInputFactory.newInstance();
      FileInputStream fis = new FileInputStream( file );
      XMLStreamReader xsr = xif.createXMLStreamReader( fis );

      while ( xsr.hasNext() )
      {
        if ( xsr.isStartElement() )
          switch ( xsr.getLocalName() )
          {
            case XmlLabels.XML_JPLANNER:
              loadXmlJPlanner( xsr );
              break;
            case XmlLabels.XML_DAY_DATA:
              m_daytypes.loadXML( xsr );
              break;
            case XmlLabels.XML_CAL_DATA:
              m_calendars.loadXML( xsr );
              break;
            case XmlLabels.XML_RES_DATA:
              m_resources.loadXML( xsr );
              break;
            case XmlLabels.XML_TASK_DATA:
              m_tasks.loadXML( xsr );
              break;
            case XmlLabels.XML_PLAN_DATA:
              loadXmlPlan( xsr );
              break;
            case XmlLabels.XML_DISPLAY_DATA:
              JPlanner.gui.loadXmlDisplayData( xsr );
              break;
            default:
              JPlanner.trace( "loadPlan - unhandled start element '" + xsr.getLocalName() + "'" );
              break;
          }

        xsr.next();
      }

      m_filename = file.getName();
      m_fileLocation = file.getParent();
      xsr.close();
      fis.close();
      return true;
    }
    catch ( XMLStreamException | IOException exception )
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }

  }

  /***************************************** loadXmlPlan *****************************************/
  private void loadXmlPlan( XMLStreamReader xsr ) throws XMLStreamException
  {
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
          m_calendar = calendar( Integer.parseInt( xsr.getAttributeValue( i ) ) );
          break;
        case XmlLabels.XML_NOTES:
          m_notes = xsr.getAttributeValue( i );
          break;
        default:
          JPlanner.trace( "loadXmlPlan - unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }
  }

  /*************************************** loadXmlJPlanner ***************************************/
  private void loadXmlJPlanner( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML JPlanner attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_USER:
          m_savedBy = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_WHEN:
          m_savedWhen = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_FORMAT:
          break;
        default:
          JPlanner.trace( "loadXmlJPlanner - unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
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

  /************************************* isDuplicateDayName **************************************/
  public boolean isDuplicateDayName( String txt, int index )
  {
    // return true if txt is a duplicate another day-type name
    txt = JPlanner.clean( txt );
    for ( int i = 0; i < daysCount(); i++ )
    {
      if ( i == index )
        continue;
      if ( txt.equals( day( i ).name() ) )
        return true;
    }

    return false;
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // schedule the plan!
    JPlanner.trace( "============================== SCHEDULE started ==============================" );
    m_tasks.schedule();
    JPlanner.trace( "============================== SCHEDULE finished ==============================" );
  }

}
