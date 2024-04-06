package br.com.gabrieldani.maps.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class XMLHelper {

    // Método estático para exibir um Toast curto
    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Método estático para exibir um Toast longo
    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Método estático para atualizar o texto de um TextView
    public static void updateTextViewText(TextView textView, String newText) {
        textView.setText(newText);
    }

    // Método estático para alterar a visibilidade de uma View
    public static void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    // Adiciona um marcador ao mapa e move a câmera para o marcador
    @SuppressLint("MissingPermission")
    public static void addMarkerAndMoveCamera(GoogleMap googleMap, LatLng position, String title) {
        // Limpa todos os marcadores do mapa
        googleMap.clear();

        // Adiciona um marcador na posição especificada
        googleMap.addMarker(new MarkerOptions().position(position).title(title));

        // Move a câmera para a posição do marcador
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17f)); // 17f é o nível de zoom
    }

}

