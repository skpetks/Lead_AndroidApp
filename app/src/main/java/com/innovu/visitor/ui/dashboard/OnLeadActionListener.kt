package com.innovu.visitor.ui.dashboard

import com.innovu.visitor.model.Lead

interface OnLeadActionListener {
    fun onCallClicked(lead: Lead)
}