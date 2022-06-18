package com.example.sistema;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class Limpiar extends AppCompatActivity {

    Button btnEst,btnLimpiar;
    TextView textEje,textCod,txtEstanteria;
    ImageView imageView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limpiar);

        btnEst = findViewById(R.id.btnEst);
        textCod = findViewById(R.id.textCod);
        txtEstanteria = findViewById(R.id.txtEstanteria);
        imageView = findViewById(R.id.imageView);
        textEje = findViewById(R.id.textEje);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProducto("https://bodegajm.000webhostapp.com/Sistema/limpiar.php");
            }
        });

        btnEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(Limpiar.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.CODE_39);
                integrador.setPrompt("Lector - CODE-39");
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
                    Toast.makeText(this,result.getContents(), Toast.LENGTH_LONG).show();
                    textCod.setVisibility(View.VISIBLE);
                    txtEstanteria.setVisibility(View.VISIBLE);
                    txtEstanteria.setText(result.getContents());
                    imageView.setVisibility(View.GONE);
                    textEje.setVisibility(View.GONE);
                    btnLimpiar.setVisibility(View.VISIBLE);
                }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    };

    private void eliminarProducto(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "La Estantería fue limpiada", Toast.LENGTH_SHORT).show();
                limpiar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put("codigo",txtEstanteria.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void limpiar(){
        textCod.setVisibility(View.GONE);
        txtEstanteria.setVisibility(View.GONE);
        txtEstanteria.setText(null);
        imageView.setVisibility(View.VISIBLE);
        textEje.setVisibility(View.VISIBLE);
        btnLimpiar.setVisibility(View.GONE);
    }
}