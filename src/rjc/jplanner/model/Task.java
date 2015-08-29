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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;

/*************************************************************************************************/
/******************************** Single task within overall plan ********************************/
/*************************************************************************************************/

public class Task implements Comparable<Task>
{
  private String          m_title;              // free text title
  private TimeSpan        m_duration;           // duration of task
  private DateTime        m_start;              // start date-time of task
  private DateTime        m_end;                // end date-time of task
  private TimeSpan        m_work;               // work effort for task
  private Predecessors    m_predecessors;       // task predecessors
  private TaskResources   m_resources;          // resources allocated to task
  private TaskType        m_type;               // task type
  private int             m_priority;           // overall task priority (0 to 999)
  private DateTime        m_deadline;           // task warning deadline
  private String          m_cost;               // calculated cost based on resource use
  private String          m_comment;            // free text comment

  private int             m_indent;             // task indent level, zero for no indent
  private int             m_summaryEnd;         // last sub-task id, or -1 for non-summaries
  private GanttData       m_gantt;              // data for gantt bar display

  public static final int SECTION_TITLE    = 0;
  public static final int SECTION_DURATION = 1;
  public static final int SECTION_START    = 2;
  public static final int SECTION_END      = 3;
  public static final int SECTION_WORK     = 4;
  public static final int SECTION_PRED     = 5;
  public static final int SECTION_RES      = 6;
  public static final int SECTION_TYPE     = 7;
  public static final int SECTION_PRIORITY = 8;
  public static final int SECTION_DEADLINE = 9;
  public static final int SECTION_COST     = 10;
  public static final int SECTION_COMMENT  = 11;
  public static final int SECTION_MAX      = 11;

  /**************************************** constructor ******************************************/
  public Task()
  {
    // initialise private variables
    m_predecessors = new Predecessors();
    m_gantt = new GanttData();
  }

  /**************************************** constructor ******************************************/
  public Task( XMLStreamReader xsr ) throws XMLStreamException
  {
    this();
    // read XML task attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_ID:
          break;
        case XmlLabels.XML_TITLE:
          m_title = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_DURATION:
          m_duration = new TimeSpan( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_START:
          m_start = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_END:
          m_end = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_WORK:
          m_work = new TimeSpan( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_RESOURCES:
          m_resources = new TaskResources( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_TYPE:
          m_type = new TaskType( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_PRIORITY:
          m_priority = Integer.parseInt( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_DEADLINE:
          m_deadline = new DateTime( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_COST:
          m_cost = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_COMMENT:
          m_comment = xsr.getAttributeValue( i );
          break;
        case XmlLabels.XML_INDENT:
          m_indent = Integer.parseInt( xsr.getAttributeValue( i ) );
          break;
        default:
          JPlanner.trace( "Task - unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }
  }

  /***************************************** initialise ******************************************/
  public void initialise()
  {
    // initialise private variables
    m_duration = new TimeSpan( "1d" );
    m_work = new TimeSpan( "0d" );
    m_start = JPlanner.plan.start();
    m_end = JPlanner.plan.start();
    m_predecessors = new Predecessors( "" );
    m_resources = new TaskResources();
    m_type = new TaskType( TaskType.ASAP_FDUR );
    m_priority = 100;
  }

  /****************************************** toString *******************************************/
  public String toString( int section )
  {
    // return display string for given section
    if ( section == SECTION_TITLE )
      return m_title;

    // if task is null return blank for all other sections
    if ( isNull() )
      return "";

    if ( section == SECTION_DURATION )
      return m_duration.toString();

    if ( section == SECTION_START )
      return start().toString( JPlanner.plan.datetimeFormat() );

    if ( section == SECTION_END )
      return end().toString( JPlanner.plan.datetimeFormat() );

    if ( section == SECTION_WORK )
      return m_work.toString();

    if ( section == SECTION_PRED )
      return m_predecessors.toString();

    if ( section == SECTION_RES )
      return m_resources.toString();

    if ( section == SECTION_TYPE )
      return m_type.toString();

    if ( section == SECTION_PRIORITY )
      return String.format( "%d", m_priority );

    if ( section == SECTION_DEADLINE )
    {
      if ( m_deadline == null )
        return "NA";

      return m_deadline.toString( JPlanner.plan.datetimeFormat() );
    }

    if ( section == SECTION_COST )
      return m_cost;

    if ( section == SECTION_COMMENT )
      return m_comment;

    throw new IllegalArgumentException( "Section=" + section );
  }

  /****************************************** setData ********************************************/
  public void setData( int section, Object newValue )
  {
    // set task data for given section
    if ( section == SECTION_TITLE )
    {
      if ( isNull() )
        initialise();

      m_title = (String) newValue;
    }

    else if ( section == SECTION_DURATION )
      m_duration = new TimeSpan( (String) newValue );

    else if ( section == SECTION_START )
      m_start = (DateTime) newValue;

    else if ( section == SECTION_END )
      m_end = (DateTime) newValue;

    else if ( section == SECTION_WORK )
      m_work = new TimeSpan( (String) newValue );

    else if ( section == SECTION_PRED )
      m_predecessors = new Predecessors( (String) newValue );

    else if ( section == SECTION_TYPE )
      m_type = new TaskType( (String) newValue );

    else if ( section == SECTION_PRIORITY )
      m_priority = Integer.parseInt( (String) newValue );

    else if ( section == SECTION_DEADLINE )
      m_deadline = (DateTime) newValue;

    else if ( section == SECTION_COMMENT )
      m_comment = (String) newValue;

    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!

    else
      throw new IllegalArgumentException( "Section=" + section );
  }

  /****************************************** isNull *********************************************/
  public boolean isNull()
  {
    // task is considered null if title not set
    return ( m_title == null );
  }

  /**************************************** sectionName ******************************************/
  public static String sectionName( int num )
  {
    // return section title
    if ( num == SECTION_TITLE )
      return "Title";

    if ( num == SECTION_DURATION )
      return "Duration";

    if ( num == SECTION_START )
      return "Start";

    if ( num == SECTION_END )
      return "End";

    if ( num == SECTION_WORK )
      return "Work";

    if ( num == SECTION_PRED )
      return "Predecessors";

    if ( num == SECTION_RES )
      return "Resources";

    if ( num == SECTION_TYPE )
      return "Type";

    if ( num == SECTION_PRIORITY )
      return "Priority";

    if ( num == SECTION_DEADLINE )
      return "Deadline";

    if ( num == SECTION_COST )
      return "Cost";

    if ( num == SECTION_COMMENT )
      return "Comment";

    throw new IllegalArgumentException( "Section=" + num );
  }

  /******************************************* type **********************************************/
  public TaskType type()
  {
    return m_type;
  }

  /****************************************** saveToXML ******************************************/
  public void saveToXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write task data to XML stream (except predecessors)
    xsw.writeStartElement( XmlLabels.XML_TASK );
    xsw.writeAttribute( XmlLabels.XML_ID, Integer.toString( JPlanner.plan.index( this ) ) );

    if ( !isNull() )
    {
      xsw.writeAttribute( XmlLabels.XML_TITLE, m_title );
      xsw.writeAttribute( XmlLabels.XML_DURATION, m_duration.toString() );
      xsw.writeAttribute( XmlLabels.XML_START, m_start.toString() );
      xsw.writeAttribute( XmlLabels.XML_END, m_end.toString() );
      xsw.writeAttribute( XmlLabels.XML_WORK, m_work.toString() );
      xsw.writeAttribute( XmlLabels.XML_RESOURCES, m_resources.toString() );
      xsw.writeAttribute( XmlLabels.XML_TYPE, m_type.toString() );
      xsw.writeAttribute( XmlLabels.XML_PRIORITY, Integer.toString( m_priority ) );
      if ( m_deadline != null )
        xsw.writeAttribute( XmlLabels.XML_DEADLINE, m_deadline.toString() );
      if ( m_cost != null )
        xsw.writeAttribute( XmlLabels.XML_COST, m_cost );
      if ( m_comment != null )
        xsw.writeAttribute( XmlLabels.XML_COMMENT, m_comment.toString() );
    }

    xsw.writeEndElement(); // XML_TASK
  }

  /************************************ savePredecessorToXML *************************************/
  public void savePredecessorToXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write task predecessor data to XML stream
    String preds = m_predecessors.toString();

    if ( preds.length() > 0 )
    {
      xsw.writeStartElement( XmlLabels.XML_PREDECESSORS );
      xsw.writeAttribute( XmlLabels.XML_TASK, Integer.toString( JPlanner.plan.index( this ) ) );
      xsw.writeAttribute( XmlLabels.XML_PREDS, preds );
      xsw.writeEndElement(); // XML_PREDECESSORS
    }
  }

  /****************************************** compareTo ******************************************/
  @Override
  public int compareTo( Task other )
  {
    // sort comparison first check for predecessors
    if ( this.hasPredecessor( other ) )
      return 1;
    if ( other.hasPredecessor( this ) )
      return -1;

    // then by priority
    if ( m_priority < other.m_priority )
      return 1;
    if ( m_priority > other.m_priority )
      return -1;

    // finally by index
    return JPlanner.plan.index( this ) - JPlanner.plan.index( other );
  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // convert to string
    String hash = super.toString();
    String id = hash.substring( hash.lastIndexOf( '.' ) + 1 );
    return id + "['" + m_title + "' " + m_type + " " + m_priority + "]";
  }

  /**************************************** hasPredecessor ***************************************/
  public boolean hasPredecessor( Task other )
  {
    // return true if task is predecessor
    if ( m_predecessors.hasPredecessor( other ) )
      return true;

    // if task is summary, then sub-tasks are implicit predecessors
    if ( m_summaryEnd > 0 )
    {
      int thisNum = JPlanner.plan.index( this );
      int otherNum = JPlanner.plan.index( other );
      if ( otherNum > thisNum && otherNum <= m_summaryEnd )
        return true;
    }

    return false;
  }

  /***************************************** isSummary *******************************************/
  public boolean isSummary()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /***************************************** summaryEnd ******************************************/
  public int summaryEnd()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // TODO Auto-generated method stub
    JPlanner.trace( "Scheduling " + this );

    if ( m_type.toString() == TaskType.ASAP_FDUR )
    {
      schedule_ASAP_FDUR();
      return;
    }

    throw new UnsupportedOperationException( "Task type = " + m_type );
  }

  /************************************* schedule_ASAP_FDUR **************************************/
  private void schedule_ASAP_FDUR()
  {
    // depending on predecessors determine task start & end
    boolean hasToStart = m_predecessors.hasToStart();
    boolean hasToFinish = m_predecessors.hasToFinish();

    // if this task doesn't have predecessors, does a summary?
    if ( !hasToStart && !hasToFinish )
    {
      int index = JPlanner.plan.index( this );
      for ( int indent = m_indent; indent > 0; indent-- )
      {
        // find task summary
        while ( JPlanner.plan.task( index ).isNull() || JPlanner.plan.task( index ).m_indent >= indent )
          index--;

        hasToStart = JPlanner.plan.task( index ).m_predecessors.hasToStart();
        if ( hasToStart )
          break;
        hasToFinish = JPlanner.plan.task( index ).m_predecessors.hasToFinish();
        if ( hasToFinish )
          break;
      }
    }

    Calendar planCal = JPlanner.plan.calendar();
    if ( hasToStart )
    {
      m_start = planCal.workUp( startDueToPredecessors() );
      m_end = planCal.workDown( planCal.workTimeSpan( m_start, m_duration ) );
    }
    else if ( hasToFinish )
    {
      m_end = planCal.workDown( startDueToPredecessors() );
      m_start = planCal.workUp( planCal.workTimeSpan( m_end, m_duration.minus() ) );
    }
    else
    {
      m_start = planCal.workUp( JPlanner.plan.start() );
      m_end = planCal.workDown( planCal.workTimeSpan( m_start, m_duration ) );
    }

    // ensure end is always greater or equal to start
    if ( m_end.isLessThan( m_start ) )
      m_end = m_start;

    // set gantt task bar data
    if ( isSummary() )
      m_gantt.setSummary( start(), end() );
    else
      m_gantt.setTask( m_start, m_end );
  }

  /********************************************* end *********************************************/
  public DateTime end()
  {
    // return task or summary end date-time
    if ( isSummary() )
    {
      int here = JPlanner.plan.index( this );
      DateTime e = new DateTime( Long.MIN_VALUE );

      // loop through each subtask
      for ( int t = here + 1; t <= m_summaryEnd; t++ )
      {
        // if task isn't summary & isn't null, check if its end is after current latest
        Task task = JPlanner.plan.task( t );
        if ( !task.isSummary() && !task.isNull() && e.isLessThan( task.m_end ) )
          e = task.m_end;
      }

      return e;
    }

    return m_end;
  }

  /******************************************** start ********************************************/
  public DateTime start()
  {
    // return task or summary start date-time
    if ( isSummary() )
    {
      int here = JPlanner.plan.index( this );
      DateTime s = new DateTime( Long.MAX_VALUE );

      // loop through each subtask
      for ( int t = here + 1; t <= m_summaryEnd; t++ )
      {
        // if task isn't summary & isn't null, check if its start is before current earliest
        Task task = JPlanner.plan.task( t );
        if ( !task.isSummary() && !task.isNull() && task.m_start.isLessThan( s ) )
          s = task.m_start;
      }

      return s;
    }

    return m_start;
  }

  /************************************ startDueToPredecessors ***********************************/
  private DateTime startDueToPredecessors()
  {
    // get start based on this task's predecessors
    DateTime start = m_predecessors.start();

    // if indented also check start against summary(s) predecessors
    int index = JPlanner.plan.index( this );
    for ( int indent = m_indent; indent > 0; indent-- )
    {
      // find task summary
      while ( JPlanner.plan.task( index ).isNull() || JPlanner.plan.task( index ).m_indent >= indent )
        index--;

      // if start from summary predecessors is later, use it instead
      DateTime summaryStart = JPlanner.plan.task( index ).m_predecessors.start();
      if ( start.isLessThan( summaryStart ) )
        start = summaryStart;
    }

    return start;
  }

  /****************************************** ganttData ******************************************/
  public GanttData ganttData()
  {
    // return gantt-data associated with the task
    return m_gantt;
  }

  /******************************************** index ********************************************/
  public int index()
  {
    return JPlanner.plan.index( this );
  }

}