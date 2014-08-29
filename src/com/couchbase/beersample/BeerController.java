package com.couchbase.beersample;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

@Controller
public class BeerController {
	static Bucket bucket = ConnectionManager.getBucket().toBlocking().single();
    
	@RequestMapping("/beers")
    String beers(Model model, @RequestParam(value = "beer", required = false) String beer) {
//		System.out.println(this.getBeers());
	    model.addAttribute("beers",this.getBeers());
	    return "beers";
    }
    
    @RequestMapping("/beers/delete")
    String delete(Model model, @RequestParam(value = "beer", required = false) String beer){
    	model.addAttribute("beer", this.getBeer(beer).content().toMap());
    	return "delete";
    }
    
    private JsonDocument getBeer(String id) {
		JsonDocument response = bucket.get(id).toBlocking().single();
		return response;
	}

	@RequestMapping("/beers/edit")
    @ResponseBody
    String edit(@RequestParam(value = "beer", required = false) String beer){
    	
    	return "edit";
    }
    
    @RequestMapping("/beers/search")
    @ResponseBody
    String search(@RequestParam(value = "beer", required = false) String beer){
    	
    	return "search";
    }
    
    
	private ArrayList<JsonDocument> getBeers() {
		
		ArrayList<JsonDocument> beers = new ArrayList<JsonDocument>();
		bucket
			.query(ViewQuery.from("beer","by_name").limit(20))
			.doOnNext(new Action1<ViewResult>(){
				@Override
				public void call(ViewResult viewResult){
					if(!viewResult.success()){
						System.out.println(viewResult.error());
					}else{
						System.out.println("Success");
					}
				}
			}).flatMap(new Func1<ViewResult, Observable<ViewRow>>(){
				@Override
				public Observable<ViewRow> call(ViewResult viewResult){
					return viewResult.rows();
				}
			})
			.flatMap(new Func1<ViewRow, Observable<JsonDocument>>(){
				@Override
				public Observable<JsonDocument> call(ViewRow viewRow){
					return viewRow.document(); 
				}
			})			
			.toBlocking()
			.forEach(new Action1<JsonDocument>(){
				@Override public void call(JsonDocument viewRow){
					beers.add(viewRow);
				}
			});
		
		return beers;
	}
}
	