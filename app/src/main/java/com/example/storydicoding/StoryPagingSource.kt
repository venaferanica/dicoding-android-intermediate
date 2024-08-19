package com.example.storydicoding

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storydicoding.api.ApiService
import com.example.storydicoding.api.response.ListStoryItem
import kotlinx.coroutines.flow.first
import retrofit2.awaitResponse

class StoryPagingSource (private val apiService: ApiService, private val pref: UserPreference) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val token = pref.getUser().first().token

            val response = apiService.getAllStories(token.toString(), position, params.loadSize)
            val responseData = response.awaitResponse().body()?.listStory

            LoadResult.Page(
                data = responseData as List<ListStoryItem>,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}