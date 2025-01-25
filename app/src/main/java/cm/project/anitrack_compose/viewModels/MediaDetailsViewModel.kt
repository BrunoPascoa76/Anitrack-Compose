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

    private val _isInLibrary = MutableStateFlow(false)
    val isInLibrary = _isInLibrary.asStateFlow()

    private val _isChanged = MutableStateFlow(false)
    val isChanged = _isChanged.asStateFlow()

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
        _mediaListEntry.value = null
        _isInLibrary.value = false
        _isChanged.value = false
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
        _isChanged.value = true
        when (status) {
            MediaListStatus.CURRENT -> {
                val now = LocalDate.now()
                val startedAt =
                    _mediaListEntry.value?.startedAt ?: GetMediaListEntryQuery.StartedAt(
                        now.year,
                        now.monthValue,
                        now.dayOfMonth
                    )
                _mediaListEntry.value = _mediaListEntry.value?.copy(
                    status = MediaListStatus.CURRENT,
                    startedAt = startedAt
                )
            }

            MediaListStatus.COMPLETED -> {
                val now = LocalDate.now()
                val completedAt = _mediaListEntry.value?.completedAt
                    ?: GetMediaListEntryQuery.CompletedAt(now.year, now.monthValue, now.dayOfMonth)

                val progress = _media.value?.episodes ?: _mediaListEntry.value?.progress ?: 0

                _mediaListEntry.value = _mediaListEntry.value?.copy(
                    status = MediaListStatus.COMPLETED,
                    completedAt = completedAt,
                    progress = progress
                )
            }

            else -> _mediaListEntry.value = _mediaListEntry.value?.copy(status = status)
        }
    }

    fun setStartedAt(startedAt: LocalDate?) {
        _isChanged.value = true
        _mediaListEntry.value = _mediaListEntry.value?.copy(
            startedAt = GetMediaListEntryQuery.StartedAt(
                year = startedAt?.year,
                month = startedAt?.monthValue,
                day = startedAt?.dayOfMonth
            )
        )
    }

    fun setCompletedAt(completedAt: LocalDate?) {
        _isChanged.value = true
        _mediaListEntry.value = _mediaListEntry.value?.copy(
            completedAt = GetMediaListEntryQuery.CompletedAt(
                year = completedAt?.year,
                month = completedAt?.monthValue,
                day = completedAt?.dayOfMonth
            )
        )
    }

    fun setScore(score: Int) {
        _isChanged.value = true
        _mediaListEntry.value = _mediaListEntry.value?.copy(score = score)
    }

    fun setProgress(progress: Int) {
        _isChanged.value = true
        if (_media.value?.episodes != null && progress >= _media.value?.episodes!!) {
            _mediaListEntry.value = _mediaListEntry.value?.copy(
                progress = _media.value?.episodes,
                status = MediaListStatus.COMPLETED
            )
        } else {
            _mediaListEntry.value = _mediaListEntry.value?.copy(progress = progress)
        }
    }

    fun getMediaListEntry() {
        _isBeingRateLimited.value = false

        _media.value?.let {
            if (_mediaListEntry.value == null) {
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
                                _isInLibrary.value = true
                                _mediaListEntry.value = MediaListEntryInput(
                                    mediaListEntryId = result.data?.id,
                                    status = result.data?.status,
                                    progress = result.data?.progress,
                                    score = result.data?.score?.toInt(),
                                    startedAt = result.data?.startedAt,
                                    completedAt = result.data?.completedAt,
                                )
                            }

                            is Result.Error -> { //note if not in list, will give 404, which is still a "success" in this scenario
                                if (result.exception is ApolloHttpException && result.exception.statusCode == 404) {
                                    _mediaListEntry.value =
                                        MediaListEntryInput(status = MediaListStatus.PLANNING)
                                } else {
                                    _mediaListEntry.value = null
                                }
                                if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                                    _isBeingRateLimited.value = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveMediaListEntry() {
        if (_media.value != null && _mediaListEntry.value != null) {
            saveMediaListEntry(
                mediaId = _media.value!!.id,
                mediaListEntryId = _mediaListEntry.value!!.mediaListEntryId,
                startedAt = _mediaListEntry.value!!.startedAt?.let {
                    if (it.year != null && it.month != null && it.day != null)
                        LocalDate.of(
                            it.year,
                            it.month,
                            it.day
                        ) else null
                },
                completedAt = _mediaListEntry.value!!.completedAt?.let {
                    if (it.year != null && it.month != null && it.day != null)
                        LocalDate.of(
                            it.year,
                            it.month,
                            it.day
                        ) else null
                },
                score = _mediaListEntry.value!!.score?.toDouble(),
                progress = _mediaListEntry.value!!.progress,
                status = _mediaListEntry.value!!.status!!
            )
        }
    }

    private fun saveMediaListEntry(
        mediaId: Int,
        mediaListEntryId: Int?,
        startedAt: LocalDate?,
        completedAt: LocalDate?,
        score: Double?,
        progress: Int?,
        status: MediaListStatus
    ) {
        viewModelScope.launch {
            _isBeingRateLimited.value = false
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
                    mediaId = mediaId,
                    mediaListEntryId = mediaListEntryId,
                    startedAt = startedAt,
                    completedAt = completedAt,
                    score = score,
                    progress = progress,
                    status = status
                )) {
                    is Result.Success -> {
                        _mediaListEntry.value =
                            _mediaListEntry.value?.copy(mediaListEntryId = result.data.id)
                        _isInLibrary.value = true
                        _isChanged.value = false
                        _showMediaListEntryPopup.value = false
                    }

                    is Result.Error -> {
                        if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                            _isBeingRateLimited.value = true
                        }
                    }
                }
            }
            _showMediaListEntryPopup.value = false
        }
    }

    fun deleteMediaListEntry() {
        viewModelScope.launch {
            _isBeingRateLimited.value = false
            if (_mediaListEntry.value != null && _mediaListEntry.value!!.mediaListEntryId != null) {
                when (val result =
                    graphQLRepository.deleteMediaListEntry(_mediaListEntry.value!!.mediaListEntryId!!)) {
                    is Result.Success -> {
                        _mediaListEntry.value =
                            MediaListEntryInput(status = MediaListStatus.PLANNING)
                        _isChanged.value = false
                        _isInLibrary.value = false
                    }

                    is Result.Error -> {
                        if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                            _isBeingRateLimited.value = true
                        }
                    }
                }
            }
            _showMediaListEntryPopup.value = false
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
}

data class MediaListEntryInput(
    val mediaListEntryId: Int? = null,
    val status: MediaListStatus? = MediaListStatus.PLANNING,
    val progress: Int? = 0,
    val score: Int? = null,
    val startedAt: GetMediaListEntryQuery.StartedAt? = null,
    val completedAt: GetMediaListEntryQuery.CompletedAt? = null,
)