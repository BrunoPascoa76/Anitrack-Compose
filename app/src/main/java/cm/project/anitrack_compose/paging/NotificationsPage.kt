package cm.project.anitrack_compose.paging

import androidx.compose.ui.util.fastFilterNotNull
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cm.project.anitrack_compose.graphql.GetNotificationsQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import javax.inject.Inject

class NotificationsPage @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    PagingSource<Int, GetNotificationsQuery.Notification>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetNotificationsQuery.Notification> {
        val page = params.key ?: 1

        return when (val result =
            graphQLRepository.getNotifications(page)) {
            is Result.Success -> {
                val data = result.data

                LoadResult.Page(
                    data = data.notifications?.fastFilterNotNull() ?: emptyList(),
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (data.pageInfo?.hasNextPage == true) page + 1 else null
                )
            }

            is Result.Error -> {
                LoadResult.Error(result.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GetNotificationsQuery.Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                ?: state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        }
    }
}