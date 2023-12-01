package com.cst3104.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State implements Comparable, Serializable {
    private String name = null;    // state name
    private String code = null;    // 2-letter code
    private String capital = null;    // state capital
    private int area = 0; // state area in square km
    private String union = null;    // entry date into union
    private String wikiUrl = null;   // wiki URL of the state
    private static Context ctx = null;   // required for JSON

    public static List<State> getShuffledStatesList(List<State> allStates, State mCurrentState) {
        List<State> shuffledList = new ArrayList<>(allStates);
        Collections.shuffle(shuffledList);
        return shuffledList;
    }

    // Default Constructor
    public State() {
        this.setName(null);
        this.setCode(null);
        this.setCapital(null);
        this.setArea(0);
        this.setUnion(null);
        this.setWikiUrl(null);
    }

    // Constructor
    public State(String name, String code, String capital, int area, String union, String wikiUrl) {
        this.setName(name);
        this.setCode(code);
        this.setCapital(capital);
        this.setArea(area);
        this.setUnion(union);
        this.setWikiUrl(wikiUrl);
    }

    // Comparison Function to sort the list after reading into JSON
    @Override
    public int compareTo(Object other) {
        return this.toString().compareTo(other.toString());
    }

    // Accessor
    public String getName() {
        return name;
    }

    // Mutator
    public void setName(String name) {
        this.name = name;
    }

    // Accesseur de l'attribut code
    public String getCode() {
        return code;
    }

    // Accessor
    public void setCode(String code) {
        this.code = code;
    }

    //Accessor
    public String getCapital() {
        return capital;
    }

    // Mutator
    public void setCapital(String capital) {
        this.capital = capital;
    }

    // Accessor
    public int getArea() {
        return area;
    }

    // Mutator
    public void setArea(int area) {
        this.area = area;
    }

    // Accessor
    public String getWikiUrl() {
        return wikiUrl;
    }

    // Mutator
    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    // Accessor
    public String getUnion() {
        return union;
    }

    // Mutator
    public void setUnion(String union) {
        this.union = union;
    }

    // Accessor
    public String getDrawable() {
        return code;
    }

    // Returns a String with a short description of the State
    @Override
    public String toString() {
        return this.getName();
    }
    // To insert an image in a provided ImageView
    // The image must be found in res/drawable and should contain
    // a name equal to the state code
    public void flagInImageView(ImageView iv) {
        String uri = "@drawable/" + this.getDrawable().toLowerCase();

        int imageResource = ctx.getResources().getIdentifier(uri, null, ctx.getPackageName());
        Log.d("State", "Drawable Resource ID: " + imageResource);

        Drawable res = ctx.getDrawable(imageResource);
        iv.setImageDrawable(res);
    }

    // Deserialize a list of states from a file in JSON format
    public static ArrayList<State> readData(Context ctx,  String fileName){

        final ArrayList<State> mylist = new ArrayList<>();

        // Needed for drawables
        State.ctx = ctx;

        try {
            // load the data in an ArrayList
            String jsonString = readJson(fileName, State.ctx);
            JSONObject json = new JSONObject(jsonString);
            JSONArray states = json.getJSONArray("states");

            // Loop through the list in the json array
            for(int i = 0; i < states.length(); i++){
                State e = new State();

                e.name = states.getJSONObject(i).getString("name");
                e.code = states.getJSONObject(i).getString("code");
                e.capital = states.getJSONObject(i).getString("capital");
                e.area = states.getJSONObject(i).getInt("area");
                e.union = states.getJSONObject(i).getString("union");
                e.wikiUrl = states.getJSONObject(i).getString("wiki");

                mylist.add(e);
            }

        } catch (JSONException e) {
            // Log the error
            e.printStackTrace();
        }
        return mylist;
    }

    // Returns a String with the contents of the JSON file
    private static String readJson (String fileName, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "ISO-8859-1");
        }
        catch (java.io.IOException ex) {
            // Log the error
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
