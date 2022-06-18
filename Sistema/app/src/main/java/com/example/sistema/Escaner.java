package com.example.sistema;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Escaner extends AppCompatActivity {

    Button btnScan;
    TextView txtResultado,txtMarca,txtPeso,txtNombre,textEje,textView2,textView3;
    TableLayout tableLayout;
    ImageView imageView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);
        tableLayout = findViewById(R.id.ListaAlma);
        txtResultado = findViewById(R.id.txtResultado);
        txtNombre = findViewById(R.id.txtNombre);
        txtMarca = findViewById(R.id.txtMarca);
        txtPeso = findViewById(R.id.txtPeso);
        imageView = findViewById(R.id.imageView);
        textEje = findViewById(R.id.textEje);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(Escaner.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
                integrador.setPrompt("Lector - EAN-13");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if (result != null){
            if (result.getContents()==null){
                Toast.makeText(this,"Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                txtNombre.setText(null);
                txtMarca.setText(null);
                txtPeso.setText(null);

                while (tableLayout.getChildCount() > 1)
                    tableLayout.removeView(tableLayout.getChildAt(tableLayout.getChildCount() - 1));

                imageView.setVisibility(View.GONE);
                textEje.setVisibility(View.GONE);
                Toast.makeText(this,result.getContents(), Toast.LENGTH_LONG).show();
                txtResultado.setText(result.getContents());
                buscarProducto("https://bodegajm.000webhostapp.com/Sistema/buscar_producto.php?codigo="+result.getContents()+"");
                buscarCantidad("https://bodegajm.000webhostapp.com/Sistema/buscarcantidad.php?codigo="+result.getContents()+"");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    };

    public void buscarCantidad(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        TableRow tbrow = new TableRow(getBaseContext());
                        TextView t1v = new TextView(getBaseContext());
                        TextView t2v = new TextView(getBaseContext());
                        t1v.setText(jsonObject.getString("CodEst"));
                        t1v.setTextColor(Color.WHITE);
                        t1v.setGravity(Gravity.CENTER);
                        t1v.setTextSize(16);
                        t1v.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1f));
                        t2v.setText(jsonObject.getString("Cantidad"));
                        t2v.setTextColor(Color.WHITE);
                        t2v.setGravity(Gravity.CENTER);
                        t2v.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                        t2v.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1f));
                        tbrow.addView(t1v);
                        tbrow.addView(t2v);
                        tableLayout.addView(tbrow);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error OnResponse", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error o No hay productos en Almacenamiento", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    private void buscarProducto(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        txtNombre.setText(jsonObject.getString("Nombre"));
                        txtMarca.setText(jsonObject.getString("Marca"));
                        txtPeso.setText(jsonObject.getString("Peso"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error OnResponse", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error o producto no registrado", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}