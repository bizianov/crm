package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.model.hotel.Hotel;
import project.model.hotel.Rate;
import project.service.HotelService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * Created by slava23 on 11/28/2016.
 */

@Controller
public class HotelController {

    private HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @RequestMapping(value = "/createHotel", method = GET)
    public String createHotel(@RequestParam(name = "name") String name,
                              @RequestParam(name = "rate") String rate,
                              @RequestParam(name = "country") String country,
                              @RequestParam(name = "region") String region,
                              Model model){
        Hotel hotel = hotelService.createHotel(name, Rate.valueOf(rate), country, region);
        model.addAttribute("hotel", hotel);
        return "showHotel";
    }

    public HotelService getHotelService() {
        return hotelService;
    }

    public void setHotelService(HotelService hotelService) {
        this.hotelService = hotelService;
    }
}