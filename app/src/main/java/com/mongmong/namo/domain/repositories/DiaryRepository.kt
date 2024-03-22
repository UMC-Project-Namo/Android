package com.mongmong.namo.domain.repositories

import androidx.paging.PagingSource
import com.mongmong.namo.data.local.entity.diary.Diary
import com.mongmong.namo.data.local.entity.diary.DiaryEvent
import java.io.File


interface DiaryRepository {
    suspend fun getDiary(localId: Long): Diary

    suspend fun addDiary(
        diary: Diary,
        images: List<File>?
    )

    suspend fun editDiary(
        diary: Diary,
        images: List<File>?
    )
    suspend fun deleteDiary(
        localId: Long,
        scheduleServerId: Long
    )
    suspend fun uploadDiaryToServer()

    suspend fun postDiaryToServer(serverId: Long, scheduleId: Long)

    fun getPersonalDiaryPagingSource(month: String): PagingSource<Int, DiaryEvent>
}