package com.flab.license.domain.plan

interface PlanRepository {
    fun save(plan: Plan): Plan
    fun update(plan: Plan): Plan
    fun delete(plan: Plan): Plan

    fun loadById(id: PlanId): Plan
    fun loadByPlanCode(planCode: PlanCode): Plan
    fun loadAll(): List<Plan>

    // 삭제된 플랜 포함 조회 (관리자용)
    fun loadByIdIncludeDeleted(id: PlanId): Plan
}
