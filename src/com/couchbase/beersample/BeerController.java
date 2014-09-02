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
			ArrayList<ViewRow> result = connectionManager.getView("beer", "by_name");
			System.out.print(result.size());
			model.addAttribute("beers", result);
		    return "beers";
		}
    }
    
    @RequestMapping("/beer/delete")
    String delete(Model model, @RequestParam(value = "beer", required = true) String beer){
    	connectionManager.deleteItem(beer);
    	model.addAttribute("deleted","Beer Deleted");
    	return "beers";
    }

	@RequestMapping(value = "/beer/edit", method=RequestMethod.GET)
    String editGet(Model model, @RequestParam(value = "beer", required = true) String beer){
		JsonDocument response = connectionManager.getItem(beer);
		if (response != null){
			Map<String, Object> map = response.content().toMap();
			model.addAttribute("beer", map);
			BeerModel beerModel = new BeerModel();
			beerModel.setId(response.id());
			beerModel.setName(map.get("name").toString());
			beerModel.setStyle(map.get("style").toString());
			beerModel.setDescription(map.get("description").toString());
			beerModel.setCategory(map.get("category").toString());
			beerModel.setAbv(map.get("abv").toString());
			beerModel.setSrm(map.get("srm").toString());
			beerModel.setIbu(map.get("ibu").toString());
			beerModel.setUpc(map.get("upc").toString());
			beerModel.setBrewery(map.get("brewery_id").toString());
			model.addAttribute("beerModel", beerModel);
		}
    	return "beerEdit";
    }
	
	@RequestMapping(value = "/beer/edit/submit", method=RequestMethod.POST)
    String editPost(Model model, @ModelAttribute(value = "beerModel") BeerModel beerModel){
		JsonObject beer = JsonObject.empty()
				.put("name", beerModel.getName())
				.put("style", beerModel.getStyle())
				.put("description", beerModel.getDescription())
				.put("abv", beerModel.getAbv())
				.put("ibu", beerModel.getIbu())
				.put("srm", beerModel.getSrm())
				.put("upc", beerModel.getUpc())
				.put("brewery_id", beerModel.getBrewery())
				.put("type", "beer");
		JsonDocument doc = JsonDocument.create(beerModel.getId(),beer);
		connectionManager.updateItem(doc);
    	return "beers";
    }
    
    @RequestMapping("/beers/search")
    String search(@RequestParam(value = "beer", required = false) String beer){
    	return "search";
    }
}
	