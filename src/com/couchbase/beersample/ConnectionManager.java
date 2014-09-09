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
import com.couchbase.client.java.view.Stale;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

/**
 * The ConnectionManager handles connecting, disconnecting and managing of the
 * Couchbase connection.
 */
public class ConnectionManager {

	private static final ConnectionManager connectionManager = new ConnectionManager();

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

	public static ArrayList<ViewRow> getView(String designDoc, String view) {
		ArrayList<ViewRow> result = new ArrayList<ViewRow>();
		final CountDownLatch latch = new CountDownLatch(1);

		bucket.query(
				ViewQuery.from(designDoc, view).limit(20).stale(Stale.FALSE))
				.doOnNext(new Action1<ViewResult>() {
					@Override
					public void call(ViewResult viewResult) {
						if (!viewResult.success()) {
							System.out.println(viewResult.error());
						}
					}
				}).flatMap(new Func1<ViewResult, Observable<ViewRow>>() {

					@Override
					public Observable<ViewRow> call(ViewResult viewResult) {
						return viewResult.rows();
					}
				}).subscribe(new Subscriber<ViewRow>() {
					@Override
					public void onCompleted() {
						latch.countDown();
					}

					@Override
					public void onError(Throwable throwable) {
						System.err.println("Whoops: " + throwable.getMessage());
					}

					@Override
					public void onNext(ViewRow viewRow) {
						result.add(viewRow);
					}
				});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static JsonDocument getItem(String id) {
		JsonDocument response = null;
		try {
			response = bucket.get(id).toBlocking().single();
		} catch (NoSuchElementException e) {
			System.out.println("ERROR: No element with message: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	public static Observable<JsonDocument> deleteItem(String id) {
		Observable<JsonDocument> response = null;
		try {
			response = bucket.remove(id, PersistTo.MASTER);
		} catch (NoSuchElementException e){
			System.out.println("ERROR: No element with message: " 
					+ e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	public static void updateItem(JsonDocument doc) {
		bucket.upsert(doc);
	}

	public static void closeBucket() {
		bucket.close();
	}
}