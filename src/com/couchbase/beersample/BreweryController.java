package com.couchbase.beersample;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.ViewRow;


@Controller
public class BreweryController {
	public ConnectionManager connectionManager = ConnectionManager.getInstance();
	@RequestMapping("/breweries")
    String breweries(Model model, @RequestParam(value = "brewery", required = false) String brewery) {
		
		if (brewery != null && !brewery.isEmpty()) {
			JsonDocument response = connectionManager.getItem(brewery);
			if (response != null){
				model.addAttribute("brewery", response.content().toMap());
			}
			
			return "brewery";
			}
		else{
			ArrayList<ViewRow> result = connectionManager.getView("brewery", "by_name");
			model.addAttribute("breweries", result);
		    return "breweries";
		}
    }
    
    @RequestMapping("/brewery/delete")
    String delete(Model model, @RequestParam(value = "brewery", required = true) String brewery){
    	connectionManager.deleteItem(brewery).toBlocking().single();
    	model.addAttribute("deleted","Brewery Deleted");
    	ArrayList<ViewRow> result = connectionManager.getView("brewery", "by_name");
		model.addAttribute("breweries", result);
    	return "breweries";
    }

	@RequestMapping(value = "/brewery/edit", method=RequestMethod.GET)
    String editGet(Model model, @RequestParam(value = "brewery", required = true) String brewery){
		JsonDocument response = connectionManager.getItem(brewery);
		if (response != null){
			Map<String, Object> map = response.content().toMap();
			model.addAttribute("brewery", map);
			BreweryModel breweryModel = new BreweryModel();
			
			breweryModel.setId(response.id());
			breweryModel.setName(map.getOrDefault("name", "").toString());
			breweryModel.setCity(map.getOrDefault("city", "").toString());
			breweryModel.setDescription(map.getOrDefault("description", "").toString());
			breweryModel.setState(map.getOrDefault("state", "").toString());
			breweryModel.setCode(map.getOrDefault("code", "").toString());
			breweryModel.setCountry(map.getOrDefault("country", "").toString());
			breweryModel.setPhone(map.getOrDefault("phone", "").toString());
			breweryModel.setWebsite(map.getOrDefault("website", "").toString());
			breweryModel.setAddress(map.getOrDefault("address", "").toString());
			breweryModel.setGeo(map.getOrDefault("geo", "").toString());
			
			model.addAttribute("breweryModel", breweryModel);
		}
    	return "breweryEdit";
    }
	
	@RequestMapping(value = "/brewery/edit/submit", method=RequestMethod.POST)
    String editPost(Model model, @ModelAttribute(value = "breweryModel") BreweryModel breweryModel){
		JsonObject brewery = JsonObject.empty()
				.put("name", breweryModel.getName())
				.put("city", breweryModel.getCity())
				.put("description", breweryModel.getDescription())
				.put("state", breweryModel.getState())
				.put("code", breweryModel.getCode())
				.put("country", breweryModel.getCountry())
				.put("phone", breweryModel.getPhone())
				.put("website", breweryModel.getWebsite())
				.put("address", breweryModel.getAddress())
				.put("geo", breweryModel.getGeo())
				.put("type", "brewery");
		JsonDocument doc = JsonDocument.create(breweryModel.getId(),brewery);
		connectionManager.updateItem(doc);
    	return "redirect:/breweries/?brewery=" + breweryModel.getId();
    }
    
    @RequestMapping("/breweries/search")
    String search(@RequestParam(value = "brewery", required = false) String brewery){
    	return "search";
    }
}
	