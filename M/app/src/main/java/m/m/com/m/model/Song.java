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

package m.m.com.m.model;

public class Song {

    private long mId;
    private String mTitle;
    private String mArtist;
    private int mDuration;

    public Song(long songID, String songTitle, String songArtist, int duration){
        this.mId = songID;
        this.mTitle = songTitle;
        this.mArtist = songArtist;
        this.mDuration = duration;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getID(){
        return mId;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getArtist() {
        return mArtist;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

}
