package com.example.tresenraya;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    boolean gameActive = true;
    private NetworkService networkService;

    public EditText serverIP;
    public EditText serverPort;
    public Button connectButton;

    public TextView status;

    public void playerTap(View view) {

        if (!gameActive) {
            return;
        }
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        // Aquí se supone que actualizas la UI con el movimiento del jugador.
        // Asegúrate de que este movimiento sea legal antes de enviarlo al servidor.


        updateGameState(tappedImage, 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    networkService.sendMove(tappedImage);
                    // Aquí recibes la respuesta del servidor.
                    String serverResponse = networkService.getServerMove();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Comprueba si el servidor ha ganado.
                            if (serverResponse.startsWith("Winner:")) {
                                // El servidor ha ganado. Muestra un mensaje al usuario y termina el juego.
                                String winner = serverResponse.substring(8);
                                status.setText("Ganador: " + winner);
                                gameActive = false;
                            }
                            // Comprueba si el juego es un empate.
                            else if (serverResponse.equals("Draw") || serverResponse.equals("No moves left")) {
                                // El juego es un empate. Muestra un mensaje al usuario y termina el juego.
                                status.setText("Empate");
                                gameActive = false;
                            }
                            // Comprueba si el servidor ha hecho su jugada.
                            else if (serverResponse.startsWith("Server moved")) {
                                // Actualiza el estado del juego según la jugada del servidor.
                                String[] parts = serverResponse.split(" ");
                                int serverMove = Integer.parseInt(parts[2].replace(".", ""));
                                updateGameState(serverMove, 1);
                            }
                            // Si ninguna de las condiciones anteriores se cumple, la respuesta del servidor es inválida.
                            else {
                                // Muestra un mensaje de error al usuario.
                                // ...
                            }
                        }
                    });
                } catch (IOException e) {
                    // Manejo de error al enviar el movimiento o recibir la respuesta del servidor.
                }
            }
        }).start();

    }
    public void updateGameState(int position, int player) {
        if (!gameActive) {
            return;
        }

        ImageView img = null;
        switch(position){
            case 0:
                img = findViewById(R.id.imageView0);
                break;
            case 1:
                img = findViewById(R.id.imageView1);
                break;
            case 2:
                img = findViewById(R.id.imageView2);
                break;
            case 3:
                img = findViewById(R.id.imageView3);
                break;
            case 4:
                img = findViewById(R.id.imageView4);
                break;
            case 5:
                img = findViewById(R.id.imageView5);
                break;
            case 6:
                img = findViewById(R.id.imageView6);
                break;
            case 7:
                img = findViewById(R.id.imageView7);
                break;
            case 8:
                img = findViewById(R.id.imageView8);
                break;
            default:
                // manejo de errores, por ejemplo:
                throw new IllegalArgumentException("Posición no válida: " + position);
        }
        // Establece la imagen adecuada dependiendo de si el jugador es X o O.
        if (player == 0) {
            img.setImageResource(R.drawable.x);
        } else {
            img.setImageResource(R.drawable.o);
        }



    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            networkService.closeConnection();
        } catch (IOException e) {
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverIP = findViewById(R.id.ED_ip);
        serverPort = findViewById(R.id.ED_port);
        connectButton = findViewById(R.id.btn_conn);
        status = findViewById(R.id.status);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = serverIP.getText().toString();
                int port = Integer.parseInt(serverPort.getText().toString());
                networkService = new NetworkService(ip, port);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            networkService.connect();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Cambia el estado del botón a "Conectado" o muestra una notificación.
                                    connectButton.setText("Conectado");
                                    connectButton.setEnabled(false);  // Deshabilita el botón
                                    serverIP.setEnabled(false);  // Deshabilita el EditText para la IP
                                    serverPort.setEnabled(false);  // Deshabilita el EditText para el puerto
                                    status.setText("Conectado");
                                }
                            });

                        } catch (IOException e) {
                            // Manejo de error al intentar conectar.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Muestra un mensaje de error al usuario.
                                    Toast.makeText(MainActivity.this, "Error al conectarse: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    connectButton.setEnabled(true);
                                    serverIP.setEnabled(true);
                                    serverPort.setEnabled(true);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
