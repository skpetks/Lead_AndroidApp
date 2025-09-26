package com.innovu.visitor.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.innovu.visitor.R
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class QrScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private var torchOn = false
    private var currentOtp: String = ""
    private lateinit var btn_Submit: AppCompatButton
    private lateinit var btn_Cancel: AppCompatButton

    private lateinit var otpFields: List<EditText>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            return
        }

        setContentView(R.layout.activity_qrscanner)
        getSupportActionBar()!!.hide();
        barcodeView = findViewById(R.id.barcode_scanner)
        btn_Submit = findViewById(R.id.btn_submit)
        btn_Cancel = findViewById(R.id.btn_cancel)

        val formats = listOf(BarcodeFormat.QR_CODE)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)

        barcodeView.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                Toast.makeText(this@QrScannerActivity, "Result: ${result.text}", Toast.LENGTH_LONG).show()

                barcodeView.pause() // pause before finishing

                val data = Intent()
                data.putExtra("qr_result", result.text)
                setResult(RESULT_OK, data)
                finish()
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
        val flashButton = findViewById<ImageButton>(R.id.btnFlash)
        flashButton.setOnClickListener {
            torchOn = !torchOn
//            barcodeView.setTorch(torchOn)
        }
        setupOtpInputs()

        btn_Submit.setOnClickListener {
            val data = Intent()
            data.putExtra("qr_result", currentOtp)
            setResult(RESULT_OK, data)
            finish()
        }
        btn_Cancel.setOnClickListener {
            finish()
        }

    }

    private fun setupOtpInputs( ) {
        otpFields = listOf(
            findViewById(R.id.otp_1),
            findViewById(R.id.otp_2),
            findViewById(R.id.otp_3),
            findViewById(R.id.otp_4),
            findViewById(R.id.otp_5),
            findViewById(R.id.otp_6)
        )

        for (i in otpFields.indices) {
            val editText = otpFields[i]

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpFields.size - 1) {
                        otpFields[i + 1].requestFocus()
                    }

                    // ✅ Check if all 6 digits are filled
                    if (i == otpFields.lastIndex) {
                        val isComplete = otpFields.all { it.text.toString().trim().length == 1 }
                        if (isComplete) {
                            val otp = otpFields.joinToString("") { it.text.toString().trim() }
                            Toast.makeText(this@QrScannerActivity, "OTP Entered: $otp", Toast.LENGTH_SHORT).show()
                            currentOtp = otp // 🔥 Store globally
                            // TODO: Call API or handle OTP logic here
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.text.isEmpty() && i > 0) {
                        otpFields[i - 1].requestFocus()
                    }
                }
                false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}
