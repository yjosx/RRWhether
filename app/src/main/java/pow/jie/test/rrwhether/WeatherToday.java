package pow.jie.test.rrwhether;

public class WeatherToday {

    private String aqi = null;
    private String ganmao = null;
    private String wendu = null;
    private String city = null;

    public WeatherToday(String aqi, String ganmao, String wendu, String city) {
        this.aqi = aqi;
        this.ganmao = ganmao;
        this.wendu = wendu;
        this.city = city;
    }

    public String getAqi() {
        return aqi;
    }

    public String getGanmao() {
        return ganmao;
    }

    public String getWendu() {
        return wendu;
    }

    public String getCity() {
        return city;
    }
}
