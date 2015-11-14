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

package rjc.jplanner.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import rjc.jplanner.JPlanner;
import rjc.jplanner.XmlLabels;
import rjc.jplanner.command.CommandTaskIndent;
import rjc.jplanner.command.CommandTaskOutdent;
import rjc.jplanner.gui.editor.XAbstractCellEditor;
import rjc.jplanner.model.DateTime;
import rjc.jplanner.model.Plan;

/*************************************************************************************************/
/******************************* Main JPlanner application window ********************************/
/*************************************************************************************************/

public class MainWindow extends Shell
{
  private MainTabWidget            m_mainTabWidget;       // MainTabWidget associated with MainWindow
  private ArrayList<MainTabWidget> m_tabWidgets;          // list of MainTabWidgets including one in MainWindow
  private Text                     m_statusBar;
  private MenuAdapter              m_menuListener;
  private MenuItem                 m_menuTask;

  public UndoStackWindow           undoWindow;            // window to show plan undo-stack
  public MenuItem                  actionUndoStackView;   // action to show plan undo-stack window
  public MenuItem                  actionUndo;
  public MenuItem                  actionRedo;

  public Color                     COLOR_BLACK;
  public Color                     COLOR_WHITE;
  public Color                     COLOR_RED;
  public Color                     COLOR_YELLOW;
  public Color                     COLOR_BLUE;
  public Color                     COLOR_GREEN;
  public Color                     COLOR_CYAN;
  public Color                     COLOR_GRAY_VDARK;
  public Color                     COLOR_GRAY_DARK;
  public Color                     COLOR_GRAY_MID;
  public Color                     COLOR_GRAY_LIGHT;

  public Color                     COLOR_CELL_SELECTED;
  public Color                     COLOR_CELL_DISABLED;
  public Color                     COLOR_CELL_ENABLED;
  public Color                     COLOR_TEXT_NORMAL;
  public Color                     COLOR_TEXT_SELECTED;
  public Color                     COLOR_BORDER_NORMAL;
  public Color                     COLOR_BORDER_SELECTED;

  public Color                     COLOR_GANTT_BACKGROUND;
  public Color                     COLOR_GANTT_NONWORKING;
  public Color                     COLOR_GANTT_DIVIDER;
  public Color                     COLOR_GANTT_TASK_EDGE;
  public Color                     COLOR_GANTT_TASK_FILL;
  public Color                     COLOR_GANTT_SUMMARY;
  public Color                     COLOR_GANTT_MILESTONE;
  public Color                     COLOR_GANTT_DEPENDENCY;

  public Color                     COLOR_ERROR;
  public Color                     COLOR_NO_ERROR;

  public Transform                 TRANSFORM;
  public int                       GANTTSCALE_HEIGHT = 15;

  /**************************************** constructor ******************************************/
  public MainWindow( Display display )
  {
    // create JPlanner main window
    super( display, SWT.SHELL_TRIM );
    setSize( 1100, 510 );
    setText( "JPlanner" );

    // initialise some public variables for use elsewhere
    COLOR_BLACK = display.getSystemColor( SWT.COLOR_BLACK );
    COLOR_WHITE = display.getSystemColor( SWT.COLOR_WHITE );
    COLOR_RED = display.getSystemColor( SWT.COLOR_RED );
    COLOR_YELLOW = display.getSystemColor( SWT.COLOR_YELLOW );
    COLOR_BLUE = display.getSystemColor( SWT.COLOR_BLUE );
    COLOR_GREEN = display.getSystemColor( SWT.COLOR_GREEN );
    COLOR_CYAN = display.getSystemColor( SWT.COLOR_CYAN );
    COLOR_GRAY_VDARK = new Color( display, 128, 128, 128 );
    COLOR_GRAY_DARK = display.getSystemColor( SWT.COLOR_GRAY );
    COLOR_GRAY_MID = new Color( display, 227, 227, 227 );
    COLOR_GRAY_LIGHT = new Color( display, 240, 240, 240 );
    COLOR_CELL_SELECTED = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    COLOR_CELL_DISABLED = COLOR_GRAY_MID;
    COLOR_CELL_ENABLED = display.getSystemColor( SWT.COLOR_LIST_BACKGROUND );
    COLOR_TEXT_NORMAL = display.getSystemColor( SWT.COLOR_LIST_FOREGROUND );
    COLOR_TEXT_SELECTED = display.getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT );
    COLOR_BORDER_NORMAL = display.getSystemColor( SWT.COLOR_WIDGET_BORDER );
    COLOR_BORDER_SELECTED = COLOR_CELL_SELECTED;
    COLOR_GANTT_BACKGROUND = COLOR_WHITE;
    COLOR_GANTT_NONWORKING = COLOR_GRAY_LIGHT;
    COLOR_GANTT_DIVIDER = COLOR_GRAY_DARK;
    COLOR_GANTT_TASK_EDGE = COLOR_BLACK;
    COLOR_GANTT_TASK_FILL = COLOR_YELLOW;
    COLOR_GANTT_SUMMARY = COLOR_BLACK;
    COLOR_GANTT_MILESTONE = COLOR_BLACK;
    COLOR_GANTT_DEPENDENCY = COLOR_GRAY_VDARK;
    COLOR_ERROR = COLOR_RED;
    COLOR_NO_ERROR = COLOR_BLACK;
    TRANSFORM = new Transform( display );

    // prepare listener to for when menus shown
    m_menuListener = new MenuAdapter()
    {
      @Override
      public void menuShown( MenuEvent e )
      {
        // clear any old status-bar message
        message( "" );

        // check for properties/notes changes
        properties().updatePlan();
        properties().updateFromPlan();
        notes().updatePlan();

        // if any table cell editing in progress, end it
        if ( XAbstractCellEditor.cellEditorInProgress != null )
          XAbstractCellEditor.cellEditorInProgress.endEditing();
      }
    };

    // check for properties/notes changes when window deactivate (i.e. other window activated)
    addListener( SWT.Deactivate, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        properties().updatePlan();
        notes().updatePlan();
      }
    } );

  }

  /**************************************** initialise *******************************************/
  public void initialise()
  {
    // add menus
    Menu menuBar = new Menu( this, SWT.BAR );
    setMenuBar( menuBar );
    addFileMenu( menuBar );
    addEditMenu( menuBar );
    addReportMenu( menuBar );
    addViewMenu( menuBar );
    addHelpMenu( menuBar );

    // layout the MainTabWidget + status-bar using a GridLayout
    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );

    // add main tab widget
    m_mainTabWidget = new MainTabWidget( this, true );
    m_mainTabWidget.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
    m_tabWidgets = new ArrayList<MainTabWidget>();
    m_tabWidgets.add( m_mainTabWidget );

    // listener to detect when selected tab changed
    m_mainTabWidget.addListener( SWT.Selection, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        // ensure plan is kept up-to-date with the properties and notes
        m_mainTabWidget.properties().updatePlan();
        m_mainTabWidget.notes().updatePlan();

        // ensure menus reflect selected tab
        updateMenus();
      }
    } );

    // add status bar
    m_statusBar = new Text( this, SWT.SINGLE | SWT.READ_ONLY );
    m_statusBar.setText( "JPlanner started" );
    m_statusBar.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    // listener to trap window close event to check user doesn't loss data
    getShell().addListener( SWT.Close, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        properties().updatePlan();
        notes().updatePlan();

        if ( !JPlanner.plan.undostack().isClean() )
        {
          boolean ask = true;
          while ( ask )
          {
            XMessageDialog dialog = new XMessageDialog( getShell(), "Closing", "Do you want to save before you quit?",
                MessageDialog.QUESTION, new String[] { "Save", "Discard", "Cancel" }, 2 );
            int ret = dialog.open();

            if ( ret == 0 ) // save
              ask = !saveAs();

            if ( ret == 1 ) // discard
              ask = false;

            if ( ret == 2 ) // cancel
            {
              event.doit = false;
              return;
            }
          }
        }
      }
    } );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  /************************************ updateWindowTitles ***************************************/
  public void updateWindowTitles()
  {
    // refresh title on each JPlanner window
    for ( MainTabWidget tabs : m_tabWidgets )
    {
      Shell window = tabs.getShell();

      if ( JPlanner.plan.filename() == null || JPlanner.plan.filename().equals( "" ) )
      {
        window.setText( "JPlanner" );
      }
      else
      {
        if ( JPlanner.plan.undostack().isClean() )
          window.setText( JPlanner.plan.filename() + " - JPlanner" );
        else
          window.setText( JPlanner.plan.filename() + "* - JPlanner" );
      }
    }
  }

  /****************************************** message ********************************************/
  public void message( String msg )
  {
    // display message on status-bar
    if ( m_statusBar == null )
      JPlanner.trace( "MESSAGE BUT NO STATUS-BAR: " + msg );
    else
      m_statusBar.setText( msg );
  }

  /****************************************** schedule *******************************************/
  public void schedule()
  {
    // re-schedule plan
    JPlanner.plan.schedule();

    // update gui
    properties().updateFromPlan();
    updateTasks();
    m_tabWidgets.forEach( tabs -> tabs.gantt().updatePlot() );
  }

  /**************************************** updateMenus ******************************************/
  private void updateMenus()
  {
    // show Task menu when Tasks tab selected, otherwise hide it
    if ( m_mainTabWidget.getSelection()[0] == m_mainTabWidget.tasksTab() )
    {
      if ( m_menuTask == null )
        addTaskMenu( this.getMenuBar() );
    }
    else
    {
      if ( m_menuTask != null )
      {
        m_menuTask.dispose();
        m_menuTask = null;
      }
    }
  }

  /**************************************** addFileMenu ******************************************/
  private void addFileMenu( Menu menuBar )
  {
    // create file menu
    MenuItem menuFile = new MenuItem( menuBar, SWT.CASCADE );
    menuFile.setText( "File" );
    Menu fileMenu = new Menu( menuFile );
    menuFile.setMenu( fileMenu );

    // clear any old status-bar message when menu shown
    fileMenu.addMenuListener( m_menuListener );

    // add file menu items
    MenuItem actionNew = new MenuItem( fileMenu, SWT.NONE );
    actionNew.setAccelerator( SWT.CTRL + 'N' );
    actionNew.setText( "New\tCtrl+N" );
    actionNew.setEnabled( true );
    actionNew.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        newPlan();
      }
    } );

    MenuItem actionOpen = new MenuItem( fileMenu, SWT.NONE );
    actionOpen.setAccelerator( SWT.CTRL + 'O' );
    actionOpen.setText( "Open...\tCtrl+O" );
    actionOpen.setEnabled( true );
    actionOpen.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        load();
      }
    } );

    MenuItem actionSave = new MenuItem( fileMenu, SWT.NONE );
    actionSave.setAccelerator( SWT.CTRL + 'S' );
    actionSave.setText( "Save\tCtrl+S" );
    actionSave.setEnabled( true );
    actionSave.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        save();
      }
    } );

    MenuItem actionSaveAs = new MenuItem( fileMenu, SWT.NONE );
    actionSaveAs.setText( "Save As..." );
    actionSaveAs.setEnabled( true );
    actionSaveAs.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        saveAs();
      }
    } );

    new MenuItem( fileMenu, SWT.SEPARATOR );

    MenuItem actionPrintPreview = new MenuItem( fileMenu, SWT.NONE );
    actionPrintPreview.setText( "Print preview..." );
    actionPrintPreview.setEnabled( false );

    MenuItem actionPrint = new MenuItem( fileMenu, SWT.NONE );
    actionPrint.setText( "Print..." );
    actionPrint.setEnabled( false );

    new MenuItem( fileMenu, SWT.SEPARATOR );

    MenuItem actionExit = new MenuItem( fileMenu, SWT.NONE );
    actionExit.setAccelerator( SWT.CTRL + 'Q' );
    actionExit.setText( "Exit\tCtrl+Q" );
    actionExit.setEnabled( true );
    actionExit.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        close();
      }
    } );
  }

  /**************************************** addEditMenu ******************************************/
  private void addEditMenu( Menu menuBar )
  {
    // create edit menu
    MenuItem menuEdit = new MenuItem( menuBar, SWT.CASCADE );
    menuEdit.setText( "Edit" );
    Menu editMenu = new Menu( menuEdit );
    menuEdit.setMenu( editMenu );

    // clear any old status-bar message when menu shown
    editMenu.addMenuListener( m_menuListener );

    // add edit menu items
    actionUndo = new MenuItem( editMenu, SWT.NONE );
    actionUndo.setText( "Undo\tCtrl+Z" );
    actionUndo.setAccelerator( SWT.CTRL + 'Z' );
    actionUndo.setEnabled( false );
    actionUndo.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        JPlanner.plan.undostack().undo();
      }
    } );

    actionRedo = new MenuItem( editMenu, SWT.NONE );
    actionRedo.setText( "Redo\tCtrl+Y" );
    actionRedo.setAccelerator( SWT.CTRL + 'Y' );
    actionRedo.setEnabled( false );
    actionRedo.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        JPlanner.plan.undostack().redo();
      }
    } );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionInsert = new MenuItem( editMenu, SWT.NONE );
    actionInsert.setAccelerator( SWT.INSERT );
    actionInsert.setText( "Insert\tIns" );
    actionInsert.setEnabled( false );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionCut = new MenuItem( editMenu, SWT.NONE );
    actionCut.setAccelerator( SWT.CTRL + 'X' );
    actionCut.setText( "Cut\tCtrl+X" );
    actionCut.setEnabled( false );

    MenuItem actionCopy = new MenuItem( editMenu, SWT.NONE );
    actionCopy.setAccelerator( SWT.CTRL + 'C' );
    actionCopy.setText( "Copy\tCtrl+C" );
    actionCopy.setEnabled( false );

    MenuItem actionPaste = new MenuItem( editMenu, SWT.NONE );
    actionPaste.setAccelerator( SWT.CTRL + 'V' );
    actionPaste.setText( "Paste\tCtrl+V" );
    actionPaste.setEnabled( false );

    MenuItem actionDelete = new MenuItem( editMenu, SWT.NONE );
    actionDelete.setAccelerator( SWT.DEL );
    actionDelete.setText( "Delete\tDel" );
    actionDelete.setEnabled( false );

    new MenuItem( editMenu, SWT.SEPARATOR );

    MenuItem actionFindReplace = new MenuItem( editMenu, SWT.NONE );
    actionFindReplace.setAccelerator( SWT.CTRL + 'F' );
    actionFindReplace.setText( "Find/Replace...\tCtrl+F" );
    actionFindReplace.setEnabled( false );

    MenuItem actionSchedule = new MenuItem( editMenu, SWT.NONE );
    actionSchedule.setText( "Schedule" );
    actionSchedule.setEnabled( true );
    actionSchedule.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        JPlanner.gui.schedule();
      }
    } );
  }

  /**************************************** addTaskMenu ******************************************/
  private void addTaskMenu( Menu menuBar )
  {
    // create task menu
    m_menuTask = new MenuItem( menuBar, SWT.CASCADE, 2 );
    m_menuTask.setText( "Task" );
    Menu taskMenu = new Menu( m_menuTask );
    m_menuTask.setMenu( taskMenu );

    // clear any old status-bar message when menu shown
    taskMenu.addMenuListener( m_menuListener );

    // add task menu items
    MenuItem actionIndent = new MenuItem( taskMenu, SWT.NONE );
    actionIndent.setAccelerator( SWT.ALT + SWT.ARROW_RIGHT );
    actionIndent.setText( "Indent" );
    actionIndent.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        Set<Integer> rows = m_mainTabWidget.tasks().selectedRows();
        rows = JPlanner.plan.tasks.canIndent( rows );
        if ( !rows.isEmpty() )
          JPlanner.plan.undostack().push( new CommandTaskIndent( rows ) );
      }
    } );

    MenuItem actionOutdent = new MenuItem( taskMenu, SWT.NONE );
    actionOutdent.setAccelerator( SWT.ALT + SWT.ARROW_LEFT );
    actionOutdent.setText( "Outdent" );
    actionOutdent.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        Set<Integer> rows = m_mainTabWidget.tasks().selectedRows();
        rows = JPlanner.plan.tasks.canOutdent( rows );
        if ( !rows.isEmpty() )
          JPlanner.plan.undostack().push( new CommandTaskOutdent( rows ) );
      }
    } );
  }

  /**************************************** addViewMenu ******************************************/
  private void addViewMenu( Menu menuBar )
  {
    // create view menu
    MenuItem menuView = new MenuItem( menuBar, SWT.CASCADE );
    menuView.setText( "View" );
    Menu viewMenu = new Menu( menuView );
    menuView.setMenu( viewMenu );

    // clear any old status-bar message when menu shown
    viewMenu.addMenuListener( m_menuListener );

    // menu item to open undo-stack window
    actionUndoStackView = new MenuItem( viewMenu, SWT.CHECK );
    actionUndoStackView.setText( "Undo Stack..." );
    actionUndoStackView.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        if ( undoWindow == null )
        {
          undoWindow = new UndoStackWindow( MainWindow.this.getDisplay() );
          undoWindow.open();
        }
        undoWindow.setVisible( actionUndoStackView.getSelection() );
        undoWindow.setMinimized( false );
        undoWindow.forceActive();
      }
    } );

    // menu item to open new window
    MenuItem actionNewWindow = new MenuItem( viewMenu, SWT.NONE );
    actionNewWindow.setText( "New window..." );
    actionNewWindow.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        newWindow().getShell().open();
      }
    } );

    new MenuItem( viewMenu, SWT.SEPARATOR );

    // menu item to toggle gantt stretching of tasks
    MenuItem actionStretchTasks = new MenuItem( viewMenu, SWT.CHECK );
    actionStretchTasks.setText( "Stretch tasks" );
    GanttPlot.ganttStretch = true;
    actionStretchTasks.setSelection( GanttPlot.ganttStretch );
    actionStretchTasks.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        GanttPlot.ganttStretch = actionStretchTasks.getSelection();
        m_tabWidgets.forEach( tabs -> tabs.gantt().updatePlot() );
      }
    } );
  }

  /***************************************** newWindow *******************************************/
  private MainTabWidget newWindow()
  {
    // create new window
    Shell newWindow = new Shell( MainWindow.this.getDisplay(), SWT.SHELL_TRIM );
    newWindow.setSize( 700, 400 );
    newWindow.setLayout( new FillLayout( SWT.FILL ) );
    newWindow.setText( getShell().getText() );

    // populate window with MainTabWidget
    MainTabWidget newTabWidget = new MainTabWidget( newWindow, false );
    newTabWidget.gantt().setConfig( m_tabWidgets.get( 0 ).gantt() );
    newTabWidget.setTasksGanttSplitter( 350 );

    // add new MainTabWidget to tracking list
    m_tabWidgets.add( newTabWidget );

    // add dispose listener to keep tracking list updated
    newWindow.addDisposeListener( new DisposeListener()
    {
      @Override
      public void widgetDisposed( DisposeEvent event )
      {
        m_tabWidgets.remove( newTabWidget );
      }
    } );

    return newTabWidget;
  }

  /*************************************** addReportMenu *****************************************/
  private void addReportMenu( Menu menuBar )
  {
    // create report menu
    MenuItem menuReport = new MenuItem( menuBar, SWT.CASCADE );
    menuReport.setText( "Report" );
    Menu reportMenu = new Menu( menuReport );
    menuReport.setMenu( reportMenu );

    // clear any old status-bar message when menu shown
    reportMenu.addMenuListener( m_menuListener );

    // add report menu items
    MenuItem actionTBD = new MenuItem( reportMenu, SWT.NONE );
    actionTBD.setText( "TBD" );
    actionTBD.setEnabled( false );
  }

  /**************************************** addHelpMenu ******************************************/
  private void addHelpMenu( Menu menuBar )
  {
    // create help menu
    MenuItem menuHelp = new MenuItem( menuBar, SWT.CASCADE );
    menuHelp.setText( "Help" );
    Menu helpMenu = new Menu( menuHelp );
    menuHelp.setMenu( helpMenu );

    // clear any old status-bar message when menu shown
    helpMenu.addMenuListener( m_menuListener );

    // add help menu items
    MenuItem actionAboutJplanner = new MenuItem( helpMenu, SWT.NONE );
    actionAboutJplanner.setText( "About JPlanner" );
    actionAboutJplanner.setEnabled( false );
  }

  /***************************************** properties ******************************************/
  public PlanProperties properties()
  {
    return m_mainTabWidget.properties();
  }

  /******************************************** notes ********************************************/
  public PlanNotes notes()
  {
    return m_mainTabWidget.notes();
  }

  /****************************************** resetGui *******************************************/
  public void resetGui( boolean resetGantt )
  {
    // update window titles and plan tab
    updateWindowTitles();
    properties().updateFromPlan();
    notes().updateFromPlan();

    // reset tasks resources calendars day-types tables
    m_tabWidgets.forEach( tabs -> tabs.tasks().refresh() );
    m_tabWidgets.forEach( tabs -> tabs.resources().refresh() );
    m_tabWidgets.forEach( tabs -> tabs.calendars().refresh() );
    m_tabWidgets.forEach( tabs -> tabs.days().refresh() );

    // update gantts including reseting to default parameters if requested
    if ( resetGantt )
      m_tabWidgets.forEach( tabs -> tabs.gantt().setDefault() );
    m_tabWidgets.forEach( tabs -> tabs.gantt().updateAll() );

    // update undo-stack window if exists
    if ( undoWindow != null )
      undoWindow.setList();
  }

  /***************************************** updateTasks *****************************************/
  public void updateTasks()
  {
    // update all tasks tables
    m_tabWidgets.forEach( tabs -> tabs.tasks().redraw() );
  }

  /*************************************** updateResources ***************************************/
  public void updateResources()
  {
    // update all resource tables
    m_tabWidgets.forEach( tabs -> tabs.resources().redraw() );
  }

  /*************************************** updateCalendars ***************************************/
  public void updateCalendars()
  {
    // update all calendar tables
    m_tabWidgets.forEach( tabs -> tabs.calendars().redraw() );
  }

  /****************************************** updateDays *****************************************/
  public void updateDays()
  {
    // update all day-type tables
    m_tabWidgets.forEach( tabs -> tabs.days().redraw() );
  }

  /******************************************* newPlan *******************************************/
  private void newPlan()
  {
    // if undo-stack not clean, ask user what to do
    if ( !JPlanner.plan.undostack().isClean() )
    {
      boolean ask = true;
      while ( ask )
      {
        XMessageDialog dialog = new XMessageDialog( getShell(), "New plan", "Do you want to save before starting new?",
            MessageDialog.QUESTION, new String[] { "Save", "Discard", "Cancel" }, 2 );
        int ret = dialog.open();

        if ( ret == 0 ) // save
          ask = !saveAs();

        if ( ret == 1 ) // discard
          ask = false;

        if ( ret == 2 ) // cancel
          return;
      }
    }

    // create new plan
    JPlanner.plan = new Plan();
    JPlanner.plan.initialise();

    // update gui
    resetGui( true );
    message( "New plan" );
  }

  /******************************************** load *********************************************/
  public boolean load()
  {
    // if undo-stack not clean, ask user what to do
    if ( !JPlanner.plan.undostack().isClean() )
    {
      boolean ask = true;
      while ( ask )
      {
        XMessageDialog dialog = new XMessageDialog( getShell(), "Open plan", "Do you want to save before opening new?",
            MessageDialog.QUESTION, new String[] { "Save", "Discard", "Cancel" }, 2 );
        int ret = dialog.open();

        if ( ret == 0 ) // save
          ask = !saveAs();

        if ( ret == 1 ) // discard
          ask = false;

        if ( ret == 2 ) // cancel
          return false;
      }
    }

    // open file-dialog to ask for filename & location
    FileDialog fd = new FileDialog( this, SWT.OPEN );
    String[] filterExt = { "*.xml", "*.*" };
    fd.setFilterExtensions( filterExt );
    String filename = fd.open();

    // if user cancels filename is null, so exit immediately
    if ( filename == null )
      return false;

    // attempt to load from user supplied filename & location
    return load( new File( filename ) );
  }

  /******************************************** load *********************************************/
  public boolean load( File file )
  {
    // check file exists
    if ( !file.exists() )
    {
      message( "Cannot find '" + file.getPath() + "'" );
      return false;
    }

    // check file can be read
    if ( !file.canRead() )
    {
      message( "Cannot read '" + file.getPath() + "'" );
      return false;
    }

    // create temporary plan for loading into
    Plan oldPlan = JPlanner.plan;
    JPlanner.plan = new Plan();

    // attempt to load plan and display-data from XML file
    try
    {
      // create XML stream reader
      XMLInputFactory xif = XMLInputFactory.newInstance();
      FileInputStream fis = new FileInputStream( file );
      XMLStreamReader xsr = xif.createXMLStreamReader( fis );

      // check first element is JPlanner
      while ( xsr.hasNext() && !xsr.isStartElement() )
        xsr.next();
      if ( !xsr.isStartElement() || !xsr.getLocalName().equals( XmlLabels.XML_JPLANNER ) )
        throw new XMLStreamException( "Missing JPlanner element" );

      // load plan data
      JPlanner.plan.loadXML( xsr, file.getName(), file.getParent() );

      // if new plan not okay, revert back to old plan
      if ( JPlanner.plan.errors() != null )
      {
        message( "Plan '" + file.getPath() + "' not valid (" + JPlanner.plan.errors() + ")" );
        JPlanner.plan = oldPlan;
        fis.close();
        xsr.close();
        return false;
      }

      // load display data
      loadDisplayData( xsr );

      fis.close();
      xsr.close();
    }
    catch ( Exception exception )
    {
      // some sort of exception thrown
      message( "Failed to load '" + file.getPath() + "'" );
      JPlanner.plan = oldPlan;
      exception.printStackTrace();
      return false;
    }

    // update gui & schedule
    resetGui( false );
    message( "Successfully loaded '" + file.getPath() + "'" );
    schedule();

    return true;
  }

  /******************************************* saveAs ********************************************/
  public boolean saveAs()
  {
    // open file-dialog to ask for filename & location
    FileDialog fd = new FileDialog( this, SWT.SAVE );
    String[] filterExt = { "*.xml", "*.*" };
    fd.setFilterExtensions( filterExt );
    String filename = fd.open();

    // if user cancels filename is null, so exit immediately
    if ( filename == null )
      return false;

    // attempt to save using user supplied filename & location
    return save( new File( filename ) );
  }

  /******************************************** save *********************************************/
  public boolean save()
  {
    // if no existing filename set, use save-as
    if ( JPlanner.plan.filename() == null || JPlanner.plan.filename().equals( "" ) )
      return saveAs();

    // attempt to save using existing filename & location
    return save( new File( JPlanner.plan.fileLocation(), JPlanner.plan.filename() ) );
  }

  /******************************************** save *********************************************/
  public boolean save( File file )
  {
    // if file exists already, check file can be written
    if ( file.exists() && !file.canWrite() )
    {
      message( "Cannot write to '" + file.getPath() + "'" );
      return false;
    }

    // create XML stream writer to temporary file
    try
    {
      File tempFile = temporaryFile( file );
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      FileOutputStream fos = new FileOutputStream( tempFile );
      XMLStreamWriter xsw = new IndentingXMLStreamWriter( xof.createXMLStreamWriter( fos, XmlLabels.ENCODING ) );

      // start XML document
      xsw.writeStartDocument( XmlLabels.ENCODING, XmlLabels.VERSION );
      xsw.writeStartElement( XmlLabels.XML_JPLANNER );
      xsw.writeAttribute( XmlLabels.XML_FORMAT, XmlLabels.FORMAT );
      String saveUser = System.getProperty( "user.name" );
      xsw.writeAttribute( XmlLabels.XML_SAVEUSER, saveUser );
      DateTime saveWhen = DateTime.now();
      xsw.writeAttribute( XmlLabels.XML_SAVEWHEN, saveWhen.toString() );
      xsw.writeAttribute( XmlLabels.XML_SAVENAME, file.getName() );
      xsw.writeAttribute( XmlLabels.XML_SAVEWHERE, file.getParent() );

      // save plan data to stream
      if ( !JPlanner.plan.savePlan( xsw, fos ) )
      {
        message( "Failed to save plan to '" + file.getPath() + "'" );
        return false;
      }

      // save display data to stream
      saveDisplayData( xsw );

      // close XML document
      xsw.writeEndElement(); // XML_JPLANNER
      xsw.writeEndDocument();
      xsw.flush();
      xsw.close();
      fos.close();

      // rename files, and update plan file details
      File backupFile = new File( file.getAbsolutePath() + "~" );
      backupFile.delete();
      file.renameTo( backupFile );
      tempFile.renameTo( file );
      JPlanner.plan.setFileDetails( file.getName(), file.getParent(), saveUser, saveWhen );
    }
    catch ( XMLStreamException | IOException exception )
    {
      // some sort of exception thrown
      exception.printStackTrace();
      return false;
    }

    // save succeed, so update gui
    properties().updateFromPlan();
    JPlanner.plan.undostack().setClean();
    updateWindowTitles();
    message( "Saved plan to '" + file.getPath() + "'" );
    return true;
  }

  /**************************************** temporaryFile ****************************************/
  private File temporaryFile( File file )
  {
    // return temporary file name based on given file
    String path = file.getParent();
    String name = file.getName();
    int last = name.lastIndexOf( '.' );
    if ( last >= 0 )
      name = name.substring( 0, last ) + DateTime.now().milliseconds() + name.substring( last, name.length() );
    else
      name += DateTime.now();

    return new File( path + File.separator + name );
  }

  /*************************************** saveDisplayData ***************************************/
  private void saveDisplayData( XMLStreamWriter xsw ) throws XMLStreamException
  {
    // save display data to stream for each window
    for ( MainTabWidget tabs : m_tabWidgets )
    {
      Shell window = tabs.getShell();

      xsw.writeStartElement( XmlLabels.XML_DISPLAY_DATA );
      xsw.writeAttribute( XmlLabels.XML_WINDOW, Integer.toString( m_tabWidgets.indexOf( tabs ) ) );
      xsw.writeAttribute( XmlLabels.XML_X, Integer.toString( window.getBounds().x ) );
      xsw.writeAttribute( XmlLabels.XML_Y, Integer.toString( window.getBounds().y ) );
      xsw.writeAttribute( XmlLabels.XML_WIDTH, Integer.toString( window.getBounds().width ) );
      xsw.writeAttribute( XmlLabels.XML_HEIGHT, Integer.toString( window.getBounds().height ) );
      xsw.writeAttribute( XmlLabels.XML_TAB, Integer.toString( tabs.getSelectionIndex() ) );

      tabs.writeXML( xsw );

      xsw.writeEndElement(); // XML_DISPLAY_DATA
    }
  }

  /*************************************** loadDisplayData ***************************************/
  public void loadDisplayData( XMLStreamReader xsr ) throws XMLStreamException
  {
    // close all but main window, need to loop around clone to avoid potential concurrent modification of m_tabWidgets
    @SuppressWarnings( "unchecked" )
    ArrayList<MainTabWidget> list = (ArrayList<MainTabWidget>) m_tabWidgets.clone();
    for ( MainTabWidget tabs : list )
    {
      Shell window = tabs.getShell();

      if ( window != this.getShell() )
        window.dispose();
    }

    // read XML display data
    MainTabWidget tabs = null;
    while ( xsr.hasNext() )
    {
      xsr.next();

      if ( xsr.isStartElement() )
        switch ( xsr.getLocalName() )
        {
          case XmlLabels.XML_DISPLAY_DATA:
            if ( tabs == null )
              tabs = m_mainTabWidget;
            else
              tabs = newWindow();

            Rectangle rect = tabs.getShell().getBounds();
            int tab = 0;

            // read XML attributes
            for ( int i = 0; i < xsr.getAttributeCount(); i++ )
              switch ( xsr.getAttributeLocalName( i ) )
              {
                case XmlLabels.XML_WINDOW:
                  break;
                case XmlLabels.XML_X:
                  rect.x = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_Y:
                  rect.y = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_WIDTH:
                  rect.width = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_HEIGHT:
                  rect.height = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;
                case XmlLabels.XML_TAB:
                  tab = Integer.parseInt( xsr.getAttributeValue( i ) );
                  break;

                default:
                  JPlanner.trace( "Unhandled attribute '" + xsr.getAttributeLocalName( i ) + "'" );
                  break;
              }

            // set selected tab and window bounding rectangle 
            tabs.setSelection( tab );
            if ( tabs == m_mainTabWidget )
              updateMenus();
            tabs.getShell().setBounds( checkShellBounds( rect ) );
            tabs.getShell().open();
            break;

          case XmlLabels.XML_TASKS_GANTT_TAB:
            tabs.loadXmlTasksGantt( xsr );
            break;
          case XmlLabels.XML_RESOURCES_TAB:
            tabs.loadXmlResources( xsr );
            break;
          case XmlLabels.XML_CALENDARS_TAB:
            tabs.loadXmlCalendars( xsr );
            break;
          case XmlLabels.XML_DAYS_TAB:
            tabs.loadXmlDayTypes( xsr );
            break;
          default:
            JPlanner.trace( "Unhandled start element '" + xsr.getLocalName() + "'" );
            break;
        }
    }

  }

  /*************************************** checkShellBounds ***************************************/
  private Rectangle checkShellBounds( Rectangle rect )
  {
    // check rectangle fits fully on a monitor
    Monitor[] monitors = getDisplay().getMonitors();

    // if top-left corner on monitor, use that monitor
    for ( int i = 0; i < monitors.length; i++ )
    {
      Rectangle monitor = monitors[i].getClientArea();
      if ( monitor.contains( rect.x, rect.y ) )
        return fitToArea( rect, monitor );
    }

    // otherwise determine which monitor nearest (using Manhattan distance)
    int distance = Integer.MAX_VALUE;
    int nearest = -1;
    for ( int i = 0; i < monitors.length; i++ )
    {
      Rectangle monitor = monitors[i].getClientArea();
      int monitorDistance = Math.abs( rect.x - monitor.x ) + Math.abs( rect.y - monitor.y );

      if ( monitorDistance < distance )
      {
        nearest = i;
        distance = monitorDistance;
      }
    }

    return fitToArea( rect, monitors[nearest].getClientArea() );
  }

  /****************************************** fitToArea ******************************************/
  private Rectangle fitToArea( Rectangle rect, Rectangle area )
  {
    // check x
    if ( rect.x < area.x )
      rect.x = area.x;

    // check y
    if ( rect.y < area.y )
      rect.y = area.y;

    // check width
    int excess = ( rect.x + rect.width ) - ( area.x + area.width );
    if ( excess > 0 )
    {
      if ( excess > rect.x - area.x )
      {
        rect.x = area.x;
        rect.width = area.width;
      }
      else
        rect.x -= excess;
    }

    // check height
    excess = ( rect.y + rect.height ) - ( area.y + area.height );
    if ( excess > 0 )
    {
      if ( excess > rect.y - area.y )
      {
        rect.y = area.y;
        rect.height = area.height;
      }
      else
        rect.y -= excess;
    }

    // return adjusted rectangle
    return rect;
  }

}