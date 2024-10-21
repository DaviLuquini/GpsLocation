package com.example.trabalhogps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//Componente 1 - Posição do usuário
public class CoordinateFormatDialog {

    public interface FormatSelectionListener {
        void onFormatSelected(String format);
    }

    public static void showFormatSelectionDialog(Context context, FormatSelectionListener listener) {
        final String[] formats = {
                "Graus [+/-DDD.DDDDD]",
                "Graus-Minutos [+/-DDD:MM.MMMMM]",
                "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Escolha o formato de apresentação das coordenadas")
                .setItems(formats, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Notifica a atividade que um formato foi selecionado
                        listener.onFormatSelected(formats[which]);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
