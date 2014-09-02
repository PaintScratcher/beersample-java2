package com.couchbase.beersample;

import java.util.ArrayList;
import java.util.Map;

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
			JsonDocument response = connectionManager.getItem(beer);
			if (response != null){
				model.addAttribute("beer", response.content().toMap());
			}
			
			return "beer";
			}
		else{
			ArrayList<JsonDocument> result = connectionManager.getView("beer", "by_name");
			System.out.print(result.size());
			model.addAttribute("beers", result);
		    return "beers";
		}
    }
    
    @RequestMapping("/beers/delete")
    String delete(Model model, @RequestParam(value = "beer", required = true) String beer){
    	connectionManager.deleteItem(beer);
    	model.addAttribute("deleted","Beer Deleted");
    	return "beers";
    }

	@RequestMapping(value = "/beers/edit", method=RequestMethod.GET)
    String editGet(Model model, @RequestParam(value = "beer", required = true) String beer){
		JsonDocument response = connectionManager.getItem(beer);
		if (response != null){
			model.addAttribute("beer", response.content().toMap());
		}
    	return "edit";
    }
	
	@RequestMapping(value = "/beers/edit", method=RequestMethod.POST)
    String editPost(Model model){
		
		
    	return "beers";
    }
    
    @RequestMapping("/beers/search")
    String search(@RequestParam(value = "beer", required = false) String beer){
    	return "search";
    }
}
	