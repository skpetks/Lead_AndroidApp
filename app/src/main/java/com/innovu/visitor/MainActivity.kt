package com.innovu.visitor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.databinding.ActivityMainBinding
import com.innovu.visitor.model.GateDetail
import com.innovu.visitor.services.CallReceiver
import com.innovu.visitor.ui.PortraitCaptureActivity
import com.innovu.visitor.ui.QrScannerActivity
import com.innovu.visitor.ui.dashboard.VisitorViewModel
import com.innovu.visitor.ui.home.HomeViewModel
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.UserRole
import com.innovu.visitor.services.SignalRManager
import com.innovu.visitor.ui.dashboard.VisitorLogFragment
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {



    private val callEndedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val phone = intent?.getStringExtra("phoneNumber")
            Log.d("MainActivity", "Received CALL_ENDED broadcast for number: $phone")
            showPopup(phone)
        }
    }


    private fun showPopup(phone: String?) {
        AlertDialog.Builder(this)
            .setTitle("Call Ended")
            .setMessage("Call ended with $phone")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private lateinit var callReceiver: CallReceiver
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityMainBinding
    private val visitorViewModel: VisitorViewModel by viewModels()
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SignalRManager.initConnection()
        // Make the content fullscreen (draw behind status bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Optional: Hide status bar and nav bar completely
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true // if your status bar has dark icons
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.white))
        )

        navView.background = null
        navView.menu.getItem(2).isEnabled = false
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Observe LiveData from ViewModel
        homeViewModel.gates.observe(this) { gates ->
            // Update your UI here with gates list
            Toast.makeText(this, "gates"+gates.size, Toast.LENGTH_LONG).show()
            showGateBottomSheet(this, gates)
            gates.forEach {
                Log.d("MainActivity", "${it.gateDetailID}: ${it.gateName} - ${it.gateNumber}")
            }
        }
//        navView.menu.getItem(2).setIcon(R.drawable.icon_plus)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
//        val menu = bottomNavigationView.menu
//        val thirdItem = menu.getItem(2) // Index 2 is the 3rd item
//        thirdItem.setIcon(R.drawable.icon_plus)
// Assuming your menu item ID is R.id.navigation_dashboard
//           setupBottomNavUnderline(navView)
        // Observe LiveData
        visitorViewModel.visitorCodeResponse.observe(this) { visitors ->
            // TODO: Update your UI (RecyclerView, etc.) with visitor list
//            Toast.makeText(this, visitors.size, Toast.LENGTH_SHORT).show()
            Log.d("error", "visitors error: $visitors")

            if (visitors != null) {
                StorePrefData.visitorJson= Gson().toJson(visitors)
                val navController =
                    findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_visitordetail)
            }else{
                Snackbar.make(binding.ivPlus, "No Data found", Snackbar.LENGTH_LONG).show()
            }
        }
        visitorViewModel.error.observe(this) { errorMsg ->
           // Toast.makeText(this,  errorMsg, Toast.LENGTH_SHORT).show()
        }
        binding.ivPlus.setOnClickListener {
          //  val bottomSheet = CustomBottomSheet()
           // bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        // Trigger API call
        val role = UserRole.fromName("SECURITY")
        if (StorePrefData.RoleId == role?.id && StorePrefData.GateID==0) {
            // Do something
            homeViewModel.fetchGateDetails(RetrofitClient.instance)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        Log.d("FCM", "Token updated:"+StorePrefData.tokenUpdate)
//        if (!StorePrefData.token.isEmpty() && StorePrefData.tokenUpdate) {
            val versionName = packageManager
                .getPackageInfo(packageName, 0)
                .versionName
//            homeViewModel.updateToken(versionName.toString())
//            StorePrefData.tokenUpdate=false
//        }
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_grid)
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened
                binding.ivPlus.visibility =View.GONE
                bottomNav.visibility = View.GONE
            } else {
                // keyboard is closed
                binding.ivPlus.visibility =View.VISIBLE
                bottomNav.visibility = View.VISIBLE
            }
        }
//        setupKeyboardVisibilityListener(bottomNav)
        fetchFirebaseToken()
        getTitleName()
        handleIntent(intent)
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
        ), 100)
    }

    fun updateTitleWithColor(titleText: String, color: Int = Color.BLACK) {

        val color1 = ContextCompat.getColor(this, R.color.txt_color)
        val spannable = SpannableString(titleText)
        spannable.setSpan(
            ForegroundColorSpan(color1),
            0,
            titleText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f), // Reduce font size by 20%
            0,
            titleText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.title = spannable
    }

    fun UpdateHomePage( firstPart: String,secondPart: String) {
        val icon = ContextCompat.getDrawable(this, R.drawable.ic_grid)
        val background = ContextCompat.getDrawable(this, R.drawable.bg_circle) // custom background shape

        val layers = arrayOf(background, icon)
        val layerDrawable = LayerDrawable(layers)
// Optionally adjust insets if needed
        layerDrawable.setLayerInset(1, 10, 10, 10, 10) // Padding for the icon layer
        supportActionBar?.setHomeAsUpIndicator(layerDrawable)



        val color1 = ContextCompat.getColor(this, R.color.txt_color)
        val color2 = ContextCompat.getColor(this, R.color.purple_200)

        val title = firstPart +""+ secondPart+"!"

        val spannable = SpannableString(title)
        spannable.setSpan(
            ForegroundColorSpan(color1),
            0,
            firstPart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f), // Reduce font size by 20%
            0,
            firstPart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(color2),
            firstPart.length,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.8f), // Also reduce second part if needed
            firstPart.length,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        supportActionBar?.title = spannable
        // supportActionBar?.title = title
    }
    public fun getTitleName() {

        val sdf: SimpleDateFormat = SimpleDateFormat("HH")
        val hr = sdf.format(Date()).toInt()
        var Message = "";
        if (hr < 12) {
            Message = "Good morning, "
        } else if (hr >= 12 && hr <= 16) {
            Message = "Good afternoon, "
        } else if (hr > 16 && hr <= 18) {
            Message = "Good evening, "
        } else if (hr > 18 && hr < 24) {
            Message = "Good Night, "
        }

        UpdateHomePage(Message, StorePrefData.UserName.toString())


    }

    fun Activity.setupKeyboardVisibilityListener(navView: BottomNavigationView) {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            val isKeyboardShown = heightDiff > dpToPx(this, 200) // threshold
            navView.visibility = if (isKeyboardShown) View.GONE else View.VISIBLE
        }
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
    fun showGateBottomSheet(context: Context, gateList: List<GateDetail>) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)

        val spinner = view.findViewById<Spinner>(R.id.spinnerOptions)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Extract gate names for spinner
        val gateNames = gateList.map { it.gateName }


        // Set adapter
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, gateNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnOk.setOnClickListener {
            val selectedIndex = spinner.selectedItemPosition
            val selectedGate = gateList[selectedIndex]
            StorePrefData.GateID =selectedGate.gateDetailID

            visitorViewModel.postGateMapping()
            Toast.makeText(context, "Selected: ${selectedGate.gateName} (${selectedGate.gateNumber})", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    @SuppressLint("RestrictedApi")
    fun setupBottomNavUnderline(bottomNav: BottomNavigationView) {
        val menuView = bottomNav.getChildAt(0) as BottomNavigationMenuView

        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i) as BottomNavigationItemView

            // Add a tag to avoid duplicates
            if (itemView.findViewWithTag<View>("underline") == null) {
                val line = View(bottomNav.context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        50,
                        10 // thickness of the line
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        bottomMargin=20
                    }
                    setBackgroundColor(ContextCompat.getColor(context, R.color.button_color))
                    visibility = View.INVISIBLE
                    tag = "underline"
                }
                itemView.addView(line)
            }
        }

        // Initial selection update
        updateUnderline(bottomNav)

        bottomNav.setOnNavigationItemSelectedListener { item ->
             updateUnderline(bottomNav, item.itemId)
            true
        }

      //  handleCallEndIntent(intent)

    }


    private fun handleCallEndIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("CALL_ENDED", false) == true) {
            val phone = intent.getStringExtra("PHONE_NUMBER")
            Log.d("MainActivity", "App opened after call ended with: $phone")
            showPopup(phone)
        }
    }


    @SuppressLint("RestrictedApi")
    fun updateUnderline(bottomNav: BottomNavigationView, selectedId: Int? = null) {
        val menuView = bottomNav.getChildAt(0) as BottomNavigationMenuView

        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i) as BottomNavigationItemView
            val underline = itemView.findViewWithTag<View>("underline")
            val isSelected = bottomNav.menu.getItem(i).itemId == (selectedId ?: bottomNav.selectedItemId)
            underline?.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val qrResult = data?.getStringExtra("qr_result")
            if (!qrResult.isNullOrEmpty()) {
                visitorViewModel.GetVisitorByCode(qrResult)
                Toast.makeText(this, "Scanned: $qrResult", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scan failed or no data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        val fragmentName = intent.getStringExtra("openFragment")

        if (fragmentName == "Chat") {
            openVisitorFragment()
        }
    }
    private fun openVisitorFragment() {


        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_chat)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("SHOW_CUSTOM_POPUP")
        LocalBroadcastManager.getInstance(this).registerReceiver(popupReceiver, filter)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(callEndedReceiver, IntentFilter("CALL_ENDED_ACTION"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(popupReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(callEndedReceiver)
    }



    private val popupReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val title = intent.getStringExtra("title") ?: "Notification"
            val message = intent.getStringExtra("message") ?: ""
            val id = intent.getStringExtra("id") ?: ""

            val inflater = LayoutInflater.from(this@MainActivity)
            val view = inflater.inflate(R.layout.custom_popup, null)

            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
            val btnOk = view.findViewById<Button>(R.id.btnOk)

            tvTitle.text = title
            tvMessage.text = "$message"

            val dialog = AlertDialog.Builder(this@MainActivity)
                .setView(view)
                .setCancelable(false)
                .create()

            btnOk.setOnClickListener {
                visitorViewModel.GetVisitorByCode(id)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SignalRManager.disconnect()


    }

    override fun onStart() {
        super.onStart()
        callReceiver = CallReceiver()
        val filter = IntentFilter().apply {
            addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        }
        registerReceiver(callReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(callReceiver)
    }
    override fun onBackPressed() {
        super.onBackPressed()
//        AlertDialog.Builder(this)
//            .setTitle("Exit")
//            .setMessage("Are you sure you want to exit?")
//            .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
//            .setNegativeButton("No", null)
//            .show()
    }
    fun fetchFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                StorePrefData.tokenUpdate=true
                // Get new FCM registration token
                val token = task.result
                StorePrefData.token=task.result
                val versionName = packageManager
                    .getPackageInfo(packageName, 0)
                    .versionName
                homeViewModel.updateToken(versionName.toString())
                Log.d("FCM", "FCM Token: $token")
                // TODO: Send token to your server or save it in preferences
            }
    }
}

class CustomBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val okButton = view.findViewById<LinearLayout>(R.id.ll_scan)


        okButton.setOnClickListener {
            dismiss()
            val intent = Intent(activity, QrScannerActivity::class.java)
            activity?.startActivityForResult(intent, 101)
        }

        val btn_entry=view.findViewById<LinearLayout>(R.id.ll_entry);

        btn_entry.setOnClickListener {
            dismiss()
            val navController = requireActivity()
                .findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_visitorcreate)
        }
        val btn_exist=view.findViewById<Button>(R.id.btn_exist);

        btn_exist.setOnClickListener {
            dismiss()
            OtpBottomSheet { otp ->
           //     Toast.makeText(activity, "OTP entered: $otp", Toast.LENGTH_SHORT).show()
            }.show(parentFragmentManager, "OtpBottomSheet")
        }
    }

}

class OtpBottomSheet(
    private val onOtpEntered: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var otpBoxes: List<TextView>
    private var currentOtp = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottomsheet_otp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        otpBoxes = listOf(
            view.findViewById(R.id.otp_1),
            view.findViewById(R.id.otp_2),
            view.findViewById(R.id.otp_3),
            view.findViewById(R.id.otp_4),
            view.findViewById(R.id.otp_5),
            view.findViewById(R.id.otp_6)
        )

        val keypad = listOf(
            R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6,
            R.id.btn_7, R.id.btn_8, R.id.btn_9,
            R.id.btn_clear, R.id.btn_0
        )

        for (id in keypad) {
            view.findViewById<View>(id).setOnClickListener { handleKeypadInput(id) }
        }
    }

    private fun handleKeypadInput(id: Int) {
        when (id) {
            R.id.btn_clear -> {
                if (currentOtp.isNotEmpty()) {
                    val index = currentOtp.length - 1
                    currentOtp.deleteAt(index)
                    otpBoxes[index].text = ""
                }
            }
            else -> {
                if (currentOtp.length < 6) {
                    val digit = when (id) {
                        R.id.btn_0 -> "0"
                        R.id.btn_1 -> "1"
                        R.id.btn_2 -> "2"
                        R.id.btn_3 -> "3"
                        R.id.btn_4 -> "4"
                        R.id.btn_5 -> "5"
                        R.id.btn_6 -> "6"
                        R.id.btn_7 -> "7"
                        R.id.btn_8 -> "8"
                        R.id.btn_9 -> "9"
                        else -> ""
                    }
                    currentOtp.append(digit)
                    otpBoxes[currentOtp.length - 1].text = digit
                }

                if (currentOtp.length == 6) {
                    onOtpEntered(currentOtp.toString())
                    val viewModel = ViewModelProvider(requireActivity())[VisitorViewModel::class.java]
                    viewModel.GetVisitorByCode(currentOtp.toString())
                    Toast.makeText(context,"6 digit", Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
        }
    }


}

