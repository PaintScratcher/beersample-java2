package com.couchbase.beersample;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class BreweriesController {
    
	@RequestMapping("/breweries")
    String breweries(Model model, @RequestParam(value = "brewery", required = false) String brewery) {
		if (brewery != null && !brewery.isEmpty()) {
			model.addAttribute("brewery", ConnectionManager.getItem(brewery).content().toMap());
			return "brewery";
			}
		else{
			model.addAttribute("breweries", ConnectionManager.getView("brewery", "by_name"));
		    return "breweries";
		}
    }
    
    @RequestMapping("/breweries/delete")
    String delete(Model model, @RequestParam(value = "brewery", required = false) String brewery){
    	model.addAttribute("brewery", ConnectionManager.getItem(brewery).content().toMap());
    	return "delete";
    }

	@RequestMapping("/breweries/edit")
    @ResponseBody
    String edit(@RequestParam(value = "brewery", required = false) String brewery){
    	
    	return "edit";
    }
    
    @RequestMapping("/breweries/search")
    @ResponseBody
    String search(@RequestParam(value = "brewery", required = false) String brewery){
    	
    	return "search";
    }
}
	