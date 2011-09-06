package net.thucydides.maven.plugins;

import net.thucydides.core.reports.ThucydidesReportData;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * This plugin deletes existing history files for Thucydides for this project.
 * @goal clean
 */
public class ThucydidesCleanMojo extends AbstractMavenReport {
        /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Directory where reports go.
     * Not relevent for this plugin, but required by the API.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    protected String outputDirectory;

   /**
     * @component
     * @required
     * @readonly
     */
    private Renderer siteRenderer;

    private HtmlAggregateStoryReporter reporter;

    private ThucydidesHTMLReportGenerator htmlReportGenerator;

    @Override
    protected MavenProject getProject() {
        return project;
    }

    protected ThucydidesHTMLReportGenerator getHtmlReportGenerator() {
        if (htmlReportGenerator == null) {
            htmlReportGenerator = new ThucydidesHTMLReportGenerator();
        }
        return htmlReportGenerator;
    }

    // Not used by Maven site plugin but required by API!
    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    // Not used by Maven site plugin but required by API!
    // (The site plugin is only calling getOutputName(), the output dir is fixed!)
    @Override
    protected String getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputName() {
        return "thucydides";
    }

    public String getName(Locale locale) {
        return "Thucydides Web tests";
    }

    public String getDescription(Locale locale) {
        return "Test reports generated by Thucydides.";
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        getLog().info("Clearing Thucydides historical reports");
        getReporter().clearHistory();
    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(MavenProjectHelper.getProjectIdentifier(project));
        }
        return reporter;

    }



}
