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

import rx.Observable;

import com.couchbase.client.java.*;

/**
 * The ConnectionManager handles connecting, disconnecting and managing
 * of the Couchbase connection.
 */
public class ConnectionManager {
	
	static Cluster cluster = CouchbaseCluster.create();
	static Observable<Bucket> bucket = cluster.openBucket("beer-sample");

	public static void disconnect() {
		cluster.disconnect().toBlocking().single();
	}
	
	 public static Observable<Bucket> getBucket() {
		    return bucket;
	}
	 
//	 public static closeBucket(){
//		 Observable<Boolean> close = bucket.close();
//	 }
}