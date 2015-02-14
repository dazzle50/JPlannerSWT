package rjc.jplanner.gui;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class MainTabWidget extends TabFolder
{
  private Text     tempPlanProperties;
  private Text     tempPlanNotes;
  private NatTable tableDays;
  private NatTable tableCalendars;
  private NatTable tableResources;
  private NatTable tableTasks;

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public MainTabWidget( Composite parent, int style )
  {
    super( parent, style );

    TabItem tabPlan = new TabItem( this, SWT.NONE );
    tabPlan.setText( "Plan" );

    SashForm splitterPlanTab = new SashForm( this, SWT.SMOOTH );
    tabPlan.setControl( splitterPlanTab );

    tempPlanProperties = new Text( splitterPlanTab, SWT.BORDER );

    tempPlanNotes = new Text( splitterPlanTab, SWT.BORDER );
    splitterPlanTab.setWeights( new int[] { 1, 1 } );

    TabItem tabTasksGantt = new TabItem( this, SWT.NONE );
    tabTasksGantt.setText( "Tasks && Gantt" );

    SashForm splitterTasksGantt = new SashForm( this, SWT.SMOOTH );
    tabTasksGantt.setControl( splitterTasksGantt );

    tableTasks = new NatTable( splitterTasksGantt );

    ScrolledComposite scrolledComposite = new ScrolledComposite( splitterTasksGantt, SWT.BORDER | SWT.H_SCROLL
        | SWT.V_SCROLL );
    scrolledComposite.setExpandHorizontal( true );
    scrolledComposite.setExpandVertical( true );
    splitterTasksGantt.setWeights( new int[] { 1, 1 } );

    TabItem tabResources = new TabItem( this, SWT.NONE );
    tabResources.setText( "Resources" );

    tableResources = new NatTable( this );
    tabResources.setControl( tableResources );

    TabItem tabCalendars = new TabItem( this, SWT.NONE );
    tabCalendars.setText( "Calendars" );

    tableCalendars = new NatTable( this );
    tabCalendars.setControl( tableCalendars );

    TabItem tabDays = new TabItem( this, SWT.NONE );
    tabDays.setText( "Days" );

    tableDays = new NatTable( this );
    tabDays.setControl( tableDays );
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
