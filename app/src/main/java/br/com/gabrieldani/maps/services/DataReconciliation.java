package br.com.gabrieldani.maps.services;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.gabrieldani.maps.utils.SensorDataSaver;

public class DataReconciliation {

    public static class FlowMeter {
        private final LatLng location;
        private long startTime;
        private long endTime;
        private final String sensorName;

        FlowMeter(LatLng location, int number) {
            this.location = location;
            this.sensorName = "Sensor" + number;
        }

        void start() {
            this.startTime = System.currentTimeMillis();
            Log.d("FlowMeter", "Iniciado em: " + this.startTime);
        }

        void stop() {
            this.endTime = System.currentTimeMillis();
            Log.d("FlowMeter", "Parado em: " + this.getElapsedTime());
        }

        public long getElapsedTime() {
            return endTime - startTime;
        }

        public String getSensorName(){
            return sensorName;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

    }

    private final List<FlowMeter> flowMeters;
    private FlowMeter currentFlowMeter;
    private boolean routeStarted;
    private long routeStartTime;
    private long routeEndTime;
    private final SensorDataSaver sensorDataSaver;

    public DataReconciliation(Context context, List<LatLng> flowMeterLocations, SensorDataSaver sensorDataSaver) {
        this.flowMeters = new ArrayList<>();
        int i = 1;
        for (LatLng location : flowMeterLocations) {
            i++;
            if(i > 6) {
                i = 1;
            }
            this.flowMeters.add(new FlowMeter(location, i));
        }
        this.routeStarted = false;
        this.sensorDataSaver = sensorDataSaver;
    }

    // Método chamado quando a localização é atualizada
    public void updateLocation(LatLng currentLocation) {
        // Verifica se a rota já foi iniciada
        if (!routeStarted) {
            // Verifica se o usuário está próximo do primeiro FlowMeter para iniciar a rota
            FlowMeter firstMeter = flowMeters.get(0);
            if (isNear(currentLocation, firstMeter.location)) {
                startRoute();
                currentFlowMeter = firstMeter;
                currentFlowMeter.start(); // Inicia o medidor do primeiro ponto
            }
        } else {
            // Verifica se o usuário passou por um medidor de fluxo e atualiza o estado
            for (FlowMeter meter : flowMeters) {
                if (isNear(currentLocation, meter.location)) {
                    if (!meter.equals(currentFlowMeter)) {
                        if (currentFlowMeter != null) {
                            currentFlowMeter.stop(); // Finaliza o medidor anterior
                        }
                        currentFlowMeter = meter;
                        currentFlowMeter.start(); // Inicia o novo medidor

                        // Verifica se é o último FlowMeter para finalizar a rota
                        if (meter.equals(flowMeters.get(flowMeters.size() - 1))) {
                            endRoute();
                        }
                    }
                }
            }
        }
    }

    // Inicia a contagem do tempo de rota
    private void startRoute() {
        Log.d("FlowMeter", "ROTA GERAL INICIADA");
        routeStartTime = System.currentTimeMillis();
        routeStarted = true;
    }

    // Finaliza a rota e armazena o tempo total
    private void endRoute() {
        routeEndTime = System.currentTimeMillis();
        routeStarted = false;

        if (currentFlowMeter != null) {
            currentFlowMeter.stop(); // Finaliza o medidor atual
        }

        // Aqui você pode calcular a variância, média e outros dados que desejar
        Log.d("FlowMeter", "ROTA GERAL FINALIZADA. Tempo total da rota: " + getTotalRouteTime() + " ms");

        sortFlowMetersByName();

        // Salva os dados dos sensores no arquivo CSV
        sensorDataSaver.saveData(flowMeters);
        sensorDataSaver.readData();
    }

    // Método para verificar se a localização atual está próxima de um ponto específico
    private boolean isNear(LatLng currentLocation, LatLng targetLocation) {
        // Define uma margem de erro para considerar o ponto como atingido
        double threshold = 0.0005; // Ajuste conforme necessário
        return Math.abs(currentLocation.latitude - targetLocation.latitude) < threshold &&
                Math.abs(currentLocation.longitude - targetLocation.longitude) < threshold;
    }

    // Retorna o tempo total da rota
    public long getTotalRouteTime() {
        return routeEndTime - routeStartTime;
    }

    // Exemplo de cálculo de variância entre os tempos dos medidores
    public double calculateVariance() {
        long mean = getTotalRouteTime() / flowMeters.size();
        long sumSquaredDiffs = 0;
        for (FlowMeter meter : flowMeters) {
            long diff = meter.getElapsedTime() - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / (double) flowMeters.size();
    }

    // Método para ordenar a lista de FlowMeters por nome
    public void sortFlowMetersByName() {
        flowMeters.sort(new Comparator<FlowMeter>() {
            @Override
            public int compare(FlowMeter fm1, FlowMeter fm2) {
                return fm1.getSensorName().compareTo(fm2.getSensorName());
            }
        });
    }
}
