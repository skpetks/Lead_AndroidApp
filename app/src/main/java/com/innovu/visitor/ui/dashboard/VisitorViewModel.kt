package com.innovu.visitor.ui.dashboard

import android.annotation.SuppressLint
import android.app.usage.UsageEvents.Event
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.model.CheckInRequest
import com.innovu.visitor.model.CheckOutRequest
import com.innovu.visitor.model.Department
import com.innovu.visitor.model.DeviceTokenRequest
import com.innovu.visitor.model.EditUserProfileRequest
import com.innovu.visitor.model.GateMappingRequest
import com.innovu.visitor.model.LogoutRequest
import com.innovu.visitor.model.MeetingStatusData
import com.innovu.visitor.model.MeetingType
import com.innovu.visitor.model.OtpRequest
import com.innovu.visitor.model.RejectRequest
import com.innovu.visitor.model.RescheduleStatusUpdateRequest
import com.innovu.visitor.model.User
import com.innovu.visitor.model.Visitor
import com.innovu.visitor.model.VisitorMeetingData
import com.innovu.visitor.model.VisitorRequest
import com.innovu.visitor.model.VisitorType
import com.innovu.visitor.utlis.StorePrefData
import com.innovu.visitor.utlis.Utils.getDeviceCurrentDateTime
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.Int

class VisitorViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text



    // You can use this for triggering refresh
    private val _refreshTrigger = MutableLiveData<Unit>()
    val refreshTrigger: LiveData<Unit> get() = _refreshTrigger

    fun triggerRefresh() {
        _refreshTrigger.value = Unit
    }


    private val _visitorResponse = MutableLiveData<List<Visitor>>()
    val visitorResponse: LiveData<List<Visitor>> = _visitorResponse

    private val _visitorCodeResponse = MutableLiveData<Visitor>()
    val visitorCodeResponse: LiveData<Visitor> = _visitorCodeResponse



    private val _visitorCreateResponse = MutableLiveData<Visitor>()
    val visitorCreateResponse: LiveData<Visitor> = _visitorCreateResponse



    private val _visitorMeetingData = MutableLiveData<List<VisitorMeetingData>>()
    val visitorMeetingData: LiveData<List<VisitorMeetingData>> = _visitorMeetingData


    private val _meetingListcount = MutableLiveData<List<MeetingStatusData>>()
    val MeetingListcount: LiveData<List<MeetingStatusData>> = _meetingListcount



    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> = _result


    private val _DepartmentList = MutableLiveData<List<Department>>()
    public val DepartmentList: LiveData<List<Department>> = _DepartmentList




    fun fetchVisitors(userId: Int, organizationId: Int, branchId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.GetVisitorByOrg(userId, organizationId, branchId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val visitors = response.body()?.data?.data ?: emptyList()
                    _visitorResponse.postValue(visitors)
                } else {
                    _visitorResponse.postValue(emptyList())
                    _error.postValue("${response.body()?.message}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }

    private val _visitorType = MutableLiveData<List<VisitorType>>()
    val VisitorType: LiveData<List<VisitorType>> = _visitorType


    private val _meetingType = MutableLiveData<List<MeetingType>>()
    val meetingType: LiveData<List<MeetingType>> = _meetingType


    private val _userlist = MutableLiveData<List<User>>()
    val userlist: LiveData<List<User>> = _userlist

    fun getVisitorTypes() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getVisitorTypes()
                if (response.isSuccessful) {
                    val visitorList = response.body()?.data

                    _visitorType.postValue(response.body()?.data ?: emptyList())
                    Log.d("API", "Visitor Types: $visitorList")
                    // Update UI or state here
                } else {
                    Log.e("API", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    fun getMeetingTypes() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMeetingTypes()
                if (response.isSuccessful) {
                    val visitorList = response.body()?.data
                    _meetingType.postValue(response.body()?.data ?: emptyList())
                    Log.d("API", "Meeting Types: $visitorList")
                    // Update UI or state here
                } else {
                    Log.e("API", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }


    fun getUserList() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getUsers(StorePrefData.OrgId)
                if (response.isSuccessful) {
                    val visitorList = response.body()?.data
                    _userlist.postValue(response.body()?.data ?: emptyList())
                    Log.d("API", "Meeting Types: $visitorList")
                    // Update UI or state here
                } else {
                    Log.e("API", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }


    fun CreateVisitorMain(visitorRequest:VisitorRequest) {

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.postVisitor(visitorRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        // Actual success
                        Log.d("API", "Success: ${body.data}")
                        _visitorCreateResponse.postValue(response.body()?.data!!)
                        _result.postValue(true)
                    } else {
                        // API returned success=false
                        val errorMsg = body?.message ?: "Unknown API error"
                        Log.e("API", "API Error: $errorMsg")
                        _error.postValue(errorMsg)
                        _result.postValue(false)
                    }
                } else {
                    // Network-level error (non-200)
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        val json = JSONObject(errorBody ?: "")
                        json.getString("message")
                    } catch (e: Exception) {
                        "Unexpected server error"
                    }
                    Log.e("API", "Network Error: $errorMsg")
                    _error.postValue(errorMsg)
                    _result.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
                _error.postValue("Exception: ${e.message}")
                _result.postValue(false)
            }
        }

    }






    fun GetVisitorByCode(VisitorCode:String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.GetVisitorByCode(VisitorCode)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _visitorCodeResponse.postValue(response.body()!!.data!!)
                    }else{
                        _error.postValue("${response.body()?.message}")
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }
    fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun UploadImage(visitorId: String,files: List<MultipartBody.Part>) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.uploadVisitorImages(createPartFromString(StorePrefData.OrgId.toString()),createPartFromString(visitorId),
                    createPartFromString(StorePrefData.UserID),files)
                if (response.isSuccessful) {
                    Log.d("API", "Success: ${response.body()?.path}")

                    _result.postValue(true)
                } else {
                    _result.postValue(false)
                    Log.e("API", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    fun RejectVisitor(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = RejectRequest(visitorId, StorePrefData.UserIId)
                val response = RetrofitClient.instance.rejectVisitor(request)
                    if(response.isSuccessful ){
                        _result.postValue(true)
                        _error.postValue("${response.body()!!.message}")
                    }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
    fun reschedule(request:RescheduleStatusUpdateRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.reschedule(request)
                if(response.isSuccessful ){
                        triggerRefresh()
                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    fun Arrived(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = RejectRequest(VisitorID = visitorId, UserID = StorePrefData.UserIId)
                val response = RetrofitClient.instance.Arrived(request)
                if(response.isSuccessful ){
                    triggerRefresh()
                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
    fun CancelledMeeting(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = RejectRequest(VisitorID = visitorId, UserID = StorePrefData.UserIId)
                val response = RetrofitClient.instance.Cancel(request)
                if(response.isSuccessful ){
                    triggerRefresh()
                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }
    fun CheckInRequestUpdate(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = CheckInRequest(visitorID = visitorId,
                    CheckINtime=getDeviceCurrentDateTime(),
                    CheckinUserID = StorePrefData.UserIId,
                    InGateID=StorePrefData.GateID)
                val response = RetrofitClient.instance.CheckIn(request)
                if(response.isSuccessful ){
                    triggerRefresh()

                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    fun CheckOutRequestUpdate(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = CheckOutRequest(visitorID = visitorId,
                    CheckOutTime=getDeviceCurrentDateTime(),
                    CheckoutUserID = StorePrefData.UserIId,
                    OutGateID=StorePrefData.GateID)
                val response = RetrofitClient.instance.CheckOut(request)
                if(response.isSuccessful ){
                    triggerRefresh()
                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }

    fun GetVisitorNotification(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = RejectRequest(VisitorID = visitorId, UserID = StorePrefData.UserIId)
                val response = RetrofitClient.instance.GetVisitorNotification(request)
                if(response.isSuccessful ){
                    triggerRefresh()

                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }



    fun ApproveVisitor(visitorId:Int) {
        viewModelScope.launch {
            try {
                val request = RejectRequest(VisitorID = visitorId, UserID = StorePrefData.UserIId)
                val response = RetrofitClient.instance.ApproveVisitor(request)
                if(response.isSuccessful ){
                    triggerRefresh()

                    _result.postValue(true)
                    _error.postValue("${response.body()!!.message}")
                }
            } catch (e: Exception) {
                Log.e("API", "Exception: ${e.message}")
            }
        }
    }


    fun postGateMapping( ) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val currentDateTime = sdf.format(Date())
                val request = GateMappingRequest(
                    gateMapID=0,
                    gateID=StorePrefData.GateID,
                    userID = StorePrefData.UserIId,
                    operationFromDate = currentDateTime,
                    operationToDate = currentDateTime,
                    operationType="Entry",
                    branchID= StorePrefData.BranchId,
                    organizationID= StorePrefData.OrgId
                )
                val response = RetrofitClient.instance.postGateMapping(request)
                if (response.isSuccessful ) {
                    Log.d("Gate Mapping ", " updated:")
                } else {
                    _error.postValue("API error: ${response.message()}")

                    Log.d("updateToken", "updateToken message: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }

    }




    fun getVisitorMeetingGraphSummaryWeek() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getVisitorMeetingGraphSummaryWeek(
                    StorePrefData.OrgId, StorePrefData.BranchId)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _visitorMeetingData.postValue(response.body()!!.data!!)
                    }else{
                        _error.postValue(response.body()!!.message!!)
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun GetmeetingStatusCount() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMeetingStatus(
                    StorePrefData.UserIId)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _meetingListcount.postValue(response.body()!!.data!!)
                    }else{
                        _error.postValue(response.body()!!.message!!)
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun GetVisitorFilter(StatusId:Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.GetVisitorFilter(StorePrefData.UserIId,StatusId)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val visitors = response.body()?.data ?: emptyList()
                        _visitorResponse.postValue(visitors)
                    }else{
                        _error.postValue(response.body()!!.message!!)
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }



    fun logoutUser() {
        viewModelScope.launch {
            try {
                val logoutRequest = LogoutRequest(
                    userId = StorePrefData.UserIId,
                    deviceType = "android",
                    deviceUID = StorePrefData.UID
                )

                val response = RetrofitClient.instance.logoutUser(logoutRequest)
                if (response.isSuccessful ) {
                    StorePrefData.clear()
                    _result.postValue(true)
                }

            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }




    fun EditUserProfile(request: EditUserProfileRequest) {
        viewModelScope.launch {
            try {


                val response = RetrofitClient.instance.editUserProfile(request)
                if (response.isSuccessful ) {
                    _result.postValue(true)
                }else{
                    _result.postValue(false)
                }

            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }


    fun getDepartments() {
        viewModelScope.launch {
            try {


                val response = RetrofitClient.instance.getDepartments(StorePrefData.OrgId)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _DepartmentList.postValue(response.body()!!.data!!)
                    }else{
                        _error.postValue(response.body()!!.message!!)
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }

            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }


    fun generateOtp(Phone:String) {
        viewModelScope.launch {
            try {
                val request = OtpRequest(
                    phoneNumber = Phone,
                    userId = StorePrefData.UserIId,
                    organizationId = StorePrefData.OrgId,
                    branchId = StorePrefData.BranchId
                )


                val response = RetrofitClient.instance.generateOtp(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.isSuccessful && response.body()?.data != null) {
                      //  _DepartmentList.postValue(response.body()!!.data!!)
                    }else{
                        _error.postValue(response.body()!!.message!!)
                    }
                } else {
                    _error.postValue("${response.body()!!.message!!}")
                }

            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }






}