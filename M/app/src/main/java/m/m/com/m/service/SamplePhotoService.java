/*
* Copyright 2014 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package m.m.com.m.service;

import com.squareup.okhttp.Request;

import m.m.com.m.core.service.AbstractService;
import m.m.com.m.core.service.AbstractService.OnRequestResponse;

public class SamplePhotoService {

    public SamplePhotoService(String title, OnRequestResponse onRequestResponse) {
        new AbstractService(new Request.Builder()
                .url("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + title)
                .build(), onRequestResponse).runOnAsyncTask();
    }

}
