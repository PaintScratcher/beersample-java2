package com.couchbase.beersample;

import java.util.ArrayList;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.couchbase.client.java.document.JsonDocument;


@Controller
public class BeerController {
	public ConnectionManager connectionManager = ConnectionManager.getInstance();
	@RequestMapping("/beers")
    String beers(Model model, @RequestParam(value = "beer", required = false) String beer) {
		
		if (beer != null && !beer.isEmpty()) {
			model.addAttribute("beer", connectionManager.getItem(beer).content().toMap());
			return "beer";
			}
		else{
			ArrayList<JsonDocument> result = connectionManager.getView("beer", "by_name");
			model.addAttribute("beers", result);
			System.out.println(result.size());
		    return "beers";
		}
    }
    
    @RequestMapping("/beers/delete")
    String delete(Model model, @RequestParam(value = "beer", required = true) String beer){
    	connectionManager.deleteItem(beer);
    	model.addAttribute("deleted","Beer Deleted");
    	return "beers";
    }

	@RequestMapping("/beers/edit")
    String edit(Model model, @RequestParam(value = "beer", required = true) String beer){
		model.addAttribute("beer", connectionManager.getItem(beer).content().toMap());
    	return "edit";
    }
    
    @RequestMapping("/beers/search")
    String search(@RequestParam(value = "beer", required = false) String beer){
    	return "search";
    }
}
	