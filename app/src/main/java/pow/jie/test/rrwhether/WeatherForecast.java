package pow.jie.test.rrwhether;

public class WeatherForecast {
    private String date = null;
    private String high = null;
    private String low = null;
    private String fengli = null;
    private String fengxiang = null;
    private String weatherType = null;
    private String wind;

    public WeatherForecast(String date, String high, String low, String fengli, String fengxiang, String weatherType) {
        this.date = date;
        this.high = high.split(" ")[1];
        this.low = low.split(" ")[1];
        fengli = fengli.split("\\[", 3)[2];
        fengli = fengli.split("<")[1];
        fengli = fengli.split("]")[0];
        this.fengli = " " + fengli;
        this.fengxiang = fengxiang;
        this.wind = this.fengxiang + this.fengli;
        this.weatherType = weatherType;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getWind() {
        return wind;
    }

}