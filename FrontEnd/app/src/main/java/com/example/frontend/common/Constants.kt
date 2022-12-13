public class Constants{
    companion object {
        const val BASE_URL = "http://147.91.204.115:10060/"; //server
//        const val BASE_URL = "http://192.168.8.112:8080/";

        const val LOCATION_ID = "locationId"

        const val USER_LIST_TYPE = "userTypeList"

        const val USER_ID = "userId"

        const val POST_ID = "postId"

        var locationId:Long = 0
        var sort:Int = 1

        var refreshPhotoConstant:Long = 0
        var refreshFollowUnfollowConstant:Long = 0
        var refreshProfileConstant:Long = 0
        //var refreshComments:Long = 0


        //var postLikeChangedSinglePostPage: Boolean = false

        var singlePostPageChanged: Boolean = false

        var locationDeletedUpdateMap: Boolean = false
    }
}