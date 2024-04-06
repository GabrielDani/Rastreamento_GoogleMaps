package br.com.gabrieldani.maps.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.gabrieldani.maps.model.Region;
import br.com.gabrieldani.maps.utils.DistanceCalculator;
import br.com.gabrieldani.maps.utils.XMLHelper;

public class FirebaseHelper {
    private final Context context;
    private static final String TAG = "FirebaseHelper";

    // Referência ao nó do Firebase Realtime Database onde as regiões serão armazenadas
    private final DatabaseReference regionsRef;

    // Construtor
    public FirebaseHelper(Context context) {
        this.context = context;
        // Obtém a referência ao nó "regions" do Firebase Realtime Database
        regionsRef = FirebaseDatabase.getInstance().getReference().child("regions");
    }

    // Método para adicionar uma região ao Firebase Realtime Database após verificar a distância mínima
    public void addRegion(Region region) {
        new Thread(() -> {
            getAllRegions(new GetRegionsCallback() {
                @Override
                public void onRegionsLoaded(List<Region> regions) {
                    boolean withinRadius = false;
                    // Verifica a distância entre a nova região e todas as regiões existentes no banco de dados
                    for (Region existingRegion : regions) {
                        // Verifica se a distância entre as regiões é menor que 30 metros
                        if (DistanceCalculator.isWithinRadius(existingRegion, region)) {
                            withinRadius = true;
                            break;
                        }
                    }
                    // Se a nova região estiver dentro do raio de alguma região existente, não adiciona ao banco de dados
                    if (withinRadius) {
                        String message = "Há uma outra região próxima no Banco de Dados.";
                        Log.d(TAG, message);
                        XMLHelper.showShortToast(context, message);
                    } else {
                        // Gera uma chave única para a região
                        String regionId = regionsRef.push().getKey();
                        if (regionId == null) {
                            Log.e(TAG, "Falha ao gerar a chave para a região.");
                            return;
                        }

                        // Define a região no nó correspondente com a chave gerada
                        regionsRef.child(regionId).setValue(region)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String message = "Região adicionada ao Banco de Dados.";
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
            });
        }).start();
    }



    // Método para obter todas as regiões do Firebase Realtime Database
    public void getAllRegions(GetRegionsCallback callback) {
        regionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Region> regions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Region region = snapshot.getValue(Region.class);
                    if (region != null) {
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

    // Interface de callback para notificar o resultado da operação de obtenção de regiões
    public interface GetRegionsCallback {
        void onRegionsLoaded(List<Region> regions);

        void onLoadFailed(String errorMessage);
    }
}

