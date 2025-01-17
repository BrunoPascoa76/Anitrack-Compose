package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetAiringAnimeCalendarQuery
import cm.project.anitrack_compose.graphql.GetMediaListsQuery
import cm.project.anitrack_compose.graphql.type.AiringSort
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.PreferencesRepository
import cm.project.anitrack_compose.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val graphQLRepository: GraphQLRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private var _userId: Int? = null
    private var _airingWatchlist: List<Int> = emptyList()

    private var _isRefreshing = false
    private var refreshJob: Job? = null

    val calendarFilterWatchlist = preferencesRepository.calendarFilterWatchlist
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _calendar =
        MutableStateFlow<List<GetAiringAnimeCalendarQuery.AiringSchedule?>?>(emptyList())
    val calendar = _calendar.asStateFlow()

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }


    fun setCalendarFilterWatchlist(value: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveCalendarFilterWatchlist(value)
        }
    }

    private suspend fun refresh() {
        if (_userId == null) {
            when (val result = graphQLRepository.getUserId()) {
                is Result.Success -> {
                    _userId = result.data
                }

                is Result.Error -> {
                    return
                }
            }
        }
        when (val result = graphQLRepository.getMediaLists(
            _userId!!,
            listOf(MediaListStatus.CURRENT, MediaListStatus.PLANNING)
        )) {
            is Result.Success -> {
                processAiringWatchlist(result.data)
            }

            is Result.Error -> {}
        }
        when (val result = graphQLRepository.getAiringAnimeCalendar(
            airingAtGreater = LocalDate.now().plusDays(_selectedIndex.value.toLong())
                .atStartOfDay(ZoneId.systemDefault()).toEpochSecond().toInt(),
            airingAtLesser = LocalDate.now().plusDays(_selectedIndex.value.toLong())
                .atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toEpochSecond().toInt(),
            sort = listOf(AiringSort.TIME),
            mediaIdIn = _airingWatchlist
        )) {
            is Result.Success -> {
                _calendar.value = result.data.airingSchedules
            }

            is Result.Error -> {}
        }
    }

    private fun processAiringWatchlist(mediaListCollection: GetMediaListsQuery.MediaListCollection) {
        _airingWatchlist = mediaListCollection.lists?.flatMap { list ->
            list?.entries?.mapNotNull { entry ->
                entry?.media?.id
            } ?: emptyList()
        } ?: emptyList()
    }

    fun startRefreshing() {
        _isRefreshing = true
        refreshJob = viewModelScope.launch {
            while (_isRefreshing) {
                refresh()
                delay(60_000)
            }
        }
    }

    fun stopRefreshing() {
        _isRefreshing = false
        refreshJob?.cancel()
    }
}