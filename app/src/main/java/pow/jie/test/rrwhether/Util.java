package pow.jie.test.rrwhether;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

public class Util {
    static List<WeatherForecast> weatherForecastList = new ArrayList<>();
    static WeatherToday weatherToday;

    public static void handleWeatherResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONObject object2 = object.getJSONObject("data");
            JSONObject yestoday = object2.getJSONObject("yesterday");
            JSONArray forecast = object2.getJSONArray("forecast");
            for (int i = 0; i < forecast.length(); i++) {
                //通过角标获取"数组"的对象
                JSONObject jsonObject = forecast.getJSONObject(i);
                String date = jsonObject.getString("date");
                String high = jsonObject.getString("high");
                String fengli = jsonObject.getString("fengli");
                String low = jsonObject.getString("low");
                String fengxiang = jsonObject.getString("fengxiang");
                String type = jsonObject.getString("type");
                weatherForecastList.add(new WeatherForecast(date, high, low, fengli, fengxiang, type));
            }
            String aqi = object2.getString("aqi");
            String ganmao = object2.getString("ganmao");
            String wendu = object2.getString("wendu");
            String city = object2.getString("city");
            weatherToday = new WeatherToday(aqi, ganmao, wendu, city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getWebInfo(final String city)  throws IOException, TimeoutException {
        String requestUrl = "https://www.apiopen.top/weatherApi?city=" + city;
        HttpsURLConnection connection = null;
        try {
            URL mURL = new URL(requestUrl);
            long start = System.currentTimeMillis();
            connection = (HttpsURLConnection) mURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            int responseCode = connection.getResponseCode();
            long end = System.currentTimeMillis();
            if ((end - start) > 5000) throw new TimeoutException();
            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            } else {
                throw new TimeoutException();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new TimeoutException("请求超时,请检查网络连接");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getStringFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }
}
