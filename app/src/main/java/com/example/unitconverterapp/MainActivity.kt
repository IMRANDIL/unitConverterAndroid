package com.example.unitconverterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitconverterapp.ui.theme.UnitConverterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConverterAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UnitConverterScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen() {
    val context = LocalContext.current
    val conversionTypes = listOf("None", "Temperature", "Length", "Weight")

    var selectedConversionType by remember { mutableStateOf("None") }
    var fromUnit by remember { mutableStateOf("") }
    var toUnit by remember { mutableStateOf("") }
    var inputValue by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    var unitPrice by remember { mutableStateOf("") }
    var givenAmount by remember { mutableStateOf("") }
    var calculatedPrice by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val unitOptions = when (selectedConversionType) {
        "Temperature" -> listOf("Celsius", "Fahrenheit")
        "Length" -> listOf("Meters", "Feet", "Inches", "Kilometers", "Miles")
        "Weight" -> listOf("Kilograms", "Pounds", "Grams")
        else -> emptyList()
    }

    val isConvertEnabled = selectedConversionType != "None"
            && inputValue.isNotBlank()
            && fromUnit.isNotBlank()
            && toUnit.isNotBlank()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Icon(
            imageVector = Icons.Default.Straighten,
            contentDescription = "Converter Icon",
            modifier = Modifier.size(60.dp),
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Smart Unit Converter",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        DropdownMenuSelector("Conversion Type", conversionTypes, selectedConversionType) {
            selectedConversionType = it
            fromUnit = ""
            toUnit = ""
            result = ""
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedConversionType != "None") {
            DropdownMenuSelector("From", unitOptions, fromUnit) { fromUnit = it }
            Spacer(modifier = Modifier.height(8.dp))
            DropdownMenuSelector("To", unitOptions, toUnit) { toUnit = it }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Enter value") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                try {
                    result = convert(inputValue.toDouble(), fromUnit, toUnit, selectedConversionType)
                } catch (e: NumberFormatException) {
                    result = "Invalid input"
                }
            },
            enabled = isConvertEnabled,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
        ) {
            Text("Convert", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (result.isNotEmpty()) {
            Text("Result: $result", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))
        Divider(color = Color.White.copy(alpha = 0.4f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Price Calculator", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = unitPrice,
            onValueChange = { unitPrice = it },
            label = { Text("Enter 1kg or 1L price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = givenAmount,
            onValueChange = { givenAmount = it },
            label = { Text("Enter given amount (grams/ml)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                try {
                    val pricePerKg = unitPrice.toDouble()
                    val gramsOrMl = givenAmount.toDouble()
                    val calculated = (gramsOrMl / 1000) * pricePerKg
                    calculatedPrice = String.format("%.2f", calculated)
                } catch (e: NumberFormatException) {
                    calculatedPrice = "Invalid input"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
        ) {
            Text("Calculate Price", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (calculatedPrice.isNotEmpty()) {
            Text("Price: ₹$calculatedPrice", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Made with ❤️ by IMRANDIL",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "github.com/imrandil",
            fontSize = 14.sp,
            color = Color(0xFFBBDEFB),
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/imrandil"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelectionChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun convert(value: Double, from: String, to: String, type: String): String {
    val result = when (type) {
        "Temperature" -> when (from to to) {
            "Celsius" to "Fahrenheit" -> (value * 9 / 5) + 32
            "Fahrenheit" to "Celsius" -> (value - 32) * 5 / 9
            else -> value
        }

        "Length" -> {
            val meters = when (from) {
                "Meters" -> value
                "Feet" -> value / 3.28084
                "Inches" -> value / 39.3701
                "Kilometers" -> value * 1000
                "Miles" -> value * 1609.34
                else -> value
            }
            when (to) {
                "Meters" -> meters
                "Feet" -> meters * 3.28084
                "Inches" -> meters * 39.3701
                "Kilometers" -> meters / 1000
                "Miles" -> meters / 1609.34
                else -> value
            }
        }

        "Weight" -> {
            val kilograms = when (from) {
                "Kilograms" -> value
                "Pounds" -> value / 2.20462
                "Grams" -> value / 1000
                else -> value
            }
            when (to) {
                "Kilograms" -> kilograms
                "Pounds" -> kilograms * 2.20462
                "Grams" -> kilograms * 1000
                else -> value
            }
        }

        else -> value
    }

    return String.format("%.2f %s", result, to)
}
