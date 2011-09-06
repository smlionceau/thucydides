package net.thucydides.maven.plugins;

import net.thucydides.core.Thucydides;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.reports.ThucydidesReportData;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Thucydides Maven site integration
 * This plugin generates an aggregate Thucydides report and integrates it into the Maven-generated site.
 * @goal thucydides
 * @requiresReports true
 * @phase site
 */
public class ThucydidesReportMojo extends AbstractMavenReport {
        /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The output directory for the intermediate report.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File targetDirectory;

    /**
     * Directory where reports will go.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    protected String outputDirectory;

    /**
     * Thucydides test reports are read from here
     *
     * @parameter expression="${project.build.directory}/site/thucydides"
     * @required
     */
    protected File sourceDirectory;

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
        getLog().info("Generating Thucydides Reports");
        ThucydidesReportData reportData = generateHtmlStoryReports();

        getHtmlReportGenerator().generateReport(reportData, getSink());

    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(MavenProjectHelper.getProjectIdentifier(project));
        }
        return reporter;

    }

    private ThucydidesReportData generateHtmlStoryReports() throws MavenReportException {
        File reportDirectory = new File(outputDirectory, "thucydides");

        getLog().info("Generating reports from " + sourceDirectory);
        getLog().info("Generating reports to " + reportDirectory);
        getReporter().setOutputDirectory(reportDirectory);
        try {
            return getReporter().generateReportsForStoriesFrom(sourceDirectory);
        } catch (IOException e) {
            throw new MavenReportException("Error generating aggregate thucydides reports", e);
        }
    }

}
