package com.example.storydicoding.view.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storydicoding.DataDummy
import com.example.storydicoding.MainDispatcherRules
import com.example.storydicoding.StoryRepository
import com.example.storydicoding.UserPreference
import com.example.storydicoding.adapter.StoryAdapter
import com.example.storydicoding.api.response.ListStoryItem
import com.example.storydicoding.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRules()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var userPreference: UserPreference

    private lateinit var logMock: AutoCloseable

    @Before
    fun setUp() {
        logMock = mockStatic(Log::class.java)
        Mockito.`when`(Log.isLoggable(Mockito.anyString(), Mockito.anyInt())).thenReturn(true)
    }

    @After
    fun tearDown() {
        logMock.close()
    }

    @Test
    fun `when getStories Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory.listStory as List<ListStoryItem>)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)
        userPreference = Mockito.mock(UserPreference::class.java)

        val mainViewModel = MainViewModel(storyRepository, userPreference)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.listStory, differ.snapshot())
        assertEquals((dummyStory.listStory as List<ListStoryItem>).size, differ.snapshot().size)
        assertEquals((dummyStory.listStory as List<ListStoryItem>)[0], differ.snapshot()[0])
    }

    @Test
    fun `when getStories with Empty List Should Return Zero Data`() = runTest {
        val emptyListStory = emptyList<ListStoryItem>()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(emptyListStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)

        userPreference = Mockito.mock(UserPreference::class.java)
        val mainViewModel = MainViewModel(storyRepository, userPreference)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }
        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
            return 0
        }
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}