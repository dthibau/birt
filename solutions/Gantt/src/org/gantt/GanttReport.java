package org.gantt;


import java.io.IOException;
import java.util.ArrayList;

import javax.lang.model.type.IntersectionType;
import javax.management.Query;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.framework.Platform;

public class GanttReport
{

	ReportDesignHandle reportDesignHandle = null;

	ElementFactory elementFactory = null;

	IMetaDataDictionary dict = null;

	ComputedColumn cs1, cs2, cs3, cs4, cs5 = null;

	public static void main( String[] args ) throws SemanticException,
	IOException
	{
		new GanttReport( ).createReport( );
	}

	void createReport( ) throws SemanticException, IOException
	{
		//Configure the Engine and start the Platform
		DesignConfig config = new DesignConfig( );
		//config.setBIRTHome("C:/birt/birt-runtime-2_6_1/birt-runtime-2_6_1/ReportEngine");
		IDesignEngine engine = null;
		try{


			Platform.startup( config );
			IDesignEngineFactory factory = (IDesignEngineFactory) Platform
			.createFactoryObject( IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY );
			engine = factory.createDesignEngine( config );

		}catch( Exception ex){
			ex.printStackTrace();
		}


		SessionHandle session = engine.newSessionHandle( ULocale.ENGLISH ) ;


		// Create a new report
		reportDesignHandle = session.createDesign( );

		// Element factory is used to create instances of BIRT elements.
		elementFactory = reportDesignHandle.getElementFactory( );

		dict = new DesignEngine( null ).getMetaData( );

		createMasterPages( );
		buildDataSource( );
		buildDataSet( );
		createBody( );


		reportDesignHandle.saveAs("output/desample/GanttReport.rptdesign" );//$NON-NLS-1$//$NON-NLS-2$
		reportDesignHandle.close();

		System.out.println("finished");

		Platform.shutdown();
	}

	void buildDataSource( ) throws SemanticException
	{

		OdaDataSourceHandle dsHandle = elementFactory.newOdaDataSource(
				"Data Source", "org.eclipse.birt.report.data.oda.jdbc" );
		dsHandle.setProperty( "odaDriverClass",
		"org.eclipse.birt.report.data.oda.sampledb.Driver" );
		dsHandle.setProperty( "odaURL", "jdbc:classicmodels:sampledb" );
		dsHandle.setProperty( "odaUser", "ClassicModels" );
		dsHandle.setProperty( "odaPassword", "" );
		reportDesignHandle.getDataSources( ).add( dsHandle );


	}

	void buildDataSet( ) throws SemanticException
	{

		OdaDataSetHandle dsHandle = elementFactory.newOdaDataSet( "ds",
		"org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet" );
		dsHandle.setDataSource( "Data Source" );
		String qry = "Select ORDERNUMBER, ORDERDATE, SHIPPEDDATE, CUSTOMERNUMBER from orders where ordernumber < 10104";

		dsHandle.setQueryText( qry );

		
		reportDesignHandle.getDataSets( ).add( dsHandle );



	}


	private void createMasterPages( ) throws ContentException, NameException
	{
		DesignElementHandle simpleMasterPage = elementFactory.newSimpleMasterPage( "Master Page" );//$NON-NLS-1$
		reportDesignHandle.getMasterPages( ).add( simpleMasterPage );
	}

	public final static Chart createGantt( )
	{
		ChartWithAxes cwaGantt = ChartWithAxesImpl.create( );
		cwaGantt.setType( "Gantt Chart" ); //$NON-NLS-1$
		cwaGantt.setSubType( "Standard Gantt Chart" ); //$NON-NLS-1$
		cwaGantt.setOrientation( Orientation.HORIZONTAL_LITERAL );
		cwaGantt.getBlock().getBounds().setWidth(600);
		cwaGantt.getBlock().getBounds().setHeight(400);
		// Plot
		cwaGantt.getBlock( ).setBackground( ColorDefinitionImpl.WHITE( ) );
		cwaGantt.getBlock( ).getOutline( ).setVisible( true );
		Plot p = cwaGantt.getPlot( );
		p.getClientArea( ).setBackground( ColorDefinitionImpl.create( 255,
				255,
				225 ) );

		// Title
		cwaGantt.getTitle( ).getLabel( ).getCaption( ).setValue( "Gantt Chart" ); //$NON-NLS-1$

		// Legend
		Legend lg = cwaGantt.getLegend( );
		lg.setItemType( LegendItemType.CATEGORIES_LITERAL );

		// X-Axis
		Axis xAxisPrimary = cwaGantt.getPrimaryBaseAxes( )[0];
		xAxisPrimary.setCategoryAxis( true );
		xAxisPrimary.getMajorGrid( ).setTickStyle( TickStyle.BELOW_LITERAL );
		xAxisPrimary.getOrigin( ).setType( IntersectionType.MIN_LITERAL );

		// Y-Axis
		Axis yAxisPrimary = cwaGantt.getPrimaryOrthogonalAxis( xAxisPrimary );
		yAxisPrimary.setType( AxisType.DATE_TIME_LITERAL );
		yAxisPrimary.getMajorGrid( ).setTickStyle( TickStyle.LEFT_LITERAL );

		DateFormatSpecifier dfs = AttributeFactory.eINSTANCE.createDateFormatSpecifier( );		
		dfs.setDetail( DateFormatDetail.DATE_LITERAL );
		dfs.setType( DateFormatType.MEDIUM_LITERAL );
		yAxisPrimary.setFormatSpecifier( dfs );

		SampleData sd = DataFactory.eINSTANCE.createSampleData( );
		BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData( );
		sdBase.setDataSetRepresentation( "" );//$NON-NLS-1$
		sd.getBaseSampleData( ).add( sdBase );

		OrthogonalSampleData sdOrthogonal1 = DataFactory.eINSTANCE.createOrthogonalSampleData( );
		sdOrthogonal1.setDataSetRepresentation( "" );//$NON-NLS-1$
		sdOrthogonal1.setSeriesDefinitionIndex( 0 );
		sd.getOrthogonalSampleData( ).add( sdOrthogonal1 );

		cwaGantt.setSampleData( sd );


		// X-Series
		Series seCategory = SeriesImpl.create( );
		seCategory.getDataDefinition().add(QueryImpl.create( "row[\"ORDERNUMBER\"]" ));


		SeriesDefinition sdX = SeriesDefinitionImpl.create( );
		sdX.getSeriesPalette( ).shift( 0 );
		xAxisPrimary.getSeriesDefinitions( ).add( sdX );
		sdX.getSeries( ).add( seCategory );

		// Y-Series
		GanttSeries taskPhase1 = (GanttSeries) GanttSeriesImpl.create( );

		taskPhase1.getLabel( ).setVisible( false );

		Query query1 = QueryImpl.create( "row[\"ORDERDATE\"]" );//$NON-NLS-1$
		Query query2 = QueryImpl.create( "row[\"SHIPPEDDATE\"]" );//$NON-NLS-1$
		Query query3 = QueryImpl.create( "row[\"CUSTOMERNUMBER\"]" );//$NON-NLS-1$

		SeriesGrouping q1 =SeriesGroupingImpl.create( );	
		q1.setAggregateExpression("Sum");
		//q1.setEnabled(true);
		q1.setGroupType( DataType.TEXT_LITERAL );
		query1.setGrouping(q1);


		SeriesGrouping q2 =SeriesGroupingImpl.create( );	
		q2.setAggregateExpression("Sum");
		//q2.setEnabled(true);
		q2.setGroupType( DataType.TEXT_LITERAL );
		query2.setGrouping(q2);


		ArrayList list = new ArrayList( );
		list.add( query1 );
		list.add( query2 );
		list.add( query3 );

		taskPhase1.getDataDefinition( ).addAll( list );


		SeriesDefinition sdY = SeriesDefinitionImpl.create( );
		sdY.getSeriesPalette( ).shift( 0 );
		yAxisPrimary.getSeriesDefinitions( ).add( sdY );
		sdY.getSeries( ).add( taskPhase1 );


		return cwaGantt;
	}

	private void createBody( ) throws SemanticException
	{

		ExtendedItemHandle extendedItemHandle = elementFactory.newExtendedItem("Gantt Chart", "Chart");
		extendedItemHandle.setWidth("700px");
		extendedItemHandle.setHeight("500px");
		extendedItemHandle.setProperty(ExtendedItemHandle.DATA_SET_PROP, "ds");
		extendedItemHandle.setProperty("outputFormat","PNG");



		Chart c = createGantt();

		extendedItemHandle.getReportItem().setProperty( "chart.instance", c );

		reportDesignHandle.getBody().add(extendedItemHandle);


		//PropertyHandle computedSet = extendedItemHandle.getColumnBindings( );
		//computedSet.clearValue();

		cs1 = StructureFactory.createComputedColumn( );
		cs1.setName( "ORDERNUMBER" );
		cs1.setExpression( "dataSetRow[\"ORDERNUMBER\"]");
		cs1.setDataType( "integer" );
		cs1.setAggregateOn(null);


		cs2 = StructureFactory.createComputedColumn( );
		cs2.setName( "ORDERDATE" );
		cs2.setExpression( "dataSetRow[\"ORDERDATE\"]");
		cs2.setDataType( "date-time" );		

		cs3 = StructureFactory.createComputedColumn( );
		cs3.setName( "SHIPPEDDATE" );
		cs3.setExpression( "dataSetRow[\"SHIPPEDDATE\"]");
		cs3.setDataType( "date-time" );	
		
		cs4 = StructureFactory.createComputedColumn( );
		cs4.setName( "CUSTOMERNUMBER" );
		cs4.setExpression( "dataSetRow[\"CUSTOMERNUMBER\"]");
		cs4.setDataType( "integer" );	
		
		extendedItemHandle.addColumnBinding(cs1, true);
		extendedItemHandle.addColumnBinding(cs2, true);
		extendedItemHandle.addColumnBinding(cs3, true);
		extendedItemHandle.addColumnBinding(cs4, true);
		

	}


}
