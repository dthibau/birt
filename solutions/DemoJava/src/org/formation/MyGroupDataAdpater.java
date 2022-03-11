package org.formation;

import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.IRowData;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.element.IDataItem;
import org.eclipse.birt.report.engine.api.script.eventadapter.DataItemEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IDataItemInstance;

public class MyGroupDataAdpater extends DataItemEventAdapter {

	String groupParam;
	String currentLettre ="";
	int indBackground = 0;
	String [] BACKGROUNDS= {"yellow","green","blue","red"};

	@Override
	public void onPrepare(IDataItem dataItemHandle, IReportContext reportContext) throws ScriptException {
		// TODO Auto-generated method stub
		super.onPrepare(dataItemHandle, reportContext);
		groupParam = (String)reportContext.getParameterValue("Groupe");
		if( groupParam.equals("CITY") ){
			dataItemHandle.setResultSetColumn("CITY");
		}else{
			dataItemHandle.setResultSetColumn("COUNTRY");
		}
	}

	@Override
	public void onCreate(IDataItemInstance data, IReportContext reportContext) throws ScriptException {
		// TODO Auto-generated method stub
		super.onCreate(data, reportContext);
		
		IRowData rowData = data.getRowData();
		
		String lettre = ((String)rowData.getColumnValue(groupParam)).substring(0, 1);
		if ( !currentLettre.equals(lettre)) {
			currentLettre = lettre;
		}

		data.getStyle( ).setBackgroundColor(BACKGROUNDS[indBackground%4]);
		data.getStyle( ).setVerticalAlign("Middle");
		data.setHeight("1cm");

	}

	
	
}
