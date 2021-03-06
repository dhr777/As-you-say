//https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=naruto&key=AIzaSyDZCUOdaBbe4WCOkYBGKerurl4myToRcWA
package com.example.dhrtmdgh.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dhrtmdgh.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//view-source:http~~
public class YoutubeActivity extends YouTubeBaseActivity {





    YouTubePlayerView youTubeView;
    YouTubePlayer.OnInitializedListener listener;//유튜브 리스너
    YouTubePlayer youTubePlayerInstance;//유튜브 플레이어의 인스턴스
    String targetVideo = "FwnsEWETu6E";//재생하려는 유튜브 비디오. url상의 맷 뒷부분(v= 부분)만 넣으면 됨.


    String a;

    static DrawableManager DM = new DrawableManager();
    final String serverKey="AIzaSyDZCUOdaBbe4WCOkYBGKerurl4myToRcWA";
    AsyncTask<?, ?, ?> searchTask;
     ArrayList<SearchData> sdata = new ArrayList<SearchData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        //  youTubeView=(YouTubePlayerView) findViewById(R.id.youtubeView);




        Intent intent=getIntent();
        a=intent.getStringExtra("SearchWord");
        a=a.trim();
        a=a.replaceAll("\\p{Z}","");


        searchTask = new searchTask().execute();






/*


        listener=new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                //  youTubePlayer.loadPlaylist("https://www.youtube.com/results?search_query=%ED%8F%AC%EC%BC%93%EB%AA%AC%EC%8A%A4%ED%84%B0");
                youTubePlayer.cueVideo("1ce456Nnkt8"); //https://www.youtube.com/watch?v= << 밸류값을 가져와야한다(특정동영상) 아마 JSON을 이용해야 할듯.
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult
                    youTubeInitializationResult) {

            }
        };

        youTubeView.initialize("AIzaSyBkdGPTvNijiJpcr7-L4WVMJNvqFUS3Ibg", listener);

        btn2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(youTubePlayerInstance.isPlaying())
                    youTubePlayerInstance.pause();
                else
                    youTubePlayerInstance.play();
            }

        });

*/

    } //end onCreate










    private class searchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                paringJsonData(getUtube());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            ListView searchlist = (ListView) findViewById(R.id.searchlist);

            StoreListAdapter mAdapter = new StoreListAdapter(
                    YoutubeActivity.this, R.layout.listview_start, sdata); //Json파싱해서 가져온 유튜브 데이터를 이용해서 리스트를 만들어줍니다.

            searchlist.setAdapter(mAdapter);

        }
    }




    public JSONObject getUtube() {

        HttpGet httpGet = new HttpGet(

                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&maxResults=20&q="
                        +a+"&key="+ serverKey);  //EditText에 입력되 값으로 겁색을 합니다.
        // part(snippet),  q(검색값) , key(서버키)
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

// https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=%22%EB%82%98%EB%A3%A8%ED%86%A0%22&key=AIzaSyDZCUOdaBbe4WCOkYBGKerurl4myToRcWA
    //json

    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        sdata.clear();

        JSONArray contacts = jsonObject.getJSONArray("items");

        for (int i = 0; i < contacts.length(); i++) {

            Log.e("contacts.lengt()",  String.valueOf(contacts.length()));
            JSONObject c = contacts.getJSONObject(i);

            //문자열을 검색해서 channerid 가 있으면 넘어가서
            //videoid를 파싱해온다.

            if((c.getJSONObject("id").getString("kind")).equals("youtube#channel")) {
               continue;
            }


            String vodid = c.getJSONObject("id").getString("videoId");  //유튜브 동영상 아이디 값입니다. 재생시 필요합니다.
            String title = c.getJSONObject("snippet").getString("title"); //유튜브 제목을 받아옵니다
            String changString = "";
            try {
                changString = new String(title.getBytes("8859_1"), "utf-8"); //한글이 깨져서 인코딩
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
                    .substring(0, 10);
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("default").getString("url");  //썸내일 이미지 URL값


            sdata.add(new SearchData(vodid, changString, imgUrl, date));
        }

    }






    String vodid = "";

    public class StoreListAdapter extends ArrayAdapter<SearchData> {
        private ArrayList<SearchData> items;
        SearchData fInfo;

        public StoreListAdapter(Context context, int textViewResourseId,
                                ArrayList<SearchData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {// listview

            // 출력
            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.listview_start, null);
            ImageView img = (ImageView) v.findViewById(R.id.img);

            String url = fInfo.getUrl();

            String sUrl = "";
            String eUrl = "";
            sUrl = url.substring(0, url.lastIndexOf("/") + 1);
            eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            try {
                eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String new_url = sUrl + eUrl;

            DM.fetchDrawableOnThread(new_url, img);  //비동기 이미지 로더

            v.setTag(position);


            //동영상 클릭
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();

                    Intent intent = new Intent(YoutubeActivity.this,
                            PlayVideo.class);
                    intent.putExtra("id", items.get(pos).getVideoId());

                  //items.get(index).getVideoId();


                    startActivity(intent); //리스트 터치시 재생하는 엑티비티로 이동합니다. 동영상 아이디를 넘겨줍니다..
                }
            });

            ((TextView) v.findViewById(R.id.title)).setText(fInfo.getTitle());
            ((TextView) v.findViewById(R.id.date)).setText(fInfo
                    .getPublishedAt());

            return v;
        }
    }

























}//endClass
