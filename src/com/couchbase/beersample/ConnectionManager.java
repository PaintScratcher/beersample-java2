/**
 * Copyright (C) 2014 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.couchbase.beersample;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

/**
 * The ConnectionManager handles connecting, disconnecting and managing
 * of the Couchbase connection.
 */
public class ConnectionManager {
	
	private static final ConnectionManager connectionManager = new ConnectionManager();
	private ConnectionManager(){
		System.out.println("Creating connectionManager");
	};
	
	public static ConnectionManager getInstance() {
        return connectionManager;
    }
	
	
	static Cluster cluster = CouchbaseCluster.create();
	static Bucket bucket = cluster.openBucket("beer-sample").toBlocking().single();

	public static void disconnect() {
		cluster.disconnect().toBlocking().single();
	}
	
	 public static Bucket getBucket() {
		    return bucket;
	}
	 
	 public static ArrayList<JsonDocument> getView(String designDoc, String view) {
		ArrayList<JsonDocument> result = new ArrayList<JsonDocument>();
		final CountDownLatch latch = new CountDownLatch(1);
		System.out.println("NEW METHOD CALL");
		bucket
			.query(ViewQuery.from(designDoc, view).limit(20))
			.doOnNext(new Action1<ViewResult>(){
				@Override
				public void call(ViewResult viewResult){
					if(!viewResult.success()){
						System.out.println(viewResult.error());
					}else{
						System.out.println("Success");
					}
				}
			})
			.flatMap(new Func1<ViewResult, Observable<ViewRow>>(){
			
					@Override
					public Observable<ViewRow> call(ViewResult viewResult){
						System.out.println(viewResult);
						return viewResult.rows();
					}
				})
				.flatMap(new Func1<ViewRow, Observable<JsonDocument>>(){
					@Override
					public Observable<JsonDocument> call(ViewRow viewRow){
						return viewRow.document(); 
					}
				})	
			.subscribe(new Subscriber<JsonDocument>(){

				@Override
				public void onCompleted() {
					 latch.countDown();
					System.out.print("Completed");
					
				}

				@Override
				public void onError(Throwable throwable) {
					System.err.println("Whoops: " + throwable.getMessage());					
				}

				@Override
				public void onNext(JsonDocument viewRow) {
					System.out.println("next");
					result.add(viewRow);	
				}
				
			});
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		}
	 
	public static JsonDocument getItem(String id) {
		JsonDocument response = null;
		try {
			response = bucket.get(id).toBlocking().single();
		} catch (NoSuchElementException e) {
			System.out.println("ERROR: No element with message: " + e.getMessage());
			e.printStackTrace();
		}
		
		return response;
	}
	 
	public static void deleteItem(String id){
		System.out.println("Deleting " + id);
		bucket.remove(id);
	}
	
	public static void updateItem(JsonDocument doc){
		bucket.upsert(doc);
	}
	 
	public static void closeBucket(){
		bucket.close();
	}
}