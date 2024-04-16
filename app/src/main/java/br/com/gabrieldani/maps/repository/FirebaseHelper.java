package br.com.gabrieldani.maps.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.gabrieldani.maps.model.Region;
import br.com.gabrieldani.maps.model.RestrictedRegion;
import br.com.gabrieldani.maps.model.SubRegion;
import br.com.gabrieldani.maps.utils.XMLHelper;
import br.com.gabrieldani.mylibrary.CryptoHelper;

public class FirebaseHelper {
    private final Context context;
    private static final String TAG = "FirebaseHelper";

    // Referência ao nó do Firebase Realtime Database onde as regiões serão armazenadas
    private final DatabaseReference regionsRef;
    private static boolean isRestricted;

    // Construtor
    public FirebaseHelper(Context context) {
        this.context = context;
        // Obtém a referência ao nó "regions" do Firebase Realtime Database
        regionsRef = FirebaseDatabase.getInstance().getReference().child("regions");
    }

    // Método para adicionar uma região ao Firebase Realtime Database após verificar a distância mínima
    public void addRegion(String regionEncryptedJson) {
        Log.d(TAG, "---------");
        new Thread(() -> getAllRegions(new GetRegionsCallback() {
            @Override
            public void onRegionsLoaded(List<Region> regions) {
                String regionDecryptedJson = CryptoHelper.decrypt(regionEncryptedJson);
                Gson gson = new Gson();
                Region region = gson.fromJson(regionDecryptedJson, Region.class);

                Region regionToDatabase = rulesToAddRegion(region, regions);

                Log.d(TAG, "Criptografado da Fila: " + regionEncryptedJson);
                Log.d(TAG, "Descriptografado da Fila: " + regionDecryptedJson);
                Log.d(TAG, "Objeto Região: " + region);

                if (regionToDatabase == null) {
                    String message = "Há uma outra Região a menos de 5m.";
                    Log.d(TAG, message);
                    XMLHelper.showShortToast(context, message);
                } else {
                    String type = regionToDatabase.getClass().getSimpleName();
                    // Gera uma chave única para a região
                    String regionId = type + regionsRef.push().getKey();
                    gson = new Gson();
                    String regionJson = gson.toJson(regionToDatabase);
                    String encryptedJson = CryptoHelper.encrypt(regionJson);

                    Log.d(TAG, "JSON para BD: " + regionJson);
                    Log.d(TAG, "Enviando para BD: " + encryptedJson);

                    // Define a região no nó correspondente com a chave gerada
                    regionsRef.child(regionId).setValue(encryptedJson)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String message = type + " adicionada ao Banco de Dados.";
                                    Log.d(TAG, message);
                                    XMLHelper.showShortToast(context, message);
                                } else {
                                    Log.e(TAG, "Falha ao adicionar região ao Banco de Dados.", task.getException());
                                }
                            });
                }
            }

            @Override
            public void onLoadFailed(String errorMessage) {
                Log.e(TAG, "Erro ao obter regiões do Banco de Dados: " + errorMessage);
            }
        })).start();
    }

    // Método para obter todas as regiões do Firebase Realtime Database
    public void getAllRegions(GetRegionsCallback callback) {
        regionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Region> regions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String encryptedJson = snapshot.getValue(String.class);
                    if (encryptedJson != null) {
                        String decryptedJson = CryptoHelper.decrypt(encryptedJson);
                        Gson gson = new Gson();
                        Region region = gson.fromJson(decryptedJson, Region.class);
                        regions.add(region);
                    }
                }
                callback.onRegionsLoaded(regions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Erro ao obter regiões do Banco de Dados: " + databaseError.getMessage());
                callback.onLoadFailed(databaseError.getMessage());
            }
        });
    }

    private Region rulesToAddRegion(Region region, List<Region> regions) {
        Region regionToDatabase = null;
        if (regions.isEmpty()) {
            regionToDatabase = region;
        } else {
            boolean withinRadius5 = false;
            for (Region existingRegion : regions) {
                double distance = region.distance(existingRegion);
                if (distance < 5) {
                    withinRadius5 = true;
                    break;
                }
            }
            if (!withinRadius5) {
                boolean withinRadius30 = false;
                for (Region existingRegion : regions) {
                    if (!existingRegion.getName().startsWith("Region")) {
                        continue;
                    }
                    double distance = region.distance(existingRegion);
                    if (distance < 30) {
                        withinRadius30 = true;
                        if (isRestricted) {
                            regionToDatabase = new RestrictedRegion(existingRegion, region.getLatitude(), region.getLongitude());
                        } else {
                            regionToDatabase = new SubRegion(existingRegion, region.getLatitude(), region.getLongitude());
                        }
                        isRestricted = !isRestricted;
                        break;
                    }
                }
                if (!withinRadius30) {
                    regionToDatabase = region;
                }
            }

        }
        return regionToDatabase;
    }

    // Interface de callback para notificar o resultado da operação de obtenção de regiões
    public interface GetRegionsCallback {
        void onRegionsLoaded(List<Region> regions);

        void onLoadFailed(String errorMessage);
    }
}
