package rjc.jplanner.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class MainTabWidget extends TabFolder
{
  private Text  text;
  private Text  text_1;
  private Table table;
  private Table table_1;
  private Table table_2;
  private Table table_3;

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

    SashForm sashForm = new SashForm( this, SWT.SMOOTH );
    tabPlan.setControl( sashForm );

    text = new Text( sashForm, SWT.BORDER );

    text_1 = new Text( sashForm, SWT.BORDER );
    sashForm.setWeights( new int[] { 1, 1 } );

    TabItem tabTasksGantt = new TabItem( this, SWT.NONE );
    tabTasksGantt.setText( "Tasks && Gantt" );

    SashForm sashForm_1 = new SashForm( this, SWT.SMOOTH );
    tabTasksGantt.setControl( sashForm_1 );

    table_3 = new Table( sashForm_1, SWT.BORDER | SWT.FULL_SELECTION );
    table_3.setHeaderVisible( true );
    table_3.setLinesVisible( true );

    ScrolledComposite scrolledComposite = new ScrolledComposite( sashForm_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    scrolledComposite.setExpandHorizontal( true );
    scrolledComposite.setExpandVertical( true );
    sashForm_1.setWeights( new int[] { 1, 1 } );

    TabItem tbtmResources = new TabItem( this, SWT.NONE );
    tbtmResources.setText( "Resources" );

    table_2 = new Table( this, SWT.BORDER | SWT.FULL_SELECTION );
    tbtmResources.setControl( table_2 );
    table_2.setHeaderVisible( true );
    table_2.setLinesVisible( true );

    TabItem tbtmCalendars = new TabItem( this, SWT.NONE );
    tbtmCalendars.setText( "Calendars" );

    table_1 = new Table( this, SWT.BORDER | SWT.FULL_SELECTION );
    tbtmCalendars.setControl( table_1 );
    table_1.setHeaderVisible( true );
    table_1.setLinesVisible( true );

    TabItem tbtmDays = new TabItem( this, SWT.NONE );
    tbtmDays.setText( "Days" );

    table = new Table( this, SWT.BORDER | SWT.FULL_SELECTION );
    tbtmDays.setControl( table );
    table.setHeaderVisible( true );
    table.setLinesVisible( true );

  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

}
