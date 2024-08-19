package com.example.storydicoding

import com.example.storydicoding.api.response.ListStoryItem
import com.example.storydicoding.api.response.StoryResponse

object DataDummy {
    fun generateDummyStoryEntity(): StoryResponse {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                "Name $i",
                "Description $i",
                i.toDouble() * 10,
                "id $i",
                i.toDouble() * 10
            )
            storyList.add(story)
        }
        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = storyList
        )
    }
}