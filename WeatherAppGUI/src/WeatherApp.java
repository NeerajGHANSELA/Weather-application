import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retrieve weather data from API - fetch the latest weather
// GUI will display the data to the user
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        // extract the latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL w/ location coordinates
        String url_str = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FDenver";

        try{
            // ca;; API and get response
            HttpURLConnection connection = fetchApiResponse(url_str);

            // check for response status
            // 200 = successful connection
            if(connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // store the resulting JSON data
            StringBuilder result_Json = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());

            while(scanner.hasNext()) {
                // read and store into the string builder
                result_Json.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();
            connection.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject result_Json_obj = (JSONObject) parser.parse(String.valueOf(result_Json));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) result_Json_obj.get("hourly");

            // we want to get the current hour's data
            // we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = FindIndexOfCurrentTime(time);

            // get temperature
            JSONArray temmperature_data = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temmperature_data.get(index);

            // get weather code
            JSONArray weather_code = (JSONArray) hourly.get("weather_code");
            String weather_condition = convertWeatherCode((long) weather_code.get(index));

            // get humidity code
            JSONArray relative_humidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relative_humidity.get(index);

            // get wind speed
            JSONArray wind_speed_data = (JSONArray) hourly.get("wind_speed_10m");
            double wind_speed = (double) wind_speed_data.get(index);

            // build the weather JSON data object that we are going to access in our frontend
            JSONObject weather_data = new JSONObject();
            weather_data.put("temperature", temperature);
            weather_data.put("weather_condition", weather_condition);
            weather_data.put("humidity", humidity);
            weather_data.put("wind speed", wind_speed);

            return weather_data;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // retrieve geographic coordinates for given location
    public static JSONArray getLocationData(String location) {
        // replace any white space in location to "+" to adhere to APIs format
        location = location.replaceAll(" ", "+");

        // build API url with location parameter
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                location + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = fetchApiResponse(url);

            // check response status
            // 200 = successful connection
            if(connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // store the API results
                StringBuilder result_Json = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                // read and store the resulting json data into our string builder
                while(scanner.hasNext()) {
                    result_Json.append(scanner.nextLine());
                }
                // close scanner
                scanner.close();

                // close url connection
                connection.disconnect();

                // parse the JSON string into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject results_Json_obj = (JSONObject) parser.parse(String.valueOf(result_Json));

                // get the list of location data the API generated from the location name
                JSONArray location_data = (JSONArray) results_Json_obj.get("results");
                return location_data;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        // couldn't find the location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String url_str) {
        try{
            // attempt to create connection
            URL url = new URL(url_str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // set request method to get
            connection.setRequestMethod("GET");

            // connect to our API
            connection.connect();
            return connection;
        } catch(IOException e) {
            e.printStackTrace();
        }

        // could not make connection
        return null;
    }

    private static int FindIndexOfCurrentTime(JSONArray time_list) {
        String current_time = getCurrentTime();

        // iterate through the time list and see which one matches our current time
        for(int i = 0; i < time_list.size(); i++) {
            String time = (String) time_list.get(i);
            if(time.equalsIgnoreCase(current_time)) {
                return i;  // return the index
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        // get current data and time
        LocalDateTime current_data_time = LocalDateTime.now();

        // format date to be 2024-09-02T00:00 (how it is read in the API)
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current data and time
        String formatted_data_time = current_data_time.format(format);

        return formatted_data_time;
    }

    // convert the weather code into sumn more readable
    public static String convertWeatherCode(long weather_code) {
        String weather_condition = "";
        if(weather_code == 0L) {
            weather_condition = "Clear";
        } else if(weather_code <= 3L && weather_code > 0L) {
            weather_condition = "Cloudy";
        } else if((weather_code >= 51L && weather_code <= 67L) || (weather_code >= 80L && weather_code <= 99L)) {
            weather_condition = "Rain";
        } else if (weather_code >= 71L && weather_code <= 77L) {
            weather_condition = "Snow";
        }
        return weather_condition;
    }

}
