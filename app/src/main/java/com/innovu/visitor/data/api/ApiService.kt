package com.innovu.visitor.data.api

import com.innovu.visitor.model.ApiResponse
import com.innovu.visitor.model.ChatPostRequest
import com.innovu.visitor.model.ChatResponse
import com.innovu.visitor.model.ChatUserListResponse
import com.innovu.visitor.model.CheckInRequest
import com.innovu.visitor.model.CheckOutRequest
import com.innovu.visitor.model.DepartmentResponse
import com.innovu.visitor.model.DeviceTokenRequest
import com.innovu.visitor.model.EditUserProfileRequest
import com.innovu.visitor.model.GateMappingRequest
import com.innovu.visitor.model.GateResponse
import com.innovu.visitor.model.ImageUploadResponse
import com.innovu.visitor.model.LeadResponse
import com.innovu.visitor.model.LoginRequest
import com.innovu.visitor.model.LoginResponse
import com.innovu.visitor.model.LogoutRequest
import com.innovu.visitor.model.LogoutRequestresponse
import com.innovu.visitor.model.MeetingStatusResponse
import com.innovu.visitor.model.MeetingTypeResponse
import com.innovu.visitor.model.OtpRequest
import com.innovu.visitor.model.OtpResponse
import com.innovu.visitor.model.RejectRequest
import com.innovu.visitor.model.RescheduleStatusUpdateRequest
import com.innovu.visitor.model.SearchRequest
import com.innovu.visitor.model.StaffResponse
import com.innovu.visitor.model.StudentResponse
import com.innovu.visitor.model.UserProfileResponse
import com.innovu.visitor.model.UsersListResponse
import com.innovu.visitor.model.VisitorCodeResponse
import com.innovu.visitor.model.VisitorFilterResponse
import com.innovu.visitor.model.VisitorMeetingSummaryResponse
import com.innovu.visitor.model.VisitorRequest
import com.innovu.visitor.model.VisitorResponse
import com.innovu.visitor.model.VisitorTypeResponse
import com.innovu.visitor.ui.visitorcreate.Student
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("Account/login")
    suspend fun userLogin(
        @Body bodyparam: LoginRequest
    ): Response<LoginResponse>

    @GET("GateDetail/GetAllGateDetailsByOrg")
    suspend fun GetAllGateDetailsByOrg(
        @Query("orgId") orgId: Int,
        @Query("branchId") branchId: Int,
    ): Response<GateResponse>




    @GET("VisitorMeeting/GetstaffByOrg")
    suspend fun GetVisitorByOrg(
        @Query("userId") userId: Int,
        @Query("organizationId") organizationId: Int,
        @Query("branchId") branchId: Int
    ): Response<VisitorResponse>  // Or Response<List<Staff>> if you want to handle errors manually

    @GET("v1/User/GetProfile")
    suspend fun getUserProfile(
        @Query("userId") userId: Int
    ): Response<UserProfileResponse>


    @GET("ChatConnect/GetChatUserByOrg")
    suspend fun GetChatUserByOrg(
        @Query("UserId") userId: Int,
        @Query("orgId") orgId: Int,
    ): Response<StaffResponse>



    @GET("ChatConnect/GetChatsdetail") // Replace with your actual endpoint like "chat/messages"
    suspend fun getChatMessages(
        @Query("userId") userId: Int,
        @Query("receiverId") receiverId: Int
    ): Response<ChatResponse>


    @POST("ChatConnect/Post") // Replace with your actual endpoint like "chat/send"
    suspend fun postChatMessage(
        @Body request: ChatPostRequest
    ): Response<ChatPostRequest> // Or use your expected response type



    @GET("ChatConnect/GetChatUser") // <-- Replace with your actual endpoint
    suspend fun getChatUserList(@Query("userId") userId: Int): Response<ChatUserListResponse>






    @POST("Setting/PostDeviceToken")
    suspend fun updateToken(
        @Body request: DeviceTokenRequest
    ): Response<Void> // or Call<ResponseBody> if you want raw response

    @GET("Setting/GetAllVisitorType") // replace with your actual endpoint
    suspend fun getVisitorTypes(): Response<VisitorTypeResponse>


    @GET("Setting/GetAllMeetingType") // replace with your actual endpoint
    suspend fun getMeetingTypes(): Response<MeetingTypeResponse>

    @GET("v1/User/GetUserByOrg") // Replace with actual endpoint path
    suspend fun getUsers(@Query("id") id: Int): Response<UsersListResponse>




    @POST("VisitorMeeting/Create")
    suspend fun postVisitor(@Body visitor: VisitorRequest): Response<VisitorCodeResponse>

    @GET("VisitorMeeting/GetVisitorByCode")
    suspend fun GetVisitorByCode(
        @Query("VisitorCode") visitorCode: String
    ): Response<VisitorCodeResponse>


        @Multipart
        @POST("ImageUpload/uploadVisitorimage")
        suspend fun uploadVisitorImages(
            @Part("organizationId") organizationId: RequestBody,
            @Part("VisitorID") visitorId: RequestBody,
            @Part("UserID") userId: RequestBody,
            @Part files: List<MultipartBody.Part>
        ): Response<ImageUploadResponse>



    @PUT("VisitorMeeting/RejectVisitor")
    suspend fun rejectVisitor(@Body request: RejectRequest): Response<VisitorCodeResponse>

    @PUT("VisitorMeeting/Reschedule")
    suspend fun reschedule(@Body status: RescheduleStatusUpdateRequest): Response<VisitorCodeResponse>

    @PUT("VisitorMeeting/Arrived")
    suspend fun Arrived(@Body request: RejectRequest): Response<VisitorCodeResponse>

    @PUT("VisitorMeeting/Cancel")
    suspend fun Cancel(@Body request: RejectRequest): Response<VisitorCodeResponse>


    @PUT("VisitorMeeting/ApproveVisitor")
    suspend fun ApproveVisitor(@Body request: RejectRequest): Response<VisitorCodeResponse>



    @PUT("VisitorMeeting/CheckIn")
    suspend fun CheckIn(@Body request: CheckInRequest): Response<VisitorCodeResponse>

    @PUT("VisitorMeeting/CheckOut")
    suspend fun CheckOut(@Body request: CheckOutRequest): Response<VisitorCodeResponse>


    @POST("GateDetail/PostGateMap")
    suspend  fun postGateMapping(@Body data: GateMappingRequest):Response<GateMappingRequest>// or Call<ResponseBody>



    @GET("Dashboard/GetVisitorMeetingGraphSummaryWeek")
    suspend fun getVisitorMeetingGraphSummaryWeek(
        @Query("organizationId") organizationId: Int,
        @Query("branchId") branchId: Int
    ): Response<VisitorMeetingSummaryResponse>


    @GET("Dashboard/GetVisitorMeeting")
    suspend fun getMeetingStatus(  @Query("userId") userId: Int): Response<MeetingStatusResponse>



    @GET("VisitorMeeting/GetMeetingByStatusFilter")
    suspend fun GetVisitorFilter(
        @Query("userId") userId: Int,
        @Query("statusId") statusId: Int
    ): Response<VisitorFilterResponse>


    @PUT("VisitorMeeting/AllStaffNotification")
    fun GetVisitorNotification(
        @Body request: RejectRequest
    ): Response<ApiResponse<String>> // or ResponseBody if you want full control



    @POST("v1/User/Logout")
    suspend fun logoutUser(
        @Body request: LogoutRequest
    ): Response<LogoutRequestresponse> // or your own response model




    @POST("StudentDetail/search")
    suspend fun searchStudents(
        @Body request: SearchRequest
    ): Response<StudentResponse> // or your own response model


    @POST("v1/User/EditUserProfile")
    suspend fun editUserProfile(
        @Body request: EditUserProfileRequest
    ): Response<EditUserProfileRequest> // Replace with your actual response model




    @GET("Department/GetDepartmentByOrg")
    suspend fun getDepartments(@Query("id") id: Int): Response<DepartmentResponse>



    @Headers("accept: application/json", "Content-Type: application/json")
    @POST("VisitorMeeting/GenerateOTP")
    suspend  fun generateOtp(@Body request: OtpRequest): Response<OtpResponse>



    @GET("LeadMain/GetLeadPagedbyuser")
    suspend fun getLeadsByUser(
        @Query("UserId") userId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("StatusID") StatusID: Int,


    ): Response<LeadResponse>




}

