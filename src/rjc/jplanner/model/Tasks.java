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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.JPlanner;

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
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML task data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of task data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( Plan.XML_TASK_DATA ) )
        return;

      // if element start, load the contents
      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case Task.XML_TASK:
            add( new Task( xsr ) );
            break;
          case Task.XML_PREDECESSORS:
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
        case Task.XML_TASK:
          task = Integer.parseInt( xsr.getAttributeValue( i ) );
          break;
        case Task.XML_PREDS:
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
    xsw.writeStartElement( Plan.XML_TASK_DATA );
    for ( Task task : this )
      task.saveToXML( xsw );
    xsw.writeEndElement(); // XML_TASK_DATA 
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // first construct list of tasks in correct order
    ArrayList<Task> scheduleList = new ArrayList<Task>();
    for ( int i = 0; i < size(); i++ )
      if ( !get( i ).isNull() )
        scheduleList.add( get( i ) );
    Collections.sort( scheduleList );

    // schedule tasks in this order
    for ( int i = 0; i < scheduleList.size(); i++ )
      scheduleList.get( i ).schedule();
  }

}