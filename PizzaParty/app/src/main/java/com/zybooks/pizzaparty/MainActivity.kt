package com.zybooks.pizzaparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.EditText
import android.widget.TextView
import kotlin.math.ceil

const val SLICES_PER_PIZZA = 8

/**
 * The main activity contains on create function that is invoked
 * first when app opens.
 * @author Melrick
 */

class MainActivity : AppCompatActivity() {
    private lateinit var numAttendEditText: EditText
    private lateinit var numPizzasTextView: TextView
    private lateinit var howHungryRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numAttendEditText = findViewById(R.id.num_attend_edit_text)
        numPizzasTextView = findViewById(R.id.num_pizzas_text_view)
        howHungryRadioGroup = findViewById(R.id.hungry_radio_group)

    }

    fun calculateClick(view: View) {
        /**
         * Calculates the number of pizza needed for the party.
         * Assumes that each person eats 2,3 or 4 as per hunger.
         * @return Total number of pizzas
         */

        /**
         * Get the text that was typed into the EditText
         */
        val numAttendStr = numAttendEditText.text.toString()

        /**
         * Convert the text into an integer
         */
        val numAttend = numAttendStr.toInt()

        /**
         * Determine how many slices on average each person will eat
         */
        val slicesPerPerson = when (howHungryRadioGroup.checkedRadioButtonId) {
            R.id.light_radio_button -> 2
            R.id.medium_radio_button -> 3
            else -> 4
        }

        /**
         * Calculate and show the number of pizzas needed
         */
        val totalPizzas = ceil(numAttend * slicesPerPerson / SLICES_PER_PIZZA.toDouble()).toInt()
        numPizzasTextView.text = "Total pizzas: $totalPizzas"
    }
}