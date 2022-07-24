package com.weather.weather;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.weather.weather.controller.WeatherService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI(path = "")
public class MainViews<cityName> extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityName;
    private Image iconImg;
    private HorizontalLayout Dashboard;
    private HorizontalLayout mainDescriptionLayout;
    private Image logo;
    private HorizontalLayout footer;
    private NativeSelect<String> unit;
    private Button button;
    private HorizontalLayout getDashboard;
    private Label location;
    private Label current;
    private HorizontalLayout dashBoardDetail;
    private Label weatherDescription;
    private Label maxTemp;
    private Label minTemp;
    private Label humidity;
    private Label pressure;
    private Label precipitation;
    private Label wind;
    private Label  feelsLike;







    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpLayout();
        setHeader();
        setLogo();
        setForm();
        getDashboard();
        dashBoardDetail();
        button.addClickListener(clickEvent -> {
            if(!cityName.getValue().equals("")) {
                try {
                    upDateUi();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Notification.show("PLEASE! ENTER THE CITY");
            }
        });



    }


    public void setUpLayout() {
        logo = new Image();
        iconImg = new Image();
        iconImg.setWidth("200px");
        iconImg.setHeight("200px");
        iconImg = new Image();


        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainLayout.setStyleName("BackColorGrey");
        setContent(mainLayout);

    }

    private void setHeader() {
        HorizontalLayout headerlayout = new HorizontalLayout();
        headerlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label Title = new Label("Weather APP");
        Title.addStyleName(ValoTheme.LABEL_LARGE);
        Title.addStyleName(ValoTheme.LABEL_BOLD);


        headerlayout.addComponents(Title);
        mainLayout.addComponents(headerlayout);


    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

         logo.setSource(new ExternalResource("https://get.pxhere.com/photo/cloud-black-and-white-sky-atmosphere-weather-cumulus-darkness-monochrome-thunder-thunderstorm-meteorological-phenomenon-monochrome-photography-atmosphere-of-earth-geological-phenomenon-1404736.jpg"));
        logo.setWidth("1382px");
        logo.setHeight("660px");
        logo.setVisible(true);


        logoLayout.addComponents(logo);
        mainLayout.addComponents(logoLayout);
    }

    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);
        unitSelect = new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("F");
        items.add("C");
        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponent(unitSelect);


        cityName = new TextField();
        cityName.setWidth("1000%");
        formLayout.addComponent(cityName);


        button = new Button();
        button.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(button);

        mainLayout.addComponent(formLayout);

    }
    private void getDashboard() {

        getDashboard = new HorizontalLayout();
        getDashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        location = new Label("Currently is " + " " +" Minsk");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LARGE);

        current = new Label("temperature");
        current.setStyleName(ValoTheme.LABEL_BOLD);
        current.setStyleName(ValoTheme.LABEL_H1);
        getDashboard.addComponents(location,iconImg, current);
        mainLayout.addComponents(getDashboard);

    }
    private void dashBoardDetail(){
        mainDescriptionLayout  = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        VerticalLayout desc = new VerticalLayout();
        desc.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        weatherDescription = new Label(" Description: Clear skies");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);
        desc.addComponent(weatherDescription);

        minTemp = new Label("Min");
        desc.addComponent(minTemp);

        maxTemp = new Label("Max");
        desc.addComponent(maxTemp);

        
        humidity = new Label("humidity");
        desc.addComponent(humidity);
        wind = new Label("wind");
        desc.addComponent(wind);
        feelsLike = new Label("FeelsLike:");
        desc.addComponents(feelsLike);




        VerticalLayout pressureLay = new VerticalLayout();
        pressureLay.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        pressure = new Label("pressure");
        desc.addComponents(pressureLay, pressure);
        mainDescriptionLayout.addComponents(pressure, desc);
        //mainLayout.addComponent(mainDescriptionLayout);



    }
    private void upDateUi() throws JSONException {
        String city = cityName.getValue();

        String defaultUnit;
        weatherService.setCityName(city);
        if (unitSelect.getValue().equals("F")) {
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";
        } else {
            weatherService.setUnit("metric");
            defaultUnit = "\u00b0" + "C";
            unitSelect.setValue("C");
        }
        location.setValue("Currently in " + city);
        JSONObject main = weatherService.returnMainObject();
        int temp = main.getInt("temp");
        current.setValue(temp + defaultUnit);

        String iconCode = null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeather();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weather = jsonArray.getJSONObject(i);
            iconCode = weather.getString("icon");
            weatherDescriptionNew = weather.getString("description");

        }
        iconImg.setSource(new ExternalResource("http://openweathermap.org/img/wn/" + iconCode + "@2x.png"));




        weatherDescription.setValue("Description: "+weatherDescriptionNew);
        minTemp.setValue("Min temperature: " +weatherService.returnMainObject().getInt("temp_min")+unitSelect.getValue());
        maxTemp.setValue("Max temperature: " +weatherService.returnMainObject().getInt("temp_max")+unitSelect.getValue());
        pressure.setValue("Pressure: " +weatherService.returnMainObject().getInt("pressure"));
        humidity.setValue("Humidity: " +weatherService.returnMainObject().getInt("humidity"));
       // precipitation.setValue("Precipitation: " +weatherService.returnMainObject().get("rain"));
        wind.setValue("Wind: " +weatherService.returnWindObject().getInt("speed"));
        feelsLike.setValue("Feelslike: "+weatherService.returnMainObject().getDouble("feels_like"));





        mainLayout.addComponents(getDashboard, mainDescriptionLayout);





    }

}
