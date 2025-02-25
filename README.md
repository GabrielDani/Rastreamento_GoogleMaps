# Aplicativo de Rastreamento e Monitoramento com Google Maps

## Descrição

Este projeto é um aplicativo Android que utiliza o **Google Maps** para monitoramento de localização e gestão de sensores de fluxo em uma rota definida. Ele exibe a posição do usuário em um mapa, registra pontos de interesse e permite salvar dados em um banco de dados utilizando **Firebase**.

## Funcionalidades

- Exibição do mapa com a localização atual do usuário.
- Registro de pontos de medição de fluxo ao longo de uma rota.
- Armazenamento de dados de sensores em **arquivos CSV**.
- Sincronização e reconciliação de dados usando Firebase.
- Gerenciamento de permissões de localização no Android.

## Tecnologias Utilizadas

- **Android SDK**
- **Google Maps API**
- **Firebase (Realtime Database)**
- **Java**

## Estrutura do Projeto

O código é organizado em módulos principais:

- `MainActivity.java`: Responsável por exibir o mapa, capturar a localização do usuário e gerenciar eventos do aplicativo.
- `LocationHelper.java`: Gerencia a obtenção de localização.
- `FirebaseHelper.java`: Manipula a interação com o Firebase.
- `SensorDataSaver.java`: Lida com leitura e gravação de dados de sensores.
- `XMLHelper.java`: Atualiza elementos da interface do usuário.

## Como Executar o Projeto

### 1. Configurar o Ambiente

- Certifique-se de ter o **Android Studio** instalado.
- Obtenha uma chave de API do **Google Maps** e configure no `AndroidManifest.xml`.

### 2. Clonar o Repositório
```bash
    git clone https://github.com/GabrielDaniAz/Avaliacao1_Avancada.git
    cd Avaliacao1_Avancada
```

### 3. Executar o Projeto

1. Abra o projeto no **Android Studio**.
2. Conecte um dispositivo Android ou inicie um emulador.
3. Compile e execute o aplicativo.