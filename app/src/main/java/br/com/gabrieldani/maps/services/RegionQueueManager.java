package br.com.gabrieldani.maps.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import br.com.gabrieldani.maps.model.Region;
import br.com.gabrieldani.maps.repository.FirebaseHelper;
import br.com.gabrieldani.maps.utils.XMLHelper;
import br.com.gabrieldani.mylibrary.CryptoHelper;

public class RegionQueueManager {
    private final BlockingQueue<String> regionQueue;
    private final Semaphore semaphore;
    private final Context context;
    private final Handler handler;
    private final FirebaseHelper firebaseHelper;
    private static final String TAG = "RegionQueueManager";

    public RegionQueueManager(Context context, FirebaseHelper firebaseHelper) {
        this.context = context;
        regionQueue = new LinkedBlockingQueue<>();
        semaphore = new Semaphore(1); // Inicializa com 1 permissão, permitindo apenas um processo por vez
        handler = new Handler(Looper.getMainLooper()); // Handler associado à UI thread
        this.firebaseHelper = firebaseHelper;
    }

    // Método para adicionar uma região à fila de forma assíncrona
    public void addRegionAsync(double latitude, double longitude) {
        // Cria uma nova região
        Region region = new Region("Region",latitude, longitude);
        Gson gson = new Gson();
        String regionJson = gson.toJson(region);
        String encryptedJson = CryptoHelper.encrypt(regionJson);

        // Inicia uma nova thread para adicionar a região à fila
        new Thread(() -> {
            try {
                // Adquire uma permissão do semáforo
                semaphore.acquire();
                // Adiciona a região à fila
                regionQueue.put(encryptedJson);
                Log.d(TAG, "Nova Região Criptografada na fila: " + encryptedJson);
                // Mensagem para usuário
                String message = "Região adicionada à fila";
                // Exibir o Toast na UI thread usando um Handler
                handler.post(() -> XMLHelper.showShortToast(context, message));
                // Libera a permissão do semáforo
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para salvar a última região da fila no banco de dados e removê-la da fila
    public void saveToDB() {
        // Verifica se a fila não está vazia
        if (!regionQueue.isEmpty()) {
            new Thread(() -> {
                try {
                    semaphore.acquire(); // Obtém a permissão do semáforo
                    String lastRegion = regionQueue.peek(); // Obtém a última região da fila
                    assert lastRegion != null;
                    firebaseHelper.addRegion(lastRegion); // Adiciona a última região ao banco de dados
                    regionQueue.poll(); // Remove a região da fila após salvar no banco de dados
                    semaphore.release(); // Libera a permissão do semáforo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start(); // Inicia a thread
        } else {
            // Se a fila estiver vazia, exibe uma mensagem ao usuário
            String emptyQueueMessage = "A fila está vazia.";
            // Exibe o Toast na UI thread usando um Handler
            handler.post(() -> XMLHelper.showLongToast(context, emptyQueueMessage));
            Log.d(TAG, emptyQueueMessage);
        }
    }

}