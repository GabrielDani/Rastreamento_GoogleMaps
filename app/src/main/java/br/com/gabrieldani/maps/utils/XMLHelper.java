package br.com.gabrieldani.maps.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

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
    public static void addMarkerAndMoveCamera(GoogleMap googleMap, LatLng position, LatLng START_LOCATION, LatLng END_LOCATION, List<LatLng> flowMeterLocations) {
        // Limpa todos os marcadores do mapa
        googleMap.clear();

        // Adiciona marcadores para startLocation e endLocation
        googleMap.addMarker(new MarkerOptions().position(START_LOCATION).title("Início"));
        googleMap.addMarker(new MarkerOptions().position(END_LOCATION).title("Destino"));

        // Habilita a localização do usuário
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Marca os "flowMeters" no mapa
        markFlowMetersOnMap(googleMap, flowMeterLocations);

        // Cria um LatLngBounds.Builder para incluir todos os pontos
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(START_LOCATION);
        boundsBuilder.include(END_LOCATION);
        boundsBuilder.include(position);

        // Constrói os limites (bounds) que incluem os três pontos
        LatLngBounds bounds = boundsBuilder.build();

        // Ajusta a câmera para incluir todos os pontos com um padding de 100 pixels (ajuste conforme necessário)
        int padding = 100; // 100 pixels de padding ao redor dos pontos
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    // Método para marcar os "flowMeters" no mapa com uma bolinha laranja
    public static void markFlowMetersOnMap(GoogleMap googleMap, List<LatLng> flowMeterLocations) {
        if (googleMap != null && flowMeterLocations != null) {
            for (LatLng location : flowMeterLocations) {
                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("Medidor de Fluxo")
                        .icon(createOrangeCircleBitmapDescriptor()));
            }
        }
    }

    // Método para criar um BitmapDescriptor de uma bolinha laranja
    private static BitmapDescriptor createOrangeCircleBitmapDescriptor() {
        int diameter = 30; // Tamanho da bolinha

        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(0xFFFFA500); // Cor laranja
        drawable.setIntrinsicWidth(diameter);
        drawable.setIntrinsicHeight(diameter);

        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}


//    public void saveStandardData() {
//        // Deleta o arquivo existente se ele existir
//        if (fileExists()) {
//            deleteDataFile();
//        }
//
//        FileWriter writer = null;
//        try {
//            writer = new FileWriter(file, true); // Cria um novo arquivo
//            // Escreve o cabeçalho
//            writer.append("Rota,Velocidade,Sensor,End Time\n");
//
//            // Adiciona os dados fornecidos
//            writer.append("Rota1,Velocidade5,Sensor1,1\n");
//            writer.append("Rota1,Velocidade5,Sensor2,33028\n");
//            writer.append("Rota1,Velocidade5,Sensor3,35727\n");
//            writer.append("Rota1,Velocidade5,Sensor4,36385\n");
//            writer.append("Rota1,Velocidade5,Sensor5,35450\n");
//            writer.append("Rota1,Velocidade5,Sensor6,38672\n");
//            writer.append("Rota1,Velocidade5,Sensor1,0\n");
//            writer.append("Rota1,Velocidade5,Sensor2,33533\n");
//            writer.append("Rota1,Velocidade5,Sensor3,36292\n");
//            writer.append("Rota1,Velocidade5,Sensor4,35841\n");
//            writer.append("Rota1,Velocidade5,Sensor5,35192\n");
//            writer.append("Rota1,Velocidade5,Sensor6,38423\n");
//            writer.append("Rota1,Velocidade4,Sensor1,0\n");
//            writer.append("Rota1,Velocidade4,Sensor2,40763\n");
//            writer.append("Rota1,Velocidade4,Sensor3,44583\n");
//            writer.append("Rota1,Velocidade4,Sensor4,45032\n");
//            writer.append("Rota1,Velocidade4,Sensor5,43952\n");
//            writer.append("Rota1,Velocidade4,Sensor6,48494\n");
//            writer.append("Rota1,Velocidade4,Sensor1,0\n");
//            writer.append("Rota1,Velocidade4,Sensor2,41251\n");
//            writer.append("Rota1,Velocidade4,Sensor3,43630\n");
//            writer.append("Rota1,Velocidade4,Sensor4,44712\n");
//            writer.append("Rota1,Velocidade4,Sensor5,44089\n");
//            writer.append("Rota1,Velocidade4,Sensor6,47924\n");
//            writer.append("Rota1,Velocidade3,Sensor1,0\n");
//            writer.append("Rota1,Velocidade3,Sensor2,54037\n");
//            writer.append("Rota1,Velocidade3,Sensor3,58749\n");
//            writer.append("Rota1,Velocidade3,Sensor4,60486\n");
//            writer.append("Rota1,Velocidade3,Sensor5,58577\n");
//            writer.append("Rota1,Velocidade3,Sensor6,63766\n");
//            writer.append("Rota1,Velocidade3,Sensor1,0\n");
//            writer.append("Rota1,Velocidade3,Sensor2,54821\n");
//            writer.append("Rota1,Velocidade3,Sensor3,58344\n");
//            writer.append("Rota1,Velocidade3,Sensor4,59729\n");
//            writer.append("Rota1,Velocidade3,Sensor5,58918\n");
//            writer.append("Rota1,Velocidade3,Sensor6,62993\n");
//            writer.append("Rota1,Velocidade2,Sensor1,0\n");
//            writer.append("Rota1,Velocidade2,Sensor2,80767\n");
//            writer.append("Rota1,Velocidade2,Sensor3,88342\n");
//            writer.append("Rota1,Velocidade2,Sensor4,89934\n");
//            writer.append("Rota1,Velocidade2,Sensor5,87031\n");
//            writer.append("Rota1,Velocidade2,Sensor6,93216\n");
//            writer.append("Rota1,Velocidade2,Sensor1,0\n");
//            writer.append("Rota1,Velocidade2,Sensor2,82348\n");
//            writer.append("Rota1,Velocidade2,Sensor3,87262\n");
//            writer.append("Rota1,Velocidade2,Sensor4,88534\n");
//            writer.append("Rota1,Velocidade2,Sensor5,87931\n");
//            writer.append("Rota1,Velocidade2,Sensor6,92512\n");
//            writer.append("Rota1,Velocidade1,Sensor1,0\n");
//            writer.append("Rota1,Velocidade1,Sensor2,165787\n");
//            writer.append("Rota1,Velocidade1,Sensor3,176432\n");
//            writer.append("Rota1,Velocidade1,Sensor4,178743\n");
//            writer.append("Rota1,Velocidade1,Sensor5,173355\n");
//            writer.append("Rota1,Velocidade1,Sensor6,183348\n");
//            writer.append("Rota1,Velocidade1,Sensor1,0\n");
//            writer.append("Rota1,Velocidade1,Sensor2,166187\n");
//            writer.append("Rota1,Velocidade1,Sensor3,175239\n");
//            writer.append("Rota1,Velocidade1,Sensor4,179112\n");
//            writer.append("Rota1,Velocidade1,Sensor5,173952\n");
//            writer.append("Rota1,Velocidade1,Sensor6,182441\n");
//            writer.append("Rota2,Velocidade5,Sensor1,0\n");
//            writer.append("Rota2,Velocidade5,Sensor2,38016\n");
//            writer.append("Rota2,Velocidade5,Sensor3,39038\n");
//            writer.append("Rota2,Velocidade5,Sensor4,39967\n");
//            writer.append("Rota2,Velocidade5,Sensor5,42392\n");
//            writer.append("Rota2,Velocidade5,Sensor6,37698\n");
//            writer.append("Rota2,Velocidade5,Sensor1,0\n");
//            writer.append("Rota2,Velocidade5,Sensor2,38512\n");
//            writer.append("Rota2,Velocidade5,Sensor3,38988\n");
//            writer.append("Rota2,Velocidade5,Sensor4,40023\n");
//            writer.append("Rota2,Velocidade5,Sensor5,41989\n");
//            writer.append("Rota2,Velocidade5,Sensor6,37998\n");
//            writer.append("Rota2,Velocidade4,Sensor1,0\n");
//            writer.append("Rota2,Velocidade4,Sensor2,47919\n");
//            writer.append("Rota2,Velocidade4,Sensor3,48629\n");
//            writer.append("Rota2,Velocidade4,Sensor4,49535\n");
//            writer.append("Rota2,Velocidade4,Sensor5,52213\n");
//            writer.append("Rota2,Velocidade4,Sensor6,47458\n");
//            writer.append("Rota2,Velocidade4,Sensor1,0\n");
//            writer.append("Rota2,Velocidade4,Sensor2,48212\n");
//            writer.append("Rota2,Velocidade4,Sensor3,48920\n");
//            writer.append("Rota2,Velocidade4,Sensor4,49228\n");
//            writer.append("Rota2,Velocidade4,Sensor5,51912\n");
//            writer.append("Rota2,Velocidade4,Sensor6,47955\n");
//            writer.append("Rota2,Velocidade3,Sensor1,0\n");
//            writer.append("Rota2,Velocidade3,Sensor2,63157\n");
//            writer.append("Rota2,Velocidade3,Sensor3,63835\n");
//            writer.append("Rota2,Velocidade3,Sensor4,65473\n");
//            writer.append("Rota2,Velocidade3,Sensor5,68316\n");
//            writer.append("Rota2,Velocidade3,Sensor6,63780\n");
//            writer.append("Rota2,Velocidade3,Sensor1,0\n");
//            writer.append("Rota2,Velocidade3,Sensor2,63623\n");
//            writer.append("Rota2,Velocidade3,Sensor3,64120\n");
//            writer.append("Rota2,Velocidade3,Sensor4,65179\n");
//            writer.append("Rota2,Velocidade3,Sensor5,67919\n");
//            writer.append("Rota2,Velocidade3,Sensor6,64021\n");
//            writer.append("Rota2,Velocidade2,Sensor1,0\n");
//            writer.append("Rota2,Velocidade2,Sensor2,93525\n");
//            writer.append("Rota2,Velocidade2,Sensor3,94192\n");
//            writer.append("Rota2,Velocidade2,Sensor4,96204\n");
//            writer.append("Rota2,Velocidade2,Sensor5,99297\n");
//            writer.append("Rota2,Velocidade2,Sensor6,94623\n");
//            writer.append("Rota2,Velocidade2,Sensor1,0\n");
//            writer.append("Rota2,Velocidade2,Sensor2,93496\n");
//            writer.append("Rota2,Velocidade2,Sensor3,94436\n");
//            writer.append("Rota2,Velocidade2,Sensor4,96138\n");
//            writer.append("Rota2,Velocidade2,Sensor5,100013\n");
//            writer.append("Rota2,Velocidade2,Sensor6,95233\n");
//            writer.append("Rota2,Velocidade1,Sensor1,0\n");
//            writer.append("Rota2,Velocidade1,Sensor2,188207\n");
//            writer.append("Rota2,Velocidade1,Sensor3,188210\n");
//            writer.append("Rota2,Velocidade1,Sensor4,193237\n");
//            writer.append("Rota2,Velocidade1,Sensor5,198025\n");
//            writer.append("Rota2,Velocidade1,Sensor6,190624\n");
//            writer.append("Rota2,Velocidade1,Sensor1,0\n");
//            writer.append("Rota2,Velocidade1,Sensor2,189001\n");
//            writer.append("Rota2,Velocidade1,Sensor3,188802\n");
//            writer.append("Rota2,Velocidade1,Sensor4,192951\n");
//            writer.append("Rota2,Velocidade1,Sensor5,197532\n");
//            writer.append("Rota2,Velocidade1,Sensor6,191059\n");
            //writer.append("Rota3,Velocidade5,Sensor1,0\n");
            //writer.append("Rota3,Velocidade5,Sensor2,45841\n");
            //writer.append("Rota3,Velocidade5,Sensor3,47132\n");
            //writer.append("Rota3,Velocidade5,Sensor4,48354\n");
            //writer.append("Rota3,Velocidade5,Sensor5,51519\n");
            //writer.append("Rota3,Velocidade5,Sensor6,45197\n");
            //writer.append("Rota3,Velocidade5,Sensor1,0\n");
            //writer.append("Rota3,Velocidade5,Sensor2,45685\n");
            //writer.append("Rota3,Velocidade5,Sensor3,47283\n");
            //writer.append("Rota3,Velocidade5,Sensor4,48589\n");
            //writer.append("Rota3,Velocidade5,Sensor5,51946\n");
            //writer.append("Rota3,Velocidade5,Sensor6,45071\n");
            //writer.append("Rota3,Velocidade4,Sensor1,0\n");
            //writer.append("Rota3,Velocidade4,Sensor2,58396\n");
            //writer.append("Rota3,Velocidade4,Sensor3,59485\n");
            //writer.append("Rota3,Velocidade4,Sensor4,60735\n");
            //writer.append("Rota3,Velocidade4,Sensor5,63688\n");
            //writer.append("Rota3,Velocidade4,Sensor6,56991\n");
            //writer.append("Rota3,Velocidade4,Sensor1,0\n");
            //writer.append("Rota3,Velocidade4,Sensor2,58737\n");
            //writer.append("Rota3,Velocidade4,Sensor3,60629\n");
            //writer.append("Rota3,Velocidade4,Sensor4,60133\n");
            //writer.append("Rota3,Velocidade4,Sensor5,63308\n");
            //writer.append("Rota3,Velocidade4,Sensor6,57225\n");
            //writer.append("Rota3,Velocidade3,Sensor1,0\n");
            //writer.append("Rota3,Velocidade3,Sensor2,76602\n");
            //writer.append("Rota3,Velocidade3,Sensor3,78008\n");
            //writer.append("Rota3,Velocidade3,Sensor4,79217\n");
            //writer.append("Rota3,Velocidade3,Sensor5,82765\n");
            //writer.append("Rota3,Velocidade3,Sensor6,76172\n");
            //writer.append("Rota3,Velocidade3,Sensor1,0\n");
            //writer.append("Rota3,Velocidade3,Sensor2,76010\n");
            //writer.append("Rota3,Velocidade3,Sensor3,78365\n");
            //writer.append("Rota3,Velocidade3,Sensor4,79871\n");
            //writer.append("Rota3,Velocidade3,Sensor5,82448\n");
            //writer.append("Rota3,Velocidade3,Sensor6,76572\n");
            //writer.append("Rota3,Velocidade2,Sensor1,0\n");
            //writer.append("Rota3,Velocidade2,Sensor2,113939\n");
            //writer.append("Rota3,Velocidade2,Sensor3,115267\n");
            //writer.append("Rota3,Velocidade2,Sensor4,116509\n");
            //writer.append("Rota3,Velocidade2,Sensor5,120117\n");
            //writer.append("Rota3,Velocidade2,Sensor6,113394\n");
            //writer.append("Rota3,Velocidade2,Sensor1,0\n");
            //writer.append("Rota3,Velocidade2,Sensor2,114002\n");
            //writer.append("Rota3,Velocidade2,Sensor3,116635\n");
            //writer.append("Rota3,Velocidade2,Sensor4,119024\n");
            //writer.append("Rota3,Velocidade2,Sensor5,121286\n");
            //writer.append("Rota3,Velocidade2,Sensor6,114635\n");
            //writer.append("Rota3,Velocidade1,Sensor1,0\n");
            //writer.append("Rota3,Velocidade1,Sensor2,230620\n");
            //writer.append("Rota3,Velocidade1,Sensor3,232154\n");
            //writer.append("Rota3,Velocidade1,Sensor4,237847\n");
            //writer.append("Rota3,Velocidade1,Sensor5,241927\n");
            //writer.append("Rota3,Velocidade1,Sensor6,235322\n");
            //writer.append("Rota3,Velocidade1,Sensor1,0\n");
            //writer.append("Rota3,Velocidade1,Sensor2,233144\n");
            //writer.append("Rota3,Velocidade1,Sensor3,234020\n");
            //writer.append("Rota3,Velocidade1,Sensor4,239497\n");
            //writer.append("Rota3,Velocidade1,Sensor5,240682\n");
            //writer.append("Rota3,Velocidade1,Sensor6,239715\n");

//
//            writer.flush();
//            Log.d("SensorDataSaver", "Dados padrão salvos com sucesso no arquivo CSV.");
//
//        } catch (IOException e) {
//            Log.e("SensorDataSaver", "Erro ao salvar os dados padrão no arquivo CSV.", e);
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    Log.e("SensorDataSaver", "Erro ao fechar o FileWriter.", e);
//                }
//            }
//        }
//    }
