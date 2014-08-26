package com.couchbase.beersample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

@Controller
public class BeerController {
	static Bucket bucket = ConnectionManager.getBucket().toBlocking().single();
    @RequestMapping("/beers")
   
    String beers(Model model) {
	    model.addAttribute("beers",this.getBeers());
	    return "beers";
    }
    
	private ArrayList<HashMap> getBeers() {
		
		ArrayList<HashMap> beers = new ArrayList<HashMap>();
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
//			.timeout(5, TimeUnit.SECONDS)
			.subscribe(new Subscriber<JsonDocument>(){
				@Override
				public void onCompleted(){
					System.out.println("Observable Complete");
				}
				@Override
				public void onError(Throwable throwable){
					System.err.println("Whoops: " + throwable.getMessage());
				}
				@Override
				public void onNext(JsonDocument viewRow){
					HashMap<String, String> beer = new HashMap<String, String>();
					beer.put("name",viewRow.content().getString("name"));
					beers.add(beer);
				}
			});
			
//			toBlocking()
//			.forEach(new Action1<JsonDocument>(){
//				@Override public void call(JsonDocument viewRow){
//					HashMap<String, String> beer = new HashMap<String, String>();
//					beer.put("name",viewRow.content().getString("name"));
//					beers.add(beer);
//				}
//			});
		return beers;
	}
}
	