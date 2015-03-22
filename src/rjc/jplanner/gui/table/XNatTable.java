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

package rjc.jplanner.gui.table;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.DateCellEditor;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

import rjc.jplanner.gui.MainWindow;
import rjc.jplanner.gui.editor.IntSpinEditor;

/*************************************************************************************************/
/**************************** JPlanner table based on Nebula NatTable ****************************/
/*************************************************************************************************/

public class XNatTable extends NatTable
{
  private static ModernNatTableThemeConfiguration m_theme; // theme to use for all the tables
  private static IConfiguration                   m_labels; // to support styling of individual cells

  public enum TableType
  {
    DAY, CALENDAR, RESOURCE, TASK
  }

  /**************************************** constructor ******************************************/
  public XNatTable( Composite parent, TableType type )
  {
    // constructor call must be the first statement in a constructor
    super( parent, false );

    // check private variables are initialised
    if ( m_theme == null )
    {
      m_theme = new ModernNatTableThemeConfiguration();
      m_theme.defaultHAlign = HorizontalAlignmentEnum.CENTER;
      m_theme.cHeaderHAlign = HorizontalAlignmentEnum.CENTER;
      m_theme.rHeaderHAlign = HorizontalAlignmentEnum.CENTER;
    }

    if ( m_labels == null )
      m_labels = new AbstractRegistryConfiguration()
      {
        @Override
        public void configureRegistry( IConfigRegistry reg )
        {
          // Style "SHADE" gray cell background colour
          Style shade = new Style();
          shade.setAttributeValue( CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, shade, DisplayMode.NORMAL, "SHADE" );

          // Style "LEFT" align left text horizontally
          Style left = new Style();
          left.setAttributeValue( CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, left, DisplayMode.NORMAL, "LEFT" );

          // Style "RIGHT" align right text horizontally
          Style right = new Style();
          right.setAttributeValue( CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, right, DisplayMode.NORMAL, "RIGHT" );

          // Cell config "WRAP" cell text word wraps
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new TextPainter( true, true ),
              DisplayMode.NORMAL, "WRAP" );

          // Edit config "EDITABLE" cell is editable 
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
              DisplayMode.EDIT, "EDITABLE" );

          // Edit config "INT_EDITOR" use date editor 
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new IntSpinEditor(), DisplayMode.EDIT,
              "INT_EDITOR" );

          // Edit config "DATE_EDITOR" use date editor 
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new DateCellEditor(), DisplayMode.EDIT,
              "DATE_EDITOR" );
        }
      };

    // depending on table type configure differently
    switch (type) {
      case DAY:
        // create table for day-types
        IDataProvider body = new DaysBody();
        IDataProvider colh = new DaysColumnHeader( body );
        IDataProvider rowh = new DaysRowHeader( body );
        IConfigLabelAccumulator label = new DaysLabelAccumulator();
        int[] widthD = { 60, 25, 150 };
        configTable( body, colh, rowh, label, widthD, -1 );
        MainWindow.dayTables().register( this );
        break;

      case CALENDAR:
        // create table for calendars
        body = new CalendarsBody();
        colh = new CalendarsColumnHeader( body );
        rowh = new CalendarsRowHeader( body );
        label = new CalendarsLabelAccumulator();
        int[] widthC = { 140, 75 };
        configTable( body, colh, rowh, label, widthC, -1 );
        MainWindow.calendarTables().register( this );
        break;

      case RESOURCE:
        // create table for resources
        body = new ResourcesBody();
        colh = new ResourcesColumnHeader( body );
        rowh = new ResourcesRowHeader( body );
        label = new ResourcesLabelAccumulator();
        int[] widthR = { 100, 25, 50 };
        configTable( body, colh, rowh, label, widthR, -1 );
        MainWindow.resourceTables().register( this );
        break;

      case TASK:
        // create table for tasks
        body = new TasksBody();
        colh = new TasksColumnHeader( body );
        rowh = new TasksRowHeader( body );
        label = new TasksLabelAccumulator();
        int[] widthT = { 100, 25, 200, 60, 100, 100, 60 };
        configTable( body, colh, rowh, label, widthT, 2 * MainWindow.GANTTSCALE_HEIGHT );
        MainWindow.taskTables().register( this );
        break;

      default:
        throw new IllegalArgumentException( "type" );
    }

  }

  /**************************************** configTable ******************************************/
  private void configTable( IDataProvider body, IDataProvider ch, IDataProvider rh, IConfigLabelAccumulator label,
      int[] widths, int chHeight )
  {
    // create body layer stack
    DataLayer bodyDataLayer = new DataLayer( body, widths[0], 20 );
    bodyDataLayer.setConfigLabelAccumulator( label );
    SelectionLayer selLayer = new SelectionLayer( bodyDataLayer );
    ViewportLayer viewport = new ViewportLayer( selLayer );

    // widths - first is default, then row header, then columns
    for ( int i = 2; i < widths.length; i++ )
      bodyDataLayer.setColumnWidthByPosition( i - 2, widths[i] );

    // create column header layer stack
    DataLayer chDataLayer = new DataLayer( ch );
    if ( chHeight > 0 )
      chDataLayer.setRowHeightByPosition( 0, chHeight );
    ColumnHeaderLayer colHeader = new ColumnHeaderLayer( chDataLayer, viewport, selLayer );

    // create row header layer stack
    DataLayer rhDataLayer = new DataLayer( rh, widths[1], 20 );
    RowHeaderLayer rowHeader = new RowHeaderLayer( rhDataLayer, viewport, selLayer );

    // create corner later stack
    DataLayer cDataLayer = new DataLayer( new DefaultCornerDataProvider( ch, rh ) );
    CornerLayer corner = new CornerLayer( cDataLayer, rowHeader, colHeader );

    // create grid layer composite
    GridLayer grid = new GridLayer( viewport, colHeader, rowHeader, corner );

    // configure NatTable with grid, theme, and labels
    setLayer( grid );
    addConfiguration( m_theme );
    addConfiguration( m_labels );
    configure();
  }

}
