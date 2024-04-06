package br.com.gabrieldani.maps.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import br.com.gabrieldani.maps.model.Region;
import br.com.gabrieldani.maps.repository.FirebaseHelper;
import br.com.gabrieldani.maps.utils.XMLHelper;

public class RegionQueueManager {
    private final BlockingQueue<Region> regionQueue;
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
        Region region = new Region(latitude, longitude);

        // Inicia uma nova thread para adicionar a região à fila
        new Thread(() -> {
            try {
                // Adquire uma permissão do semáforo
                semaphore.acquire();
                // Adiciona a região à fila
                regionQueue.put(region);
                // Mensagem para usuário
                String message = region.getName() + " adicionada à fila";
                // Exibir o Toast na UI thread usando um Handler
                handler.post(() -> XMLHelper.showLongToast(context, message));
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
                    Region lastRegion = regionQueue.peek(); // Obtém a última região da fila
                    assert lastRegion != null;
                    firebaseHelper.addRegion(lastRegion); // Adiciona a última região ao banco de dados
                    regionQueue.poll(); // Remove a região da fila após salvar no banco de dados
                    // Mensagem para o usuário
                    String message = lastRegion.getName() + " removida da fila";
                    Log.d(TAG, message);
                    semaphore.release(); // Libera a permissão do semáforo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start(); // Inicia a thread
        } else {
            // Se a fila estiver vazia, exibe uma mensagem ao usuário
            String emptyQueueMessage = "A fila está vazia. Não há regiões para salvar no banco de dados.";
            // Exibe o Toast na UI thread usando um Handler
            handler.post(() -> XMLHelper.showLongToast(context, emptyQueueMessage));
            Log.d(TAG, emptyQueueMessage);
        }
    }

}
