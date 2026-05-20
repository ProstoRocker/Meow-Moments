package com.ilyadev.meowmoments.data.local.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity

class CatFactPagingSource(
    private val catFactDao: CatFactDao
) : PagingSource<Int, CatFactEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CatFactEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatFactEntity> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val offset = (page - 1) * pageSize

            val facts = catFactDao.getCatFactsPaged(offset, pageSize)

            LoadResult.Page(
                data = facts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (facts.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}