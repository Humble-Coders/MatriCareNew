//package com.example.matricareog.repository
//
//import com.example.matricareog.HealthReport
//import javax.inject.Inject
//
//interface HealthReportRepository {
//    suspend fun getHealthReport(patientId: String): Result<HealthReport>
//    suspend fun saveReport(report: HealthReport): Result<Boolean>
//    suspend fun shareReport(report: HealthReport): Result<String>
//}
//
//class HealthReportRepositoryImpl @Inject constructor(
//    private val apiService: HealthApiService,
//    private val localDatabase: HealthDatabase
//) : HealthReportRepository {
//
//    override suspend fun getHealthReport(patientId: String): Result<HealthReport> {
//        return try {
//            val response = apiService.getHealthReport(patientId)
//            Result.success(response.toHealthReport())
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun saveReport(report: HealthReport): Result<Boolean> {
//        return try {
//            localDatabase.healthReportDao().insertReport(report.toEntity())
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun shareReport(report: HealthReport): Result<String> {
//        return try {
//            val shareUrl = apiService.generateShareUrl(report)
//            Result.success(shareUrl)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}