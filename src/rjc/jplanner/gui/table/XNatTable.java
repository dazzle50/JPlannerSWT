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

package rjc.jplanner.gui.table;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
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

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.editor.CalendarCellEditor;
import rjc.jplanner.gui.editor.DayCellEditor;
import rjc.jplanner.gui.editor.ResourceCellEditor;
import rjc.jplanner.gui.editor.TaskCellEditor;

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

  public static final String LABEL_SHADE           = "Shade";
  public static final String LABEL_ALIGN_LEFT      = "Left";
  public static final String LABEL_ALIGN_RIGHT     = "Right";
  public static final String LABEL_WRAP_TEXT       = "Wrap";
  public static final String LABEL_CELL_EDITABLE   = "Edit";
  public static final String LABEL_DAY_EDITOR      = "Day";
  public static final String LABEL_CALENDAR_EDITOR = "Cal";
  public static final String LABEL_RESOURCE_EDITOR = "Res";
  public static final String LABEL_TASK_EDITOR     = "Task";

  public SelectionLayer      selectionLayer;

  /**************************************** constructor ******************************************/
  public XNatTable( Composite parent, TableType type )
  {
    // call super constructor with autoconfig off
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
          // Style gray cell background colour
          Style shade = new Style();
          shade.setAttributeValue( CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, shade, DisplayMode.NORMAL, LABEL_SHADE );

          // Style align left text horizontally
          Style left = new Style();
          left.setAttributeValue( CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, left, DisplayMode.NORMAL, LABEL_ALIGN_LEFT );

          // Style align right text horizontally
          Style right = new Style();
          right.setAttributeValue( CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT );
          reg.registerConfigAttribute( CellConfigAttributes.CELL_STYLE, right, DisplayMode.NORMAL, LABEL_ALIGN_RIGHT );

          // Cell config cell text word wraps
          reg.registerConfigAttribute( CellConfigAttributes.CELL_PAINTER, new TextPainter( true, true ),
              DisplayMode.NORMAL, LABEL_WRAP_TEXT );

          // Edit config cell is editable
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
              DisplayMode.EDIT, LABEL_CELL_EDITABLE );

          // Edit config use day cell editor
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new DayCellEditor(), DisplayMode.EDIT,
              LABEL_DAY_EDITOR );

          // Edit config use calendar cell editor
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new CalendarCellEditor(), DisplayMode.EDIT,
              LABEL_CALENDAR_EDITOR );

          // Edit config use resource cell editor
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new ResourceCellEditor(), DisplayMode.EDIT,
              LABEL_RESOURCE_EDITOR );

          // Edit config use task cell editor
          reg.registerConfigAttribute( EditConfigAttributes.CELL_EDITOR, new TaskCellEditor(), DisplayMode.EDIT,
              LABEL_TASK_EDITOR );
        }
      };

    // depending on table type configure differently
    switch ( type )
    {
      case DAY:
        // create table for day-types
        IDataProvider body = new DaysBody();
        IDataProvider colh = new DaysColumnHeader( body );
        IDataProvider rowh = new DaysRowHeader( body );
        IConfigLabelAccumulator label = new DaysLabelAccumulator();
        int[] widthD = { 60, 25, 150 };
        configTable( body, colh, rowh, label, widthD, -1 );
        JPlanner.gui.dayTables().register( this );
        break;

      case CALENDAR:
        // create table for calendars
        body = new CalendarsBody();
        colh = new CalendarsColumnHeader( body );
        rowh = new CalendarsRowHeader( body );
        label = new CalendarsLabelAccumulator();
        int[] widthC = { 140, 75 };
        configTable( body, colh, rowh, label, widthC, -1 );
        JPlanner.gui.calendarTables().register( this );
        break;

      case RESOURCE:
        // create table for resources
        body = new ResourcesBody();
        colh = new ResourcesColumnHeader( body );
        rowh = new ResourcesRowHeader( body );
        label = new ResourcesLabelAccumulator();
        int[] widthR = { 100, 25, 50 };
        configTable( body, colh, rowh, label, widthR, -1 );
        JPlanner.gui.resourceTables().register( this );
        break;

      case TASK:
        // create table for tasks
        body = new TasksBody();
        colh = new TasksColumnHeader( body );
        rowh = new TasksRowHeader( body );
        label = new TasksLabelAccumulator();
        int[] widthT = { 110, 25, 200, 60, 130, 130, 60 };
        configTable( body, colh, rowh, label, widthT, 2 * JPlanner.gui.GANTTSCALE_HEIGHT );
        JPlanner.gui.taskTables().register( this );
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
    selectionLayer = new SelectionLayer( bodyDataLayer );
    ViewportLayer viewport = new ViewportLayer( selectionLayer );

    // widths - first is default, then row header, then columns
    for ( int i = 2; i < widths.length; i++ )
      bodyDataLayer.setColumnWidthByPosition( i - 2, widths[i] );

    // create column header layer stack
    DataLayer chDataLayer = new DataLayer( ch );
    if ( chHeight > 0 )
      chDataLayer.setRowHeightByPosition( 0, chHeight );
    ColumnHeaderLayer colHeader = new ColumnHeaderLayer( chDataLayer, viewport, selectionLayer );

    // create row header layer stack
    DataLayer rhDataLayer = new DataLayer( rh, widths[1], 20 );
    RowHeaderLayer rowHeader = new RowHeaderLayer( rhDataLayer, viewport, selectionLayer );

    // create corner later stack
    DataLayer cDataLayer = new DataLayer( new DefaultCornerDataProvider( ch, rh ) );
    CornerLayer corner = new CornerLayer( cDataLayer, rowHeader, colHeader );

    // create grid layer composite
    GridLayer grid = new GridLayer( viewport, colHeader, rowHeader, corner, false );
    grid.addConfiguration( new XGridLayerConfiguration() );

    // configure NatTable with grid, theme, and labels
    setLayer( grid );
    addConfiguration( m_theme );
    addConfiguration( m_labels );
    configure();
  }

}
