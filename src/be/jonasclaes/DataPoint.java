package be.jonasclaes;

public class DataPoint {
    public double number;
    public double measuredFlow;
    public double measuredPressure;

    DataPoint(double number, double measuredFlow, double measuredPressure) {
        this.number = number;
        this.measuredFlow = measuredFlow;
        this.measuredPressure = measuredPressure;
    }
}
