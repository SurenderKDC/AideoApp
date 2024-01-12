package com.aideo.app.ApiCalling


data class Tag(val _id: String?)

data class Creator(val _id: String?, val name: String?, val thumbnail: String?)

data class Story(val contributedBy: String?)

data class Visual(val allocatedBy: String?)

data class Audio(val singer: String?, val source: String?,)

data class CompleteProject(val contributedBy: String?)

data class Allocated(val allocatedBy: String?, val allocatedTo: String?, val _id: String?)

data class ContentData(
    val version: Int?,
    var isViewed : Int = 0,
    var currentIndex: Int = 0,
    val background: Background?,
    val segments: List<Segment>?,
    val id : String?,
    val thumbnail : String?,
    val title: String?,
    val description: String?,
    val tagsData: List<Tag>?,
    var callToAction : String?
    )

data class Background(val video: Video?, val audio: Audio?,val image:Image?,var exoplayerUrlPosition :Int?)

data class Video(val source: String?)

data class Segment(
    val chapter: Int?, val image: Image?,val video: Video?, val audio: Audio?,var exoPlayerUrlPos:Int = 0
)

data class Image(val source: String?, val interval: Int?)


data class PlaylistData(
    val _id: String?,
    val title: String?,
    val zip: String?,
    val tags: List<Tag>?,
    val ageRating: String?,
    val thumbnail: String?,
    val createdDate: String?,
    val creatorId: Creator?,
    val description: String?,
    val story: List<Story>?,
    val visual: List<Visual>?,
    val audio: List<Audio>?,
    val completeProject: List<CompleteProject>?,
    val allocated: List<Allocated>?,
    val liveStatus: String?,
    val verifiedStatus: Boolean?,
    val merchandise: Boolean?,
    val contentData: String?,
    val verifiedBy: String?,
    val disabledDate: String?,
    val liveDate: String?,
    val watchDuration: Int?,
    var callToAction : String?
)



data class PrefsVideoResponse (
    val playListData : ArrayList<PlaylistData>
)


//{"Version":1,"Background":{"Video":{"Source":"ZenQuotes.mp4"},"Audio":{"Source":"Dawn.m4a"}},"Segments":[{"Chapter":1,"Image":{"Source":"2.png","Interval":5000},"Audio":{"Source":"ZenChime.m4a"}},{"Chapter":1,"Image":{"Source":"3.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"4.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"5.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"6.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"7.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"8.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"9.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"10.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"11.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"12.png","Interval":5000}},{"Chapter":1,"Image":{"Source":"13.png","Interval":5000}}]}