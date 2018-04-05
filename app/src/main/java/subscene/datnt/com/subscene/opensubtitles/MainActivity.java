//package subscene.datnt.com.subscene.opensubtitles;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//
//import org.apache.xmlrpc.XmlRpcException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        new OpenSubtitleAsyn().execute();
//
//    }
//
//    private class OpenSubtitleAsyn extends AsyncTask<Void,Void,List<SubtitleInfo>> {
//
//        @Override
//        protected List<SubtitleInfo> doInBackground(Void... voids) {
//            List<SubtitleInfo> a = new ArrayList<>();
//            OpenSubtitle openSubtitle=new OpenSubtitle();
//            try {
//                openSubtitle.login();
//                //        openSubtitle.ServerInfo();
////        openSubtitle.getSubLanguages();
//                a = openSubtitle.getMovieSubsByName("kong","20","eng");
//
////        openSubtitle.getTvSeriesSubs("game of thrones","1","1","10","eng");
//
//                //a = openSubtitle.Search("/home/sachin/Vuze Downloads/Minions.2015.720p.BRRip.850MB.MkvCage.mkv");
//
//                //openSubtitle.downloadSubtitle(new URL("http://dl.opensubtitles.org/en/download/src-api/vrf-19d80c5b/sid-smrk911ll2vaj2lt5h1144mic1/filead/1954929317.gz".replaceAll(".gz","")),"tata1.txt");
//                openSubtitle.logOut();
//            } catch (XmlRpcException e) {
//                e.printStackTrace();
//            }
//            return a;
//        }
//
//        @Override
//        protected void onPostExecute(List<SubtitleInfo> subtitleInfos) {
//            super.onPostExecute(subtitleInfos);
//            String x = "";
//        }
//    }
//}