package com.innovu.visitor.utlis

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.innovu.visitor.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object  Utils {

      fun  getCurrencySymbol(currencyCode: String?): String? {
        return try {
            val currency = Currency.getInstance(currencyCode)
            currency.symbol
        } catch (e: Exception) {
            currencyCode
        }
    }

    fun getCurrencySymbolw(currencyCode: String?): String? {
        return try {
            val currency = Currency.getInstance(currencyCode)
            currency.symbol
        } catch (e: Exception) {
            currencyCode
        }
    }



    fun convertToUtcDateTime(date: String, time: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getDefault() // Local time zone
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault() // Keep it in local time

        val combined = "$date $time"
        val parsedDate = inputFormat.parse(combined)
        return outputFormat.format(parsedDate!!)
    }

    fun convertDateToIsoUtc(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getDefault() // Local time zone

        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault() // Local time zone

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
    fun getDeviceCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // force UTC
        return sdf.format(Date())
    }

    fun formatUtcToLocalTime(dateTime: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a dd/MM") // 12-hour format with AM/PM
            val date = LocalDateTime.parse(dateTime, inputFormatter)
            outputFormatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }
    fun getDateOnly(isoString: String): String {
        val dateTime = LocalDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("dd:MM-yyyy", Locale.getDefault())
        return dateTime.format(formatter)
    }

    fun getTimeOnly(isoString: String): String {
        val dateTime = LocalDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return dateTime.format(formatter)
    }

    fun setStatusAppearance(textView: AppCompatTextView, status: Int, context: Context) {
        when (status) {
            2 -> {
                textView.setBackgroundResource(R.drawable.blue_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }

            3 -> {
                textView.setBackgroundResource(R.drawable.approve_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.lorange))
            }
            4 -> {
                textView.setBackgroundResource(R.drawable.lgreen_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.purple_200))
            }
            5 -> {
                textView.setBackgroundResource(R.drawable.blue_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
            6 -> {
                textView.setBackgroundResource(R.drawable.lgreen_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.purple_200))
            }
            7 -> {
                textView.setBackgroundResource(R.drawable.orange_button_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            else -> {
                textView.setBackgroundResource(R.drawable.grey_btn_background)
                textView.setTextColor(ContextCompat.getColor(context, R.color.grey))
            }
        }
    }
}