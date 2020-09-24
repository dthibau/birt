package org.formation.birt;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;

/**
 * Servlet implementation class GenerateRapport
 */
@WebServlet("/GenerateRapport")
public class GenerateRapport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IReportEngine birtEngine;

	/**
	 * Default constructor.
	 */
	public GenerateRapport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		// TODO Auto-generated method stub
		super.init(servletConfig);
		EngineConfig config = new EngineConfig();
		config.setBIRTHome("");
		config.setPlatformContext(new PlatformServletContext(
				((ServletContext) servletConfig.getServletContext())));
		try {
			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			birtEngine = factory.createReportEngine(config);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_generateRapport(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_generateRapport(request, response);
	}

	private void _generateRapport(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String report = request.getParameter("report");
			if (report == null) {
				report = "Script";
			}
			IReportRunnable runnable = birtEngine.openReportDesign(this
					.getClass().getClassLoader()
					.getResourceAsStream("reports/" + report + ".rptdesign"));
			HashMap<String, Object> parameters = _setParameters(request,
					runnable);
			_renderReport(response, runnable, parameters,
					request.getParameter("format"), report);
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HashMap<String, Object> _setParameters(HttpServletRequest request,
			IReportRunnable runnable) {
		// Création de la tâche
		IGetParameterDefinitionTask task = birtEngine
				.createGetParameterDefinitionTask(runnable);
		@SuppressWarnings("rawtypes")
		Collection<IParameterDefnBase> parametersDef = task
				.getParameterDefns(false);
		Map<String, Object> map = new HashMap<String, Object>();
		for (IParameterDefnBase def : parametersDef) {
			if (request.getParameter(def.getName()) != null) {
				task.setParameterValue(def.getName(),
						request.getParameter(def.getName()));
			}
			System.out.println(def.getTypeName());			
		}
		// Positionner le paramètre
		// task.setParameterValue("Groupe", request.getParameter("Groupe"));
		// // Récupérer la HashMap
		// @SuppressWarnings("unchecked")
		HashMap<String, Object> parameterValues = task.getParameterValues();

		// Fermer la tâche
		task.close();
		return parameterValues;
	}

	private void _renderReport(HttpServletResponse response,
			IReportRunnable runnable, HashMap<String, Object> parameters,
			String format, String fileName) {
		// IRunTask runTask = birtEngine.createRunTask(runnable);
		// TaskOption options = new DataExtractionOption();
		// runTask.
		IRunAndRenderTask task = birtEngine.createRunAndRenderTask(runnable);
		task.setParameterValues(parameters);
		if (format == null) {
			format = "pdf";
		}
		try {
			if (format.equalsIgnoreCase("xls")) {
				_renderExcel(response, task, fileName);
			} else {
				_renderPdf(response, task, fileName);
			}
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void _renderPdf(HttpServletResponse response,
			IRunAndRenderTask task, String fileName) throws IOException, EngineException {
		IRenderOption options = new RenderOption();
		options.setSupportedImageFormats("SVG;PNG;JPG;GIF");
		// provide corresponding file ending to enable BIRT to find the right
		// emitter (ppt, pdf, doc, ...)

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + ".pdf\"");
		options.setOutputFormat("pdf");
		options.setOutputStream(response.getOutputStream());
		options.setOption(RenderOption.CLOSE_OUTPUTSTREAM_ON_EXIT, true);
		task.setRenderOption(options);

		task.run();
		task.close();

	}

	private void _renderExcel(HttpServletResponse response,
			IRunAndRenderTask task, String fileName) throws EngineException, IOException {
		EXCELRenderOption options = new EXCELRenderOption();
		options.setOutputFormat("xls");
//		options.setOutputFileName(fileName + ".xls");
		options.setOutputStream(response.getOutputStream());
		options.setOption(RenderOption.CLOSE_OUTPUTSTREAM_ON_EXIT, true);
		task.setRenderOption(options);

		task.run();
		task.close();
	}
}
