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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;

/*************************************************************************************************/
/**************************** Holds the complete list of plan tasks ******************************/
/*************************************************************************************************/

public class Tasks extends ArrayList<Task>
{
  private static final long serialVersionUID = 1L;

  /****************************************** initialise *****************************************/
  public void initialise()
  {
    // initialise list with default tasks
    clear();
    for ( int count = 0; count <= 20; count++ )
      add( new Task() );

    // setup special task 0
    Task task = get( 0 );
    task.setData( Task.SECTION_TITLE, "PROJECT" );
    task.setIndent( -1 );
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML task data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of task data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_TASK_DATA ) )
        return;

      // if element start, load the contents
      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_TASK:
            add( new Task( xsr ) );
            break;
          case XmlLabels.XML_PREDECESSORS:
            loadPredecessors( xsr );
            break;
          default:
            JPlanner.trace( "tasks.loadXml - unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }
  }

  /*************************************** loadPredecessors **************************************/
  private void loadPredecessors( XMLStreamReader xsr )
  {
    // initialise variables
    int task = -1;
    String preds = "";

    // read XML predecessors attributes
    for ( int i = 0; i < xsr.getAttributeCount(); i++ )
      switch ( xsr.getAttributeLocalName( i ) )
      {
        case XmlLabels.XML_TASK:
          task = Integer.parseInt( xsr.getAttributeValue( i ) );
          break;
        case XmlLabels.XML_PREDS:
          preds = xsr.getAttributeValue( i );
          break;
        default:
          JPlanner.trace( "Predecessors - unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
          break;
      }

    // set the task predecessors, remembering array starts from zero but id from one
    get( task ).setData( Task.SECTION_PRED, preds );
  }

  /******************************************* writeXML ******************************************/
  public void writeXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write tasks data to XML stream
    xsw.writeStartElement( XmlLabels.XML_TASK_DATA );
    for ( Task task : this )
      task.saveToXML( xsw );

    // write predecessors data to XML stream
    for ( Task task : this )
      task.savePredecessorToXML( xsw );

    xsw.writeEndElement(); // XML_TASK_DATA 
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // first construct list of tasks in correct order
    ArrayList<Task> scheduleList = new ArrayList<Task>();
    for ( int index = 0; index < size(); index++ )
      if ( !get( index ).isNull() )
        scheduleList.add( get( index ) );
    Collections.sort( scheduleList );

    // schedule tasks in this order
    for ( int index = 0; index < scheduleList.size(); index++ )
      scheduleList.get( index ).schedule();
  }

  /******************************************* indent ********************************************/
  public boolean indent( Set<Integer> rows )
  {
    // remove any rows from set that can't be indented
    Set<Integer> temp = new HashSet<Integer>();
    rows.remove( 0 ); // hidden task 0
    rows.remove( 1 ); // can't indent task 1
    for ( int row : rows )
    {
      Task task = get( row );
      if ( task.isNull() ) // exclude null tasks
      {
        temp.add( row );
        continue;
      }

      int above = row - 1;
      while ( get( above ).isNull() )
        above--;
      if ( task.indent() > get( above ).indent() ) // excluded tasks already indented compared with task above
        temp.add( row );
    }
    rows.removeAll( temp );

    // add summary task's subtasks
    temp.clear();
    for ( int row : rows )
    {
      int summaryEnd = get( row ).summaryEnd();
      if ( summaryEnd > row )
        for ( int i = row + 1; i <= summaryEnd; i++ )
          if ( !get( i ).isNull() )
            temp.add( i );
    }
    rows.addAll( temp );

    if ( rows.size() > 0 )
    {
      // increment indent of each row in set by one
      for ( int row : rows )
      {
        Task task = get( row );
        task.setIndent( task.indent() + 1 );
      }

      // update task summary end & summary start
      updateSummaryMarkers();

      return true;
    }

    // no rows to indent so return false
    return false;
  }

  /************************************ updateSummaryMarkers *************************************/
  public void updateSummaryMarkers()
  {
    // for each task ensure summaryEnd and SummaryStart set correctly
    for ( int row = 1; row < size(); row++ )
    {
      Task task = get( row );
      if ( task.isNull() )
        continue;

      int indent = task.indent();

      // check if summary, and if summary set summary end
      Task other;
      int end = -1;
      int check = row;
      while ( ++check < size() )
      {
        other = get( check );
        if ( other.isNull() )
          continue;

        if ( other.indent() <= indent )
          break;
        else
          end = check;
      }
      task.setSummaryEnd( end );

      // look for summary of this task
      for ( int start = row - 1; start >= 0; start-- )
      {
        other = get( start );
        if ( other.isNull() )
          continue;

        if ( other.indent() < indent )
        {
          task.setSummaryStart( start );
          break;
        }
      }
    }
  }

  /******************************************* outdent *******************************************/
  public boolean outdent( Set<Integer> rows )
  {
    // TODO Auto-generated method stub
    for ( int row : rows )
    {
      Task task = get( row );
      task.setIndent( task.indent() - 1 );
    }

    return true;
  }

}