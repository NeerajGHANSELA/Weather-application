import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weather_data;

    public WeatherAppGUI() {
        // add a title
        super("Weather App");

        addGUIComponents();
        setSize(450, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addGUIComponents() {
        // add the search field
        JTextField searchtf = new JTextField();
        searchtf.setBounds(15, 15, 351, 45);
        searchtf.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchtf);

        // weather image
        // cloudy image
        JLabel weatherConditionImage = new JLabel(loadImage("src/Images/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperature_text = new JLabel("10 C");
        temperature_text.setBounds(0, 350, 450, 54);
        temperature_text.setFont(new Font("Dialog", Font.BOLD, 48));
        temperature_text.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperature_text);

        // weather condition description
        JLabel weather_condition_des = new JLabel("Cloudy");
        weather_condition_des.setBounds(0, 405, 450, 36);
        weather_condition_des.setFont(new Font("Dialog", Font.PLAIN, 32));
        weather_condition_des.setHorizontalAlignment(SwingConstants.CENTER);
        add(weather_condition_des);

        // humidity image
        JLabel humidity_image = new JLabel(loadImage("src/Images/humidity.png"));
        humidity_image.setBounds(15, 500, 74, 66);
        add(humidity_image);

        // humidity text
        // make the word "Humidity" appear in bold. The rest of the string, "100%", is displayed as plain text.
        JLabel humidity_text = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidity_text.setBounds(90, 500, 85, 55);
        humidity_text.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidity_text);

        // wind speed image
        JLabel wind_speed_image = new JLabel(loadImage("src/Images/windspeed.png"));
        wind_speed_image.setBounds(220, 500, 74, 66);
        add(wind_speed_image);

        // wind speed text
        JLabel wind_speed_text = new JLabel("<html><b>Wind Speed</b> 15km/h </html>");
        wind_speed_text.setBounds(310, 500, 100, 55);
        wind_speed_text.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(wind_speed_text);


        // add the search button
        JButton search_button = new JButton(loadImage("src/Images/search.png"));
        // change the cursor to a hand cursor when hovering over the search button
        search_button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        search_button.setBounds(375, 13, 47, 45);
        search_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // gte location from user
                String location_entered = searchtf.getText();

                // validate input - remove whitespace to ensure non-empty text
                if(location_entered.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // retrieve weather data
                weather_data = WeatherApp.getWeatherData(location_entered);

                // update the GUI

                // update the weather image
                String weather_condition = (String) weather_data.get("weather_condition");

                switch (weather_condition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/Images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/Images/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/Images/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/Images/snow.png"));
                        break;
                }

                // update the temperature text
                double temperature = (double) weather_data.get("temperature");
                temperature_text.setText(temperature + " C");

                // update the weather text
                weather_condition_des.setText(weather_condition);

                // update humidity text
                long humidity = (long) weather_data.get("humidity");
                humidity_text.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update wind speed text
                double wind_speed = (double) weather_data.get("wind speed");
                wind_speed_text.setText("<html><b>Wind Speed</b> " + wind_speed + "km/h</html>");
            }
        });
        add(search_button);

    }

    public ImageIcon loadImage(String resource_path) {
        try{
            BufferedImage image = ImageIO.read(new File(resource_path));
            return new ImageIcon(image);
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find the resource");
        return null;
    }


}
