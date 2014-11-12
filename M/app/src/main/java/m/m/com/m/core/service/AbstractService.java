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

package m.m.com.m.core.service;

import android.os.AsyncTask;
import android.os.Build;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class AbstractService extends AsyncTask<Request, Void, String> {

    private static final int ERROR_REQUEST = -1;

    private OnRequestResponse mOnRequestResponse;
    private Request mRequest;

    public AbstractService(Request request, OnRequestResponse onRequestResponse) {
        mRequest = request;
        mOnRequestResponse = onRequestResponse;

    }

    public void runOnAsyncTask() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRequest);
        } else {
            execute(mRequest);
        }
    }

    public void runOnUiThread() {
        try {
            new OkHttpClient()
                    .newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            mOnRequestResponse.onFail(-2);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            mOnRequestResponse.onSucess(response.body().string());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Request... request) {
        try {
            return new OkHttpClient()
                    .newCall(request[0])
                    .execute()
                    .body()
                    .string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null) {
            mOnRequestResponse.onSucess(result);
        } else {
            mOnRequestResponse.onFail(ERROR_REQUEST);
        }

    }

    public static interface OnRequestResponse {
        public void onSucess(String response);
        public void onFail(int error);
    }

}
