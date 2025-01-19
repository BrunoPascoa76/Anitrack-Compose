package cm.project.anitrack_compose.paging

import androidx.compose.ui.util.fastFilterNotNull
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cm.project.anitrack_compose.graphql.SearchMediaPageQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result

class SearchMediaPage(
    private val graphQLRepository: GraphQLRepository,
    private val query: String
) : PagingSource<Int, SearchMediaPageQuery.Medium>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchMediaPageQuery.Medium> {
        val page = params.key ?: 1

        return when (val result =
            graphQLRepository.searchMedia(page, query)) {
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

    override fun getRefreshKey(state: PagingState<Int, SearchMediaPageQuery.Medium>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                ?: state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        }
    }
}