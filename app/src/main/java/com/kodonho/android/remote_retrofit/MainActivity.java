package com.kodonho.android.remote_retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
/*
    1. gradle에 retrofit 라이브러리추가
    2. manifest에 INTERNET 권한 추가

    Open Api key : 4c425976676b6f643437665377554c
                 : sample
    사용 Api : http://openapi.seoul.go.kr:8088/(인증키)/json/SeoulRoadNameInfo/1/5/
                                                                 ↑ 서비스명 : 서울시 도로명 정보
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String key = "4c425976676b6f643437665377554c";
        String serviceName = "SeoulRoadNameInfo";
        int begin = 1;
        int end = 5;

        String url = "http://openapi.seoul.go.kr:8088/"+key+"/json/"+serviceName+"/"+begin+"/"+end+"/";
        Log.e("base url",url);

        //1. Retrofit client 생성
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://openapi.seoul.go.kr:8088") // 베이스 도메인 지정
                                .addConverterFactory(GsonConverterFactory.create()) // json 컨버팅 라이브러리 지정
                                .build();
        // 2. Retrofit client 에서 사용할 interface 지정
        ISeoulOpenData service = retrofit.create(ISeoulOpenData.class);
        // 3. interface(서비스)를 통해서 데이터를 호출한다
        Call<RemoteData> remoteData = service.getData(key,serviceName,begin,end);
        // 4. 비동기 데이터를 받기위한 리스너 세팅
        remoteData.enqueue(new Callback<RemoteData>() {
            @Override
            public void onResponse(Call<RemoteData> call, Response<RemoteData> response) {
                if(response.isSuccessful()){
                    RemoteData data = response.body();
                    for(RemoteData.Row row : data.getSeoulRoadNameInfo().getRow()){
                        Log.i("Remote Data Result","roadNM="+row.getROAD_NM());
                    }
                }else{
                    Log.e("RemoteData",response.message());
                }
            }
            @Override
            public void onFailure(Call<RemoteData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

// 인터페이스 생성
interface ISeoulOpenData {
    @GET("/{key}/json/{serviceName}/{begin}/{end}/")
    Call<RemoteData> getData(@Path("key")String key, @Path("serviceName")String serviceName, @Path("begin")int begin, @Path("end")int end);
}

// JSON 데이터와 매핑되는 클래스
// 인터넷에 변환툴 제공
// http://pojo.sodhanalibrary.com/
class RemoteData {
    SeoulRoadNameInfo SeoulRoadNameInfo;
    public SeoulRoadNameInfo getSeoulRoadNameInfo() {
        return SeoulRoadNameInfo;
    }
    class SeoulRoadNameInfo{
        String list_total_count;
        Result RESULT;
        List<Row> row;
        public String getList_total_count() {
            return list_total_count;
        }
        public Result getRESULT() {
            return RESULT;
        }
        public List<Row> getRow() {
            return row;
        }
    }
    class Result{
        String CODE;
        String MESSAGE;

        public String getCODE() {
            return CODE;
        }

        public String getMESSAGE() {
            return MESSAGE;
        }
    }
    class Row{
        String ROAD_NM;
        String ROAD_TYPE;
        String ROAD_FUNC;
        String ROAD_SCALE;
        String ROAD_WIDTH;
        String CGG_DIV;

        public String getROAD_NM() {
            return ROAD_NM;
        }

        public String getROAD_TYPE() {
            return ROAD_TYPE;
        }

        public String getROAD_FUNC() {
            return ROAD_FUNC;
        }

        public String getROAD_SCALE() {
            return ROAD_SCALE;
        }

        public String getROAD_WIDTH() {
            return ROAD_WIDTH;
        }

        public String getCGG_DIV() {
            return CGG_DIV;
        }
    }
}