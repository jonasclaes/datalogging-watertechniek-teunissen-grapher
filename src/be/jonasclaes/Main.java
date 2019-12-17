package be.jonasclaes;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static void createChart(String chartName, ArrayList<DataPoint> dataPoints, String fileName) {
        try {
            double highestFlow = 0;
            double highestPressure = 0;
            double lowestFlow = Double.MAX_VALUE;
            double lowestPressure = Double.MAX_VALUE;
            final XYSeries data = new XYSeries( "data" );

            System.out.println("Looping through datapoints.");
            for (DataPoint dataPoint: dataPoints) {
                data.add(dataPoint.measuredFlow, dataPoint.measuredPressure);

                if (dataPoint.measuredFlow > highestFlow) {
                    highestFlow = dataPoint.measuredFlow * 1.1;
                }

                if (dataPoint.measuredFlow < lowestFlow) {
                    lowestFlow = dataPoint.measuredFlow / 1.1;
                }

                if (dataPoint.measuredPressure > highestPressure) {
                    highestPressure = dataPoint.measuredPressure * 1.1;
                }

                if (dataPoint.measuredPressure < lowestPressure) {
                    lowestPressure = dataPoint.measuredPressure / 1.1;
                }
            }

            final XYSeriesCollection dataset = new XYSeriesCollection( );
            dataset.addSeries( data );

            // Grafiek aanmaken
            System.out.println("Creating chart...");
            JFreeChart lineChart = ChartFactory.createXYLineChart(chartName,
                    "",
                    "",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false);

            // Plot nemen
            XYPlot plot = lineChart.getXYPlot();

            // Achtergrond en lijnkleur toepassen
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.BLACK);
            plot.setRangeGridlinePaint(Color.BLACK);

            // Y-as bar
            System.out.println("Adding bar on y-axis.");
            NumberAxis rangePressure = new NumberAxis();
            double maxP = highestPressure;
            double lowP = lowestPressure;
            rangePressure.setRange(lowP, maxP);
            rangePressure.setLabel("Pressure (bar)");
            rangePressure.setTickUnit(new NumberTickUnit(maxP / 15));

            // Y-as atm
            System.out.println("Adding atm on y-axis.");
            NumberAxis rangeAtm = new NumberAxis();
            double factorToAtm = 1.013;
            double maxAtm = highestPressure / factorToAtm;
            double lowAtm = lowestPressure / factorToAtm;
            rangeAtm.setRange(lowAtm, maxAtm);
            rangeAtm.setLabel("atm");
            rangeAtm.setTickUnit(new NumberTickUnit(maxAtm / 15));
            rangeAtm.setLabelLocation(AxisLabelLocation.MIDDLE);

            // Defineer 2 mogelijke Y-assen
            ValueAxis[] rangeAxes = new ValueAxis[2];
            rangeAxes[0] = rangePressure;
            rangeAxes[1] = rangeAtm;
            plot.setRangeAxes(rangeAxes);

            // X-as m3/h
            System.out.println("Adding m3/h on x-axis.");
            NumberAxis domainm3h = new NumberAxis();
            double maxm3h = highestFlow;
            double minm3h = lowestFlow;
            domainm3h.setRange(minm3h, maxm3h);
            domainm3h.setLabel("Capacity (m3/h)");
            domainm3h.setTickUnit(new NumberTickUnit(maxm3h / 15));
            domainm3h.setLabelLocation(AxisLabelLocation.MIDDLE);

            // X-as l/min
            System.out.println("Adding l/min on x-axis.");
            NumberAxis domainlmin = new NumberAxis();
            double factorToLMin = 16.666667;
            double maxLMin = highestFlow * factorToLMin;
            double minLMin = lowestFlow * factorToLMin;
            domainlmin.setRange(minLMin, maxLMin);
            domainlmin.setLabel("l/min");
            domainlmin.setTickUnit(new NumberTickUnit(maxLMin / 15));
            domainlmin.setLabelLocation(AxisLabelLocation.HIGH_END);

            // X-as gpm
            System.out.println("Adding gpm on x-axis.");
            NumberAxis domaingpm = new NumberAxis();
            double factorToGpm = 3.666667;
            double maxGpm = highestFlow * factorToGpm;
            double minGpm = lowestFlow * factorToGpm;
            domaingpm.setRange(minGpm, maxGpm);
            domaingpm.setLabel("gpm");
            domaingpm.setTickUnit(new NumberTickUnit(maxGpm / 15));
            domaingpm.setLabelLocation(AxisLabelLocation.HIGH_END);

            // X-as imp gpm
            System.out.println("Adding imp gpm on x-axis.");
            NumberAxis domainimpgpm = new NumberAxis();
            double factorToImpGpm = 4.402867;
            double maxImpGpm = highestFlow * factorToImpGpm;
            double minImpGpm = lowestFlow * factorToImpGpm;
            domainimpgpm.setRange(minImpGpm, maxImpGpm);
            domainimpgpm.setLabel("imp gpm");
            domainimpgpm.setTickUnit(new NumberTickUnit(maxImpGpm / 15));
            domainimpgpm.setLabelLocation(AxisLabelLocation.HIGH_END);

            // Defineer 4 mogelijke X-assen
            ValueAxis[] domainAxes = new ValueAxis[4];
            domainAxes[0] = domainm3h;
            domainAxes[1] = domainlmin;
            domainAxes[2] = domaingpm;
            domainAxes[3] = domainimpgpm;
            plot.setDomainAxes(domainAxes);

            // Renderer nemen
            XYItemRenderer renderer = plot.getRenderer();

            // Kleur toepassen op grafiek
            renderer.setSeriesPaint(0, Color.BLACK);

            int chartWidth = 960;
            int chartHeight = 640;
            File file = new File(fileName);
            System.out.println("Saving chart...");
            ChartUtilities.saveChartAsJPEG(file, lineChart, chartWidth, chartHeight);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1003);
        }
    }

    private static void addImage(PdfStamper stamper, AcroFields form, String field, String fieldValue) {
        try {
            System.out.println("Adding image to PDF...");
            List<AcroFields.FieldPosition> photograph = form.getFieldPositions(field);
            if(photograph!=null && photograph.size()>0){
                Rectangle rect = photograph.get(0).position;
                Image img = Image.getInstance(fieldValue);
                img.scaleToFit(rect.getWidth(), rect.getHeight());
                img.setBorder(2);
                img.setAbsolutePosition(
                        photograph.get(0).position.getLeft() + (rect.getWidth() - img.getScaledWidth())
                        , photograph.get(0).position.getTop() - (rect.getHeight()));
                PdfContentByte cb = stamper.getOverContent(photograph.get(0).page);
                cb.addImage(img);
            }
            System.out.println("Created PDF.");
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1002);
        }
    }

    private static void createPdf(String chartName, ArrayList<DataPoint> dataPoints, String fileName, String pdfFileNameReader, String pdfFileName) {
        try {
            System.out.println("Starting chart creation.");
            createChart(chartName, dataPoints, fileName);

            System.out.println("Opening PDF reader and stamper.");
            PdfReader reader = new PdfReader(pdfFileNameReader);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(pdfFileName));
            System.out.println("Creating AcroFields form handler.");
            AcroFields form = stamper.getAcroFields();

            System.out.println("Adding image to graph.");
            addImage(stamper, form, "Graph", fileName);

            System.out.println("Flattening PDF.");
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            System.out.println("PDF created.");
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1001);
        }
    }

    public static void main(String[] args) {
        String sqlId = args[0];
        String chartName = args[1];
        String fileName = args[2];
        String pdfFileName = args[3];
        String pdfFileNameReader = args[4];
        String connectionString = args[5];
        String connectionUsername = args[6];
        String connectionPassword = args[7];
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connectionString, connectionUsername, connectionPassword);
            System.out.println("Connected to database.");

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, name, data FROM runs WHERE id = " + sqlId);
            resultSet.next();
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            Array dataArrayRaw = resultSet.getArray("data");
            Double[][] dataArray = (Double[][]) dataArrayRaw.getArray();

            // Print out data.
            System.out.println("ID = " + id);
            System.out.println("NAME = " + name);
            for (Double[] data: dataArray) {
                System.out.println("DATA = [" + data[0] + ", " + data[1] + ", " + data[2] + "]");
                dataPoints.add(new DataPoint(data[0], data[2], data[1]));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1000);
        }

        createPdf(chartName, dataPoints, fileName, pdfFileName, pdfFileNameReader);

        System.exit(0);
    }
}
