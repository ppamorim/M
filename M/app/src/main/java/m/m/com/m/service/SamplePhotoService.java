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
