package com.example.sistema;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Estanteria extends AppCompatActivity {

    Button btnEst, btnEscaner, btnAgr;
    TextView txtEstanteria, txtResultado, textProducto, textCantidad, textCod, textEje;
    EditText txtCantidad;
    ImageView imageView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        btnEst = findViewById(R.id.btnEst);
        textEje = findViewById(R.id.textEje);
        imageView = findViewById(R.id.imageView);
        btnAgr = findViewById(R.id.btnAgr);
        btnEscaner = findViewById(R.id.btnEscaner);
        textCod = findViewById(R.id.textCod);
        txtEstanteria = findViewById(R.id.txtEstanteria);
        txtResultado = findViewById(R.id.txtResultado);
        txtCantidad = findViewById(R.id.txtCantidad);
        textProducto = findViewById(R.id.textProducto);
        textCantidad = findViewById(R.id.textCantidad);
        btnAgr.setEnabled(false);

        txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("") || charSequence.toString().equals(null)) {
                    btnAgr.setEnabled(false);
                } else {
                    btnAgr.setEnabled(true);
                    btnAgr.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("") || editable.toString().equals(null)) {
                    btnAgr.setEnabled(false);
                } else {
                    btnAgr.setEnabled(true);
                    btnAgr.setVisibility(View.VISIBLE);
                }
            }
        });

        btnEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(Estanteria.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.CODE_39);
                integrador.setPrompt("Lector - CODE-39");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });

        btnEscaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador2 = new IntentIntegrator(Estanteria.this);
                integrador2.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
                integrador2.setPrompt("Lector - EAN-13");
                integrador2.setCameraId(0);
                integrador2.setBeepEnabled(true);
                integrador2.setBarcodeImageEnabled(true);
                integrador2.initiateScan();
            }
        });

        btnAgr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarProducto("https://bodegajm.000webhostapp.com/Sistema/insertar.php");
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().chars().anyMatch(Character::isLetter)) {
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    textCod.setVisibility(View.VISIBLE);
                    txtEstanteria.setVisibility(View.VISIBLE);
                    txtEstanteria.setText(result.getContents());
                    btnEscaner.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    textEje.setVisibility(View.GONE);
                } else {
                    txtResultado.setVisibility(View.VISIBLE);
                    txtResultado.setText(result.getContents());
                    txtCantidad.setText(null);
                    txtCantidad.setVisibility(View.VISIBLE);
                    textCantidad.setVisibility(View.VISIBLE);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    ;

    private void agregarProducto(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Producto agregado a la estantería", Toast.LENGTH_SHORT).show();
                txtResultado.setVisibility(View.GONE);
                txtResultado.setText(null);
                txtCantidad.setText(null);
                txtCantidad.setVisibility(View.GONE);
                textCantidad.setVisibility(View.GONE);
                btnAgr.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("codigo", txtEstanteria.getText().toString());
                parametros.put("producto", txtResultado.getText().toString());
                parametros.put("cantidad", txtCantidad.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}