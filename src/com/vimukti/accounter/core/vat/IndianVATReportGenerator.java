package com.vimukti.accounter.core.vat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

public abstract class IndianVATReportGenerator {
	protected IXDocReport report;
	protected IContext context;

	public abstract String getPdfFileName();

	public abstract File getTemplateFile();

	public abstract void assignValues();

	public void generate() {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					getTemplateFile()));
			XDocReportRegistry registry = XDocReportRegistry.getRegistry();
			report = registry.loadReport(in, TemplateEngineKind.Velocity);
			context = report.createContext();
			assignValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createPDF(OutputStream os) throws Exception {
		Options options = Options.getTo(ConverterTypeTo.PDF).via(
				ConverterTypeVia.ITEXT);
		if (report != null) {
			report.convert(context, options, os);
		}
	}
}
