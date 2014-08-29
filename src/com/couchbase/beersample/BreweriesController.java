package com.couchbase.beersample;

import java.util.ArrayList;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.couchbase.client.java.document.JsonDocument;


@Controller
public class BreweriesController {
	public ConnectionManager connectionManager = ConnectionManager.getInstance();
	@RequestMapping("/breweries")
    String breweries(Model model, @RequestParam(value = "brewery", required = false) String brewery) {
		
		if (brewery != null && !brewery.isEmpty()) {
			model.addAttribute("brewery", connectionManager.getItem(brewery).content().toMap());
			return "brewery";
			}
		else{
			ArrayList<JsonDocument> result = connectionManager.getView("brewery", "by_name");
			model.addAttribute("breweries", result);
			System.out.println(result.size());
		    return "breweries";
		}
    }
    
    @RequestMapping("/breweries/delete")
    String delete(Model model, @RequestParam(value = "brewery", required = true) String brewery){
    	connectionManager.deleteItem(brewery);
    	model.addAttribute("deleted","brewery Deleted");
    	return "breweries";
    }

	@RequestMapping("/breweries/edit")
    String edit(Model model, @RequestParam(value = "brewery", required = true) String brewery){
		model.addAttribute("brewery", connectionManager.getItem(brewery).content().toMap());
    	return "edit";
    }
    
    @RequestMapping("/breweries/search")
    String search(@RequestParam(value = "brewery", required = false) String brewery){
    	return "search";
    }
}
	