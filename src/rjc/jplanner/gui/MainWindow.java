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

package rjc.jplanner.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import rjc.jplanner.JPlanner;
import rjc.jplanner.gui.table.TableRegister;
import rjc.jplanner.model.Plan;

/*************************************************************************************************/
/******************************* Main JPlanner application window ********************************/
/*************************************************************************************************/

public class MainWindow extends Shell
{
  private MainTabWidget    m_mainTabWidget;       // MainTabWidget associated with MainWindow
  private ArrayList<Shell> m_windows;             // list of windows including this one
  private Text             m_statusBar;
  private MenuListener     m_menuListener;
  private MenuItem         m_menuTask;
  private TableRegister    m_taskTables;          // register of tables showing tasks
  private TableRegister    m_resourceTables;      // register of tables showing resources
  private TableRegister    m_calendarTables;      // register of tables showing calendars
  private TableRegister    m_dayTables;           // register of tables showing day-types

  public UndoStackWindow   undoWindow;            // window to show plan undo-stack
  public MenuItem          actionUndoStackView;   // action to show plan undo-stack window
  public MenuItem          actionUndo;
  public MenuItem          actionRedo;

  public Color             COLOR_GANTT_BACKGROUND;
  public Color             COLOR_GANTT_NONWORKING;
  public Color             COLOR_GANTT_DIVIDER;
  public Color             COLOR_BLACK;
  public Transform         TRANSFORM;
  public int               GANTTSCALE_HEIGHT = 15;

  /**************************************** constructor ******************************************/
  public MainWindow( Display display )
  {
    // create JPlanner main window
    super( display, SWT.SHELL_TRIM );
    setSize( 1100, 510 );
    setText( "JPlanner" );

    // initialise some public variables for use elsewhere
    COLOR_GANTT_BACKGROUND = display.getSystemColor( SWT.COLOR_WHITE );
    COLOR_GANTT_NONWORKING = new Color( display, 240, 240, 240 );
    COLOR_GANTT_DIVIDER = display.getSystemColor( SWT.COLOR_GRAY );
    COLOR_BLACK = display.getSystemColor( SWT.COLOR_BLACK );
    TRANSFORM = new Transform( display );

    // prepare the table registers for the different types of tables
    m_taskTables = new TableRegister();
    m_resourceTables = new TableRegister();
    m_calendarTables = new TableRegister();
    m_dayTables = new TableRegister();

    // prepare listener to clear any old status-bar message when menu shown & check for properties/notes changes
    m_menuListener = new MenuListener()
    {
      @Override
      public void menuShown( MenuEvent e )
      {
        message( "" );
        properties().updatePlan();
        notes().updatePlan();
      }

      @Override
      public void menuHidden( MenuEvent e )
      {
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

    // listener to detect when selected tab changed
    m_mainTabWidget.addListener( SWT.Selection, new Listener()
    {
      @Override
      public void handleEvent( Event event )
      {
        // ensure plan is kept up-to-date with the properties and notes
        m_mainTabWidget.properties().updatePlan();
        m_mainTabWidget.notes().updatePlan();

        // show Task menu when Tasks tab selected, otherwise hide it
        if ( m_mainTabWidget.getSelection()[0] == m_mainTabWidget.tasksTab() )
        {
          if ( m_menuTask == null )
            addTaskMenu( menuBar );
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
    } );

    // add status bar
    m_statusBar = new Text( this, SWT.SINGLE | SWT.READ_ONLY );
    m_statusBar.setText( "JPlanner started" );
    m_statusBar.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

    // add this window to list used for example when updating titles
    m_windows = new ArrayList<Shell>();
    m_windows.add( this );

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
              ask = !saveAsPlan();

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

  /*************************************** updateTitles ******************************************/
  public void updateTitles()
  {
    // refresh title on each JPlanner window
    Iterator<Shell> iter = m_windows.iterator();
    while ( iter.hasNext() )
    {
      // if table has been disposed, remove from list and skip
      Shell window = iter.next();
      if ( window.isDisposed() )
      {
        iter.remove();
        continue;
      }

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
    m_statusBar.setText( msg );
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
        loadPlan();
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
        savePlan();
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
        saveAsPlan();
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
    actionSchedule.setEnabled( false );
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
    actionIndent.setText( "Indent" );
    actionIndent.setEnabled( false );

    MenuItem actionOutdent = new MenuItem( taskMenu, SWT.NONE );
    actionOutdent.setText( "Outdent" );
    actionOutdent.setEnabled( false );
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

    // add view menu items
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

    MenuItem actionNewWindow = new MenuItem( viewMenu, SWT.NONE );
    actionNewWindow.setText( "New window..." );
    actionNewWindow.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( SelectionEvent event )
      {
        Shell newWindow = new Shell( MainWindow.this.getDisplay(), SWT.SHELL_TRIM );
        newWindow.setSize( 700, 400 );
        newWindow.setLayout( new FillLayout( SWT.FILL ) );
        new MainTabWidget( newWindow, false );
        newWindow.open();

        m_windows.add( newWindow );
        updateTitles();
      }
    } );

    new MenuItem( viewMenu, SWT.SEPARATOR );

    MenuItem actionStretchTasks = new MenuItem( viewMenu, SWT.NONE );
    actionStretchTasks.setText( "Stretch tasks" );
    actionStretchTasks.setEnabled( false );
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

  /****************************************** dayTables ******************************************/
  public TableRegister dayTables()
  {
    return m_dayTables;
  }

  /**************************************** calendarTables ***************************************/
  public TableRegister calendarTables()
  {
    return m_calendarTables;
  }

  /**************************************** resourceTables ***************************************/
  public TableRegister resourceTables()
  {
    return m_resourceTables;
  }

  /****************************************** taskTables *****************************************/
  public TableRegister taskTables()
  {
    return m_taskTables;
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
          ask = !saveAsPlan();

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
    properties().updateFromPlan();
    notes().updateFromPlan();
    dayTables().refresh();
    calendarTables().refresh();
    resourceTables().refresh();
    taskTables().refresh();
    updateTitles();
    message( "New plan" );
  }

  /****************************************** loadPlan *******************************************/
  public boolean loadPlan()
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
          ask = !saveAsPlan();

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
    return loadPlan( new File( filename ) );
  }

  /****************************************** loadPlan *******************************************/
  public boolean loadPlan( File file )
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

    // attempt to load plan from file
    Plan oldPlan = JPlanner.plan;
    JPlanner.plan = new Plan();
    if ( !JPlanner.plan.loadPlan( file ) )
    {
      message( "Failed to load '" + file.getPath() + "'" );
      return false;
    }

    // if new plan not okay, revert back to old plan
    if ( !JPlanner.plan.isOK() )
    {
      JPlanner.plan = oldPlan;
      message( "Plan '" + file.getPath() + "' not valid." );
      return false;
    }

    // successfully loaded plan, so update gui
    properties().updateFromPlan();
    notes().updateFromPlan();
    dayTables().refresh();
    calendarTables().refresh();
    resourceTables().refresh();
    taskTables().refresh();
    updateTitles();
    message( "Successfully loaded '" + file.getPath() + "'" );
    return true;
  }

  /***************************************** saveAsPlan ******************************************/
  public boolean saveAsPlan()
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
    return savePlan( new File( filename ) );
  }

  /****************************************** savePlan *******************************************/
  public boolean savePlan()
  {
    // if no existing filename set, use save-as
    if ( JPlanner.plan.filename() == null || JPlanner.plan.filename().equals( "" ) )
      return saveAsPlan();

    // attempt to save using existing filename & location
    return savePlan( new File( JPlanner.plan.fileLocation(), JPlanner.plan.filename() ) );
  }

  /****************************************** savePlan *******************************************/
  public boolean savePlan( File file )
  {
    // check file can be written
    if ( !file.canWrite() )
    {
      message( "Cannot write to '" + file.getPath() + "'" );
      return false;
    }

    // save plan to file
    if ( !JPlanner.plan.savePlan( file ) )
    {
      message( "Failed to save plan to '" + file.getPath() + "'" );
      return false;
    }

    // save succeed, so update gui
    properties().updateFromPlan();
    JPlanner.plan.undostack().setClean();
    updateTitles();
    message( "Saved plan to '" + file.getPath() + "'" );
    return true;
  }
}
