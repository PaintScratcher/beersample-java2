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
public class BreweriesController {
	static Bucket bucket = ConnectionManager.getBucket().toBlocking().single();
    
	@RequestMapping("/breweries")
    String breweries(Model model, @RequestParam(value = "brewery", required = false) String brewery) {
		System.out.println(this.getbreweries());
	    model.addAttribute("breweries",this.getbreweries());
		return "breweries";
    }
    
    @RequestMapping("/breweries/delete")
    String delete(Model model, @RequestParam(value = "brewery", required = false) String brewery){
    	model.addAttribute("brewery", this.getbrewery(brewery).content().toMap());
    	return "delete";
    }
    
    private JsonDocument getbrewery(String id) {
		JsonDocument response = bucket.get(id).toBlocking().single();
		return response;
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
    
    
	private ArrayList<JsonDocument> getbreweries() {
		
		ArrayList<JsonDocument> breweries = new ArrayList<JsonDocument>();
		bucket
			.query(ViewQuery.from("brewery","by_name").limit(20))
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
					breweries.add(viewRow);
				}
			});
		
//		.subscribe(new Subscriber<JsonDocument>(){
//			@Override
//			public void onCompleted(){
//				System.out.println("Observable Complete");
//			}
//			@Override
//			public void onError(Throwable throwable){
//				System.err.println("Whoops: " + throwable.getMessage());
//			}
//			@Override
//			public void onNext(JsonDocument viewRow){
//				HashMap<String, String> brewery = new HashMap<String, String>();
//				brewery.put("name",viewRow.content().getString("name"));
//				breweries.add(brewery);
//			}
//		});
		return breweries;
	}
}
	