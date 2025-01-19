package cm.project.anitrack_compose.paging

import androidx.compose.ui.util.fastFilterNotNull
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cm.project.anitrack_compose.graphql.DiscoverMediaPageQuery
import cm.project.anitrack_compose.graphql.type.MediaSeason
import cm.project.anitrack_compose.graphql.type.MediaSort
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import javax.inject.Inject

class DiscoverMediaPage @Inject constructor(
    private val graphQLRepository: GraphQLRepository,
    private val sortCriteria: List<MediaSort>,
    private val season: MediaSeason? = null,
    private val year: Int? = null
) : PagingSource<Int, DiscoverMediaPageQuery.Medium>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiscoverMediaPageQuery.Medium> {
        val page = params.key ?: 1

        return when (val result =
            graphQLRepository.getDiscoverPage(page, sortCriteria, year, season)) {
            is Result.Success -> {
                val data = (result.data.media ?: emptyList()).fastFilterNotNull()

                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (result.data.pageInfo?.hasNextPage == true) page + 1 else null
                )
            }

            is Result.Error -> {
                LoadResult.Error(result.exception)
            }

        }
    }

    override fun getRefreshKey(state: PagingState<Int, DiscoverMediaPageQuery.Medium>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                ?: state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        }
    }
}