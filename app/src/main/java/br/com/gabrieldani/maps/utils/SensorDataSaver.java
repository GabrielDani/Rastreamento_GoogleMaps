package br.com.gabrieldani.maps.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.gabrieldani.maps.services.DataReconciliation;

public class SensorDataSaver {

    private final File file;

    // Construtor que define o caminho e o nome do arquivo CSV
    public SensorDataSaver(Context context, String fileName) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        file = new File(directory, fileName);
    }

    // Método para verificar se o arquivo já existe
    private boolean fileExists() {
        return file.exists();
    }

    // Método para salvar os dados dos sensores no arquivo CSV
    public void saveData(List<DataReconciliation.FlowMeter> flowMeters) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true); // 'true' para adicionar novos dados ao invés de sobrepor
            if (!fileExists()) {
                // Escreve o cabeçalho apenas se o arquivo ainda não existir
                writer.append("Rota,Velocidade,Sensor,End Time\n");
            }

            for (DataReconciliation.FlowMeter meter : flowMeters) {
                writer.append("Rota2,Velocidade1,");
                writer.append(String.valueOf(meter.getSensorName())).append(",");
                writer.append(String.valueOf(meter.getElapsedTime())).append("\n");
            }
            writer.append("------------");

            writer.flush();
            Log.d("SensorDataSaver", "Dados salvos com sucesso no arquivo CSV.");

        } catch (IOException e) {
            Log.e("SensorDataSaver", "Erro ao salvar dados no arquivo CSV.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e("SensorDataSaver", "Erro ao fechar o FileWriter.", e);
                }
            }
        }
    }

    // Método para ler os dados do arquivo CSV e imprimir no log
    public void readData() {
        if (fileExists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("SensorDataSaver", line);
                }
            } catch (IOException e) {
                Log.e("SensorDataSaver", "Erro ao ler dados do arquivo CSV.", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("SensorDataSaver", "Erro ao fechar o BufferedReader.", e);
                    }
                }
            }
        } else {
            Log.d("SensorDataSaver", "O arquivo não existe.");
        }
    }

    // Método para deletar o arquivo CSV
    public boolean deleteDataFile() {
        if (fileExists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.d("SensorDataSaver", "Arquivo CSV deletado com sucesso.");
            } else {
                Log.e("SensorDataSaver", "Erro ao deletar o arquivo CSV.");
            }
            return deleted;
        } else {
            Log.d("SensorDataSaver", "O arquivo não existe, não é necessário deletar.");
            return false;
        }
    }

    // Método para ler os dados do arquivo CSV e retornar uma lista de mapas (dicionários)
    public List<Map<String, String>> readDataAsList() {
        List<Map<String, String>> dataList = new ArrayList<>();
        if (fileExists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line;
                boolean isHeader = true;
                String[] headers = null;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        headers = line.split(",");
                        isHeader = false;
                    } else {
                        String[] values = line.split(",");
                        if (values.length == headers.length) {
                            Map<String, String> dataMap = new HashMap<>();
                            for (int i = 0; i < headers.length; i++) {
                                dataMap.put(headers[i], values[i]);
                            }
                            dataList.add(dataMap);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("SensorDataSaver", "Erro ao ler dados do arquivo CSV.", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("SensorDataSaver", "Erro ao fechar o BufferedReader.", e);
                    }
                }
            }
        } else {
            Log.d("SensorDataSaver", "O arquivo não existe.");
        }
        return dataList;
    }
}

