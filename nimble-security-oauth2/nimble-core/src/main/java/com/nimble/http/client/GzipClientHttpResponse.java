package com.nimble.http.client;
/*
   Copyright 2011 BTI, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

public class GzipClientHttpResponse implements ClientHttpResponse {

    private final HttpURLConnection connection;
    private HttpHeaders headers;

    public GzipClientHttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    public InputStream getBody() throws IOException {
        InputStream errorStream = this.connection.getErrorStream();
        return (errorStream != null ? errorStream : StringUtils.hasLength(
                this.connection.getContentEncoding()) &&
                this.connection.getContentEncoding().equalsIgnoreCase(
                        GzipClientHttpRequest.ENCODING_GZIP) ? new GZIPInputStream(
                this.connection.getInputStream()) : this.connection.getInputStream());
    }

    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            // Header field 0 is the status line for most HttpURLConnections, but not on GAE
            String name = this.connection.getHeaderFieldKey(0);
            if (StringUtils.hasLength(name)) {
                this.headers.add(name, this.connection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.connection.getHeaderFieldKey(i);
                if (!StringUtils.hasLength(name)) {
                    break;
                }
                this.headers.add(name, this.connection.getHeaderField(i));
                i++;
            }
        }
        return this.headers;
    }

    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(this.connection.getResponseCode());
    }

    public int getRawStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public String getStatusText() throws IOException {
        return this.connection.getResponseMessage();
    }

    public void close() {
        this.connection.disconnect();
    }
}