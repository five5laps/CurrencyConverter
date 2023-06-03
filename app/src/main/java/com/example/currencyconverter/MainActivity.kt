package com.example.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.example.currencyconverter.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "RUB"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    lateinit var bind : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        spinnerSetup();
        textChanged();
    }

    private fun textChanged(){
        bind.etFirstConversion.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }

            override fun afterTextChanged(s: Editable?) {
                try{
                    getApiResult()
                    Log.d("Debug", "Api called")
                } catch (e: Exception){
                    Log.e("Main", "$e")
                }
            }
        })
    }

    //get API result
    private fun getApiResult(){
        if(bind.etFirstConversion.text.isNotEmpty() && bind.etFirstConversion.text.isNotBlank()){
           val API = "https://api.exchangerate.host/convert?from=$baseCurrency&to=$convertedToCurrency"
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val apiResult = URL(API).readText()
                    val jsonObject = JSONObject(apiResult)

                    conversionRate = jsonObject.getJSONObject("info").getString("rate").toFloat()

                    Log.d("Main", "Current rate is $conversionRate")

                    Log.d("Main", "$conversionRate")
                    Log.d("Main", apiResult)

                    withContext(Dispatchers.Main){
                        val text = ((bind.etFirstConversion.text.toString().toFloat()) * conversionRate).toString()
                        bind.etSecondConversion.setText(text)
                    }
                } catch (e: Exception){
                    Log.e("Main", "$e")
                }
            }
        }
    }

    private fun spinnerSetup(){

        ArrayAdapter.createFromResource(
            this,
            R.array.curriencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            bind.spinnerFirstConversion.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.curriencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            bind.spinnerSecondConversion.adapter = adapter
        }

        bind.spinnerFirstConversion.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        bind.spinnerSecondConversion.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

    }
}