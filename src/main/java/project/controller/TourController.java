package project.controller;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.model.hotel.Hotel;
import project.model.tour.Tour;
import project.model.tour.TourOperator;
import project.model.tourist.Tourist;
import project.service.HotelService;
import project.service.TourService;
import project.service.TouristService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static project.model.tour.Tour.DAY0;

/**
 * Created by slava23 on 12/3/2016.
 */

@Controller
@Data
@RequiredArgsConstructor(staticName = "of")
public class TourController {

    @Autowired
    @NonNull
    private TourService tourService;
    @Autowired
    @NonNull
    private HotelService hotelService;
    @Autowired
    @NonNull
    private TouristService touristService;

    @RequestMapping("/createTour")
    public String createTour(Model model,
                             @RequestParam(name = "startDate") String startDate,
                             @RequestParam(name = "endDate") String endDate,
                             @RequestParam(name = "tourOperator") String tourOperator,
                             @RequestParam(name = "isAvia") String isAvia,
                             @RequestParam(name = "visaRequired") String visaRequired,
                             @RequestParam(name = "priceBrutto") double priceBrutto,
                             @RequestParam(name = "hotelId") int hotelId,
                             @RequestParam(name = "closureDate") String closureDate,
                             @RequestParam(name = "touristId") List<String> touristIds) {
        Hotel hotelById = hotelService.findHotelById(hotelId);
        if (hotelById == null) {
            model.addAttribute("hotelId", hotelById);
            return "tour/error/invalidHotelId";
        }
        List<Tourist> touristList = new ArrayList<>();
        List<Integer> notFound = new ArrayList<>();
        touristIds
                .stream()
                .forEach(s -> {
                    if (s != null && !s.isEmpty()) {
                        Tourist touristById = touristService.findTouristById(Integer.parseInt(s));
                        if (touristById != null) {
                            touristList.add(touristById);
                        } else {
                            notFound.add(Integer.parseInt(s));
                        }
                    }
                });
        if (!notFound.isEmpty()) {
            model.addAttribute("notFound", notFound);
            return "tour/error/invalidTouristId";
        }
        Tour tour = Tour.of(LocalDate.parse(startDate), LocalDate.parse(endDate),
                touristList, hotelById, TourOperator.valueOf(tourOperator),
                Boolean.parseBoolean(isAvia), Boolean.parseBoolean(visaRequired),
                priceBrutto, LocalDate.parse(closureDate));
        Tour savedTour = tourService.saveTour(tour);
        model.addAttribute("tour", savedTour);
        return "tour/showTour";
    }

    @RequestMapping("/updateTour")
    public String updateTour(Model model,
                             @RequestParam(name = "id") int id,
                             @RequestParam(name = "startDate", required = false) String startDate,
                             @RequestParam(name = "endDate", required = false) String endDate,
                             @RequestParam(name = "tourOperator", required = false) String tourOperator,
                             @RequestParam(name = "isAvia", required = false) String isAvia,
                             @RequestParam(name = "visaRequired", required = false) String visaRequired,
                             @RequestParam(name = "priceBrutto", required = false) Double priceBrutto,
                             @RequestParam(name = "hotelId", required = false) Integer hotelId,
                             @RequestParam(name = "closureDate", required = false) String closureDate,
                             @RequestParam(name = "touristId", required = false) List<String> touristIds) {
        Tour tourById = tourService.findTourById(id);
        if (tourById != null) {
            if (startDate != null && !startDate.isEmpty()) {
                tourById.setStartDate(LocalDate.parse(startDate));
            }
            if (endDate != null && !endDate.isEmpty()) {
                tourById.setEndDate(LocalDate.parse(endDate));
            }
            if (tourOperator != null && !tourOperator.isEmpty()) {
                tourById.setTourOperator(TourOperator.valueOf(tourOperator));
            }
            if (isAvia != null && !isAvia.isEmpty()) {
                tourById.setAvia(Boolean.parseBoolean(isAvia));
            }
            if (visaRequired != null && !visaRequired.isEmpty()) {
                tourById.setVisaRequired(Boolean.parseBoolean(visaRequired));
            }
            if (priceBrutto != null && !(priceBrutto.doubleValue() == 0)) {
                tourById.setPriceBrutto(priceBrutto);
            }
            if (hotelId != null && !(hotelId == 0)) {
                Hotel hotelById = hotelService.findHotelById(hotelId);
                if (hotelById == null) {
                    model.addAttribute("hotelId", hotelId);
                    return "tour/error/invalidHotelId";
                } else {
                    tourById.setHotel(hotelById);
                }
            }
            if (closureDate != null && !closureDate.isEmpty()) {
                tourById.setClosureDate(LocalDate.parse(closureDate));
            }
            if (touristIds != null && !touristIds.isEmpty()) {
                List<Tourist> newTouristList = new ArrayList<>();
                List<Integer> notFound = new ArrayList<>();
                touristIds
                        .stream()
                        .forEach(s -> {
                            if (s != null && !s.isEmpty()) {
                                Tourist touristById = touristService.findTouristById(Integer.parseInt(s));
                                if (touristById != null) {
                                    newTouristList.add(touristById);
                                } else {
                                    notFound.add(Integer.parseInt(s));
                                }
                            }
                        });
                if (!notFound.isEmpty()) {
                    model.addAttribute("notFound", notFound);
                    return "tour/error/invalidTouristId";
                }
                if (!newTouristList.isEmpty()) {
                    tourById.setTouristList(newTouristList);
                }
            }
            tourService.saveTour(tourById);
            model.addAttribute("tour", tourById);
            return "tour/showTour";
        } else {
            model.addAttribute("tourId", id);
            return "tour/error/invalidTourId";
        }
    }

    @RequestMapping("/getTourById")
    public String findTourById(@RequestParam(name = "id") int id,
                               Model model) {
        Tour tourById = tourService.findTourById(id);
        model.addAttribute("tour", tourById);
        return "tour/showTour";
    }

    @RequestMapping("/getToursByParameters")
    public String findToursByParameters(@RequestParam(name = "touristId", required = false) Integer touristId,
                                        @RequestParam(name = "byTourist", required = false) String byTourist,
                                        @RequestParam(name = "tourOperator", required = false) String tourOperator,
                                        @RequestParam(name = "byTourOperator", required = false) String byTourOperator,
                                        @RequestParam(name = "hotelId", required = false) Integer hotelId,
                                        @RequestParam(name = "byHotel", required = false) String byHotel,
                                        @RequestParam(name = "country", required = false) String country,
                                        @RequestParam(name = "byCountry", required = false) String byCountry,
                                        @RequestParam(name = "region", required = false) String region,
                                        @RequestParam(name = "byRegion", required = false) String byRegion,
                                        @RequestParam(name = "startDate", required = false) String startDate,
                                        @RequestParam(name = "byStartDate", required = false) String byStartDate,
                                        @RequestParam(name = "endDate", required = false) String endDate,
                                        @RequestParam(name = "byEndDate", required = false) String byEndDate,
                                        @RequestParam(name = "closureDateStart", required = false) String closureDateStart,
                                        @RequestParam(name = "closureDateEnd", required = false) String closureDateEnd,
                                        @RequestParam(name = "byClosureDate", required = false) String byClosureDate,
                                        Model model) {
        Boolean filtered = false;
        List<Tour> resultList = new ArrayList<>();
        List<Tour> invokingList = new ArrayList<>();

        if (byTourist != null) {
            if (touristId != null) {
                invokingList = filtered == false ? tourService.findAll() : resultList;
                Tourist touristById = touristService.findTouristById(touristId);
                resultList = tourService.filterToursByTourist(touristById, invokingList);
                filtered = true;
            }
        }

        if (byTourOperator != null){
            if (tourOperator != null && !tourOperator.isEmpty()) {
                invokingList = filtered == false ? tourService.findAll() : resultList;
                resultList = tourService.filterToursByTourOperator(TourOperator.valueOf(tourOperator), invokingList);
                filtered = true;
            }
        }

        if (byHotel != null) {
            if (hotelId != null) {
                invokingList = filtered == false ? tourService.findAll() : resultList;
                Hotel hotelById = hotelService.findHotelById(hotelId);
                resultList = tourService.filterToursByHotel(hotelById, invokingList);
                filtered = true;
            }
        }

        if (byCountry != null){
            if (country != null && !country.isEmpty()){
                invokingList = filtered == false ? tourService.findAll() : resultList;
                resultList = tourService.filterToursByCountry(country, invokingList);
                filtered = true;
            }
        }

        if (byRegion != null){
            if (region != null && !region.isEmpty()){
                invokingList = filtered == false ? tourService.findAll() : resultList;
                resultList = tourService.filterToursByRegion(region, invokingList);
                filtered = true;
            }
        }

        if (byStartDate != null){
            if (startDate != null && !startDate.isEmpty()){
                invokingList = filtered == false ? tourService.findAll() : resultList;
                resultList = tourService.filterToursByStartDate(LocalDate.parse(startDate),invokingList);
                filtered = true;
            }
        }

        if (byEndDate != null){
            if (endDate != null && !endDate.isEmpty()){
                invokingList = filtered == false ? tourService.findAll() : resultList;
                resultList = tourService.filterToursByEndDate(LocalDate.parse(endDate),invokingList);
                filtered = true;
            }
        }

        if (byClosureDate != null){
            LocalDate start = closureDateStart == null || closureDateStart.isEmpty() ? DAY0 : LocalDate.parse(closureDateStart);
            LocalDate end = closureDateEnd == null || closureDateEnd.isEmpty() ? LocalDate.now() : LocalDate.parse(closureDateEnd);
            invokingList = filtered == false ? tourService.findAll() : resultList;
            resultList = tourService.filterToursByClosureDate(start, end, invokingList);
            filtered = true;
        }

        model.addAttribute("allTours", resultList);
        return "tour/showAllTours";
    }

    @RequestMapping("/getAllToursCurrentMonth")
    public String findAllTours(Model model) {
        List<Tour> resultList = tourService.filterToursByYearMonth(
                LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        model.addAttribute("allTours", resultList);
        return "tour/showAllTours";
    }


    @RequestMapping("/deleteTour")
    public String deleteTour(@RequestParam(name = "id") int id,
                             Model model) {
        Tour deleteTour = tourService.deleteTour(id);
        model.addAttribute("tour", deleteTour);
        return "tour/deleteTour";
    }

    @RequestMapping("/tour")
    public String tourMenu(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoggedIn = authentication.getName();
        model.addAttribute("userLoggedIn", userLoggedIn);
        return "tour/tour";
    }

    @RequestMapping("/findMenu")
    public String findTourMenu(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoggedIn = authentication.getName();
        model.addAttribute("userLoggedIn", userLoggedIn);
        return "tour/menu/findMenu";
    }

    @RequestMapping("/createMenu")
    public String createTourMenu(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoggedIn = authentication.getName();
        model.addAttribute("userLoggedIn", userLoggedIn);
        return "tour/menu/createMenu";
    }
}
