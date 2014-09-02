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
public class BreweriesController {
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
			System.out.print(result.size());
			model.addAttribute("breweries", result);
		    return "breweries";
		}
    }
    
    @RequestMapping("/brewery/delete")
    String delete(Model model, @RequestParam(value = "brewery", required = true) String brewery){
    	connectionManager.deleteItem(brewery);
    	model.addAttribute("deleted","brewery Deleted");
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
			breweryModel.setName(map.get("name").toString());
			breweryModel.setCity(map.get("city").toString());
			breweryModel.setDescription(map.get("description").toString());
			breweryModel.setState(map.get("state").toString());
			breweryModel.setCode(map.get("code").toString());
			breweryModel.setCountry(map.get("country").toString());
			breweryModel.setPhone(map.get("phone").toString());
			breweryModel.setWebsite(map.get("webiste").toString());
			breweryModel.setAddress(map.get("address").toString());
			breweryModel.setGeo(map.get("geo").toString());
			model.addAttribute("breweryModel", breweryModel);
		}
    	return "edit";
    }
	
	@RequestMapping(value = "/brewery/edit/submit", method=RequestMethod.POST)
    String editPost(Model model, @ModelAttribute(value = "breweryModel") BreweryModel breweryModel){
		System.out.println(breweryModel.getName());
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
    	return "breweries";
    }
    
    @RequestMapping("/breweries/search")
    String search(@RequestParam(value = "brewery", required = false) String brewery){
    	return "search";
    }
}
	