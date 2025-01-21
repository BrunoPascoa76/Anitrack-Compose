package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.graphql.GetMediaListEntryQuery
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import com.apollographql.apollo3.exception.ApolloHttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MediaDetailsViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private val _media = MutableStateFlow<GetMediaDetailsQuery.Media?>(null)
    val media = _media.asStateFlow()

    private var userId: Int? = null

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    private val _isBeingRateLimited = MutableStateFlow(false)
    val isBeingRateLimited = _isBeingRateLimited.asStateFlow()

    private val _mediaListEntry = MutableStateFlow<MediaListEntryInput?>(null)
    val mediaListEntry = _mediaListEntry.asStateFlow()

    private val _showMediaListEntryPopup = MutableStateFlow(false)
    val showMediaListEntryPopup = _showMediaListEntryPopup.asStateFlow()


    fun toggleMediaListEntryPopup() {
        _showMediaListEntryPopup.value = !_showMediaListEntryPopup.value
    }

    fun getMediaDetails(mediaId: Int) {
        _media.value = null
        _isBeingRateLimited.value = false
        viewModelScope.launch {
            when (val result = graphQLRepository.getMediaDetails(mediaId)) {
                is Result.Success -> {
                    _media.value = result.data
                }

                is Result.Error -> {
                    if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                        _isBeingRateLimited.value = true
                    }
                }
            }
        }
    }


    fun setStatus(status: MediaListStatus?) {
        _mediaListEntry.value = _mediaListEntry.value?.copy(status = status)
    }

    fun setStartedAt(startedAt: LocalDate?) {
        _mediaListEntry.value = _mediaListEntry.value?.copy(
            startedAt = GetMediaListEntryQuery.StartedAt(
                year = startedAt?.year,
                month = startedAt?.monthValue,
                day = startedAt?.dayOfMonth
            )
        )
    }

    fun setCompletedAt(completedAt: LocalDate?) {
        _mediaListEntry.value = _mediaListEntry.value?.copy(
            completedAt = GetMediaListEntryQuery.CompletedAt(
                year = completedAt?.year,
                month = completedAt?.monthValue,
                day = completedAt?.dayOfMonth
            )
        )
    }

    fun setScore(score: Int) {
        _mediaListEntry.value = _mediaListEntry.value?.copy(score = score)
    }

    fun setProgress(progress: Int) {
        _mediaListEntry.value = _mediaListEntry.value?.copy(progress = progress)
    }

    fun getMediaListEntry() {
        _media.value?.let {
            viewModelScope.launch {
                if (userId == null) {
                    when (val result = graphQLRepository.getUserId()) {
                        is Result.Success -> {
                            userId = result.data
                        }

                        is Result.Error -> {
                            if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                                _isBeingRateLimited.value = true
                            }
                        }
                    }
                }

                if (userId != null) {
                    when (val result =
                        graphQLRepository.getMediaListEntry(userId!!, it.id)) {
                        is Result.Success -> {
                            _mediaListEntry.value = MediaListEntryInput(
                                status = result.data?.status,
                                progress = result.data?.progress,
                                score = result.data?.score?.toInt(),
                                startedAt = result.data?.startedAt,
                                completedAt = result.data?.completedAt,
                            )
                        }

                        is Result.Error -> { //note if not in list, will give 404, which is still a "success" in this scenario
                            _mediaListEntry.value = null
                            if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                                _isBeingRateLimited.value = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveMediaListEntry(
        mediaId: Int,
        mediaListEntryId: Int?,
        startedAt: LocalDate?,
        completedAt: LocalDate?,
        score: Double?,
        progress: Int?,
        progressVolumes: Int?,
        status: MediaListStatus
    ) {
        viewModelScope.launch {
            if (userId == null) {
                when (val result = graphQLRepository.getUserId()) {
                    is Result.Success -> {
                        userId = result.data
                    }

                    is Result.Error -> {
                        if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                            _isBeingRateLimited.value = true
                        }
                    }
                }
            }
            if (userId != null) {
                when (val result = graphQLRepository.saveMediaListEntry(
                    mediaId,
                    mediaListEntryId,
                    startedAt,
                    completedAt,
                    score,
                    progress,
                    progressVolumes,
                    status
                )) {
                    is Result.Success -> {}
                    is Result.Error -> {
                        if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                            _isBeingRateLimited.value = true
                        }
                    }
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
}

data class MediaListEntryInput(
    val status: MediaListStatus? = MediaListStatus.PLANNING,
    val progress: Int? = 0,
    val score: Int? = null,
    val startedAt: GetMediaListEntryQuery.StartedAt? = null,
    val completedAt: GetMediaListEntryQuery.CompletedAt? = null,
)