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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.model.Calendar.DefaultCalendarTypes;

/*************************************************************************************************/
/************************** Holds the complete list of plan calendars ****************************/
/*************************************************************************************************/

public class Calendars extends ArrayList<Calendar>
{
  private static final long serialVersionUID = 1L;

  /**************************************** initialise *******************************************/
  public void initialise()
  {
    // initialise list with default calendars
    clear();
    for ( DefaultCalendarTypes type : DefaultCalendarTypes.values() )
      add( new Calendar( type ) );
  }

  /******************************************* loadXML *******************************************/
  public void loadXML( XMLStreamReader xsr ) throws XMLStreamException
  {
    // read XML calendar data
    while ( xsr.hasNext() )
    {
      xsr.next();

      // if reached end of calendar data, return
      if ( xsr.isEndElement() && xsr.getLocalName().equals( XmlLabels.XML_CAL_DATA ) )
        return;

      // if a calendar element, construct a calendar from it
      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_CALENDAR:
            add( new Calendar( xsr ) );
            break;
          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }

    }
  }

  /******************************************* writeXML ******************************************/
  public void writeXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write calendars data to XML stream
    xsw.writeStartElement( XmlLabels.XML_CAL_DATA );
    for ( Calendar cal : this )
      cal.saveToXML( xsw );
    xsw.writeEndElement(); // XML_CAL_DATA
  }

  /******************************************* fromName ******************************************/
  public Calendar fromName( String name )
  {
    // return calendar with matching name
    for ( Calendar cal : this )
      if ( cal.name().equals( name ) )
        return cal;

    // no calendar found with matching name, so return null
    return null;
  }

}
