package com.zybooks.todolist

import ToDoList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.Manifest

const val SHAKE_THRESHOLD = 600

class MainActivity : AppCompatActivity(), SensorEventListener, DeleteDialog.OnYesClickListener{
    private var toDoList = ToDoList(this)
    private lateinit var itemEditText: EditText
    private lateinit var listTextView: TextView
    private lateinit var soundEffects: SoundEffects

    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemEditText = findViewById(R.id.todo_item)
        listTextView = findViewById(R.id.item_list)
        registerForContextMenu(listTextView)
        soundEffects = SoundEffects.getInstance(applicationContext)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        findViewById<Button>(R.id.add_button).setOnClickListener { addButtonClick() }
        findViewById<Button>(R.id.clear_button).setOnClickListener { clearButtonClick() }
        findViewById<Button>(R.id.location_button).setOnClickListener { locationButtonClick() }
    }

    override fun onCreateContextMenu(menu: ContextMenu?,
                                     v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                clearToDoList()
                true
            }
            R.id.get_location -> {
                    findLocation()
                    true
            }
            else -> super.onContextItemSelected(item)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        soundEffects.release()
    }

    override fun onResume() {
        super.onResume()

        // Attempt to load a previously saved list
        toDoList.readFromFile()
        displayList()

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()

        // Save list for later
        toDoList.saveToFile()
        sensorManager.unregisterListener(this, accelerometer)
    }

    private fun addButtonClick() {

        // Ignore any leading or trailing spaces
        val item = itemEditText.text.toString().trim()

        // Clear the EditText so it's ready for another item
        itemEditText.setText("")

        // Add the item to the list and display it
        if (item.isNotEmpty()) {
            soundEffects.playTone()
            toDoList.addItem(item)
            displayList()
        }
    }

    private fun displayList() {

        // Display a numbered list of items
        val itemText = StringBuffer()
        val items = toDoList.getItems()
        val lineSeparator = System.getProperty("line.separator")

        for (i in items.indices) {
            itemText.append(i + 1).append(". ").append(items[i]).append(lineSeparator)
        }

        listTextView.text = itemText.toString()
    }

     override fun onYesClick() {
        clearToDoList()
    }

    fun clearToDoList() {
        toDoList.clear()
        displayList()
    }

    private fun clearButtonClick() {
        val dialog = DeleteDialog()
        dialog.show(supportFragmentManager, "warningDialog")
    }

    private fun locationButtonClick() {
        if (hasLocationPermission()) {
            findLocation()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Get accelerometer values
        val x: Float = event.values[0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]

        // Get magnitude of acceleration
        val currentAcceleration: Float = x * x + y * y + z * z

        // Calculate difference between two readings
        val delta = currentAcceleration - lastAcceleration

        // Save for next time
        lastAcceleration = currentAcceleration

        // Detect shake
        if (abs(delta) > SHAKE_THRESHOLD) {
            onYesClick()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nothing to do
    }

    @SuppressLint("MissingPermission")
    private fun findLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)

        client.lastLocation
            .addOnSuccessListener(this) { location ->
                Toast.makeText(applicationContext, "location = $location", Toast.LENGTH_SHORT).show()
                 }
    }

    private fun hasLocationPermission(): Boolean {

        // Request fine location permission if not already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }
        return true
    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission())
    { isGranted: Boolean ->
        if (isGranted) {
            findLocation()
        }
    }
}