package com.example.thiagocarvalho.meguia30;

import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiSettings controlezoom;
    private Button like, dislike, rota, calcRout;
    private AbsListView listaLike;
    private EditText yourDest;
    private AbsListView listaDislike;
    private double latit=0;
    private boolean menulike=false, menudislike=false;

    private double longi=0;
    GPSTracker gps;
    double latitude, longitude;
    String nome_referencia;
    int qtdMarker=0;
    private Polyline polyline;
    private List<LatLng> list;
    private double distance;
    ArrayList<Ponto> listPoint = new ArrayList<>();

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync();
        setUpMapIfNeeded();
        like = (Button) findViewById(R.id.like);
        like.setOnClickListener(this);
        dislike = (Button) findViewById(R.id.dislike);
        dislike.setOnClickListener(this);
        rota = (Button) findViewById(R.id.rota);
        rota.setOnClickListener(this);

        //showDist = (Button)findViewById(R.id.showDistance);
        calcRout = (Button)findViewById(R.id.calculaRota);
        yourDest = (EditText)findViewById(R.id.destination);

        //showDist.setVisibility(View.INVISIBLE);
        calcRout.setVisibility(View.INVISIBLE);
        yourDest.setVisibility(View.INVISIBLE);

    }

    public void preecheListLike() {

        String[] motivoLike = new String[]{
                "Rampa de Acesso",
                "Calçada sem Obstrução",
                "Terreno Plano",
                "Local com Acessibilidade",
                "Vaga para Deficiente"
        };

        ArrayAdapter<String> adapterL = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, motivoLike);
        listaLike = (ListView)findViewById(R.id.listLike);
        listaLike.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listaLike.setAdapter(adapterL);

    }

    public void preecheListDislike() {

        String[] motivoDislike = new String[]{
                "Obstrução ",
                "Terreno Íngrime",
                "Burraco",
                "Obstáculo",
                "Local Inacessível",

        };
        ArrayAdapter<String> adapterL = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, motivoDislike);
        listaDislike = (ListView)findViewById(R.id.listDislike);
        listaDislike.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listaDislike.setAdapter(adapterL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        controlezoom = mMap.getUiSettings();
        controlezoom.setZoomControlsEnabled(true);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                //getMarker();

                /*for (int i=0; i<markerLocal.size(); i++){
                    setMarker(markerLocal.get(i).getLatitude(), markerLocal.get(i).getLongitude()
                            ,markerLocal.get(i).getReferencia(),markerLocal.get(i).isIdentificador());
                }*/
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    /*public void starGPS(){

        LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                updateView(locat);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }

    public void updateView(Location locat){

        latit = locat.getLatitude();
        longi = locat.getLongitude();

        System.out.println(latit);
        System.out.println(longi);
    }*/

    public void setMarker(double LaTi, double LoGi, String nomeM, boolean identif, int position, String snippet){

        if(identif) {
            if(position==0){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LaTi, LoGi))
                        .title(nomeM).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rampa)));
            }if(position==1){

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LaTi, LoGi))
                        .title(nomeM).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.acesso)));

            }
            if(position==2){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LaTi, LoGi))
                        .title(nomeM).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.road)));
            }
            if (position==3){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LaTi, LoGi))
                        .title(nomeM).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.disability)));
            }
            if(position==4){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LaTi, LoGi))
                        .title(nomeM).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.stopd)));

            }
        }else{
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(LaTi, LoGi))
                    .title(nomeM).snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.caution)));

        }
    }


    private void setUpMap() {

        setMarker(-23.558050, -46.732032, "Terreno Íngrime", false, 1, "Universidade de São Paulo");
        setMarker(-23.557873, -46.730595, "Local com Acessibilidade", true, 3, "Universidade de São Paulo");
        setMarker(-23.556536, -46.729135, "Vaga para Deficiente", true, 4, "Universidade de São Paulo");
        setMarker(-23.557263, -46.731614, "Terreno Plano", true, 2, "Universidade de São Paulo");
        setMarker(-23.556762, -46.728728, "Terreno Plano", true, 2, "Universidade de São Paulo");
        setMarker(-23.555670, -46.729135, "Obstáculo", false, 3, "Universidade de São Paulo");


        getLocationGPS();

        LatLng latlng = new LatLng(latit,longi);
        CameraPosition position = CameraPosition.builder().target(latlng).zoom(25).bearing(0).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);

    mMap.animateCamera(update, 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }

            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }
        });



    }

    @Override
    public void onClick(View v) {

        getLocationGPS();

        //listaDislike.setVisibility(View.INVISIBLE);
        //listaLike.setVisibility(View.INVISIBLE);
        switch (v.getId()) {
            case R.id.like:


                preecheListLike();

                if(menudislike==true){

                    listaDislike.setVisibility(View.INVISIBLE);
                    listaLike.setVisibility(View.VISIBLE);
                    menulike=true;
                    menudislike=false;
                }else{

                    listaLike.setVisibility(View.VISIBLE);
                    menulike=true;

                }



                listaLike.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String nome = listaLike.getItemAtPosition(position).toString();

                        setMarker(latit, longi, nome, true, position, "Universidade de São Paulo");
                        listaLike.setVisibility(View.INVISIBLE);
                        menulike=true;
                        setUpMap();
                    }

                });

                break;
            case R.id.dislike:

                preecheListDislike();

                if(menulike==true){

                    listaLike.setVisibility(View.INVISIBLE);
                    listaDislike.setVisibility(View.VISIBLE);
                    menulike=false;
                    menudislike=true;
                }else{

                    listaLike.setVisibility(View.VISIBLE);
                    menudislike=false;

                }
                listaDislike.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String nome = listaDislike.getItemAtPosition(position).toString();

                        setMarker(latit, longi, nome, false, position, "Universidade de São Paulo");
                        listaDislike.setVisibility(View.INVISIBLE);
                        setUpMap();
                    }
                });
                break;
            case R.id.rota:
                //AQUI ATIVA O BOTAO CALCULAR ROTA
                //showDist.setVisibility(View.VISIBLE);
                calcRout.setVisibility(View.VISIBLE);
                yourDest.setVisibility(View.VISIBLE);

            break;


        }

    }

    //método para recuperar dados do xml

        //METODO PARA USAR GPS
    public void getLocationGPS(){
        gps = new GPSTracker(MapsActivity.this);
        if(gps.canGetLocation()){

            gps.getLocation();

            latit = gps.getLatitude();
            longi = gps.getLongitude();

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latit + "\nLong: " + longi, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    //CALCULO DE ROTA++++++++++++++++++++++++++++++++++++++++

    public void drawRoute(){
        PolylineOptions po;

        if(polyline == null){
            po = new PolylineOptions();

            for(int i = 0, tam = list.size(); i < tam; i++){
                po.add(list.get(i));
            }

            po.color(Color.BLUE).width(8);
            polyline = mMap.addPolyline(po);
        }
        else{
            polyline.setPoints(list);
        }
    }

    public void getDistance(){
		/*double distance = 0;

		for(int i = 0, tam = list.size(); i < tam; i++){
			if(i < tam - 1){
				distance += distance(list.get(i), list.get(i+1));
			}
		}*/

        Toast.makeText(MapsActivity.this, "Distancia: "+distance/1000+" KM", Toast.LENGTH_LONG).show();
    }

    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }

    /* ***************************************** ROTA ***************************************** */

    public void getRouteByGMAV2(View view) throws UnsupportedEncodingException {
        //EditText etO = (EditText) findViewById(R.id.origin);
        EditText etD = (EditText) findViewById(R.id.destination);
        //String origin = URLEncoder.encode(etO.getText().toString(), "UTF-8");
        String destination = URLEncoder.encode(etD.getText().toString(), "UTF-8");

        getRoute(destination);
    }



    // WEB CONNECTION

    public void getRoute(final String destination){
        new Thread(){
            public void run(){
                String url= "http://maps.googleapis.com/maps/api/directions/json?origin="
                        + latit+","+longi+"&destination="
                        + destination+"&sensor=false";

               /* String url= "http://maps.googleapis.com/maps/api/directions/json?origin="
                        + origin.latitude+","+origin.longitude+"&destination="
                        + destination.latitude+","+destination.longitude+"&sensor=false";*/


                HttpResponse response;
                HttpGet request;
                AndroidHttpClient client = AndroidHttpClient.newInstance("route");

                request = new HttpGet(url);
                try {
                    response = client.execute(request);
                    final String answer = EntityUtils.toString(response.getEntity());

                    runOnUiThread(new Runnable(){
                        public void run(){
                            try {
                                //Log.i("Script", answer);
                                list = buildJSONRoute(answer);
                                drawRoute();
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }




    // PARSER JSON
    public List<LatLng> buildJSONRoute(String json) throws JSONException{
        JSONObject result = new JSONObject(json);
        JSONArray routes = result.getJSONArray("routes");

        distance = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        List<LatLng> lines = new ArrayList<LatLng>();

        for(int i=0; i < steps.length(); i++) {
            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lng"));


            String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

            for(LatLng p : decodePolyline(polyline)) {
                lines.add(p);
            }

            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));
        }
        getDistance();
        return(lines);

    }




    // DECODE POLYLINE
    private List<LatLng> decodePolyline(String encoded) {

        List<LatLng> listPoints = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            Log.i("Script", "POL: LAT: "+p.latitude+" | LNG: "+p.longitude);
            listPoints.add(p);
        }
        return listPoints;
    }

    public void carPoint(double lat, double log, String referencia,  boolean id, int position, int tam){

        Ponto onePoint = new Ponto(lat, log, referencia, id, position);
        while(count<tam) {
            System.out.println("VER: "+count);
            listPoint.add(count, onePoint);
            count++;
        }


    }

}



