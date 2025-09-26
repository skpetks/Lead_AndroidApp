package com.innovu.visitor.utlis

import com.chibatching.kotpref.KotprefModel

object StorePrefData: KotprefModel() {
    var token by stringPref()
    var UserName by stringPref()
    var UserID by stringPref()
    var ParameterUserID by stringPref()
    var isAdminUserID by stringPref()
    var Role by stringPref()
    var RoleId  by intPref(default = -1)
    var OrgId  by intPref(default = -1)
    var BranchId  by intPref(default = -1)
    var UserIId  by intPref(default = -1)
    var visitorID  by intPref(default = -1)
    var GateID  by intPref(default = -1)
    var DepartmentID  by intPref(default = -1)

    var UID by stringPref()
    var visitorJson by stringPref()
    var isFirtsTimeLogin by booleanPref()
    var isTempPassword by intPref(default = -1)
    var isTermsAgree by intPref(default = -1)
    var isConsent by intPref(default = -1)
    var isLoggged by intPref(default = -1)
    var storeTempPassword by stringPref()
    var isDashBoard by booleanPref()
    var pass by stringPref()
    var encKey by stringPref()
    // profile data
    var firstName by stringPref()
    var lastName by stringPref()
    var email by stringPref()
    var site by stringPref()
    var isCameraFront by booleanPref()
    var isLogin by booleanPref(default = false)
    var isTourDone by booleanPref(default = false)
    var isTokenExpired by booleanPref()
    var isImageByHcp by booleanPref(default = false)
    var isInstructionTour by booleanPref(default = false)
    var tokenUpdate by booleanPref(default = false)
    var selectedAstrologer by stringPref()
}
