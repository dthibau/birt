package org.formation;

import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.element.ILabel;
import org.eclipse.birt.report.engine.api.script.eventadapter.LabelEventAdapter;

public class MyLabelAdapter extends LabelEventAdapter{

	@Override
	public void onPrepare(ILabel labelHandle, IReportContext reportContext)
			throws ScriptException {
		super.onPrepare(labelHandle, reportContext);
		labelHandle.getStyle().setBackgroundColor("red");

	}

	
}
