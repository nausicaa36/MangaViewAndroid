package ml.melun.mangaview;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ml.melun.mangaview.mangaview.Login;
import ml.melun.mangaview.mangaview.MTitle;
import ml.melun.mangaview.mangaview.Title;

import static ml.melun.mangaview.mangaview.Title.isInteger;

public class Preference {
    SharedPreferences sharedPref;
    //ArrayList<Title> recent;
    List<MTitle> recent;
    List<MTitle> favorite;
    SharedPreferences.Editor prefsEditor;
    JSONObject pagebookmark;
    JSONObject bookmark;
    String homeDir;
    boolean darkTheme;
    int viewerType;
    boolean reverse;
    boolean dataSave;
    int startTab;
    String url;
    boolean stretch;
    boolean leftRight;
    Login login;
    String defUrl;
    boolean autoUrl;
    float pageControlButtonOffset;
    int prevPageKey, nextPageKey;
    String baseMode = "";

    public SharedPreferences getSharedPref(){
        return this.sharedPref;
    }

    public void reset(){
        setUrl(defUrl);
        resetFavorites();
        resetRecent();
        resetBookmark();
        resetViewerBookmark();
    }

    //Offline manga has id of -1
    public Preference(Context context){
        init(context);
    }
    public void init(Context mcontext){
        sharedPref = mcontext.getSharedPreferences("mangaView",Context.MODE_PRIVATE);
        prefsEditor = sharedPref.edit();
        try {
            Gson gson = new Gson();
            recent = gson.fromJson(sharedPref.getString("recent", ""),new TypeToken<ArrayList<MTitle>>(){}.getType());
            if(recent==null) recent = new ArrayList<>();
            favorite = gson.fromJson(sharedPref.getString("favorite", ""),new TypeToken<ArrayList<MTitle>>(){}.getType());
            if(favorite==null) favorite = new ArrayList<>();
            homeDir = sharedPref.getString("homeDir","/sdcard/MangaView/saved");
            prevPageKey = sharedPref.getInt("prevPageKey", -1);
            nextPageKey = sharedPref.getInt("nextPageKey", -1);
            pagebookmark = new JSONObject(sharedPref.getString("bookmark", "{}"));
            bookmark = new JSONObject(sharedPref.getString("bookmark2", "{}"));
            darkTheme = sharedPref.getBoolean("darkTheme", false);
            viewerType = sharedPref.getInt("viewerType",0);
            reverse = sharedPref.getBoolean("pageReverse",false);
            dataSave = sharedPref.getBoolean("dataSave", false);
            startTab = sharedPref.getInt("startTab", 0);
            defUrl = sharedPref.getString("defUrl", "설정되지 않음");
            url = sharedPref.getString("url", defUrl);
            stretch = sharedPref.getBoolean("stretch", false);
            leftRight = sharedPref.getBoolean("leftRight", false);
            login = gson.fromJson(sharedPref.getString("login","{}"),new TypeToken<Login>(){}.getType());
            autoUrl = sharedPref.getBoolean("autoUrl", true);
            pageControlButtonOffset = sharedPref.getFloat("pageControlButtonOffset", -1);
            baseMode = sharedPref.getString("baseMode", "comic");
//            if(login != null && login.isValid()){
//                setSession(login.getCookie());
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getBaseMode(){
        return this.baseMode;
    }

    public void setBaseMode(String baseMode){
        this.baseMode = baseMode;
        prefsEditor.putString("baseMode", baseMode);
        prefsEditor.commit();
    }

    public void setDefUrl(String defUrl){
        this.defUrl = defUrl;
        prefsEditor.putString("defUrl", defUrl);
        prefsEditor.commit();
        setUrl(defUrl);
    }

    public String getDefUrl() {
        return defUrl;
    }

    public boolean getLeftRight() {
        return leftRight;
    }

    public void setLeftRight(boolean leftRight) {
        this.leftRight = leftRight;
        prefsEditor.putBoolean("leftRight", leftRight);
        prefsEditor.commit();
    }

    public int getViewerType() {
        return viewerType;
    }

    public void setViewerType(int viewerType) {
        this.viewerType = viewerType;
        prefsEditor.putInt("viewerType", viewerType);
        prefsEditor.commit();
    }

    public boolean getStretch() {
        return stretch;
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
        prefsEditor.putBoolean("stretch", stretch);
        prefsEditor.commit();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        prefsEditor.putString("url", url);
        prefsEditor.commit();
    }

    public int getStartTab() {
        return startTab;
    }

    public void setStartTab(int startTab) {
        this.startTab = startTab;
        prefsEditor.putInt("startTab", startTab);
        prefsEditor.commit();
    }

    public boolean getDataSave() {
        return dataSave;
    }

    public void setDataSave(boolean dataSave) {
        this.dataSave = dataSave;
        prefsEditor.putBoolean("dataSave", dataSave);
        prefsEditor.commit();
    }

    public boolean getReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
        prefsEditor.putBoolean("pageReverse", reverse);
        prefsEditor.commit();
    }


    public boolean getDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
        prefsEditor.putBoolean("darkTheme", darkTheme);
        prefsEditor.commit();
    }



    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
        prefsEditor.putString("homeDir", homeDir);
        prefsEditor.commit();
    }
    public void removeRecent(int position){
        recent.remove(position);
        writeRecent();
    }

    public void addRecent(MTitle tmp){
        if(tmp.getId()>0) {
            int position = getIndexOf(tmp);
            if (position > -1) {
                recent.add(0, recent.get(position));
                recent.remove(position + 1);
            } else recent.add(0, tmp);
            writeRecent();
        }
    }
    public void addRecent(Title tmp){
        if(tmp.getId()>0) {
            MTitle title = tmp.minimize();
            int position = getIndexOf(title);
            if (position > -1) {
                recent.add(0, recent.get(position));
                recent.remove(position + 1);
            } else recent.add(0, title);
            writeRecent();
        }
    }


    public void updateRecentData(MTitle title){
        MTitle tmp = title.clone();
        recent.set(0, tmp);
        writeRecent();
        int index = findFavorite(tmp);
        if(index>-1){
            favorite.set(index,tmp);
            Gson gson = new Gson();
            prefsEditor.putString("favorite", gson.toJson(favorite));
            prefsEditor.commit();
        }
    }

    public void updateRecentData(Title title){
        MTitle tmp = title.minimize();
        recent.set(0, tmp);
        writeRecent();
        int index = findFavorite(tmp);
        if(index>-1){
            favorite.set(index, tmp);
            Gson gson = new Gson();
            prefsEditor.putString("favorite", gson.toJson(favorite));
            prefsEditor.commit();
        }
    }

    private int getIndexOf(MTitle title){
        if(title.getId()>0) {
            return recent.indexOf(title);
        }
        return -1;
    }

    public void setBookmark(Title title, int id){
        int titleId = title.getId();
        if(titleId>0) {
            try {
                bookmark.put(String.valueOf(title.getId()), id);
            } catch (Exception e) {
                //
            }
            writeBookmark();
        }
    }
    public int getBookmark(MTitle title){
        //return recent.mget(0).getBookmark();
        int titleId = title.getId();
        if(titleId>0) {
            try {
                return bookmark.getInt(String.valueOf(titleId));
            } catch (Exception e) {
                //
            }
        }
        return -1;
    }
    public void writeBookmark(){
        prefsEditor.putString("bookmark2", bookmark.toString());
        prefsEditor.commit();
    }

    public void resetBookmark(){
        try {
            bookmark = new JSONObject("{}");
        }catch (Exception e){}
        writeBookmark();
    }
    public void resetRecent(){
        recent = new ArrayList<>();
        writeRecent();
    }

    public void resetFavorites(){
        favorite = new ArrayList<>();
        prefsEditor.putString("favorite", new Gson().toJson(favorite));
        prefsEditor.commit();

    }

    private void writeRecent(){
        Gson gson = new Gson();
        prefsEditor.putString("recent", gson.toJson(recent));
        prefsEditor.commit();
    }


    public void setViewerBookmark(int id,int index){
        if(id>-1) {
            if (index > 0) {
                try {
                    pagebookmark.put(id + "", index);
                } catch (Exception e) {
                    //
                }
                writeViewerBookmark();
            }
        }
    }
    public int getViewerBookmark(int id){
        if(id>-1) {
            try {
                return pagebookmark.getInt(id + "");
            } catch (Exception e) {
                //
            }
        }
        return 0;
    }
    public void removeViewerBookmark(int id){
        pagebookmark.remove(id+"");
        writeViewerBookmark();
    }
    public void resetViewerBookmark(){
        try {
            pagebookmark = new JSONObject("{}");
        }catch (Exception e){}
        writeViewerBookmark();
    }
    private void writeViewerBookmark(){
        prefsEditor.putString("bookmark", pagebookmark.toString());
        prefsEditor.commit();
    }

    public boolean toggleFavorite(Title tmp, int position){
            return toggleFavorite(tmp.minimize(), position);
    }

    public boolean toggleFavorite(MTitle title, int position){
        int index = findFavorite(title);
        if(index==-1){
            favorite.add(position,title);
            Gson gson = new Gson();
            prefsEditor.putString("favorite", gson.toJson(favorite));
            prefsEditor.commit();
            return true;
        }else{
            favorite.remove(index);
            Gson gson = new Gson();
            prefsEditor.putString("favorite", gson.toJson(favorite));
            prefsEditor.commit();
            return false;
        }
    }

    public int findFavorite(MTitle title){
        if(title.getId()>0){
            return favorite.indexOf(title);
        }
        return -1;
    }

    public List<MTitle> getFavorite(){
        return favorite;
    }

    public void setFavorites(List<MTitle> fav){
        this.favorite = (List<MTitle>)(List<?>)fav;
        Gson gson = new Gson();
        prefsEditor.putString("favorite", gson.toJson(favorite));
        prefsEditor.commit();
    }

    public void setRecents(List<MTitle> rec){
        this.recent = (List<MTitle>)(List<?>)rec;
        writeRecent();
    }

    public void setBookmarks(JSONObject book){
        this.bookmark = book;
        writeBookmark();
    }

    public List<MTitle> getRecent(){
        return recent;
    }

    public boolean isViewed(int id){
        return pagebookmark.has(id+"");
    }

//    public boolean match(String s1, String s2){
//        return filterString(s1).matches(filterString(s2));
//    }
//    private String filterString(String input){
//        int i=0, j=0, m=0, k=0;
//        while(i>-1||j>-1||m>-1||k>-1){
//            i = input.indexOf('(');
//            j = input.indexOf(')');
//            m = input.indexOf('/');
//            k = input.indexOf('?');
//            char[] tmp = input.toCharArray();
//            if(i>-1) tmp[i] = ' ';
//            if(j>-1) tmp[j] = ' ';
//            if(m>-1) tmp[m] = ' ';
//            if(k>-1) tmp[k] = ' ';
//            input = String.valueOf(tmp);
//        }
//        return input;
//    }

    //for debug
//    public void removeEpsFromData(){
//        for(Title t:recent){t.removeEps();}
//        for(Title t:favorite){t.removeEps();}
//        writeRecent();
//        Gson gson = new Gson();
//        prefsEditor.putString("favorite", gson.toJson(favorite));
//        prefsEditor.commit();
//    }

    public void setLogin(Login login){
        this.login = login;
        if(login == null)
            prefsEditor.putString("login", "{}");
        else
            prefsEditor.putString("login", new Gson().toJson(login));
        prefsEditor.commit();
    }

    public boolean check(){
        //returns false if needs update
        for(MTitle t: recent){
            if(isInteger(t.getRelease())) return false;
        }
        for(MTitle t: favorite){
            if(isInteger(t.getRelease())) return false;
        }
        return true;
    }
    public Login getLogin(){
        return login;
    }

    public JSONObject getBookmarkObject() {
        return bookmark;
    }

//    public String getSession() {
//        return session;
//    }

//    public void setSession(String session) {
//        this.session = session;
//        prefsEditor.putString("session", session);
//        prefsEditor.commit();
//    }

    public boolean getAutoUrl() {
        return autoUrl;
    }

    public void setAutoUrl(boolean autoUrl) {
        this.autoUrl = autoUrl;
        prefsEditor.putBoolean("autoUrl", autoUrl);
        prefsEditor.commit();
    }


    public int getPrevPageKey() {
        return prevPageKey;
    }

    public void setPrevPageKey(int prevPageKey) {
        this.prevPageKey = prevPageKey;
        prefsEditor.putInt("prevPageKey", prevPageKey);
        prefsEditor.commit();
    }

    public int getNextPageKey() {
        return nextPageKey;
    }

    public void setNextPageKey(int nextPageKey) {
        this.nextPageKey = nextPageKey;
        prefsEditor.putInt("nextPageKey", nextPageKey);
        prefsEditor.commit();
    }

    public float getPageControlButtonOffset() {
        return pageControlButtonOffset;
    }

    public void setPageControlButtonOffset(float pageControlButtonOffset) {
        this.pageControlButtonOffset = pageControlButtonOffset;
        prefsEditor.putFloat("pageControlButtonOffset", pageControlButtonOffset);
        prefsEditor.commit();
    }
}