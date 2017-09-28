package net.spatula.dspatula.examples.sequence;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import net.spatula.dspatula.exception.ProcessingException;
import net.spatula.dspatula.time.sequence.Sequence;

public abstract class AbstractSequenceChart<T extends Sequence<T>> extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    protected final String plotName;
    protected final String xAxisTitle;
    protected final String yAxisTitle;

    protected abstract T getSequence() throws ProcessingException;

    protected abstract void populateSeries(final XYSeries series) throws ProcessingException;

    protected AbstractSequenceChart(String title, String plotName, String xAxisTitle, String yAxisTitle)
            throws ProcessingException {
        super(title);
        this.plotName = plotName;
        this.xAxisTitle = xAxisTitle;
        this.yAxisTitle = yAxisTitle;
        initChart();
    }

    public void render() {
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }

    private void initChart() throws ProcessingException {
        final XYSeries series = new XYSeries("Sequence");

        populateSeries(series);

        final XYSeriesCollection collection = new XYSeriesCollection(series);

        final JFreeChart chart = createPlot(collection);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        configureRenderShape(chart);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 512));
        setContentPane(chartPanel);
    }

    protected void configureRenderShape(final JFreeChart chart) {
        // Make the plot use small points instead of polygons.
        final Shape shape = new Ellipse2D.Double(0, 0, 2, 2);
        final XYPlot xyPlot = (XYPlot) chart.getPlot();
        final XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setBaseShape(shape);
        renderer.setBasePaint(Color.red);
        renderer.setSeriesShape(0, shape);
    }

    protected JFreeChart createPlot(final XYSeriesCollection collection) {
        return ChartFactory.createScatterPlot(plotName, xAxisTitle, yAxisTitle, collection, PlotOrientation.VERTICAL, false, false,
                false);
    }

}
