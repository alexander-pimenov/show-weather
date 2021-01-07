package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.json.JSONObject;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    // Переменные, ссылающиеся на объекты из окна приложения
    @FXML
    private TextField city; //поле ввода названия города

    @FXML
    private Text date;

    @FXML
    private Text temp_info; //текущая температура

    @FXML
    private Text temp_max; // максимальная температура

    @FXML
    private Text temp_min; // минимальная температура

    @FXML
    private Button getData; //кнопка Узнать погоду

    @FXML
    private Text temp_feels; //какая темп ощущается

    @FXML
    private ImageView image;

    @FXML
    private Text pressure; //давление

    @FXML
    void initialize() {
        // При нажатии на кнопку, т.е. это обработчик клика
        getData.setOnAction(event -> {
            // Получаем данные из текстового поля приложения
            String getUserCity = city.getText().trim();
            if (!getUserCity.equals("")) { // Если данные не пустые
                // Получаем данные о погоде с сайта openweathermap. Мой API ключ 981af7073dbe5d068e0fdefb07311c09
                String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + getUserCity
                        + "&appid=981af7073dbe5d068e0fdefb07311c09&units=metric");
                System.out.println(output);

                // Данные получаем в json формате.
                if (!output.isEmpty()) { // Нет ошибки и такой город есть
                    // Парсим json данные.
                    JSONObject obj = new JSONObject(output);
                    // Обрабатываем JSON и устанавливаем данные в текстовые надписи
                    /*Сообщение с ресурса имеет json формат. И объект main имеет такие значения:
                    "main": {
                        "temp": 0.49,
                        "feels_like": -2.82,
                        "temp_min": 0,
                        "temp_max": 1,
                        "pressure": 1028,
                        "humidity": 100
                    },*/

                    final double temp = obj.getJSONObject("main").getDouble("temp");
                    final double tempFeelsLike = obj.getJSONObject("main").getDouble("feels_like");
                    final double tempMax = obj.getJSONObject("main").getDouble("temp_max");
                    final double tempMin = obj.getJSONObject("main").getDouble("temp_min");
                    final double press = obj.getJSONObject("main").getDouble("pressure");
                    date.setText("Дата: " + currentDateTime());

                    temp_info.setText("Температура: " + temp + " \u2103"); // ℃ - градус Цельсия в юникоде (\u2103)
                    temp_feels.setText("Ощущается: " + tempFeelsLike + " \u2103");
                    temp_max.setText("Максимум: " + tempMax + " \u2103");
                    temp_min.setText("Минимум: " + tempMin + " \u2103");
                    pressure.setText("Давление: " + press + " hPa ( " + convertHPAtoMMHg(press) + " мм рт. ст.)");
                }
            } else {
                city.setText("Укажите город!");
            }
        });
    }

    /* Обработка по паттерну текущего времени и даты*/
    private String currentDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        //System.out.println("LocalDateTime без форматирования: " + localDateTime);
        //Pattern
        //DateTimeFormatter patternTime = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter patternDateTime = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
        return localDateTime.format(patternDateTime);
    }

    /* Обработка URL адреса и получение данных с него*/
    private static String getUrlContent(String urlAdress) {
        StringBuffer content = new StringBuffer();

        try {
            /*Используем стандартную структуру по url и получение данных с него.*/
            URL url = new URL(urlAdress);
            // Открываем соединение.
            URLConnection urlConn = url.openConnection();

            // Открываем поток на чтение данных и записваем эти данные в буфер.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;

            // Записываем данные из буфера в строку.
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Такой город не был найден!");
        }
        return content.toString();
    }

    /*Метод переводящий давление в (hPa) в (мм рт ст), с последующим округлением double величины до 2-х знаков после запятой.*/
    public double convertHPAtoMMHg(double amount) {
        //1. вычисляем значение
        double result = Math.round(amount * 100 / 133.3224);
        //2. округляем значение
        return new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue();
    }

    /*Метод преобразования даты в миллисекундах в день/месяц/год*/
    public String convertDateToStringDate(int longDate) {
        Date d = new Date(longDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return dateFormat.format(d);
    }
}