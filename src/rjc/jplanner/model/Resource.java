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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/************************************* Single plan resource **************************************/
/*************************************************************************************************/

public class Resource
{
  private String          m_id;                 // must be unique across all resources in model
  private String          m_name;               // free text
  private String          m_org;                // free text
  private String          m_group;              // free text
  private String          m_role;               // free text
  private String          m_alias;              // free text
  private Date            m_start;              // date availability starts inclusive
  private Date            m_end;                // date availability end inclusive
  private double          m_availability;       // number available
  private double          m_cost;               // cost TODO
  private Calendar        m_calendar;           // calendar for resource
  private String          m_comment;            // free text

  final public static int SECTION_ID       = 0;
  final public static int SECTION_NAME     = 1;
  final public static int SECTION_ORG      = 2;
  final public static int SECTION_GROUP    = 3;
  final public static int SECTION_ROLE     = 4;
  final public static int SECTION_ALIAS    = 5;
  final public static int SECTION_START    = 6;
  final public static int SECTION_END      = 7;
  final public static int SECTION_AVAIL    = 8;
  final public static int SECTION_COST     = 9;
  final public static int SECTION_CALENDAR = 10;
  final public static int SECTION_COMMENT  = 11;
  final public static int SECTION_MAX      = 11;

  /****************************************** toString *******************************************/
  public String toString( int section )
  {
    // if resource is null return blank
    if ( isNull() )
      return "";

    // return display string for given section
    if ( section == SECTION_ID )
      return m_id;

    if ( section == SECTION_NAME )
      return m_name;

    if ( section == SECTION_ORG )
      return m_org;

    if ( section == SECTION_GROUP )
      return m_group;

    if ( section == SECTION_ROLE )
      return m_role;

    if ( section == SECTION_ALIAS )
      return m_alias;

    if ( section == SECTION_START )
    {
      if ( m_start == null )
        return "NA";
      else
        return m_start.toString();
    }

    if ( section == SECTION_END )
    {
      if ( m_end == null )
        return "NA";
      else
        return m_end.toString();
    }

    if ( section == SECTION_AVAIL )
      return String.format( "%.2f", m_availability );

    if ( section == SECTION_COST )
      return String.format( "%.2f", m_cost );

    if ( section == SECTION_CALENDAR )
      return m_calendar.name();

    if ( section == SECTION_COMMENT )
      return m_comment;

    throw new IllegalArgumentException( "Section=" + section );
  }

  /****************************************** setData ********************************************/
  public void setData( int section, Object newValue )
  {
    // set resource data for given section 
    if ( section == SECTION_ID )
    {
      if ( isNull() )
        m_calendar = JPlanner.plan.calendar();

      m_id = (String) newValue;
    }

    else if ( section == SECTION_NAME )
      m_name = (String) newValue;

    else if ( section == SECTION_ORG )
      m_org = (String) newValue;

    else if ( section == SECTION_GROUP )
      m_group = (String) newValue;

    else if ( section == SECTION_ROLE )
      m_role = (String) newValue;

    else if ( section == SECTION_ALIAS )
      m_alias = (String) newValue;

    else if ( section == SECTION_COMMENT )
      m_comment = (String) newValue;

    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!

    else
      throw new IllegalArgumentException( "Section=" + section );
  }

  /****************************************** isNull *********************************************/
  public boolean isNull()
  {
    // resource is considered null if id not set
    return ( m_id == null );
  }

  /**************************************** sectionName ******************************************/
  public static String sectionName( int num )
  {
    // return section title
    if ( num == SECTION_ID )
      return "Id";

    if ( num == SECTION_NAME )
      return "Name";

    if ( num == SECTION_ORG )
      return "Organisation";

    if ( num == SECTION_GROUP )
      return "Group";

    if ( num == SECTION_ROLE )
      return "Role";

    if ( num == SECTION_ALIAS )
      return "Alias";

    if ( num == SECTION_START )
      return "Start";

    if ( num == SECTION_END )
      return "End";

    if ( num == SECTION_AVAIL )
      return "Available";

    if ( num == SECTION_COST )
      return "Cost";

    if ( num == SECTION_CALENDAR )
      return "Calendar";

    if ( num == SECTION_COMMENT )
      return "Comment";

    throw new IllegalArgumentException( "Section=" + num );
  }

  /****************************************** saveToXML ******************************************/
  public void saveToXML( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // write resource data to xml stream
    xsw.writeStartElement( Plan.XML_RESOURCE );
    xsw.writeAttribute( Plan.XML_ID, Integer.toString( JPlanner.plan.index( this ) ) );

    xsw.writeEndElement(); // XML_RESOURCE
  }

}
