package org.formation;

import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SessionHandle;

import com.ibm.icu.util.ULocale;

public class ModifyReport {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		modifyReport();

	}

	static void modifyReport() {
		// Création de la configuration
		SessionHandle session = new DesignEngine(new DesignConfig())
				.newSessionHandle(ULocale.ENGLISH);

		// Création du handle pour un design existant
		String name = "./sample.rptdesign";
		try {
			ReportDesignHandle design = session.openDesign(name);
			DesignElementHandle logoImage = design.findElement("TheLogo");
			if (logoImage == null || !(logoImage instanceof ImageHandle)) {
				return;
			}
			// Retrieve the URI of the image.
			String imageURI = ((ImageHandle) logoImage).getURI();
			System.out.println(imageURI);
			((ImageHandle)logoImage).setURL("http://www.plb.fr/Content/images/layout/logoPlb.png");
			design.saveAs("./plb.rptdesign");

		} catch (Exception e) {
			System.err.println("Report " + name + " not opened!\nReason is "
					+ e.toString());

		}

	}
}
