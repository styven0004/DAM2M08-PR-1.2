package com.styvencatagua.pr12_jocendevinaelnmero_repo

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Random

class MainActivity : AppCompatActivity() {

    // Propietats per gestionar l'estat del joc
    private var numeroSecret: Int = 0
    private var intents: Int = 0

    // Vistes
    private lateinit var etNumero: EditText
    private lateinit var b: Button
    private lateinit var tvHistorial: TextView
    private lateinit var tvIntents: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialitzar vistes
        etNumero = findViewById(R.id.etNumero)
        b = findViewById(R.id.button)
        tvHistorial = findViewById(R.id.tvHistorial)
        tvIntents = findViewById(R.id.tvIntents)
        scrollView = findViewById(R.id.scrollView)

        // Començar una nova partida generant el número aleatori inicial
        reiniciarJoc()

        // Listener del botó
        b.setOnClickListener {
            processarIntent()
        }

        // Listener per detectar la tecla ENTER del teclat en pantalla (evita tancar el teclat i duplicats)
        etNumero.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == android.view.KeyEvent.ACTION_DOWN && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                processarIntent()
                true // Indica que hem gestionat l'esdeveniment
            } else {
                false
            }
        }
    }

    /**
     * Genera un número aleatori entre 1 i 100 utilitzant java.util.Random
     */
    private fun generarNumeroAleatori(): Int {
        val random = Random()
        return random.nextInt(100) + 1
    }

    /**
     * Comprova la resposta de l'usuari i actualitza la interfície
     */
    private fun processarIntent() {
        val textEntrat = etNumero.text.toString()

        if (textEntrat.isEmpty()) {
            Toast.makeText(this, "S'il us plau, entra un número", Toast.LENGTH_SHORT).show()
            return
        }

        val numeroUsuari = textEntrat.toInt()
        intents++
        tvIntents.text = "Intents: $intents"

        if (numeroUsuari == numeroSecret) {
            // L'usuari ha encertat
            mostrarDialogVictoria()
        } else {
            val missatge = if (numeroUsuari < numeroSecret) {
                "El número buscat és MAJOR que $numeroUsuari"
            } else {
                "El número buscat és MENOR que $numeroUsuari"
            }

            // Mostrar Toast amb el resultat
            Toast.makeText(this, missatge, Toast.LENGTH_SHORT).show()

            // Actualitzar l'historial de respostes
            tvHistorial.append("Intent $intents: $numeroUsuari -> $missatge\n")

            // Netejar la casella d'entrada i demanar el focus de nou
            etNumero.text.clear()
            etNumero.requestFocus()

            // Desplaçar el ScrollView cap al final de forma actualitzada
            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    /**
     * Mostra l'AlertDialog quan s'acaba la partida i demana tornar a jugar
     */
    private fun mostrarDialogVictoria() {
        AlertDialog.Builder(this)
            .setTitle("Felicitats!")
            .setMessage("Molt bé! Vas encertar el número $numeroSecret en $intents intents.")
            .setCancelable(false)
            .setPositiveButton("Tornar a jugar") { dialog, _ ->
                dialog.dismiss()
                reiniciarJoc()
            }
            .show()
    }

    /**
     * Reinicia els comptadors i el número aleatori per començar una partida nova
     */
    private fun reiniciarJoc() {
        numeroSecret = generarNumeroAleatori()
        intents = 0
        tvIntents.text = "Intents: 0"
        tvHistorial.text = ""
        etNumero.text.clear()
        etNumero.requestFocus()
    }
}