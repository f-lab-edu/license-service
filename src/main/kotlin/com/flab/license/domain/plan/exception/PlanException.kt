package com.flab.license.domain.plan.exception

import com.flab.license.common.exception.CustomException

class PlanException(errorCode: PlanErrorCode) : CustomException(errorCode)
