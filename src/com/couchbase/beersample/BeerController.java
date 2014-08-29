package com.couchbase.beersample;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class BeerController {
    
	@RequestMapping("/beers")
    String beers(Model model, @RequestParam(value = "beer", required = false) String beer) {

		if (beer != null && !beer.isEmpty()) {
			model.addAttribute("beer", ConnectionManager.getItem(beer).content().toMap());
			return "beer";
			}
		else{
			model.addAttribute("beers", ConnectionManager.getView("beer", "by_name"));
		    return "beers";
		}
    }
    
    @RequestMapping("/beers/delete")
    String delete(Model model, @RequestParam(value = "beer", required = true) String beer){
    	ConnectionManager.deleteItem(beer);
    	model.addAttribute("deleted","Beer Deleted");
    	return "beers";
    }

	@RequestMapping("/beers/edit")
    String edit(Model model, @RequestParam(value = "beer", required = true) String beer){
		model.addAttribute("beer", ConnectionManager.getItem(beer).content().toMap());
    	return "edit";
    }
    
    @RequestMapping("/beers/search")
    String search(@RequestParam(value = "beer", required = false) String beer){
    	return "search";
    }
}
	